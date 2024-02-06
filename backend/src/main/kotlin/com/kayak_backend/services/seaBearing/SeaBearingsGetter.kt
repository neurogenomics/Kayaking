package com.kayak_backend.services.seaBearing

import com.kayak_backend.models.Location
import com.kayak_backend.models.SeaBearingInfo
import com.kayak_backend.services.coastline.IsleOfWightCoastline
import org.locationtech.jts.geom.Coordinate
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class SeaBearingsGetter {

    // finds the bearing out to sea between two coordinates
    private fun seaDirection(coor1: Coordinate, coor2: Coordinate): Double{
        val lon1 = Math.toRadians(coor1.x)
        val lat1 = Math.toRadians(coor1.y)
        val lon2 = Math.toRadians(coor2.x)
        val lat2 = Math.toRadians(coor2.y)
        val y = sin(lon2 - lon1) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(lon2 - lon1)
        val r = atan2(y, x)

        return (Math.toDegrees(r) + 450) % 360
    }

    // returns list of bearings between each pair of coordinates
    fun getSeaBearings(): List<SeaBearingInfo> {
        val coastlineService = IsleOfWightCoastline()
        val coastline = coastlineService.getCoastline().coordinates

        var prev = coastline[0]

        return coastline.drop(1).map{
            val brng = seaDirection(it, prev)
            prev = it
            SeaBearingInfo(brng, Location(prev.x, prev.y))
        }

    }
}
