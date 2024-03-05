package com.kayak_backend.services.windFiltering

import com.kayak_backend.models.Location

data class WindZonesInfo(val location: Location, val bad: Boolean)
