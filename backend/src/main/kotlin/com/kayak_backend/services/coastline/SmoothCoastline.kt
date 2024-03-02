package com.kayak_backend.services.coastline

import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Polygon
import org.locationtech.jts.simplify.TopologyPreservingSimplifier

class SmoothCoastline(coastlineService: CoastlineService, smoothingTolerance: Double = 0.0005) : CoastlineService {
    private val coastline: Polygon

    init {
        val polygon = coastlineService.getCoastline()
        val simplifiedGeometry = TopologyPreservingSimplifier.simplify(polygon, smoothingTolerance)
        val geometryFactory = GeometryFactory()
        coastline = geometryFactory.createPolygon(simplifiedGeometry.coordinates)
    }

    override fun getCoastline() = coastline
}
