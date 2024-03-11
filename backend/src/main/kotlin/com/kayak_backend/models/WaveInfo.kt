package com.kayak_backend.models

import kotlinx.serialization.Serializable

@Serializable
data class WaveInfo(val height: Double, val direction: Double)

@Serializable
data class WaveGrid(val grid: List<List<WaveInfo?>>, val latIndex: List<Double>, val lonIndex: List<Double>)
