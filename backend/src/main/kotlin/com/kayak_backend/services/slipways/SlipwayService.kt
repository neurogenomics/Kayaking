package com.kayak_backend.services.slipways

import com.kayak_backend.models.Location
import com.kayak_backend.services.isleOfWightLocation1
import com.kayak_backend.services.isleOfWightLocation2
import okhttp3.OkHttpClient

class SlipwayService(private val client: OkHttpClient = OkHttpClient()) {
    var slipways: List<Location> = listOf()
    val slipwayGetter = SlipwaysGetter(client, isleOfWightLocation1, isleOfWightLocation2)

    fun getAllSlipways(): List<Location> {
        return slipways
    }

    fun getClosestSlipway(coords: Location): Location {
        if (slipways.isEmpty()) {
            slipways = slipwayGetter.getSlipways()
        }
        return slipways.reduce { closest, current ->
            val distanceToClosest = coords.distance(closest)
            val distanceToCurrent = coords.distance(current)

            if (distanceToCurrent < distanceToClosest) {
                current
            } else {
                closest
            }
        }
    }
}
