package com.kayak_backend.models

// Should this be a model or somewhere else?
// It is only being used in the backend, so I have not made it serializable
data class SeaBearingInfo(val bearing: Double, val coor: Location)

