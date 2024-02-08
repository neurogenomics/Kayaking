package com.kayak_backend.services.route

import com.kayak_backend.models.Location
import com.kayak_backend.services.coastline.IsleOfWightCoastline
import com.kayak_backend.services.slipways.SlipwayService
import org.locationtech.jts.geom.Polygon
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.time.LocalDateTime

class RoutePlanner(
    private val baseRoutePolygon: Polygon,
    private val startPositions: List<Location>,
) {
    private val sections = mutableListOf<Leg>()
    private val routeToStarts = mutableMapOf<Location, Location>()

    init {
        val baseRoute = polygonToCoords(baseRoutePolygon)
        for (startPos in startPositions) {
            val closestPoint = closestLocation(startPos, baseRoute)
            val x = closestPoint.distance(startPos)
            if (closestPoint.distance(startPos) < 1000) {
                routeToStarts[closestPoint] = startPos
            }
        }
        var firstSection: List<Location>? = null
        var currentLegLocations = mutableListOf<Location>()
        for (location in baseRoute) {
            currentLegLocations.add(location)
            if (routeToStarts.contains(location)) {
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
        return Leg.MultipleLegs(listOf(Leg.SingleLeg(leg.start, routeToStarts[leg.start]!!), leg))
    }

    private fun connectToEnd(leg: Leg): Leg {
        return Leg.MultipleLegs(listOf(leg, Leg.SingleLeg(leg.end, routeToStarts[leg.end]!!)))
    }

    private fun routeGenerator(condition: (Leg) -> Boolean): Iterator<Leg> {
        val x = sections.indices.map { index -> OneRouteGenerator(condition, index) }
        return AlternatingGenerator(x)
    }

    fun generateRoutes(condition: (Leg) -> Boolean): List<Leg> {
        return routeGenerator(condition).asSequence().filter(condition).take(40).toList()
    }
}

private fun outputLegs(
    legs: List<Leg>,
    timer: LegTimer,
    dateTime: LocalDateTime,
) {
    try {
        PrintWriter(FileWriter(File("/home/jamie/thirdyear/tests/coast/sections.csv"))).use { writer ->
            writer.println("latitude,longitude,line_id,length")
            for ((i, section) in legs.withIndex()) {
                var curr = dateTime
                var duration = 0L
                duration = timer.getDuration(section, curr)
                for (location in section.locations) {
                    writer.println("${location.latitude},${location.longitude},$i,$duration")
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun main() {
    val coast = IsleOfWightCoastline().getCoastline()
    val route = createBaseRoute(coast, 500.0)

    val slipways = SlipwayService().getAllSlipways()

    val startPos = slipways // mutableListOf(Location(50.668004, -1.494413), Location(50.631615, -1.400700), Location(50.662056, -1.569421))

    val planner = RoutePlanner(route, startPos)
    val timer = LegTimer(BasicKayak())
    val now = LocalDateTime.now()
    val routes =
        planner.generateRoutes {
            timer.getDuration(it, now) < 45 * 60
        }

    outputLegs(routes, timer, now)
}
