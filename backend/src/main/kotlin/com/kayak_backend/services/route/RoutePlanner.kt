package com.kayak_backend.services.route

import com.kayak_backend.models.Location
import org.locationtech.jts.geom.Polygon

class StartPos(val location: Location, val name: String)

class RoutePlanner(
    private val baseRoutePolygon: Polygon,
    private val startPositions: List<StartPos>,
) {
    // The base route slip into sections by the possible start positions
    private val sections = mutableListOf<Leg>()

    // Maps the closet point in the base route to a start position
    private val routeToStart = mutableMapOf<Location, StartPos>()

    init {
        // Construct routeToStart
        val baseRoute = polygonToCoords(baseRoutePolygon)
        for (startPos in startPositions) {
            val closestPoint = closestLocation(startPos.location, baseRoute)
            if (closestPoint.distance(startPos.location) < 1000) {
                routeToStart[closestPoint] = startPos
            }
        }
        // Construct sections
        var firstSection: List<Location>? = null
        var currentLegLocations = mutableListOf<Location>()
        for (location in baseRoute) {
            currentLegLocations.add(location)
            if (routeToStart.contains(location)) {
                if (firstSection == null) {
                    firstSection = currentLegLocations
                } else {
                    sections.add(Leg.create(currentLegLocations))
                }
                currentLegLocations = mutableListOf(location)
            }
        }
        if (firstSection != null) {
            currentLegLocations.addAll(firstSection)
        }
        sections.add(Leg.create(currentLegLocations))
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
        startIndices: IntRange,
    ): Iterator<Leg> {
        val x = startIndices.map { index -> OneRouteGenerator(condition, index) }
        return AlternatingGenerator(x)
    }

    private fun fullLegToRoute(leg: Leg): Route {
        return Route(leg.length, leg.locations)
    }

    private fun filterStartPositionIndices(
        location: Location,
        startLocationRadius: Double,
    ): IntRange {
        return startPositions.filter { location.distance(it.location) < startLocationRadius }.indices
    }

    fun generateRoutes(
        startLocation: Location,
        startLocationRadius: Double = 5000.0,
        condition: (Leg) -> Boolean,
        maxGenerated: Int = 300,
    ): Sequence<Route> {
        val startIndices = filterStartPositionIndices(startLocation, startLocationRadius)
        return routeGenerator(condition, startIndices).asSequence().take(maxGenerated).filter(condition).map { fullLegToRoute(it) }
    }
}
