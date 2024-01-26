package com.kayak_backend.services.slipways

import com.kayak_backend.models.Location
import okhttp3.OkHttpClient

class SlipwayService (private val client: OkHttpClient = OkHttpClient()){

    val ISLEOFWIGHTLOCATION1 = Location(50.564485309567644, -1.6005677025384493)
    val ISLEOFWIGHTLOCATION2 = Location(50.8605772841442, -1.0457581322259493)
    var slipways : List<Location>  = listOf()
    val slipwayGetter = SlipwaysGetter(client, ISLEOFWIGHTLOCATION1, ISLEOFWIGHTLOCATION2);



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