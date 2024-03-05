package com.kayak_backend.services.route

import com.kayak_backend.models.Location
import org.locationtech.jts.geom.Polygon

class StartPos(val location: Location, val name: String)

class RoutePlanner(
    baseRoutePolygon: Polygon,
    inStartPositions: List<StartPos>,
    maxStartDistance: Int = 1000,
) {
    // The base route split into sections by the possible start positions
    private val sectionedRoute = SectionedRoute(baseRoutePolygon, inStartPositions, maxStartDistance)

    // Given a list of sequences, create a sequence which alternates between them
    private fun <T> alternate(sequences: List<Sequence<T>>): Sequence<T> {
        val iterators = sequences.map { it.iterator() }.toMutableList()

        return sequence {
            while (iterators.isNotEmpty()) {
                val it = iterators.iterator()
                while (it.hasNext()) {
                    val iterator = it.next()
                    if (iterator.hasNext()) {
                        yield(iterator.next())
                    } else {
                        it.remove()
                    }
                }
            }
        }
    }

    // Given a leg, create a longer leg that connects to the start and end slipways
    private fun connectToStart(leg: Leg): Pair<Leg, String> {
        // TODO allow route to connect to multiple start locations
        val start = sectionedRoute.getStartPos(leg.start)
        val end = sectionedRoute.getStartPos(leg.end)
        val combinedLeg =
            Leg.MultipleLegs(
                listOf(
                    Leg.SingleLeg(start.location, leg.start),
                    leg,
                    Leg.SingleLeg(leg.end, end.location),
                ),
            )
        val name = "${start.name} to ${end.name}"
        return Pair(combinedLeg, name)
    }

    // Given the start locations, generate a sequence of routes that all abide by condition
    private fun routeGenerator(
        condition: (Leg) -> Boolean,
        routeLocations: List<Location>,
    ): Sequence<Pair<Leg, String>> {
        val forwardRoutes =
            routeLocations.map { routeLocation ->
                sectionedRoute.stepFromAccumulating(routeLocation).map { connectToStart(it) }
                    .takeWhile { condition(it.first) }
            }

        // TODO combines these
        //        val backwardsRoutes =
        //            routeLocations.map { routeLocation ->
        //                SectionCombiner(
        //                    routeToNextSectionIndex[routeLocation]!!, -1
        //                ).asSequence().map { connectToStart(it) }.takeWhile(condition)
        //            }

        return alternate(forwardRoutes)
    }

    // Generates a sequence of routes starting from the filtered start positions and that all abide by condition
    fun generateRoutes(
        startPositionFilter: (StartPos) -> Boolean,
        condition: (Leg) -> Boolean,
        maxGenerated: Int = 300,
    ): Sequence<Route> {
        val validStarts = sectionedRoute.getStarts(startPositionFilter)
        val generator = routeGenerator(condition, validStarts.values.toList())
        return generator.take(maxGenerated).map { Route(it.second, it.first.length, it.first) }
            .sortedByDescending { it.length }
    }
}
