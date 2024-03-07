package com.kayak_backend.services.dangerousWindWarning

import com.kayak_backend.getConf
import com.kayak_backend.getSeaBearingService
import com.kayak_backend.getWindService
import com.kayak_backend.models.Location
import com.kayak_backend.models.WindInfo
import com.kayak_backend.services.wind.WindService
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

private const val BAD_WIND_ANGLE_LIMIT = 60

// TODO figure out what this limit should be and if it should just be a component
private const val BAD_WIND_MAGNITUDE_LIMIT = 2

class DangerousWindService(
    private val windService: WindService = getWindService(getConf("./config.yaml")),
    private val seaBearings: Map<Location, Double> = getSeaBearingService().getSeaBearings(),
) {
    fun findBadWinds(
        locations: List<Location>,
        checkpoints: List<Long>,
        startTime: LocalDateTime,
    ): List<Boolean> {
        return locations.zip(checkpoints).map {
                (loc, checkpoint) ->
            dangerousArea(loc, windService.getWind(loc, startTime.plusSeconds(checkpoint)))
        }
    }

    // if location not on the basic route, we default to it not being dangerous (so starting points)
    // could include starting points with a second seaBearingService for the actual coastline
    private fun dangerousArea(
        loc: Location,
        wind: WindInfo,
    ): Boolean {
        val bearing = seaBearings[loc] ?: return false
        val magnitude = sqrt(wind.u * wind.u + wind.v * wind.v)
        val resultWind = Math.toDegrees(atan2(wind.u, wind.v))
        val dif = abs(resultWind - bearing)

        return magnitude >= BAD_WIND_MAGNITUDE_LIMIT &&
            (dif < BAD_WIND_ANGLE_LIMIT || dif > (360 - BAD_WIND_ANGLE_LIMIT))
    }
}
