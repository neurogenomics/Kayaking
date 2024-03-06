package com.kayak_backend.services.waves

import com.kayak_backend.models.Location
import com.kayak_backend.models.WaveGrid
import com.kayak_backend.models.WaveInfo
import java.time.LocalDateTime

interface WaveService {
    fun getWave(
        loc: Location,
        time: LocalDateTime,
    ): WaveInfo

    fun getWaveGrid(
        cornerSW: Location,
        cornerNE: Location,
        time: LocalDateTime,
        resolutions: Pair<Double, Double>,
    ): WaveGrid
}
