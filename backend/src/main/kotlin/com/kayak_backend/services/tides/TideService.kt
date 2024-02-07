package com.kayak_backend.services.tides

import com.kayak_backend.models.Location
import com.kayak_backend.models.TideGrid
import com.kayak_backend.models.TideInfo
import java.time.LocalDateTime

interface TideService {
    fun getTide(
        loc: Location,
        time: LocalDateTime,
    ): TideInfo

    fun getTideGrid(
        corner1: Location,
        corner2: Location,
        time: LocalDateTime,
        resolutions: Pair<Double, Double>,
    ): TideGrid
}
