package com.kayak_backend.services.route

import com.kayak_backend.models.Location
import kotlinx.serialization.Serializable
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Polygon
import org.locationtech.jts.simplify.TopologyPreservingSimplifier

@Serializable
open class Route(
    val name: String,
    val length: Double,
    val locations: Leg,
)

@Serializable
data class TimedRoute(
    val name: String,
    val length: Double,
    val locations: Leg,
    val checkpoints: List<Long>,
)

class BaseRoute {
    fun createBaseRoute(
        polygon: Polygon,
        buffer: Double,
        smoothingTolerance: Double = 0.00005,
    ): Polygon {
        val simplifiedGeometry = TopologyPreservingSimplifier.simplify(polygon, smoothingTolerance)
        // TODO don't approximate meters in degrees as this varies with longitude
        // instead will need to project all projecting all the coordinates so that meters are unit
        val approxBufferInDeg = buffer / (111.0 * 1000.0)
        val bufferedGeometry = simplifiedGeometry.buffer(approxBufferInDeg)
        val longestGeometry = getLongestGeometry(bufferedGeometry.boundary)
        val geometryFactory = GeometryFactory()
        return geometryFactory.createPolygon(longestGeometry.coordinates)
    }

    private fun getLongestGeometry(geometry: Geometry): Geometry {
        if (geometry.isEmpty) return geometry
        var longestPart = geometry.getGeometryN(0)
        var maxLength = longestPart.length
        for (i in 1 until geometry.numGeometries) {
            val part = geometry.getGeometryN(i)
            if (part.length > maxLength) {
                maxLength = part.length
                longestPart = part
            }
        }
        return longestPart
    }
}

class SectionedRoute(
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

    fun getStartPos(location: Location): StartPos {
        return routeToStarts[location]!![0]
    }

    fun stepFrom(
        location: Location,
        step: Int = 1,
    ): Sequence<Leg> {
        return generateSequence(seed = routeToNextSectionIndex[location]!!) { (it + step) % sections.size }.map { sections[it] }
    }

    fun stepFromAccumulating(
        location: Location,
        step: Int = 1,
    ): Sequence<Leg> {
        return stepFrom(location, step).scan<Leg, Leg?>(null) { acc, value ->
            if (acc == null) {
                value
            } else {
                Leg.MultipleLegs(
                    listOf(acc, value),
                )
            }
        }.filterNotNull()
    }

    fun getStarts(filter: (StartPos) -> Boolean = { true }): Map<StartPos, Location> {
        return startToRoute.filter { filter(it.key) }
    }
}
