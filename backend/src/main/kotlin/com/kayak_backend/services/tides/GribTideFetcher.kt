package com.kayak_backend.services.tides

import com.kayak_backend.gribReader.NetCDFGribReader
import com.kayak_backend.models.Location
import com.kayak_backend.models.TideInfo
import java.time.LocalDateTime

class GribTideFetcher: TideService {
    private val filePath = "gribFiles/Cherbourg_4km_WRF_WAM_240125-12.grb"
    private val uVariableName = "TwoD/LatLon_100X120-49p74N-1p333W/u-component_of_current_surface"
    private val vVariableName = "TwoD/LatLon_100X120-49p74N-1p333W/v-component_of_current_surface"
    private val latName = "TwoD/LatLon_100X120-49p74N-1p333W/lat"
    private val lonName = "TwoD/LatLon_100X120-49p74N-1p333W/lon"
    private val timeName = "TwoD/LatLon_100X120-49p74N-1p333W/reftime"

    override fun getTide(loc: Location, time: LocalDateTime): TideInfo {
        val gribReader = NetCDFGribReader()
        val pair =  gribReader.getVarPair(loc.latitude, loc.longitude, time, uVariableName, vVariableName, filePath, latName, lonName, timeName)
        return TideInfo(u = pair.first, v = pair.second)
    }
}