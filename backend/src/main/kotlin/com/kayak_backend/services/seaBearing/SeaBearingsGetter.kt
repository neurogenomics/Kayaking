package com.kayak_backend.services.seaBearing

import com.kayak_backend.models.Location
import com.kayak_backend.services.coastline.CoastlineService
import com.kayak_backend.services.route.createBaseRoute
import org.locationtech.jts.geom.Coordinate
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

// idk about this
private const val ROUTE_BUFFER = 100.0

// TODO maybe change to instead pass in a polygon not the coastline service
class SeaBearingsGetter(private val coastlineService: CoastlineService) {
    /*
     * Returns list of bearings between each pair of coordinates in the coastline
     * Requires the coastline service to return the coordinates in a clockwise order.
     * */
    fun getSeaBearings(): List<SeaBearingInfo> {
        // TODO change the route/coastline service design maybe so seaBearings isn't using both?
        val coastline = createBaseRoute(coastlineService.getCoastline(), ROUTE_BUFFER).coordinates

        if (coastline.isEmpty()) return emptyList()

        var prev = coastline[0]

        return coastline.drop(1).mapNotNull {
            if (it != prev) {
                val brng = seaDirection(it, prev)
                prev = it
                SeaBearingInfo(brng, Location(prev.x, prev.y))
            } else {
                null
            }
        }
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

        return (Math.toDegrees(r) + 450) % 360
    }
}
