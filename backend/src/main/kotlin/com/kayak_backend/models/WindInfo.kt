package com.kayak_backend.models

import kotlinx.serialization.Serializable
import kotlin.math.atan2
import kotlin.math.sqrt

@Serializable
data class WindInfo(val u: Double, val v: Double)

fun getWindMagnitude(windInfo: WindInfo): Double {
    return sqrt(windInfo.u * windInfo.u + windInfo.v * windInfo.v)
}

fun getWindBearing(windInfo: WindInfo): Double {
    val bearing = Math.toDegrees(atan2(windInfo.u, windInfo.v))
    // Adjust the bearing to be in the range [0, 360)
    return (bearing + 360) % 360
}

@Serializable
data class WindGrid(val grid: List<List<WindInfo?>>, val latIndex: List<Double>, val lonIndex: List<Double>)
