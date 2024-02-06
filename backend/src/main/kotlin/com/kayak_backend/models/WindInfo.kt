package com.kayak_backend.models

import kotlinx.serialization.Serializable

@Serializable
data class WindInfo(val u: Double, val v: Double)
