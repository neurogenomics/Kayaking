package com.kayak_backend.gribReader

import com.kayak_backend.models.Range
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.pow

data class SingleVarPoint(val lat: Double, val lon: Double, val time: LocalDateTime, val variable: String)

data class VarPairPoint(
    val lat: Double,
    val lon: Double,
    val time: LocalDateTime,
    val variable1: String,
    val variable2: String,
)

class CachingGribReader(val gribReader: GribReader, locationDecimalPlaces: Int = 2) : GribReader {
    private val locationPrecision = 10.0.pow(locationDecimalPlaces)
    private val singleVarCache = HashMap<SingleVarPoint, Double>()
    private val pairVarCache = HashMap<VarPairPoint, Pair<Double, Double>>()

    private fun locationKey(
        lat: Double,
        lon: Double,
    ): Pair<Double, Double> {
        val roundedLat = Math.round(lat * locationPrecision) / locationPrecision
        val roundedLon = Math.round(lon * locationPrecision) / locationPrecision
        return Pair(roundedLat, roundedLon)
    }

    private fun timeKey(time: LocalDateTime): LocalDateTime {
        return time.withMinute(0).withSecond(0).withNano(0)
    }

    override fun getSingleVar(
        lat: Double,
        lon: Double,
        time: LocalDateTime,
        variableName: String,
        filePath: String,
    ): Double {
        val (roundedLat, roundedLon) = locationKey(lat, lon)
        val key = SingleVarPoint(roundedLat, roundedLon, timeKey(time), variableName)
        return singleVarCache.getOrPut(key) { gribReader.getSingleVar(lat, lon, time, variableName, filePath) }
    }

    override fun getVarPair(
        lat: Double,
        lon: Double,
        time: LocalDateTime,
        var1Name: String,
        var2Name: String,
        filePath: String,
    ): Pair<Double, Double> {
        val (roundedLat, roundedLon) = locationKey(lat, lon)
        val key = VarPairPoint(roundedLat, roundedLon, timeKey(time), var1Name, var2Name)
        return pairVarCache.getOrPut(key) { gribReader.getVarPair(lat, lon, time, var1Name, var2Name, filePath) }
    }

    override fun getVarGrid(
        latRange: Range,
        lonRange: Range,
        time: LocalDateTime,
        variableName: String,
        filePath: String,
    ): Triple<List<List<Double>>, List<Double>, List<Double>> {
        return gribReader.getVarGrid(latRange, lonRange, time, variableName, filePath)
    }

    override fun getTimeRange(filePath: String): List<LocalDateTime> {
        return gribReader.getTimeRange(filePath)
    }

    override fun getDayData(
        lat: Double,
        lon: Double,
        date: LocalDate,
        var1Name: String,
        var2Name: String,
        filePath: String,
    ): Map<LocalTime, Pair<Double, Double>> {
        // TODO figure out how to cache this
        return gribReader.getDayData(lat, lon, date, var1Name, var2Name, filePath)
    }
}
