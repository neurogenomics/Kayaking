package com.kayak_backend.services.route

import com.kayak_backend.models.Location
import com.kayak_backend.services.route.kayak.Kayak
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.roundToLong

class LegTimers(val slowLegTimer: LegTimer, val normalLegTimer: LegTimer, val fastLegTimer: LegTimer)

class LegTimer(private val kayak: Kayak) {
    // TODO change duration cache to have 15 minute periods
    // TODO find way to remove old entries after a period
    private val durationCache = mutableMapOf<Leg, MutableMap<Long, Long>>()

    fun getCheckpoints(
        route: Route,
        dateTime: LocalDateTime,
    ): List<Long> {
        return accumulateCheckpoints(mutableListOf(0), route.locations, dateTime, 0).second
    }

    private fun accumulateCheckpoints(
        checkpoints: MutableList<Long>,
        leg: Leg,
        time: LocalDateTime,
        accumulator: Long,
    ): Pair<Long, List<Long>> {
        return when (leg) {
            is Leg.SingleLeg -> {
                val duration = getDuration(leg, time.plusSeconds(accumulator))
                checkpoints.add(accumulator + duration) // matches index of leg end location in coordinate list
                Pair(accumulator + duration, checkpoints)
            }

            is Leg.MultipleLegs -> {
                var acc = accumulator
                for (subLeg in leg.legs) {
                    val result = accumulateCheckpoints(mutableListOf(), subLeg, time, acc)
                    acc = result.first
                    checkpoints.addAll(result.second)
                }
                Pair(acc, checkpoints)
            }
        }
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
                        (leg.start.latitude + leg.end.latitude) / 2,
                        (leg.start.longitude + leg.end.longitude) / 2,
                    )

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
