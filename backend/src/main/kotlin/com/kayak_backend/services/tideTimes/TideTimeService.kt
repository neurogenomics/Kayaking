package com.kayak_backend.services.tideTimes

import com.kayak_backend.models.Location
import com.kayak_backend.models.TideTimes

interface TideTimeService {
    fun getTideTimes(location: Location): TideTimes
}
