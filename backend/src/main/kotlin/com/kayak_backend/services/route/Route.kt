package com.kayak_backend.services.route

import com.kayak_backend.getConf
import com.kayak_backend.getWindService
import com.kayak_backend.services.wind.WindService
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

    fun getDifficulty(
        route: Route,
        windService: WindService = getWindService(getConf("./config.yaml")),
    ) {
    }
}
