package com.kayak_backend.services.wind

import com.kayak_backend.gribReader.NetCDFGribReader
import com.kayak_backend.models.Location
import com.kayak_backend.models.WindInfo
import java.time.LocalDateTime

class GribWindFetcher: WindService{
    private val filePath = "gribFiles/Cherbourg_4km_WRF_WAM_240125-12.grb"
    private val uVariableName = "TwoD/LatLon_76X92-49p73N-1p324W/u-component_of_wind_height_above_ground"
    private val vVariableName = "TwoD/LatLon_76X92-49p73N-1p324W/v-component_of_wind_height_above_ground"
    private val latName = "TwoD/LatLon_76X92-49p73N-1p324W/lat"
    private val lonName = "TwoD/LatLon_76X92-49p73N-1p324W/lon"
    private val timeName = "TwoD/LatLon_76X92-49p73N-1p324W/reftime"

    override fun getWind(loc: Location, time: LocalDateTime): WindInfo {
        val gribReader = NetCDFGribReader()
        val pair =  gribReader.getVarPair(loc.latitude, loc.longitude, time, uVariableName, vVariableName, filePath, latName, lonName, timeName)
        return WindInfo(u = pair.first, v = pair.second)
    }
}

fun main() {
    val lat = 50.0
    val lon = -2.0

    val windFetcher = GribWindFetcher()
    println(windFetcher.getWind(Location(lat, lon), LocalDateTime.now()))
}