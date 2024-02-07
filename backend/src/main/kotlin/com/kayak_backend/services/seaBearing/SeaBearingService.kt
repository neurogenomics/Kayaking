package com.kayak_backend.services.seaBearing

import com.kayak_backend.services.coastline.CoastlineService
import com.kayak_backend.services.coastline.IsleOfWightCoastline

class SeaBearingService(private val coastlineService: CoastlineService = IsleOfWightCoastline()) {

    private var seaBearings : List<SeaBearingInfo> = listOf()
    private val seaBearingsGetter = SeaBearingsGetter(coastlineService)

    fun getSeaBearings() : List<SeaBearingInfo> {
        if (seaBearings.isEmpty()){
            seaBearings = seaBearingsGetter.getSeaBearings()
        }
        return seaBearings
    }
}
