package com.kayak_backend.services.route

import com.kayak_backend.models.Location
import org.locationtech.jts.geom.Polygon
import java.time.LocalDateTime
import kotlin.random.Random

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

    fun <T> randomSequence(sequences: List<Sequence<T>>): Sequence<T> {
        val random = Random.Default
        val iterators = sequences.map { it.iterator() }.toMutableList()

        return generateSequence {
            iterators[random.nextInt(iterators.size)].next()
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
            }

        return alternate(forwardRoutes)
    }

    private fun getRouteGenerators(
        condition: (Leg) -> Boolean,
        routeLocations: List<Location>,
    ): List<Sequence<Pair<Leg, String>>> {
        val forwardRoutes =
            routeLocations.map { routeLocation ->
                sectionedRoute.stepFromAccumulating(routeLocation)
            }
        val backwardsRoutes =
            routeLocations.map { routeLocation ->
                sectionedRoute.stepFromAccumulating(routeLocation, -1)
            }
        return (forwardRoutes.plus(backwardsRoutes)).map { generator ->
            generator.filter {
                condition(it)
            }.map { connectToStart(sectionedRoute, it) }
        }
    }

    // Generates a sequence of routes starting from the filtered start positions and that all abide by condition
    fun generateRoutes(
        startPositionFilter: (NamedLocation) -> Boolean,
        condition: (Leg) -> Boolean,
        startTime: LocalDateTime,
    ): Sequence<Route> {
        val validStarts = sectionedRoute.getStarts(startPositionFilter)
        val generators = getRouteGenerators(condition, validStarts.values.toList())
        return randomSequence(generators).map { Route(it.second, it.first.length, it.first, startTime) }
    }
}
