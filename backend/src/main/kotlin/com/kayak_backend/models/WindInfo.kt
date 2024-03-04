package com.kayak_backend.models

import kotlinx.serialization.Serializable
import kotlin.math.sqrt

@Serializable
data class WindInfo(val u: Double, val v: Double)

fun getWindMagnitude(windInfo: WindInfo): Double {
    return sqrt(windInfo.u * windInfo.u + windInfo.v * windInfo.v)
}

@Serializable
data class WindGrid(val grid: List<List<WindInfo?>>, val latIndex: List<Double>, val lonIndex: List<Double>)
