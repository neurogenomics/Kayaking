package com.kayak_backend.services.coastline

class IsleOfWightCoastline : CoastlineService {
    private val coastline = GeoJSONCoastline("isleOfWight.geojson").getCoastline()

    override fun getCoastline() = coastline
}
