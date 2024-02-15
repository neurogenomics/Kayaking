package com.kayak_backend.services.route

import com.kayak_backend.models.Location
import org.locationtech.jts.geom.Polygon

class StartPos(val location: Location, val name: String)

class RoutePlanner(
    private val baseRoutePolygon: Polygon,
    private val inStartPositions: List<StartPos>,
    private val maxStartDistance: Int = 1000,
) {
    // The base route split into sections by the possible start positions
    private var sections: List<Leg>

    // Maps the closest point on the base route to a start position(s)
    private val routeToStarts = mutableMapOf<Location, MutableList<StartPos>>()

    // Maps the closest point on the base route to a start position(s)
    private val startToRoute = mutableMapOf<StartPos, Location>()
    private val routeToNextSectionIndex = mutableMapOf<Location, Int>()
    private val routeToPrevSectionIndex = mutableMapOf<Location, Int>()

    init {
        // Construct startPositions and routeToStart
        val baseRoute = baseRoutePolygon.coordinates.map { Location(it.x, it.y) }

        // Find valid startPositions along the route and connect them to the startPositions
        inStartPositions.forEach { startPos ->
            val closestPoint = baseRoute.minWith(compareBy { it.distance(startPos.location) })
            if (closestPoint.distance(startPos.location) < maxStartDistance) {
                routeToStarts.getOrPut(closestPoint) { mutableListOf() }.add(startPos)
                startToRoute[startPos] = closestPoint
            }
        }

        sections = splitRouteIntoSections(baseRoute, routeToStarts.keys)
        sections.forEachIndexed { index, section ->
            routeToNextSectionIndex[section.start] = index
            routeToPrevSectionIndex[section.end] = (index + 1) % sections.size
        }
    }

    private fun splitRouteIntoSections(
        route: List<Location>,
        startPosOnRoute: Set<Location>,
    ): List<Leg> {
        val sections = mutableListOf<Leg>()
        var currentLegLocations = mutableListOf<Location>()
        route.forEach { location ->
            currentLegLocations.add(location)
            if (startPosOnRoute.contains(location)) {
                sections.add(Leg.create(currentLegLocations))
                currentLegLocations = mutableListOf(location)
            }
        }
        // Connect first and last section
        if (sections.isNotEmpty()) {
            currentLegLocations.addAll(sections.removeFirst().locations)
        }
        sections.addFirst(Leg.create(currentLegLocations))
        return sections
    }

    inner class SectionIterator(private var currentIndex: Int = 0, private var step: Int = 1) : Iterator<Leg> {
        override fun hasNext(): Boolean = true

        override fun next(): Leg {
            val leg = sections[currentIndex]
            currentIndex = (currentIndex + step) % sections.size
            return leg
        }
    }

    inner class SectionCombiner(private var currentIndex: Int = 0, private var step: Int = 1) : Iterator<Leg> {
        private val sectionIterator = SectionIterator(currentIndex, step)
        private var current: Leg? = null

        override fun hasNext(): Boolean = true

        override fun next(): Leg {
            var temp = current
            val newLeg = sectionIterator.next()
            if (temp == null) {
                current = newLeg
                return newLeg
            }
            temp = Leg.MultipleLegs(listOf(temp, newLeg))
            current = temp
            return temp
        }
    }

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

    private fun connectToStart(leg: Leg): Leg {
        // TODO allow route to connect to multiple start locations
        val start = routeToStarts[leg.start]!![0]
        val end = routeToStarts[leg.end]!![0]
        return Leg.MultipleLegs(listOf(Leg.SingleLeg(start.location, leg.start), leg, Leg.SingleLeg(leg.end, end.location)))
    }

    private fun routeGenerator(
        condition: (Leg) -> Boolean,
        routeLocations: List<Location>,
    ): Sequence<Leg> {
        val forwardRoutes =
            routeLocations.map { routeLocation ->
                SectionCombiner(
                    routeToNextSectionIndex[routeLocation]!!,
                ).asSequence().map { connectToStart(it) }.takeWhile(condition)
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

    fun generateRoutes(
        startPositionFilter: (StartPos) -> Boolean,
        condition: (Leg) -> Boolean,
        maxGenerated: Int = 300,
    ): Sequence<Route> {
        val validStarts = startToRoute.filter { startPositionFilter(it.key) }
        val generator = routeGenerator(condition, validStarts.values.toList())
        return generator.take(maxGenerated).map { Route(it.length, it.locations) }.sortedByDescending { it.length }
    }
}
