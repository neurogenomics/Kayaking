package com.kayak_backend.services.route

import com.kayak_backend.models.Location
import java.time.LocalDateTime

class BasicKayak : Kayak {
    override fun getSpeed(
        dateTime: LocalDateTime,
        location: Location,
        bearing: Double,
    ): Double {
        return 1.54
    }
}
