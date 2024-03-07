package com.kayak_backend.services.slipways

import com.kayak_backend.models.Location
import com.kayak_backend.services.isleOfWightLocation1
import com.kayak_backend.services.isleOfWightLocation2
import com.kayak_backend.services.route.NamedLocation
import okhttp3.OkHttpClient

class SlipwayService(private val client: OkHttpClient = OkHttpClient()) {
    var slipways: List<NamedLocation> = listOf()
    private val slipwayGetter = SlipwaysGetter(client, isleOfWightLocation1, isleOfWightLocation2)

    fun getAllSlipways(): List<NamedLocation> {
        if (slipways.isEmpty()) {
            slipways = slipwayGetter.getSlipways()
        }
        return slipways
    }

    fun getClosestSlipway(coords: Location): NamedLocation {
        if (slipways.isEmpty()) {
            slipways = slipwayGetter.getSlipways()
        }
        return slipways.reduce { closest, current ->
            val distanceToClosest = coords distanceTo closest.location
            val distanceToCurrent = coords distanceTo current.location

            if (distanceToCurrent < distanceToClosest) {
                current
            } else {
                closest
            }
        }
    }
}
