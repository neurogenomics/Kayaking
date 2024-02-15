package com.kayak_backend.services.route

import com.kayak_backend.models.Location
import com.kayak_backend.services.coastline.IsleOfWightCoastline
import com.kayak_backend.services.slipways.BeachesService
import com.kayak_backend.services.slipways.SlipwayService
import org.locationtech.jts.geom.Polygon
import java.io.File

class StartPos(val location: Location, val name: String)

class RoutePlanner(
    private val baseRoutePolygon: Polygon,
    private val inStartPositions: List<StartPos>,
    private val maxStartDistance: Int = 1000,
) {
    // The base route split into sections by the possible start positions
    private var sections: List<Leg>

    // All the start positions after filtering out any too far from the route (further than maxStartDistance)
    // private val startPositions: List<StartPos> = mutableListOf()

    // Maps the closet point in the base route to a start position(s)
    private val routeToStarts = mutableMapOf<Location, MutableList<StartPos>>()
    private val startToRoute = mutableMapOf<StartPos, Location>()
    private val routeToNextSectionIndex = mutableMapOf<Location, Int>()
    private val routeToPrevSectionIndex = mutableMapOf<Location, Int>()

    // Maps the index of start positions to the index of sections

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
        // Create a sequence of iterators for each input sequence
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

    private fun routeGenerator(
        condition: (Leg) -> Boolean,
        routeLocations: List<Location>,
    ): Sequence<Leg> {
        val x = routeLocations.map { SectionCombiner(routeToNextSectionIndex[it]!!).asSequence().takeWhile(condition) }
        return alternate(x)
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

fun main() {
    val coast = IsleOfWightCoastline().getCoastline()
    val route = createBaseRoute(coast, 500.0)
    val slipways = SlipwayService().getAllSlipways()
    val beaches = BeachesService().getAllBeaches()
    val slipwayStarts = slipways.mapIndexed { index, location -> StartPos(location, "Slipway $index") }
    val beachStarts = beaches.map { beachInfo -> StartPos(beachInfo.avergeLocation, beachInfo.name ?: "Unnamed beach") }
    val startPositions = slipwayStarts.plus(beachStarts)
    val routePlanner = RoutePlanner(route, startPositions)
    val location = Location(50.575, -1.295)
    val routes =
        routePlanner.generateRoutes(
            { location.distance(it.location) < 5000 },
            { it.length < 1000 },
        ).take(5).toList()

    val outputFile = File("/home/jamie/thirdyear/tests/coast/sections.csv")
    outputFile.bufferedWriter().use { out ->
        out.write("id,latitude,longitude\n")
        routes.forEachIndexed { index, route ->
            route.locations.forEach { location2 ->
                out.write("$index,${location2.latitude},${location2.longitude}\n")
            }
        }
    }
}
