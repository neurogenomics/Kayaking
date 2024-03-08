package com.kayak_backend.services.dangerousWindWarning.seaBearing

import com.kayak_backend.models.Location
import com.kayak_backend.services.coastline.CoastlineService
import com.kayak_backend.services.route.BaseRoute
import org.locationtech.jts.geom.Coordinate
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class SeaBearingsGetter(
    private val coastlineService: CoastlineService,
    private val route: BaseRoute,
    private val routeBuffer: Double,
) {
    /*
     * Returns list of bearings between each pair of coordinates in the coastline
     * assigns this bearing to the first of the pair of coordinates
     * Requires the coastline service to return the coordinates in a clockwise order.
     * */
    fun getSeaBearings(): Map<Location, Double> {
        val coastline = route.createBaseRoute(coastlineService.getCoastline(), routeBuffer).coordinates

        return coastline.toList().zipWithNext {
                a: Coordinate, b: Coordinate ->
            if (a != b) {
                val bearing = seaDirection(b, a)
                (Location(a.x, a.y) to bearing)
            } else {
                // Null for the case the same coordinate is twice in a row, to avoid non-meaningful bearings
                null
            }
        }.filterNotNull().toMap()
    }

    /*
     * Finds the bearing out to sea between two coordinates.
     * */
    private fun seaDirection(
        coor1: Coordinate,
        coor2: Coordinate,
    ): Double {
        val lon1 = Math.toRadians(coor1.x)
        val lat1 = Math.toRadians(coor1.y)
        val lon2 = Math.toRadians(coor2.x)
        val lat2 = Math.toRadians(coor2.y)
        val y = sin(lon2 - lon1) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(lon2 - lon1)
        val r = atan2(y, x)

        // atan returns value between -180 and 180 so +450 to first make positive (+360) then find perpendicular (+90)
        return (Math.toDegrees(r) + 450) % 360
    }
}
