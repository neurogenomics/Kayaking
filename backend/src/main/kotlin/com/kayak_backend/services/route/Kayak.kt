package com.kayak_backend.services.route

import com.kayak_backend.models.Location
import java.time.LocalDateTime

private const val DEFAULT_KAYAK_SPEED = 3.0

interface Kayak {
    fun getSpeed(
        dateTime: LocalDateTime,
        location: Location,
        bearing: Double,
        kayakerSpeed: Double = DEFAULT_KAYAK_SPEED,
    ): Double
}
