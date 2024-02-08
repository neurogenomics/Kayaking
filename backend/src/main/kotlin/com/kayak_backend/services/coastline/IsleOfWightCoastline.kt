package com.kayak_backend.services.coastline

import okhttp3.OkHttpClient
import org.json.JSONObject
import org.locationtech.jts.geom.*
import java.io.File

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
