package com.kayak_backend.services.seaBearing

import com.kayak_backend.services.coastline.CoastlineService
import com.kayak_backend.services.coastline.IsleOfWightCoastline
import com.kayak_backend.services.route.BaseRoute

class SeaBearingService(
    coastlineService: CoastlineService = IsleOfWightCoastline(),
    routeBuffer: Double = 500.0,
) {
    private var seaBearings: List<SeaBearingInfo> = listOf()

    private val seaBearingsGetter = SeaBearingsGetter(coastlineService, BaseRoute(), routeBuffer)

    fun getSeaBearings(): List<SeaBearingInfo> {
        if (seaBearings.isEmpty()) {
            seaBearings = seaBearingsGetter.getSeaBearings()
        }

        return seaBearings
    }
}
