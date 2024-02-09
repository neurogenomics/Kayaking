package com.kayak_backend.services.route.kayak

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
        kayakerSpeed: Double,
    ): Double {
        val windinfo = windService.getWind(location, dateTime)
        val tideinfo = tideService.getTide(location, dateTime)
        val goalBearingRad = Math.toRadians(bearing)
        val sqr: (Double) -> Double = { it * it }

        val weatherU: Double = windinfo.u + tideinfo.u
        val weatherV: Double = windinfo.v + tideinfo.v
        val weatherBearing = (atan2(weatherU, weatherV) + 2 * PI) % (2 * PI)
        val weatherMag = sqrt(sqr(weatherU) + sqr(weatherV))

        val resultSpeed: Double

        val dif = abs(weatherBearing - goalBearingRad)
        if (dif == PI || dif == 0.0) {
            resultSpeed = kayakerSpeed + if (dif == 0.0) weatherMag else -weatherMag
        } else {
            val angle: Double =
                if (dif < PI && dif > 0) {
                    dif
                } else {
                    2 * PI - dif
                }

            resultSpeed = findThirdSideOfTriangle(kayakerSpeed, weatherMag, angle)
        }

        return resultSpeed
    }

    private fun findThirdSideOfTriangle(
        sideA: Double,
        sideB: Double,
        angleA: Double,
    ): Double {
        val angleB = asin(sin(angleA) * (sideB / sideA))
        val angleC = PI - angleA - angleB
        return sideA * sin(angleC) / sin(angleA)
    }
}
