package com.kayak_backend.services.windFiltering

import com.kayak_backend.services.seaBearing.SeaBearingInfo
import kotlinx.serialization.Serializable

@Serializable
data class WindZonesInfo(val bearing: SeaBearingInfo, val bad: Boolean)
