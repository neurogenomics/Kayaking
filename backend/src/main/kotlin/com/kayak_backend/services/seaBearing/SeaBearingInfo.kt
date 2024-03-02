package com.kayak_backend.services.seaBearing

import com.kayak_backend.models.Location
import kotlinx.serialization.Serializable

@Serializable
data class SeaBearingInfo(val bearing: Double, val coor: Location)
