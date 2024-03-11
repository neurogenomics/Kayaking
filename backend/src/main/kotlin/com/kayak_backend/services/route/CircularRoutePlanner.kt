package com.kayak_backend.services.route

import com.kayak_backend.models.TideInfo
import com.kayak_backend.services.tides.TideService
import org.locationtech.jts.geom.Polygon
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class CircularRoutePlanner(
    baseRoutePolygon: Polygon,
    inStartPositions: List<NamedLocation>,
    private val tideService: TideService,
    maxStartDistance: Int = 1000,
) {
    private val sectionedRoute = SectionedRoute(baseRoutePolygon, inStartPositions, maxStartDistance)
    private val sectionsShuffled = sectionedRoute.sections.shuffled()

    private val resistances: ConcurrentHashMap<Pair<Leg, LocalDate>, ConcurrentHashMap<LocalTime, Double>> =
        ConcurrentHashMap()

    private fun Leg.resistance(tide: TideInfo): Double {
        val bearing = this.bearing()
        val xResistance = cos(Math.toRadians(bearing))
        val yResistance = sin(Math.toRadians(bearing))

        val resistance = -(tide.u * xResistance + tide.v * yResistance)
        return resistance
    }

    private fun getResistance(
        leg: Leg,
        time: LocalDateTime,
    ): Double? {
        val timeslice =
            resistances.getOrPut(Pair(leg, time.toLocalDate())) {
                ConcurrentHashMap(
                    tideService.getTideAllDay(leg.midpoint(), time.toLocalDate())
                        .mapValues { leg.resistance(it.value) },
                )
            }
        return timeslice[time.toLocalTime().truncatedTo(ChronoUnit.HOURS)]
    }

    private fun stepThrough(
        switchLeg: Leg,
        time: LocalDateTime,
        minDuration: Duration,
        legTimer: LegTimer,
    ): Pair<Leg, LocalDateTime>? {
        var currentStart = time
        var currentEnd = time

        val step = if ((getResistance(switchLeg, time) ?: return null) >= 0) 1 else -1
        val legs =
            sectionedRoute.stepFrom(switchLeg.end, step).takeWhile {
                if (Duration.between(currentStart, currentEnd) >= minDuration) return@takeWhile false
                val newStart = currentStart - Duration.ofSeconds(legTimer.getDuration(it, currentStart))
                val newEnd = currentEnd + Duration.ofSeconds(legTimer.getDuration(it.reverse(), currentEnd))
                val startResistance = getResistance(it, newStart) ?: return@takeWhile false
                if (startResistance > 0 && abs(startResistance) > 0.1) return@takeWhile false

                val endResistance = getResistance(it, newEnd) ?: return@takeWhile false
                if (endResistance < 0 && abs(endResistance) > 0.1) return@takeWhile false
                currentStart = newStart
                currentEnd = newEnd
                true
            }.toList()

        if (Duration.between(currentStart, currentEnd) < minDuration) return null
        return Pair(Leg.MultipleLegs(legs + legs.reversed().map(Leg::reverse)), currentStart)
    }

    private fun createRoute(
        date: LocalDate,
        switchLeg: Leg,
        minDuration: Duration,
        legTimer: LegTimer,
    ): List<Pair<Leg, LocalDateTime>> {
        val switchResistances =
            resistances.getOrPut(Pair(switchLeg, date)) {
                ConcurrentHashMap(
                    tideService.getTideAllDay(switchLeg.midpoint(), date).mapValues { switchLeg.resistance(it.value) },
                )
            }
        val switchpoints =
            switchResistances.mapValues {
                if (it.key.hour == (switchResistances.keys.min().hour)) {
                    false
                } else {
                    (
                        (switchResistances[it.key]!! >= 0) != (
                            switchResistances[
                                it.key.minusHours(
                                    1,
                                ),
                            ]!! >= 0
                        )
                    )
                }
            }.filterValues { it }

        return switchpoints.map {
            stepThrough(switchLeg, LocalDateTime.of(date, it.key), minDuration, legTimer)
        }.filterNotNull()
    }

    private fun routeGenerator(
        date: LocalDate,
        minDuration: Duration,
        legTimer: LegTimer,
    ): Sequence<Triple<Leg, LocalDateTime, String>> {
        return sectionsShuffled.asSequence().map { createRoute(date, it, minDuration, legTimer) }.flatMap { it }
            .map {
                val connected = connectToStart(sectionedRoute, it.first)
                Triple(connected.first, it.second, connected.second)
            }
    }

    fun generateRoutes(
        legTimer: LegTimer,
        date: LocalDate = LocalDate.now(),
        minTime: Duration = Duration.ofHours(4),
    ): Sequence<Route> {
        val generator = routeGenerator(date, minTime, legTimer)
        return generator.map { Route(it.third, it.first.length, it.first, it.second) }
    }
}
