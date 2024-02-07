package com.kayak_backend.services.route

import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Polygon
import org.locationtech.jts.simplify.TopologyPreservingSimplifier

class Route {
    companion object {
        fun create(
            polygon: Polygon,
            buffer: Double,
            smoothingTolerance: Double = 0.00005,
        ): Polygon {
            val simplifiedGeometry = TopologyPreservingSimplifier.simplify(polygon, smoothingTolerance)
            // TODO don't approximate meters in degrees as this varies with longitude
            // instead will need to project all projecting all the coordinates so that meters are unit
            val approxBufferInDeg = buffer * (111.0 / 1000.0)
            val bufferedGeometry = simplifiedGeometry.buffer(approxBufferInDeg)
            val longestGeometry = getLongestGeometry(bufferedGeometry.boundary)
            val geometryFactory = GeometryFactory()
            return geometryFactory.createPolygon(longestGeometry.coordinates)
        }

        private fun getLongestGeometry(geometry: Geometry): Geometry {
            var longestPart = geometry.getGeometryN(0)
            var maxLength = longestPart.numPoints
            for (i in 1 until geometry.numGeometries) {
                val part = geometry.getGeometryN(i)
                if (geometry.numPoints > maxLength) {
                    maxLength = part.numPoints
                    longestPart = part
                }
            }
            return longestPart
        }
    }
}
