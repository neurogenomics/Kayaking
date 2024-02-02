package com.kayak_backend.services.route

import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Polygon
import org.locationtech.jts.simplify.TopologyPreservingSimplifier


class Route {
    companion object {
        fun create(polygon: Polygon, buffer : Double, smoothingTolerance: Double = 0.00005): Geometry {
            val simplifiedGeometry = TopologyPreservingSimplifier.simplify(polygon, smoothingTolerance) as Polygon
            return simplifiedGeometry.buffer(buffer / 1000);
        }
        fun extractLargestPart(multiPolygon: Geometry): Geometry {

            var maxPoints = -1
            var largestPart: Geometry? = null

            for (i in 0 until multiPolygon.numGeometries) {
                val part = multiPolygon.getGeometryN(i)
                val numPoints = part.numPoints

                if (numPoints > maxPoints) {
                    maxPoints = numPoints
                    largestPart = part
                }
            }

            return largestPart!!
        }
    }
}
