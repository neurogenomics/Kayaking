package com.kayak_backend.gribReader

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
        timeVarName: String
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
        timeVarName: String
    ): Pair<Double, Double> {
        val file = NetcdfDataset.openFile(filePath, null)

        val (latIndex, lonIndex) = findLatLon(file, latVarName, lat, lonVarName, lon)
        val timeIndex = findTime(file, timeVarName, time)
        val val1 = fetchVarAtLoc(file, latIndex, lonIndex, timeIndex, var1Name)
        val val2 = fetchVarAtLoc(file, latIndex, lonIndex, timeIndex, var2Name)

        return Pair(val1, val2)
    }

    private fun findLatLon(
        file: NetcdfFile,
        latVarName: String,
        lat: Double,
        lonVarName: String,
        lon: Double
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

    private fun processDateString(input: String): LocalDateTime {
        val pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        val formatter = DateTimeFormatter.ofPattern(pattern)
        val dateTimeString = input.substringAfter("since ").trim()
        return LocalDateTime.parse(dateTimeString, formatter)
    }

    private fun findTime(file: NetcdfFile, timeVarName: String, time: LocalDateTime): Int {
        val timeVar = file.findVariable(timeVarName) ?: throw GribFileError("Time variable not found")
        val reftime = processDateString(timeVar.unitsString)
        val duration = Duration.between(reftime, time)
        val firstTime = timeVar.read().getDouble(0).toInt()
        return duration.toHours().toInt() - firstTime
    }

    private fun fetchVarAtLoc(file: NetcdfFile, latIndex: Int, lonIndex: Int, timeIndex: Int, variableName: String): Double {
        val variable = file.findVariable(variableName) ?: throw GribFileError("Variable $variableName not found")
        val rank = variable.rank
        val origin = IntArray(rank)

        var latDim = 0
        var lonDim = 0
        var timeDim = 0

        for((i, dim) in variable.dimensionsAll.withIndex()) {
            val name = dim.dodsName
            when (name) {
                "lat" -> latDim = i
                "lon" -> lonDim = i
                "time" -> timeDim = i
                "time1" -> timeDim = i
            }
        }
        val varShape = variable.shape
        if (latIndex !in 1..<varShape[latDim] - 1) throw GribIndexError("Latitude out of bounds")
        if (lonIndex !in 1..<varShape[lonDim] - 1) throw GribIndexError("Longitude out of bounds")
        if (timeIndex !in 1..<varShape[timeDim] - 1) throw GribIndexError("Time is out of bounds")

        origin[latDim] = latIndex
        origin[lonDim] = lonIndex
        origin[timeDim] = timeIndex

        val shape = IntArray(rank) { 1 }
        val res = variable.read(origin, shape).reduce().getDouble(0)
        if (res.isNaN()) {
            var i = 1
            var resList: List<Double>
            do  {
                resList = trySurrounding(variable, origin, shape, latDim, lonDim, i)
                i++
            } while (resList.isEmpty())
            return resList.average()
        }
        return res
    }

    private fun trySurrounding(variable: Variable, origin: IntArray, shape: IntArray, latDim: Int, lonDim: Int, i: Int): List<Double> {
        val above = origin.copyOf()
        val below = origin.copyOf()
        val right = origin.copyOf()
        val left = origin.copyOf()

        above[latDim] += i
        below[latDim] -= i
        right[lonDim] += i
        left[lonDim] -= i

        val surroundingValues = arrayOf(above, below, right, left).map {x ->
            variable.read(x, shape).reduce(0).getDouble(0) }.filter {
                x -> !x.isNaN() }
        return surroundingValues
    }
}