package com.kayak_backend.services.windFiltering

import com.kayak_backend.getConf
import com.kayak_backend.getWindService
import com.kayak_backend.models.WindInfo
import com.kayak_backend.services.seaBearing.SeaBearingService
import com.kayak_backend.services.wind.WindService
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

private const val BAD_WIND_LIMIT = 90

// TODO figure out what this limit should be and if it should just be a component
private const val BAD_WIND_MAGNITUDE_LIMIT = 2

class WindFiltering(
    private val windService: WindService = getWindService(getConf("./config.yaml")),
    private val seaBearingService: SeaBearingService = SeaBearingService(),
) {
    // returns list of SeaBearingInfo and bool for if the wind is out to sea
    fun classifyAreas(): List<WindZonesInfo> {
        val seaBearings = seaBearingService.getSeaBearings()

        return seaBearings.map {
            val wind = windService.getWind(it.coor, LocalDateTime.now())

            WindZonesInfo(it, badArea(it.bearing, wind))
        }
    }

    // returns true if wind direction is out to sea (meaning bad zone)
    private fun badArea(
        bearing: Double,
        wind: WindInfo,
    ): Boolean {
        val magnitude = sqrt(wind.u * wind.u + wind.v * wind.v)
        val resultWind = Math.toDegrees(atan2(wind.u, wind.v))
        val dif = abs(resultWind - bearing)

        return magnitude >= BAD_WIND_MAGNITUDE_LIMIT && (dif < BAD_WIND_LIMIT || dif > (360 - BAD_WIND_LIMIT))
    }
}
