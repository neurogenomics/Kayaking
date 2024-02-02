package com.kayak_backend.services.coastline

import com.kayak_backend.services.route.Route
import okhttp3.OkHttpClient
import org.json.JSONObject
import org.locationtech.jts.geom.*
import org.locationtech.jts.operation.union.CascadedPolygonUnion
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier
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
        val coordinates : MutableList<Coordinate> = mutableListOf()

        val jsonCoordinates = json.getJSONArray("features").getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0)



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

fun smoothPolygon(polygon: Polygon, tolerance: Double): Polygon {
    // Use Douglas-Peucker simplification to smooth the polygon
    val simplifiedGeometry = DouglasPeuckerSimplifier.simplify(polygon, tolerance)
    // Ensure the result is a polygon (it might be a MultiPolygon)
    val factory = GeometryFactory(PrecisionModel(), polygon.srid)
    return factory.createPolygon(simplifiedGeometry.coordinates)
}

fun main() {
    val test = IsleOfWightCoastline()
    val coast = test.getCoastline()

    val route = Route.create(coast, 1.0)
    val route2 = coast
    val x = route.numGeometries
    val b = route.boundary
    val y =  Route.extractLargestPart(b)

    try {
        PrintWriter(FileWriter(File("/home/jamie/thirdyear/tests/coast/out.csv"))).use { writer ->
            writer.println("latitude,longitude")
            //writer.println("Polygon: $polygon")
            for (i in 0 until y.numPoints) {
        val p0 = y.coordinates[i]
                writer.println("${p0.x},${p0.y}")
    }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    try {
        PrintWriter(FileWriter(File("/home/jamie/thirdyear/tests/coast/out2.csv"))).use { writer ->
            writer.println("latitude,longitude")
            //writer.println("Polygon: $polygon")
            for (i in 0 until route2.numPoints) {
                val p0 = route2.coordinates[i]
                writer.println("${p0.x},${p0.y}")
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
//    for (i in 0 until polygon.numPoints) {
//        val p0 = polygon.coordinates[i]
//        println("${p0.x},${p0.y}")
//    }
}