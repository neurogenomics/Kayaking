package com.kayak_backend.services.routeFiltering

import com.kayak_backend.getConf
import com.kayak_backend.getWindService
import com.kayak_backend.models.Location
import com.kayak_backend.services.seaBearing.SeaBearingService
import com.kayak_backend.services.seaBearing.SeaBearingsGetter
import com.kayak_backend.services.wind.WindService
import java.io.File
import java.time.LocalDateTime
import kotlin.math.abs

class WindFiltering {

    fun classifyAreas(){
        val windService : WindService = getWindService(getConf("./config.yaml"))
        val seaBearings = SeaBearingService().getSeaBearings()

        seaBearings.forEach{
            val wind = windService.getWind(it.coor, LocalDateTime.of(2024,1,25,12,1))
            println("${wind.u} and ${wind.v}")
            val result = 0

            //TODO find resultant wind direction and if <90 different to bearing out to sea, mark as bad (or filter)
            if (badArea(it.bearing, result)){
                //mark as bad
                // either add to csv or add indicator to list depending how we decided
                // or maybe accumulate 10 then decide if that group is good/bad
            }
        }
    }

    private fun badArea(bearing: Double, result: Int): Boolean {
        return false
    }

}

fun main() {
    val filter = WindFiltering()
    filter.classifyAreas()
}
