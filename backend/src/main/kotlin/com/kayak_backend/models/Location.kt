package com.kayak_backend.models

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Location(val latitude: Double, val longitude: Double) {

    private val RADIUS: Double = 6371.0;
    fun distance(coord2: Location): Double {
        val coord1 = this;
        val deltaLat = Math.toRadians(coord2.latitude - coord1.latitude)
        val deltaLon = Math.toRadians(coord2.longitude - coord1.longitude)

        val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
                cos(Math.toRadians(coord1.latitude)) * cos(Math.toRadians(coord2.latitude)) *
                sin(deltaLon / 2) * sin(deltaLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return RADIUS * c
    }
}

