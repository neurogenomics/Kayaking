package com.kayak_backend.services.wind

import com.kayak_backend.models.Location
import com.kayak_backend.models.WindGrid
import com.kayak_backend.models.WindInfo
import java.time.LocalDateTime

interface WindService {
    fun getWind(
        loc: Location,
        time: LocalDateTime,
    ): WindInfo

    fun getWindGrid(
        corner1: Location,
        corner2: Location,
        time: LocalDateTime,
        resolutions: Pair<Double, Double>,
    ): WindGrid
}
