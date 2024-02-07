package com.kayak_backend.services.route

import com.kayak_backend.models.Location
import com.kayak_backend.services.coastline.IsleOfWightCoastline
import com.kayak_backend.services.slipways.SlipwayService
import org.locationtech.jts.geom.Polygon
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter

class Arc(val start: Location, val end: Location, var nextArc: Arc?, var prevArc: Arc?) {
    val length: Double by lazy { start.distance(end) }
}

class Section(val arcs: List<Arc>, var nextSec: Section?, var prevSec: Section?) {
    val length: Double by lazy { arcs.sumOf { it.length } }
    val locations: List<Location> by lazy { arcs.map { it.start } + arcs.last().end }
}

class RoutePlanner(val baseRoutePolygon: Polygon, val startPositions: List<Location>) {
    private val sections = mutableListOf<Section>()
    private val routeToStarts = mutableMapOf<Location, Location>()

    init {
        val baseRoute = polygonToCoords(baseRoutePolygon)

        val arcs = createArcs(baseRoute)

        outputArcs(arcs)

        for (startPos in startPositions) {
            val closestPoint = closestLocation(startPos, baseRoute)
            // To exclude start locations too far from route
            if (closestPoint.distance(startPos) < 1) {
                routeToStarts[closestPoint] = startPos
            }
        }

        val outputs = mutableListOf<Location>()
        outputs.addAll(routeToStarts.keys)
        outputs.addAll(routeToStarts.values)
        output(outputs)

        var maybeStartArc: Arc? = null
        for (arc in arcs) {
            if (arc.start in routeToStarts.keys) {
                maybeStartArc = arc
                break
            }
        }
        val startArc: Arc = maybeStartArc!!

        var currArc = startArc
        var currSection = mutableListOf(startArc)

        while (currArc.end != startArc.start) {
            if (currArc.end in routeToStarts.keys) {
                sections.add(Section(currSection, null, null))
                currSection = mutableListOf(currArc)
            }
            currArc = currArc.nextArc!!
            currSection.add(currArc)
        }
        currSection.add(currArc)
        sections.add(Section(currSection, null, null))

        for (i in 0 until sections.size - 1) {
            sections[i].nextSec = sections[i + 1]
            sections[i + 1].prevSec = sections[i]
        }
        // Connect last section to the first one
        sections.last().nextSec = sections.first()
        sections.first().prevSec = sections.last()
        outputSections(sections)
    }

    private fun connectRouteToStart(section: Section): List<Location> {
        val startPos = routeToStarts[section.arcs.first().start]
        val endPos = routeToStarts[section.arcs.last().end]
        val locations = mutableListOf<Location>()

        if (startPos != null) {
            locations.add(startPos)
        }
        locations.addAll(section.locations)
        if (endPos != null) {
            locations.add(endPos)
        }
        // locations.add(endPos)
        return locations
    }

    fun getRoutes(): List<List<Location>> {
        val maxLength = 60
        var num = 0

        val routes = mutableListOf<List<Section>>()

        for (startSection in sections) {
            var curr = startSection
            var length = curr.length
            val currRoute = mutableListOf<Section>()
            while (length < maxLength) {
                currRoute.add(curr)
                routes.add(currRoute.toList())
                curr = curr.nextSec!!
                length += curr.length
                num += 1
            }
        }
        return routes.map { sections -> connectRouteToStart(Section(sections.flatMap { section -> section.arcs }, null, null)) }
    }

    private fun output(locations: List<Location>) {
        try {
            PrintWriter(FileWriter(File("/home/jamie/thirdyear/tests/coast/out.csv"))).use { writer ->
                writer.println("latitude,longitude")
                for (location in locations) {
                    writer.println("${location.latitude},${location.longitude}")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun outputArcs(sections: List<Arc>) {
        try {
            PrintWriter(FileWriter(File("/home/jamie/thirdyear/tests/coast/arcs.csv"))).use { writer ->
                writer.println("latitude,longitude,line_id")
                for ((i, section) in sections.withIndex()) {
                    writer.println("${section.start.latitude},${section.start.longitude},$i")
                    writer.println("${section.end.latitude},${section.end.longitude},$i")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun createArcs(locations: List<Location>): List<Arc> {
        val arcs = mutableListOf<Arc>()

        for (i in locations.indices) {
            val startLocation = locations[i]
            val endLocation = locations[(i + 1) % locations.size]

            val arc = Arc(startLocation, endLocation, null, null)
            arcs.add(arc)
        }

        // Connect sections
        for (i in 0 until arcs.size - 1) {
            arcs[i].nextArc = arcs[i + 1]
            arcs[i + 1].prevArc = arcs[i]
        }
        // Connect last section to the first one
        arcs.last().nextArc = arcs.first()
        arcs.first().prevArc = arcs.last()

        return arcs
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
}

private fun outputSections(sections: List<Section>) {
    try {
        PrintWriter(FileWriter(File("/home/jamie/thirdyear/tests/coast/sections.csv"))).use { writer ->
            writer.println("latitude,longitude,line_id,length")
            for ((i, section) in sections.withIndex()) {
                for (location in section.locations) {
                    writer.println("${location.latitude},${location.longitude},$i,${section.length}")
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun main() {
    val coast = IsleOfWightCoastline().getCoastline()
    val route = Route.create(coast, 0.05)

    val slipways = SlipwayService().getAllSlipways()

    val startPos = slipways // mutableListOf(Location(50.668004, -1.494413), Location(50.631615, -1.400700), Location(50.662056, -1.569421))

    val planner = RoutePlanner(route, startPos)
    val routes = planner.getRoutes()

    try {
        PrintWriter(FileWriter(File("/home/jamie/thirdyear/tests/coast/sections.csv"))).use { writer ->
            writer.println("latitude,longitude,line_id")
            var id = 0
            for (r in routes) {
                for (location in r) {
                    writer.println("${location.latitude},${location.longitude},$id")
                }
                id += 1
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
