package com.kayak_backend.services.coastline

import com.kayak_backend.models.Location
import org.locationtech.jts.geom.Polygon

interface CoastlineService {
    fun getCoastline(): Polygon
}