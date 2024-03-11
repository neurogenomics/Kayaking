package com.kayak_backend.services.dangerousWindWarning.seaBearing

import com.kayak_backend.models.Location
import com.kayak_backend.services.coastline.CoastlineService
import com.kayak_backend.services.route.BaseRoute

class SeaBearingService(
    coastlineService: CoastlineService,
    routeBuffer: Double,
    private val seaBearingsGetter: SeaBearingsGetter = SeaBearingsGetter(coastlineService, BaseRoute(), routeBuffer),
) {
    private var seaBearings: Map<Location, Double> = emptyMap()

    fun getSeaBearings(): Map<Location, Double> {
        if (seaBearings.isEmpty()) {
            seaBearings = seaBearingsGetter.getSeaBearings()
        }

        return seaBearings
    }
}
