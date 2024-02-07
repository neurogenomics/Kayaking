package com.kayak_backend.services.coastline

import com.kayak_backend.services.route.Route
import okhttp3.OkHttpClient
import org.json.JSONObject
import org.locationtech.jts.geom.*
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter

class IsleOfWightCoastline(private val client: OkHttpClient = OkHttpClient()) : CoastlineService {
    override fun getCoastline(): Polygon {
        val json = File("./src/main/kotlin/com/kayak_backend/gribReader/wight.geojson").readText()
        return parseCoastline(JSONObject(json))
    }

    private fun parseCoastline(json: JSONObject): Polygon {
        val coordinates: MutableList<Coordinate> = mutableListOf()

        val jsonCoordinates =
            json.getJSONArray(
                "features",
            ).getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0)

        for (i in 0 until jsonCoordinates.length()) {
            val jsonCoordinate = jsonCoordinates.getJSONArray(i)
            coordinates.add(Coordinate(jsonCoordinate.getDouble(1), jsonCoordinate.getDouble(0)))
        }
        // Close the polygon by adding the first point to the end

        coordinates.add(coordinates.first())

        val geometryFactory = GeometryFactory()
        return geometryFactory.createPolygon(coordinates.toTypedArray())
    }
}

fun main() {
    val test = IsleOfWightCoastline()
    val coast = test.getCoastline()

    val y = Route.create(coast, 0.05) // Route.extractLargestPart(b)

    try {
        PrintWriter(FileWriter(File("/home/jamie/thirdyear/tests/coast/coast.csv"))).use { writer ->
            writer.println("latitude,longitude")
            // writer.println("Polygon: $polygon")
            for (i in 0 until y.numPoints) {
                val p0 = y.coordinates[i]
                writer.println("${p0.x},${p0.y}")
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

//    try {
//        PrintWriter(FileWriter(File("/home/jamie/thirdyear/tests/coast/out2.csv"))).use { writer ->
//            writer.println("latitude,longitude")
//            // writer.println("Polygon: $polygon")
//            for (i in 0 until coast.numPoints) {
//                val p0 = coast.coordinates[i]
//                writer.println("${p0.x},${p0.y}")
//            }
//        }
//    } catch (e: IOException) {
//        e.printStackTrace()
//    }
}
