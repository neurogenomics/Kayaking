package com.kayak_backend.services.dangerousWindWarning.seaBearing

import com.kayak_backend.models.Location
import com.kayak_backend.services.coastline.CoastlineService
import com.kayak_backend.services.coastline.IsleOfWightCoastline
import com.kayak_backend.services.route.BaseRoute

class SeaBearingService(
    coastlineService: CoastlineService = IsleOfWightCoastline(),
    routeBuffer: Double = 500.0,
) {
    private var seaBearings: Map<Location, Double> = emptyMap()

    private val seaBearingsGetter = SeaBearingsGetter(coastlineService, BaseRoute(), routeBuffer)

    fun getSeaBearings(): Map<Location, Double> {
        if (seaBearings.isEmpty()) {
            seaBearings = seaBearingsGetter.getSeaBearings()
        }

        return seaBearings
    }
}
