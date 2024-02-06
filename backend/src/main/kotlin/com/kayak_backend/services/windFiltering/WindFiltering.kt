package com.kayak_backend.services.windFiltering

import com.kayak_backend.getConf
import com.kayak_backend.getWindService
import com.kayak_backend.models.WindInfo
import com.kayak_backend.services.coastline.IsleOfWightCoastline
import com.kayak_backend.services.seaBearing.SeaBearingService
import com.kayak_backend.services.wind.WindService
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.atan2

private const val BAD_WIND_LIMIT = 90
class WindFiltering {


    fun classifyAreas() : List<WindZonesInfo> {
        val windService : WindService = getWindService(getConf("./config.yaml"))
        val seaBearings = SeaBearingService().getSeaBearings()

        return seaBearings.map{
            val wind = windService.getWind(it.coor, LocalDateTime.of(2024,1,25,12,1))

            if (badArea(it.bearing, wind)){
                //TODO group somehow
                WindZonesInfo(it, false)
            } else {
                WindZonesInfo(it,true)
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


//prints the bearings, coordinates and a boolean for good/bad area into windZones.csv (for manual testing)
fun main() {
    val filter = WindFiltering()
    val result = filter.classifyAreas()

    val coastlineService = IsleOfWightCoastline()
    val coastline = coastlineService.getCoastline().coordinates

    try {
        PrintWriter(FileWriter(File("src/main/resources/windZones.csv"))).use { writer ->
            for (i in 0..<coastline.size - 1) {
                writer.println("${result[i].bearing.bearing},${result[i].bearing.coor.latitude},${result[i].bearing.coor.longitude},${result[i].good}")
            }
        }

    } catch (e: IOException) {
        e.printStackTrace()
    }
}
