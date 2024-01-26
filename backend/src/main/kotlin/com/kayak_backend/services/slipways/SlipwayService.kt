package com.kayak_backend.services.slipways

import com.kayak_backend.models.Location

class SlipwayService {
    var slipways : List<Location>  = listOf()
    val slipwayGetter = SlipwaysGetter();

    fun closestSlipway(coords : Location) : Location {
        if (slipways.isEmpty()){
            slipways = slipwayGetter.getSlipways();
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