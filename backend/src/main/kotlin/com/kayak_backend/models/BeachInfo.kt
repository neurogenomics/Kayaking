package com.kayak_backend.models

import kotlinx.serialization.Serializable

@Serializable
data class BeachInfo(val name: String?, val coordinates: List<Location>)

// , val lifeguard: Boolean?, val supervised: Boolean?, val surface: String?
