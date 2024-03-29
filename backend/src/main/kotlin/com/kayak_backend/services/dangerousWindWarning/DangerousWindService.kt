package com.kayak_backend.services.dangerousWindWarning

import com.kayak_backend.getConf
import com.kayak_backend.getSeaBearingService
import com.kayak_backend.getWindService
import com.kayak_backend.models.Location
import com.kayak_backend.models.WindInfo
import com.kayak_backend.models.getWindBearing
import com.kayak_backend.models.getWindMagnitude
import com.kayak_backend.services.wind.WindService
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min

private const val BAD_WIND_ANGLE_LIMIT = 90

// TODO figure out what this limit should be
private const val BAD_WIND_MAGNITUDE_LIMIT = 2

class DangerousWindService(
    private val windService: WindService = getWindService(getConf("./config.yaml")),
    private val seaBearings: Map<Location, Double> = getSeaBearingService().getSeaBearings(),
) {
    // assumes locations and checkpoints are the same length
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

    // returns true if that location with that wind is dangerous
    // if location not on the basic route, we default to it not being dangerous (so starting points)
    // could include starting points with a second seaBearingService for the actual coastline
    private fun dangerousArea(
        loc: Location,
        wind: WindInfo,
    ): Boolean {
        val bearing = seaBearings[loc] ?: return false
        val difference = abs(bearing - getWindBearing(wind))
        val dif = min(difference, 360 - difference)
        val magnitude = abs(getWindMagnitude(wind) * cos(Math.toRadians(dif)))

        return magnitude >= BAD_WIND_MAGNITUDE_LIMIT &&
            (dif < BAD_WIND_ANGLE_LIMIT)
    }
}
