package com.kayak_backend.services.coastline

import org.locationtech.jts.geom.Coordinate
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter


class DirectionToSea {

    // finds the normal bearing for the two coordinates
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
    fun bearingToSea(): List<Double> {
        val coastlineService = IsleOfWightCoastline()
        val coastline = coastlineService.getCoastline().coordinates

        var prev = coastline[0]

        return coastline.drop(1).map{
            val brng = seaDirection(it, prev)
            prev = it
            brng
        }

    }

}

//prints the bearing and 2 coordinates into out.csv so can plot file in python
fun main() {
    val dir = DirectionToSea()
    val bearings = dir.bearingToSea()

    val coastlineService = IsleOfWightCoastline()
    val coastline = coastlineService.getCoastline().coordinates

    try {
        PrintWriter(FileWriter(File("src/main/resources/out.csv"))).use { writer ->
            for (i in 0..<coastline.size - 1) {
                //below is for plotting
                val bearing = bearings[i]
                val lon = coastline[i].x
                val lat = coastline[i].y

                writer.println("$bearing,$lon,$lat")
            }
        }

    } catch (e: IOException) {
        e.printStackTrace()
    }
}
