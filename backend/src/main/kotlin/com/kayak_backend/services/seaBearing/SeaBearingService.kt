package com.kayak_backend.services.seaBearing

import com.kayak_backend.models.Location
import com.kayak_backend.services.coastline.CoastlineService
import com.kayak_backend.services.coastline.IsleOfWightCoastline
import com.kayak_backend.services.route.BaseRoute

class SeaBearingService(coastlineService: CoastlineService = IsleOfWightCoastline()) {
    private var seaBearings: Map<Location, Double> = mapOf()

    private val seaBearingsGetter = SeaBearingsGetter(coastlineService, BaseRoute())

    fun getSeaBearings(): Map<Location, Double> {
        if (seaBearings.isEmpty()) {
            seaBearings = seaBearingsGetter.getSeaBearings()
        }

        return seaBearings
    }
}
