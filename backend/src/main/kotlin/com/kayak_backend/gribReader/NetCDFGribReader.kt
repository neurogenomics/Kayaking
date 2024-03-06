package com.kayak_backend.gribReader

import com.kayak_backend.models.Range
import ucar.nc2.NetcdfFile
import ucar.nc2.Variable
import ucar.nc2.dataset.NetcdfDataset
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NetCDFGribReader : GribReader {
    private val timeRegex = ".+/time"

    override fun getSingleVar(
        lat: Double,
        lon: Double,
        time: LocalDateTime,
        variableName: String,
        filePath: String,
    ): Double {
        val file = NetcdfDataset.openFile(filePath, null)

        val variable = getVariable(file, variableName)
        val (latIndex, lonIndex) = findLatLon(variable, lat, lon)
        val timeIndex = findTime(variable, time)

        return fetchVarAtLoc(variable, latIndex, lonIndex, timeIndex).also { file.close() }
    }

    override fun getVarPair(
        lat: Double,
        lon: Double,
        time: LocalDateTime,
        var1Name: String,
        var2Name: String,
        filePath: String,
    ): Pair<Double, Double> {
        val file = NetcdfDataset.openFile(filePath, null)

        val variable1 = getVariable(file, var1Name)
        val variable2 = getVariable(file, var2Name)

        if (variable1.group.fullName == variable2.group.fullName) {
            val (latIndex, lonIndex) = findLatLon(variable1, lat, lon)
            val timeIndex = findTime(variable1, time)
            val val1 = fetchVarAtLoc(variable1, latIndex, lonIndex, timeIndex)
            val val2 = fetchVarAtLoc(variable2, latIndex, lonIndex, timeIndex)
            file.close()
            return Pair(val1, val2)
        } else {
            file.close()
            val val1 = getSingleVar(lat, lon, time, var1Name, filePath)
            val val2 = getSingleVar(lat, lon, time, var2Name, filePath)
            return Pair(val1, val2)
        }
    }

    override fun getVarGrid(
        latRange: Range,
        lonRange: Range,
        time: LocalDateTime,
        variableName: String,
        filePath: String,
    ): Triple<List<List<Double>>, List<Double>, List<Double>> {
        val file = NetcdfDataset.openFile(filePath, null)

        val variable = getVariable(file, variableName)

        val (latIndex1, lonIndex1) = findLatLon(variable, latRange.start, lonRange.start)
        val (latIndex2, lonIndex2) = findLatLon(variable, latRange.end, lonRange.end)
        val timeIndex = findTime(variable, time)

        val data = fetchVarGrid(variable, latIndex1, latIndex2, lonIndex1, lonIndex2, timeIndex)
        val (latIndex, lonIndex) =
            getLatLonRange(
                variable,
                Pair(latIndex1, latIndex2),
                Pair(lonIndex1, lonIndex2),
            )
        return Triple(data, latIndex, lonIndex)
    }

    override fun getTimeRange(filePath: String): List<LocalDateTime> {
        val file = NetcdfDataset.openFile(filePath, null)
        val timeVar = getVariable(file, timeRegex)
        val refTime = getRefTime(timeVar)
        val timeData = timeVar.read()
        file.close()
        return List(timeData.shape.max()) { refTime.plusHours(timeData.getDouble(it).toLong()) }
    }

    private fun getVariable(
        file: NetcdfFile,
        name: String,
    ): Variable {
        val pattern = Regex(name)
        file.variables.forEach {
            if (pattern matches it.fullName) {
                return it
            }
        }
        throw GribFileError("$name variable not found")
    }

    private fun findLatLon(
        variable: Variable,
        lat: Double,
        lon: Double,
    ): Pair<Int, Int> {
        // TODO: Make this less inefficient
        val group = variable.group

        val latVar = group.findVariable("lat") ?: throw GribFileError("Latitude variable not found")
        val latData = latVar.read()
        if (lat < latData.getDouble(0) || lat > latData.getDouble(latData.shape[0] - 1)) throw GribIndexError("Latitude out of bounds")

        var latIndex = 0
        while (latData.getDouble(latIndex + 1) < lat) {
            latIndex++
        }

        val lonVar = group.findVariable("lon") ?: throw GribFileError("Longitude variable not found")
        val lonData = lonVar.read()
        if (lon < lonData.getDouble(0) || lon > lonData.getDouble(lonData.shape[0] - 1)) throw GribIndexError("Longitude out of bounds")

        var lonIndex = 0
        while (lonData.getDouble(lonIndex + 1) < lon) {
            lonIndex++
        }

        return Pair(latIndex, lonIndex)
    }

    private fun getLatLonRange(
        variable: Variable,
        latIndices: Pair<Int, Int>,
        lonIndices: Pair<Int, Int>,
    ): Pair<List<Double>, List<Double>> {
        val group = variable.group

        val latVar = group.findVariable("lat") ?: throw GribFileError("Latitude variable not found")
        val latData = latVar.read()

        val lonVar = group.findVariable("lon") ?: throw GribFileError("Longitude variable not found")
        val lonData = lonVar.read()

        val latIndex =
            (latIndices.first + 1..latIndices.second).map {
                latData.getDouble(it)
            }
        val lonIndex =
            (lonIndices.first + 1..lonIndices.second).map {
                lonData.getDouble(it)
            }

        return Pair(latIndex, lonIndex)
    }

    private fun getRefTime(timeVar: Variable): LocalDateTime {
        val input = timeVar.unitsString
        val pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        val formatter = DateTimeFormatter.ofPattern(pattern)
        val dateTimeString = input.substringAfter("since ").trim()
        return LocalDateTime.parse(dateTimeString, formatter)
    }

    private fun findTime(
        variable: Variable,
        time: LocalDateTime,
    ): Int {
        val group = variable.group

        val timeVar = group.findVariable("time") ?: throw GribFileError("Time variable not found")
        val reftime = getRefTime(timeVar)
        val duration = Duration.between(reftime, time)
        val firstTime = timeVar.read().getDouble(0).toInt()

        val timeIndex = duration.toHours().toInt() - firstTime
        if (timeIndex < 0 || timeIndex > timeVar.shape.max() - 1) throw GribIndexError("Time variable out of bounds")
        return timeIndex
    }

    private fun fetchVarAtLoc(
        variable: Variable,
        latIndex: Int,
        lonIndex: Int,
        timeIndex: Int,
    ): Double {
        val rank = variable.rank
        val origin = IntArray(rank)

        val (latDim, lonDim, timeDim) = getDimensionsIndex(variable)

        origin[latDim] = latIndex
        origin[lonDim] = lonIndex
        origin[timeDim] = timeIndex

        val shape = IntArray(rank) { 1 }
        val res = variable.read(origin, shape).reduce().getDouble(0)
        if (res.isNaN()) {
            var i = 1
            var resList: List<Double>
            do {
                resList = trySurrounding(variable, origin, shape, latDim, lonDim, i)
                i++
            } while (resList.isEmpty())
            return resList.average()
        }
        return res
    }

    private fun fetchVarGrid(
        variable: Variable,
        latIndex1: Int,
        latIndex2: Int,
        lonIndex1: Int,
        lonIndex2: Int,
        timeIndex: Int,
    ): List<List<Double>> {
        val rank = variable.rank
        val origin = IntArray(rank)
        val (latDim, lonDim, timeDim) = getDimensionsIndex(variable)

        origin[latDim] = latIndex1 + 1
        origin[lonDim] = lonIndex1 + 1
        origin[timeDim] = timeIndex

        val shape = IntArray(rank) { 1 }
        shape[latDim] = latIndex2 - latIndex1
        shape[lonDim] = lonIndex2 - lonIndex1

        val res = variable.read(origin, shape)

        val newShape = intArrayOf(shape[latDim], shape[lonDim])

        // converts library arraytype to kotlin list
        return List(newShape[0]) { List(newShape[1]) { it2 -> res.getDouble((it * shape[lonDim]) + it2) } }
    }

    private fun getDimensionsIndex(variable: Variable): Triple<Int, Int, Int> {
        var latDim = 0
        var lonDim = 0
        var timeDim = 0
        var maxTimeSize = 0

        val assignTimeDim = { size: Int, i: Int ->
            if (size > maxTimeSize) {
                maxTimeSize = size
                timeDim = i
            }
        }

        for ((i, dim) in variable.dimensionsAll.withIndex()) {
            // Since the variable is stored in a Nd array with n likely being 3 (lon lat time)
            // this checks along which axis are these all being stored
            // this isn't constant, as e.g. wind has another dimension which is height above surface
            // however in the grib files we have encountered this dimension is of size one (only one measurement)
            // sometimes time is called different things, this gets the one that is actually has size
            val name = dim.dodsName
            when (name) {
                "lat" -> latDim = i
                "lon" -> lonDim = i
                "time" -> assignTimeDim(dim.length, i)
                "time1" -> assignTimeDim(dim.length, i)
                "reftime" -> assignTimeDim(dim.length, i)
            }
        }
        return Triple(latDim, lonDim, timeDim)
    }

    private fun trySurrounding(
        variable: Variable,
        origin: IntArray,
        shape: IntArray,
        latDim: Int,
        lonDim: Int,
        i: Int,
    ): List<Double> {
        val above = origin.copyOf()
        val below = origin.copyOf()
        val right = origin.copyOf()
        val left = origin.copyOf()

        above[latDim] += i
        below[latDim] -= i
        right[lonDim] += i
        left[lonDim] -= i

        val surroundingValues =
            arrayOf(above, below, right, left).map { x ->
                variable.read(x, shape).reduce(0).getDouble(0)
            }.filter { x ->
                !x.isNaN()
            }
        return surroundingValues
    }

    fun findTimeFromDay(
        variable: Variable,
        date: LocalDate,
    ): Int {
        val group = variable.group
        val time = date.atStartOfDay()

        val timeVar = group.findVariable("time") ?: throw GribFileError("Time variable not found")
        val reftime = getRefTime(timeVar)
        val duration = Duration.between(reftime, time)
        val firstTime = timeVar.read().getDouble(0).toInt()

        val timeIndex = duration.toHours().toInt() - firstTime
        if (timeIndex < 0 || timeIndex > timeVar.shape.max() - 1) throw GribIndexError("Time variable out of bounds")
        if (timeIndex + 24 > timeVar.shape.max() - 1) throw GribIndexError("Time variable out of bounds")

        return timeIndex
    }

    fun fetchTimeSlice(
        variable: Variable,
        latIndex: Int,
        lonIndex: Int,
        timeIndex: Int,
    ): List<Double> {
        val rank = variable.rank
        val origin = IntArray(rank)

        val (latDim, lonDim, timeDim) = getDimensionsIndex(variable)

        origin[latDim] = latIndex
        origin[lonDim] = lonIndex
        origin[timeDim] = timeIndex

        val shape = IntArray(rank) { 1 }
        shape[timeDim] = 24

        val res = List(24) { variable.read(origin, shape).getDouble(it) }
        res.mapIndexed { index, it ->
            if (it.isNaN()) {
                var i = 1
                var resList: List<Double>
                do {
                    origin[timeDim] = timeIndex + index
                    resList = trySurrounding(variable, origin, IntArray(rank) { 1 }, latDim, lonDim, i)
                    i++
                } while (resList.isEmpty())
                resList.average()
            }
        }
        return res
    }

    override fun getTimeSlice(
        lat: Double,
        lon: Double,
        date: LocalDate,
        var1Name: String,
        var2Name: String,
        filePath: String,
    ): List<Pair<Double, Double>> {
        val file = NetcdfDataset.openFile(filePath, null)

        val variable1 = getVariable(file, var1Name)
        val variable2 = getVariable(file, var2Name)

        if (variable1.group.fullName == variable2.group.fullName) {
            val (latIndex, lonIndex) = findLatLon(variable1, lat, lon)
            val timeIndex = findTimeFromDay(variable1, date)
            val val1 = fetchTimeSlice(variable1, latIndex, lonIndex, timeIndex)
            val val2 = fetchTimeSlice(variable2, latIndex, lonIndex, timeIndex)
            file.close()
            return val1 zip val2
        } else {
            throw NotImplementedError("Cannot fetch timeslice of unrelated variables")
        }
    }
}
