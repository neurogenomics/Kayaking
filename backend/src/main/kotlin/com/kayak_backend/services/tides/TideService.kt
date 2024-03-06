package com.kayak_backend.services.tides

import com.kayak_backend.models.Location
import com.kayak_backend.models.TideGrid
import com.kayak_backend.models.TideInfo
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

interface TideService {
    fun getTide(
        loc: Location,
        time: LocalDateTime,
    ): TideInfo

    fun getTideGrid(
        cornerSW: Location,
        cornerNE: Location,
        time: LocalDateTime,
        resolutions: Pair<Double, Double>,
    ): TideGrid

    fun getTideAllDay(
        loc: Location,
        date: LocalDate,
    ): Map<LocalTime, TideInfo>
}
