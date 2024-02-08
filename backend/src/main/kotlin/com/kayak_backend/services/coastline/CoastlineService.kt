package com.kayak_backend.services.coastline

import org.locationtech.jts.geom.Polygon

interface CoastlineService {
    fun getCoastline(): Polygon
}
