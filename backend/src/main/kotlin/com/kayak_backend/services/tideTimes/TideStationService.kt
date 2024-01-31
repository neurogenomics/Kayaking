package com.kayak_backend.services.tideTimes

import com.kayak_backend.models.Location
import com.kayak_backend.models.TideStation

interface TideStationService {
    fun getTideStations(): List<TideStation>
}