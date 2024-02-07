package com.kayak_backend.gribReader

import java.time.LocalDateTime

interface GribReader {
    fun getSingleVar(
        lat: Double,
        lon: Double,
        time: LocalDateTime,
        variableName: String,
        filePath: String,
        latVarName: String,
        lonVarName: String,
        timeVarName: String,
    ): Double

    fun getVarPair(
        lat: Double,
        lon: Double,
        time: LocalDateTime,
        var1Name: String,
        var2Name: String,
        filePath: String,
        latVarName: String,
        lonVarName: String,
        timeVarName: String,
    ): Pair<Double, Double>

    fun getVarGrid(
        latRange: Pair<Double, Double>,
        lonRange: Pair<Double, Double>,
        time: LocalDateTime,
        variableName: String,
        filePath: String,
        latVarName: String,
        lonVarName: String,
        timeVarName: String,
    ): Triple<List<List<Double>>, List<Double>, List<Double>>
}
