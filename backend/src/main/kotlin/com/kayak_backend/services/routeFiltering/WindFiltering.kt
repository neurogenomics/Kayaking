package com.kayak_backend.services.routeFiltering

import com.kayak_backend.models.Location
import com.kayak_backend.services.wind.GribWindFetcher
import java.io.File
import java.time.LocalDateTime

class WindFiltering {

    // if file does not exist, run the main function in DirectionToSea.kt
    private val seaBearingPath = "src/main/resources/seaBearings.csv"

    fun classifyAreas(){
        val windService = GribWindFetcher()
        File(seaBearingPath).useLines { lines ->
            val dataLines = lines.drop(1)

            dataLines.forEach { line ->
                val point = line.split(",")
                println(point[0]) //bearing
                println(point[1]) //y lon
                println(point[2]) //x lat
                val lat = -1.29
                val lon = 50.76
                // val wind = windService.getWind(Location((point[2]).toDouble(), (point[1]).toDouble()), LocalDateTime.now())
                val wind = windService.getWind(Location(lat, lon), LocalDateTime.now())
                println("${wind.u} and ${wind.v}")
            }
        }

    }

}

fun main() {

    val filter = WindFiltering()
    filter.classifyAreas()
}
