package com.kayak_backend.services.route.kayak

import com.kayak_backend.models.Location
import java.time.LocalDateTime

const val DEFAULT_KAYAK_SPEED = 1.54

class BasicKayak : Kayak {
    override fun getSpeed(
        dateTime: LocalDateTime,
        location: Location,
        bearing: Double,
        kayakerSpeeed: Double,
    ): Double {
        return kayakerSpeeed
    }
}
