package com.kayak_backend.gribReader

import com.kayak_backend.models.Range
import java.time.LocalDate
import java.time.LocalDateTime

interface GribReader {
    fun getSingleVar(
        lat: Double,
        lon: Double,
        time: LocalDateTime,
        variableName: String,
        filePath: String,
    ): Double

    fun getVarPair(
        lat: Double,
        lon: Double,
        time: LocalDateTime,
        var1Name: String,
        var2Name: String,
        filePath: String,
    ): Pair<Double, Double>

    fun getVarGrid(
        latRange: Range,
        lonRange: Range,
        time: LocalDateTime,
        variableName: String,
        filePath: String,
    ): Triple<List<List<Double>>, List<Double>, List<Double>>

    fun getTimeRange(filePath: String): List<LocalDateTime>

    fun getTimeSlice(
        lat: Double,
        lon: Double,
        date: LocalDate,
        var1Name: String,
        var2Name: String,
        filePath: String,
    ): List<Pair<Double, Double>>
}
