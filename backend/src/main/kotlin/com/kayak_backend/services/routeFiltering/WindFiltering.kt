package com.kayak_backend.services.routeFiltering

import com.kayak_backend.getConf
import com.kayak_backend.getWindService
import com.kayak_backend.models.WindInfo
import com.kayak_backend.services.seaBearing.SeaBearingService
import com.kayak_backend.services.wind.WindService
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.atan2

private const val BAD_WIND_LIMIT = 90
class WindFiltering {


    fun classifyAreas(){
        val windService : WindService = getWindService(getConf("./config.yaml"))
        val seaBearings = SeaBearingService().getSeaBearings()

        seaBearings.forEach{
            val wind = windService.getWind(it.coor, LocalDateTime.of(2024,1,25,12,1))

            if (badArea(it.bearing, wind)){
                //mark as bad
                // or maybe accumulate 10 then decide if that group is good/bad
                println("bad :(")
            } else {
                println("good :)")
            }
        }
    }

    private fun badArea(bearing: Double, wind: WindInfo): Boolean {
        val displace = if (wind.v < 0) 180.0 else 0.0
        val resultWind = Math.toDegrees(atan2(wind.u, wind.v)) + displace
        val dif = abs(resultWind - bearing)

        println("Bearing is $bearing")
        println("Wind is ${wind.u},${wind.v}")
        println(resultWind)

        return (dif < BAD_WIND_LIMIT || dif > (360 - BAD_WIND_LIMIT))
    }

}

fun main() {
    val filter = WindFiltering()
    filter.classifyAreas()
}
