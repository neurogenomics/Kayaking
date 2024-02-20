package com.kayak_backend.models

import kotlinx.serialization.Serializable

@Serializable
data class WindInfo(val u: Double, val v: Double)

@Serializable
data class WindGrid(val grid: List<List<WindInfo?>>, val latIndex: List<Double>, val lonIndex: List<Double>)
