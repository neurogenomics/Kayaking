package com.kayak_backend.services.tides

import com.kayak_backend.models.Location
import java.time.LocalDateTime

interface TideService {
    fun getTide(loc: Location, time: LocalDateTime): Pair<Double, Double>
}