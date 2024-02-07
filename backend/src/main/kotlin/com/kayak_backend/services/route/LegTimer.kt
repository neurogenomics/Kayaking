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
        val times = durationCache.getOrPut(leg) { mutableMapOf(epoch to calculateDuration(leg, dateTime)) }
        val x = times.getOrPut(epoch) { calculateDuration(leg, dateTime) }
        return x
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
                val x = (leg.length / kayak.getSpeed(dateTime, midpoint, leg.bearing)).roundToLong()
                x
            }
            is Leg.MultipleLegs -> {
                var totalDuration = 0L
                var currDateTime = dateTime
                for (subLeg in leg.legs) {
                    val duration = getDuration(subLeg, currDateTime)
                    currDateTime = currDateTime.plusSeconds(duration)
                    totalDuration += duration
                }
                totalDuration
            }
        }
    }
}
