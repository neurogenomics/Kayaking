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
    private val sections: List<Leg>

    // Maps the closest point on the base route to a start position(s)
    private val routeToStarts: Map<Location, MutableList<StartPos>>

    // Maps the closest point on the base route to a start position(s)
    private val startToRoute: Map<StartPos, Location>
    private val routeToNextSectionIndex: Map<Location, Int>
    private val routeToPrevSectionIndex: Map<Location, Int>

    init {
        // Construct startPositions and routeToStart
        val baseRoute = baseRoutePolygon.coordinates.map { Location(it.x, it.y) }

        val mutableStartToRoute = mutableMapOf<StartPos, Location>()
        val mutableRouteToStarts = mutableMapOf<Location, MutableList<StartPos>>()

        // Find valid startPositions along the route and connect them to the startPositions
        for (startPos in inStartPositions) {
            val closestPoint = baseRoute.minWith(compareBy { it distanceTo startPos.location })
            if (closestPoint distanceTo startPos.location < maxStartDistance) {
                mutableRouteToStarts.getOrPut(closestPoint) { mutableListOf() }.add(startPos)
                mutableStartToRoute[startPos] = closestPoint
            }
        }
        startToRoute = mutableStartToRoute
        routeToStarts = mutableRouteToStarts

        sections = splitRouteIntoSections(baseRoute, routeToStarts.keys)
        val mutableRouteToNextSectionIndex = mutableMapOf<Location, Int>()
        val mutableRouteToPrevSectionIndex = mutableMapOf<Location, Int>()
        sections.forEachIndexed { index, section ->
            mutableRouteToNextSectionIndex[section.start] = index
            mutableRouteToPrevSectionIndex[section.end] = (index + 1) % sections.size
        }
        routeToNextSectionIndex = mutableRouteToNextSectionIndex
        routeToPrevSectionIndex = mutableRouteToPrevSectionIndex
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
        sections.add(0, Leg.create(currentLegLocations))
        return sections
    }

    // Iterate along the sections starting at section currentIndex
    inner class SectionIterator(private var currentIndex: Int = 0, private var step: Int = 1) : Iterator<Leg> {
        override fun hasNext(): Boolean = true

        override fun next(): Leg {
            val leg = sections[currentIndex]
            currentIndex = (currentIndex + step) % sections.size
            return leg
        }
    }

    // Combines a section with all the sections from previous itterations
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
    private fun connectToStart(leg: Leg): Leg {
        // TODO allow route to connect to multiple start locations
        val start = routeToStarts[leg.start]!![0]
        val end = routeToStarts[leg.end]!![0]
        return Leg.MultipleLegs(
            listOf(
                Leg.SingleLeg(start.location, leg.start),
                leg,
                Leg.SingleLeg(leg.end, end.location),
            ),
        )
    }

    // Given the start locations, generate a sequence of routes that all abide by condition
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

    // Generates a sequence of routes starting from the filtered start positions and that all abide by condition
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
