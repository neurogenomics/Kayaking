package com.kayak_backend.services.route

import com.kayak_backend.models.Location
import org.locationtech.jts.geom.Polygon

class StartPos(val location: Location, val name: String)

class RoutePlanner(
    private val baseRoutePolygon: Polygon,
    private val inStartPositions: List<StartPos>,
) {
    // The base route slip into sections by the possible start positions
    private val sections = mutableListOf<Leg>()

    // Maps the closet point in the base route to a start position
    private val routeToStart = mutableMapOf<Location, StartPos>()
    private val startIndexToSectionIndex = mutableMapOf<Int, Int>()
    private val startPositions: List<StartPos> = mutableListOf()

    init {
        // Construct routeToStart
        val baseRoute = polygonToCoords(baseRoutePolygon)
        for (startPos in inStartPositions) {
            val closestPoint = closestLocation(startPos.location, baseRoute)
            if (closestPoint.distance(startPos.location) < 1000) {
                if (!routeToStart.contains(closestPoint)) {
                    routeToStart[closestPoint] = startPos
                    startPositions.addFirst(startPos)
                }
            }
        }
        // Construct sections
        var firstSection: List<Location>? = null
        var currentLegLocations = mutableListOf<Location>()
        var startIndex = 0
        for (location in baseRoute) {
            currentLegLocations.add(location)
            if (routeToStart.contains(location)) {

                if (firstSection == null) {
                    startIndex = startPositions.indexOf(routeToStart[location])
                    firstSection = currentLegLocations
                } else {
                    startIndexToSectionIndex[startPositions.indexOf(routeToStart[location])] = sections.size
                    sections.add(Leg.create(currentLegLocations))
                }
                currentLegLocations = mutableListOf(location)
            }
        }
        if (firstSection != null) {
            currentLegLocations.addAll(firstSection)
        }
        startIndexToSectionIndex[startIndex] = sections.size
        sections.addFirst(Leg.create(currentLegLocations))
        assert(sections.size == startPositions.size)
    }

    private fun polygonToCoords(polygon: Polygon): List<Location> {
        val locations = mutableListOf<Location>()
        for (coordinate in polygon.coordinates) {
            locations.add(Location(coordinate.x, coordinate.y))
        }
        return locations
    }

    private fun closestLocation(
        start: Location,
        locations: List<Location>,
    ): Location {
        var closest = locations.first()
        var shortestDistance = start.distance(closest)

        for (location in locations) {
            val distance = start.distance(location)
            if (distance < shortestDistance) {
                shortestDistance = distance
                closest = location
            }
        }
        return closest
    }

    inner class SectionIterator(private var currentIndex: Int = 0) : Iterator<Leg> {
        override fun hasNext(): Boolean = true

        override fun next(): Leg {
            val leg = sections[currentIndex]
            currentIndex = (currentIndex + 1) % sections.size
            return leg
        }
    }

    inner class OneRouteGenerator(private val condition: (Leg) -> Boolean, private var startIndex: Int = 0) :
        Iterator<Leg> {
        private val sectionIterator = SectionIterator(startIndex)
        private var current: Leg? = null

        override fun hasNext(): Boolean {
            val temp = current
            return temp == null || condition(temp)
        }

        override fun next(): Leg {
            var temp = current
            val newLeg = sectionIterator.next()
            if (temp == null) {
                temp = connectToStart(newLeg)
                current = temp
                return connectToEnd(temp)
            }
            temp = Leg.MultipleLegs(listOf(temp, newLeg))
            current = temp
            return connectToEnd(temp)
        }
    }

    private fun connectToStart(leg: Leg): Leg {
        return Leg.MultipleLegs(listOf(Leg.SingleLeg(leg.start, routeToStart[leg.start]!!.location), leg))
    }

    private fun connectToEnd(leg: Leg): Leg {
        return Leg.MultipleLegs(listOf(leg, Leg.SingleLeg(leg.end, routeToStart[leg.end]!!.location)))
    }

    private fun routeGenerator(
        condition: (Leg) -> Boolean,
        startIndices: List<Int>,
    ): Iterator<Leg> {
        val x = startIndices.map { index -> OneRouteGenerator(condition, index) }
        return AlternatingGenerator(x)
    }

    private fun fullLegToRoute(leg: Leg): Route {
        return Route(leg.length, leg.locations)
    }

    fun generateRoutes(
        startPositionFilter: (StartPos) -> Boolean,
        condition: (Leg) -> Boolean,
        maxGenerated: Int = 300,
    ): Sequence<Route> {
        val startIndices = startPositions.indices.filter { startPositionFilter(startPositions[it]) }
        val sectionIndices = startIndices.map { startIndexToSectionIndex[it]!! }
        return routeGenerator(condition, sectionIndices).asSequence().take(maxGenerated).filter(condition).map { fullLegToRoute(it) }
    }
}
