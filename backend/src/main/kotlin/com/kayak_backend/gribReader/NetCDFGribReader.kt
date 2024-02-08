package com.kayak_backend.gribReader

import com.kayak_backend.models.Range
import ucar.nc2.NetcdfFile
import ucar.nc2.Variable
import ucar.nc2.dataset.NetcdfDataset
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NetCDFGribReader : GribReader {
    override fun getSingleVar(
        lat: Double,
        lon: Double,
        time: LocalDateTime,
        variableName: String,
        filePath: String,
        latVarName: String,
        lonVarName: String,
        timeVarName: String,
    ): Double {
        val file = NetcdfDataset.openFile(filePath, null)

        val (latIndex, lonIndex) = findLatLon(file, latVarName, lat, lonVarName, lon)
        val timeIndex = findTime(file, timeVarName, time)

        return fetchVarAtLoc(file, latIndex, lonIndex, timeIndex, variableName)
    }

    override fun getVarPair(
        lat: Double,
        lon: Double,
        time: LocalDateTime,
        var1Name: String,
        var2Name: String,
        filePath: String,
        latVarName: String,
        lonVarName: String,
        timeVarName: String,
    ): Pair<Double, Double> {
        val file = NetcdfDataset.openFile(filePath, null)

        val (latIndex, lonIndex) = findLatLon(file, latVarName, lat, lonVarName, lon)
        val timeIndex = findTime(file, timeVarName, time)
        val val1 = fetchVarAtLoc(file, latIndex, lonIndex, timeIndex, var1Name)
        val val2 = fetchVarAtLoc(file, latIndex, lonIndex, timeIndex, var2Name)

        return Pair(val1, val2)
    }

    override fun getVarGrid(
        latRange: Range,
        lonRange: Range,
        time: LocalDateTime,
        variableName: String,
        filePath: String,
        latVarName: String,
        lonVarName: String,
        timeVarName: String,
    ): Triple<List<List<Double>>, List<Double>, List<Double>> {
        val file = NetcdfDataset.openFile(filePath, null)
        val (latIndex1, lonIndex1) = findLatLon(file, latVarName, latRange.start, lonVarName, lonRange.end)
        val (latIndex2, lonIndex2) = findLatLon(file, latVarName, latRange.start, lonVarName, lonRange.end)
        val timeIndex = findTime(file, timeVarName, time)

        val data = fetchVarGrid(file, latIndex1, latIndex2, lonIndex1, lonIndex2, timeIndex, variableName)
        val (latIndex, lonIndex) =
            getLatLonRange(
                file,
                latVarName,
                Pair(latIndex1, latIndex2),
                lonVarName,
                Pair(lonIndex1, lonIndex2),
            )
        return Triple(data, latIndex, lonIndex)
    }

    private fun findLatLon(
        file: NetcdfFile,
        latVarName: String,
        lat: Double,
        lonVarName: String,
        lon: Double,
    ): Pair<Int, Int> {
        val latVar = file.findVariable(latVarName) ?: throw GribFileError("Latitude variable not found")
        val latData = latVar.read()

        var latIndex = 0
        while (latIndex < (latVar.shape[0] - 1) && latData.getDouble(latIndex + 1) < lat) {
            latIndex++
        }

        val lonVar = file.findVariable(lonVarName) ?: throw GribFileError("Longitude variable not found")
        val lonData = lonVar.read()

        var lonIndex = 0
        while (lonIndex < (lonVar.shape[0] - 1) && lonData.getDouble(lonIndex + 1) < lon) {
            lonIndex++
        }
        return Pair(latIndex, lonIndex)
    }

    private fun getLatLonRange(
        file: NetcdfFile,
        latVarName: String,
        latIndices: Pair<Int, Int>,
        lonVarName: String,
        lonIndices: Pair<Int, Int>,
    ): Pair<List<Double>, List<Double>> {
        val latVar = file.findVariable(latVarName) ?: throw GribFileError("Latitude variable not found")
        val latData = latVar.read()

        val lonVar = file.findVariable(lonVarName) ?: throw GribFileError("Longitude variable not found")
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

    private fun processDateString(input: String): LocalDateTime {
        val pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        val formatter = DateTimeFormatter.ofPattern(pattern)
        val dateTimeString = input.substringAfter("since ").trim()
        return LocalDateTime.parse(dateTimeString, formatter)
    }

    private fun findTime(
        file: NetcdfFile,
        timeVarName: String,
        time: LocalDateTime,
    ): Int {
        val timeVar = file.findVariable(timeVarName) ?: throw GribFileError("Time variable not found")
        val reftime = processDateString(timeVar.unitsString)
        val duration = Duration.between(reftime, time)
        val firstTime = timeVar.read().getDouble(0).toInt()
        return duration.toHours().toInt() - firstTime
    }

    private fun fetchVarAtLoc(
        file: NetcdfFile,
        latIndex: Int,
        lonIndex: Int,
        timeIndex: Int,
        variableName: String,
    ): Double {
        val variable = file.findVariable(variableName) ?: throw GribFileError("Variable $variableName not found")
        val rank = variable.rank
        val origin = IntArray(rank)

        val (latDim, lonDim, timeDim) = getDimensionsIndex(variable)
        testRanges(variable, latIndex, lonIndex, timeIndex, latDim, lonDim, timeDim)
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
        file: NetcdfFile,
        latIndex1: Int,
        latIndex2: Int,
        lonIndex1: Int,
        lonIndex2: Int,
        timeIndex: Int,
        variableName: String,
    ): List<List<Double>> {
        val variable = file.findVariable(variableName) ?: throw GribFileError("Variable $variableName not found")
        val rank = variable.rank
        val origin = IntArray(rank)
        val (latDim, lonDim, timeDim) = getDimensionsIndex(variable)

        testRanges(variable, latIndex1, lonIndex1, timeIndex, latDim, lonDim, timeDim)
        testRanges(variable, latIndex2, lonIndex2, timeIndex, latDim, lonDim, timeDim)

        origin[latDim] = latIndex1 + 1
        origin[lonDim] = lonIndex1 + 1
        origin[timeDim] = timeIndex

        val shape = IntArray(rank) { 1 }
        shape[latDim] = latIndex2 - latIndex1
        shape[lonDim] = lonIndex2 - lonIndex1

        val res = variable.read(origin, shape)

        val newShape = intArrayOf(shape[latDim], shape[lonDim])
        val reShape = res.reshape(newShape)

        // converts library arraytype to kotlin list
        return List(newShape[0]) { List(newShape[1]) { it2 -> reShape.getDouble((it * latDim) + it2) } }
    }

    private fun getDimensionsIndex(variable: Variable): Triple<Int, Int, Int> {
        var latDim = 0
        var lonDim = 0
        var timeDim = 0

        for ((i, dim) in variable.dimensionsAll.withIndex()) {
            // Since the variable is stored in a Nd array with n likely being 3 (lon lat time)
            // this checks along which axis are these all being stored
            // this isn't constant, as e.g. wind has another dimension which is height above surface
            // however in the grib files we have encountered this dimension is of size one (only one measurement)
            val name = dim.dodsName
            when (name) {
                "lat" -> latDim = i
                "lon" -> lonDim = i
                "time" -> timeDim = i
                "time1" -> timeDim = i // Sometimes time is stored under the time1 variable for reasons unknown
            }
        }
        return Triple(latDim, lonDim, timeDim)
    }

    private fun testRanges(
        variable: Variable,
        latIndex: Int,
        lonIndex: Int,
        timeIndex: Int,
        latDim: Int,
        lonDim: Int,
        timeDim: Int,
    ) {
        val varShape = variable.shape
        // shape contains the sizes of each of the grids, i.e. longitude latitude and
        if (latIndex !in 1..<varShape[latDim] - 1) throw GribIndexError("Latitude out of bounds")
        if (lonIndex !in 1..<varShape[lonDim] - 1) throw GribIndexError("Longitude out of bounds")
        if (timeIndex !in 1..<varShape[timeDim] - 1) throw GribIndexError("Time is out of bounds")
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
}
