package com.kayak_backend.services.route

import com.kayak_backend.models.Location
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.roundToLong

class LegTimer(private val kayak: Kayak) {
    // TODO change duration cache to have 15 minute periods
    // TODO find way to remove old entries after a period
    private val durationCache = mutableMapOf<Leg, MutableMap<Long, Long>>()

    // TODO will probably need start and end points when backwards routes are working
    // map of coordinates to the single leg they are the end of
    private val endToLeg = mutableMapOf<Location, Leg>()

    // TODO should this have an error/ debug message if not?
    // ONLY Works on the assumption getDuration has already been called for all the single legs on this route
    fun getCheckpoints(
        route: Route,
        dateTime: LocalDateTime,
    ): List<Pair<Location, Long>> {
        var timer = 0L
        val time = dateTime.toEpochSecond(ZoneOffset.UTC)
        val checkpoints =
            route.locations.drop(1).map { loc ->
                val leg = durationCache.getOrDefault(endToLeg[loc], emptyMap())
                timer += leg.getOrDefault(time + timer, 0L)
                loc to timer
            }.toMutableList()
        checkpoints.add(0, route.locations[0] to 0L)

        return checkpoints
    }

    fun getDuration(
        leg: Leg,
        dateTime: LocalDateTime,
    ): Long {
        val epoch = dateTime.toEpochSecond(ZoneOffset.UTC)
        val times = durationCache.getOrPut(leg) { mutableMapOf() }
        return times.getOrPut(epoch) { calculateDuration(leg, dateTime) }
    }

    private fun calculateDuration(
        leg: Leg,
        dateTime: LocalDateTime,
    ): Long {
        return when (leg) {
            is Leg.SingleLeg -> {
                val midpoint =
                    Location(
                        (leg.start.latitude + leg.start.latitude) / 2,
                        (leg.start.longitude + leg.start.longitude) / 2,
                    )
                endToLeg[leg.end] = leg
                (leg.length / kayak.getSpeed(dateTime, midpoint, leg.bearing)).roundToLong()
            }

            is Leg.MultipleLegs -> {
                val (totalDuration, _) =
                    leg.legs.fold(0L to dateTime) { (total, currDateTime), subLeg ->
                        val duration = getDuration(subLeg, currDateTime)
                        val nextDateTime = currDateTime.plusSeconds(duration)
                        total + duration to nextDateTime
                    }
                totalDuration
            }
        }
    }
}
