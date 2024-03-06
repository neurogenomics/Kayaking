package com.kayak_backend.services.route

import com.kayak_backend.models.TideInfo
import com.kayak_backend.services.tides.TideService
import org.locationtech.jts.geom.Polygon
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.cos
import kotlin.math.sin

class CircularRoutePlanner(
    baseRoutePolygon: Polygon,
    inStartPositions: List<StartPos>,
    private val legTimer: LegTimer,
    private val tideService: TideService,
    maxStartDistance: Int = 1000,
) {
    private val sectionedRoute = SectionedRoute(baseRoutePolygon, inStartPositions, maxStartDistance)
    private val sectionsShuffled = sectionedRoute.sections.shuffled()

    private val resistances: MutableMap<Pair<Leg, LocalDate>, Map<LocalTime, Double>> = mutableMapOf()

    private fun Leg.resistance(tide: TideInfo): Double {
        val bearing = this.bearing
        val xResistance = cos(Math.toRadians(bearing))
        val yResistance = sin(Math.toRadians(bearing))

        val resistance = tide.u * xResistance + tide.v * yResistance
        return resistance
    }

    private fun getResistance(
        leg: Leg,
        time: LocalDateTime,
    ): Double? {
        val timeslice =
            resistances.getOrPut(Pair(leg, time.toLocalDate())) {
                tideService.getTideAllDay(leg.midpoint(), time.toLocalDate()).mapValues { leg.resistance(it.value) }
            }
        return timeslice[time.toLocalTime().truncatedTo(ChronoUnit.HOURS)]
    }

    private fun stepThrough(
        switchLeg: Leg,
        time: LocalDateTime,
        minDuration: Duration,
    ): Pair<Leg, LocalDateTime>? {
        var currentStart = time
        var currentEnd = time

        val step = if ((getResistance(switchLeg, time) ?: return null) >= 0) 1 else -1
        val legs =
            sectionedRoute.stepFrom(switchLeg.end, step).takeWhile {
                val newStart = currentStart - Duration.ofSeconds(legTimer.getDuration(it.reverse(), currentStart))
                val newEnd = currentEnd + Duration.ofSeconds(legTimer.getDuration(it, currentEnd))
                if ((getResistance(it, newStart) ?: return@takeWhile false) > 0) return@takeWhile false
                if ((getResistance(it, newEnd) ?: return@takeWhile false) < 0) return@takeWhile false
                currentStart = newStart
                currentEnd = newEnd
                Duration.between(currentStart, currentEnd) < minDuration
            }.toList()

        if (Duration.between(currentStart, currentEnd) < minDuration) return null
        return Pair(Leg.MultipleLegs(legs + legs.reversed().map(Leg::reverse)), currentStart)
    }

    private fun createRoute(
        date: LocalDate,
        switchLeg: Leg,
        minDuration: Duration,
    ): List<Pair<Leg, LocalDateTime>> {
        val switchResistances =
            resistances.getOrPut(Pair(switchLeg, date)) {
                tideService.getTideAllDay(switchLeg.midpoint(), date).mapValues { switchLeg.resistance(it.value) }
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
            stepThrough(switchLeg, LocalDateTime.of(date, it.key), minDuration)
        }.filterNotNull()
    }

    private fun routeGenerator(
        condition: (Leg) -> Boolean,
        date: LocalDate,
        minDuration: Duration,
    ): Sequence<Triple<Leg, LocalDateTime, String>> {
        return sectionsShuffled.asSequence().map { createRoute(date, it, minDuration) }.flatMap { it }
            .filter { condition(it.first) }.map {
                Triple(connectToStart(sectionedRoute, it.first).first, it.second, "")
            }
    }

    fun generateRoutes(
        condition: (Leg) -> Boolean,
        date: LocalDate,
        maxGenerated: Int = 10,
        minTime: Duration,
    ): Sequence<Route> {
        val generator = routeGenerator(condition, date, minTime)
        return generator.take(maxGenerated).map { Route(it.third, it.first.length, it.first, it.second) }
            .sortedByDescending { it.length }
    }
}
