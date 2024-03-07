package com.kayak_backend.services.windFiltering

import com.kayak_backend.getConf
import com.kayak_backend.getWindService
import com.kayak_backend.models.Location
import com.kayak_backend.models.WindInfo
import com.kayak_backend.services.seaBearing.SeaBearingsGetter
import com.kayak_backend.services.wind.WindService
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

private const val BAD_WIND_LIMIT = 60

// TODO figure out what this limit should be and if it should just be a component
private const val BAD_WIND_MAGNITUDE_LIMIT = 2

class DangerousWindService(
    private val windService: WindService = getWindService(getConf("./config.yaml")),
) {
    fun findBadWinds(
        locations: List<Location>,
        checkpoints: List<Long>,
        startTime: LocalDateTime,
        coastBuffer: Double,
    ): List<Boolean> {
        val bearings = getDirectionToSea(locations, coastBuffer)
        return locations.zip(checkpoints.zip(bearings)).map {
                (loc, timeBearing) ->
            val wind = windService.getWind(loc, startTime.plusSeconds(timeBearing.first))
            dangerousArea(timeBearing.second, startTime.plusSeconds(timeBearing.first), wind)
        }
    }

    private fun getDirectionToSea(
        locations: List<Location>,
        coastBuffer: Double,
    ): List<Long> {
        // get the coastline, so can find the bearings of the relevant poitns,
        SeaBearingsGetter(coastlineService)
    }

    private fun dangerousArea(
        bearing: Long,
        dateTime: LocalDateTime,
        wind: WindInfo,
    ): Boolean {
        val magnitude = sqrt(wind.u * wind.u + wind.v * wind.v)
        val resultWind = Math.toDegrees(atan2(wind.u, wind.v))
        val dif = abs(resultWind - bearing)

        return magnitude >= BAD_WIND_MAGNITUDE_LIMIT && (dif < BAD_WIND_LIMIT || dif > (360 - BAD_WIND_LIMIT))
    }
}
