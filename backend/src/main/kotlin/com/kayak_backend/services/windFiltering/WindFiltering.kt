package com.kayak_backend.services.windFiltering

import com.kayak_backend.getConf
import com.kayak_backend.getWindService
import com.kayak_backend.models.WindInfo
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
class WindFiltering(private val windService : WindService = getWindService(getConf("./config.yaml")), private val seaBearingService: SeaBearingService = SeaBearingService()) {


    //returns list of SeaBearingInfo and bool for if the wind is out to sea
    fun classifyAreas() : List<WindZonesInfo> {
        val seaBearings = seaBearingService.getSeaBearings()

        return seaBearings.map{
            //TODO change the LocalDateTime after grib updater included
            val wind = windService.getWind(it.coor, LocalDateTime.of(2024, 1, 25, 14, 0))

            WindZonesInfo(it, badArea(it.bearing, wind))
        }
    }

    //returns true if wind direction is out to sea (meaning bad zone)
    private fun badArea(bearing: Double, wind: WindInfo): Boolean {
        val resultWind = Math.toDegrees(atan2(wind.u, wind.v))
        val dif = abs(resultWind - bearing)

        return (dif < BAD_WIND_LIMIT || dif > (360 - BAD_WIND_LIMIT))
    }

}


//prints the bearings, coordinates and a boolean for good/bad area into windZones.csv (for manual testing)
fun main() {
    val filter = WindFiltering()
    val result = filter.classifyAreas()

    try {
        PrintWriter(FileWriter(File("src/main/resources/windZones.csv"))).use { writer ->
            for (i in result.indices) {
                writer.println("${result[i].bearing.bearing},${result[i].bearing.coor.latitude},${result[i].bearing.coor.longitude},${result[i].bad}")
            }
        }

    } catch (e: IOException) {
        e.printStackTrace()
    }
}
