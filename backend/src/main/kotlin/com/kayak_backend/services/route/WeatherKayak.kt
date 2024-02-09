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
    private val windService: WindService = getWindService(getConf("./config.yaml")),
    private val tideService: TideService = getTideService(getConf("./config.yaml")),
) : Kayak {
    override fun getSpeed(
        dateTime: LocalDateTime,
        location: Location,
        bearing: Double,
    ): Double {
        val windinfo = windService.getWind(location, dateTime)
        val tideinfo = tideService.getTide(location, dateTime)
        // TODO need way to set this, should all kayaks have kayaker speed as a parameter?
        val kayakerSpeed = 3.0
        val goalBearingRad = Math.toRadians(bearing)

        // elsi ou es tu parti ;(

        val weatherU: Double = windinfo.u + tideinfo.u
        val weatherV: Double = windinfo.v + tideinfo.v

        // i think these maths equations are all wrong
        // need to find out how to solve the simultaneous equations :(
        var resultSpeed = 0.0
        var resultBearing = 0.0
        if (weatherU == 0.0) {
            // do something
        } else if (sin(goalBearingRad) == 0.0) {
            // do something
        } else {
            resultBearing = acos(weatherV / weatherU)
            resultSpeed = (weatherU + kayakerSpeed * sin(resultBearing)) / sin(goalBearingRad)
        }

        return resultSpeed
    }
}
