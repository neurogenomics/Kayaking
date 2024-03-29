package com.kayak_backend.models

import kotlinx.serialization.Serializable
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val RADIUS: Double = 6371.0 * 1000

@Serializable
data class Location(val latitude: Double, val longitude: Double) {
    infix fun distanceTo(coord2: Location): Double {
        val coord1 = this
        val deltaLat = Math.toRadians(coord2.latitude - coord1.latitude)
        val deltaLon = Math.toRadians(coord2.longitude - coord1.longitude)

        val a =
            sin(deltaLat / 2) * sin(deltaLat / 2) +
                cos(Math.toRadians(coord1.latitude)) * cos(Math.toRadians(coord2.latitude)) *
                sin(deltaLon / 2) * sin(deltaLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return RADIUS * c
    }

    // returns 0 when the same coordinate is used as parameter
    infix fun bearingTo(coord2: Location): Double {
        val lat1 = Math.toRadians(this.latitude)
        val lon1 = Math.toRadians(this.longitude)
        val lat2 = Math.toRadians(coord2.latitude)
        val lon2 = Math.toRadians(coord2.longitude)

        val dLon = lon2 - lon1

        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)

        var initialBearing = atan2(y, x)
        initialBearing = Math.toDegrees(initialBearing)
        initialBearing = (initialBearing + 360) % 360

        return initialBearing
    }
}

fun averageLocation(locs: List<Location>): Location {
    val n = locs.size
    var latitude = 0.0
    var longitude = 0.0

    for (l in locs) {
        latitude += l.latitude
        longitude += l.longitude
    }

    return Location(latitude / n, longitude / n)
}
