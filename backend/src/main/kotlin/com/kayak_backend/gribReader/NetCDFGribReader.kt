package com.kayak_backend.gribReader

import ucar.nc2.NetcdfFile
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
        val latVar = file.findVariable(latVarName)
        val latData = latVar.read()

        var latIndex = 0
        while (latData.getDouble(latIndex) < lat) {
            latIndex++
        }

        val lonVar = file.findVariable(lonVarName)
        val lonData = lonVar.read()

        var lonIndex = 0
        while (lonData.getDouble(lonIndex) < lon) {
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
        val timeVar = file.findVariable(timeVarName)
        val reftime = processDateString(timeVar.unitsString)
        val duration = Duration.between(reftime, time)

        return duration.toHours().toInt()
    }

    private fun fetchVarAtLoc(file: NetcdfFile, latIndex: Int, lonIndex: Int, timeIndex: Int, variableName: String): Double {
        val variable = file.findVariable(variableName)
        val rank = variable.rank
        var origin = IntArray(rank)

        for((i, dim) in variable.dimensionsAll.withIndex()) {
            val name = dim.dodsName
            when (name) {
                "lat" -> origin[i] = latIndex
                "lon" -> origin[i] = lonIndex
                "time" -> origin[i] = timeIndex
            }
        }

        val shape = IntArray(rank) { 1 }

        return variable.read(origin, shape).reduce().getDouble(0)
    }
}