package com.kayak_backend.services.route

import com.kayak_backend.models.Location
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.roundToLong

class LegTimer(private val kayak: Kayak) {
    private val durationCache = mutableMapOf<Leg, MutableMap<Long, Long>>()

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
