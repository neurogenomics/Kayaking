package com.kayak_backend.services.windFiltering

import com.kayak_backend.getConf
import com.kayak_backend.getWindService
import com.kayak_backend.models.WindInfo
import com.kayak_backend.services.seaBearing.SeaBearingService
import com.kayak_backend.services.wind.WindService
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.atan2

private const val BAD_WIND_LIMIT = 90

class WindFiltering(
    private val windService: WindService = getWindService(getConf("./config.yaml")),
    private val seaBearingService: SeaBearingService = SeaBearingService(),
) {
    // returns list of SeaBearingInfo and bool for if the wind is out to sea
    fun classifyAreas(): List<WindZonesInfo> {
        val seaBearings = seaBearingService.getSeaBearings()

        return seaBearings.map {
            val now = LocalDateTime.now()
            val wind = windService.getWind(it.coor, LocalDateTime.of(now.year, now.month, now.dayOfMonth, now.hour, now.minute))

            WindZonesInfo(it, badArea(it.bearing, wind))
        }
    }

    // returns true if wind direction is out to sea (meaning bad zone)
    private fun badArea(
        bearing: Double,
        wind: WindInfo,
    ): Boolean {
        val resultWind = Math.toDegrees(atan2(wind.u, wind.v))
        val dif = abs(resultWind - bearing)

        return (dif < BAD_WIND_LIMIT || dif > (360 - BAD_WIND_LIMIT))
    }
}
