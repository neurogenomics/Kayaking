package com.kayak_backend.models

import kotlinx.serialization.Serializable

@Serializable
data class TideInfo(val u: Double, val v: Double)

@Serializable
data class TideGrid(val grid: List<List<TideInfo>>, val latIndex: List<Double>, val lonIndex: List<Double>)
