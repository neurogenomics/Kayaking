package com.kayak_backend.services.seaBearing

import com.kayak_backend.models.Location

// Should this be a model or somewhere else?
// It is only being used in the backend, so I have not made it serializable
data class SeaBearingInfo(val bearing: Double, val coor: Location)

