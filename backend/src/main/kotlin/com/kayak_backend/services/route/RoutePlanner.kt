package com.kayak_backend.services.route

import com.kayak_backend.models.Location
import org.locationtech.jts.geom.Polygon
import java.time.LocalDateTime

class RoutePlanner(
    private val baseRoutePolygon: Polygon,
    inStartPositions: List<NamedLocation>,
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

    // Given the start locations, generate a sequence of routes that all abide by condition
    private fun routeGenerator(
        condition: (Leg) -> Boolean,
        routeLocations: List<Location>,
    ): Sequence<Pair<Leg, String>> {
        val forwardRoutes =
            routeLocations.map { routeLocation ->
                sectionedRoute.stepFromAccumulating(routeLocation).map { connectToStart(sectionedRoute, it) }
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
        startPositionFilter: (NamedLocation) -> Boolean,
        condition: (Leg) -> Boolean,
        startTime: LocalDateTime,
        maxGenerated: Int = 300,
    ): Sequence<Route> {
        val validStarts = sectionedRoute.getStarts(startPositionFilter)
        val generator = routeGenerator(condition, validStarts.values.toList())
        return generator.take(maxGenerated).map { Route(it.second, it.first.length, it.first, startTime) }
            .sortedByDescending { it.length }
    }
}
