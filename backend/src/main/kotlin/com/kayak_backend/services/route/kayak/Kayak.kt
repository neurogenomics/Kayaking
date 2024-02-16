package com.kayak_backend.services.route.kayak

import com.kayak_backend.models.Location
import java.time.LocalDateTime

interface Kayak {
    fun getSpeed(
        dateTime: LocalDateTime,
        location: Location,
        bearing: Double,
    ): Double
}
