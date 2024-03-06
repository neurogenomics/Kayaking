package com.kayak_backend.services.windFiltering

import com.kayak_backend.getConf
import com.kayak_backend.getWindService
import com.kayak_backend.models.Location
import com.kayak_backend.models.WindInfo
import com.kayak_backend.services.route.Route
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
    fun classifyRoute(route: Route): Boolean {
        val seaBearings = seaBearingService.getSeaBearings()
        val locs = route.locations.locations
        return locs.any { classifyArea(it, seaBearings) }
    }

    // returns map of locations and bool for if the wind is out to sea for entire coastline
    fun classifyAreas(): Map<Location, Boolean> {
        val seaBearings = seaBearingService.getSeaBearings()
        return seaBearings.mapValues {
            val wind = windService.getWind(it.key, LocalDateTime.now())
            badArea(it.value, wind)
        }
    }

    private fun classifyArea(
        location: Location,
        seaBearings: Map<Location, Double>,
    ): Boolean {
        val wind = windService.getWind(location, LocalDateTime.now())
        return badArea(getClosestBearing(location, seaBearings), wind)
    }

    // returns true if wind direction is out to sea and strong
    private fun badArea(
        bearing: Double,
        wind: WindInfo,
    ): Boolean {
        val magnitude = sqrt(wind.u * wind.u + wind.v * wind.v)
        val resultWind = Math.toDegrees(atan2(wind.u, wind.v))
        val dif = abs(resultWind - bearing)

        return magnitude >= BAD_WIND_MAGNITUDE_LIMIT && (dif < BAD_WIND_LIMIT || dif > (360 - BAD_WIND_LIMIT))
    }

    private fun getClosestBearing(
        location: Location,
        seaBearings: Map<Location, Double>,
    ): Double {
        return seaBearings.getOrElse(location) {
            val closestPoint = seaBearings.keys.minByOrNull { it.distanceTo(location) }
            seaBearings.getValue(closestPoint!!)
        }
    }
}
