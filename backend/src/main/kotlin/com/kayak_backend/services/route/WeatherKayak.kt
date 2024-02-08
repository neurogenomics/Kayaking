package com.kayak_backend.services.route

import com.kayak_backend.getConf
import com.kayak_backend.getTideService
import com.kayak_backend.getWindService
import com.kayak_backend.models.Location
import com.kayak_backend.services.tides.TideService
import com.kayak_backend.services.wind.WindService
import java.time.LocalDateTime
import kotlin.math.*

class WeatherKayak(
    val windService : WindService = getWindService(getConf("./config.yaml")),
    val tideService :TideService = getTideService(getConf("./config.yaml"))
    ) : Kayak{


    override fun getSpeed(
        dateTime: LocalDateTime,
        location: Location,
        bearing: Double,
    ): Double {
        val windinfo = windService.getWind(location, dateTime)
        val tideinfo = tideService.getTide(location, dateTime)
        val kayakerSpeed = 3.0

        val goalBearingRad = Math.toRadians(bearing)

        // elsi ou es tu parti ;(

        val weatherU : Double = windinfo.u + tideinfo.u
        val weatherV : Double = windinfo.v + tideinfo.v

        //if sin and cos are wrong then it will be opposite
        var resultSpeed = 0.0
        var resultBearing = 0.0
        if (weatherU == 0.0){
            // do something
        } else if (sin(goalBearingRad) == 0.0){
            // do something
        } else {
            resultBearing = acos(weatherV / weatherU)
            resultSpeed = (weatherU + kayakerSpeed * sin(resultBearing)) / sin(goalBearingRad)

        }

        return resultSpeed
    }
}

fun main() {
    /* please sanity check because chat gpt thinks the sin and cos should be the other way round
    * and i usually trust it*/
    val speed = 1.0
    val bearing = 35.0
    val bearingRad = Math.toRadians(bearing)

    val u = speed * sin(bearingRad)
    val v = speed * cos(bearingRad)



    val brng = (Math.toDegrees(atan2(u, v)) + 360) % 360


    println("u is $u and v is $v and bearing is $brng")
}


/*
* so they
* */