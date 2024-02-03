package com.kayak_backend.services.routeFiltering

import com.kayak_backend.getConf
import com.kayak_backend.getWindService
import com.kayak_backend.models.Location
import com.kayak_backend.services.wind.WindService
import java.io.File
import java.time.LocalDateTime

class WindFiltering {

    // if file does not exist, run the main function in DirectionToSea.kt
    // need to decide if we store in the csv or if we run the directionToSea class each time
    // could it be done in a setup stage?
    private val seaBearingPath = "src/main/resources/seaBearings.csv"

    fun classifyAreas(){
        val windService : WindService = getWindService(getConf("./config.yaml"))
        File(seaBearingPath).useLines { lines ->
            val dataLines = lines.drop(1)

            dataLines.forEach { line ->
                val point = line.split(",")
                val bearing = (point[0]).toDouble()
                val lat = (point[2]).toDouble()
                val lon = (point[1]).toDouble()

                //time needs to be within range of the grib file
                val wind = windService.getWind(Location(lat, lon), LocalDateTime.now())
                println("${wind.u} and ${wind.v}")

                //TODO find resultant wind direction and if <90 different to bearing out to sea, mark as bad (or filter)
            }
        }

    }

}

fun main() {

    val filter = WindFiltering()
    filter.classifyAreas()
}
