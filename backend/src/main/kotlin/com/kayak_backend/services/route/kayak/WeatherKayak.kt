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
    private val kayakerSpeed: Double,
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
        val goalBearingRad = Math.toRadians(bearing)

        val weatherU: Double = windinfo.u * WIND_MULT + tideinfo.u * TIDE_MULT
        val weatherV: Double = windinfo.v * WIND_MULT + tideinfo.v * TIDE_MULT
        val weatherBearing = (atan2(weatherU, weatherV) + 2 * PI) % (2 * PI)
        val sqr: (Double) -> Double = { it * it }
        val weatherMag = sqrt(sqr(weatherU) + sqr(weatherV))

        val resultSpeed: Double

        val dif = abs(weatherBearing - goalBearingRad)

        /*
        We use the sine rule to resolve the velocities and find the speed in the wanted direction based on a triangle
        If else used to filter out the case the difference in angle is 0 or 180 degrees (so no triangle)
         */
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

        if (resultSpeed.isNaN()) {
            return kayakerSpeed
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

    companion object {
        // factors that wind and tide speed impact kayaker speed
        private const val WIND_MULT = 0.02
        private const val TIDE_MULT = 0.05
    }
}
