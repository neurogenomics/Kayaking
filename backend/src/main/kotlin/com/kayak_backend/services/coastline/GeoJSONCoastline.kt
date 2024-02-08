package com.kayak_backend.services.coastline

import org.json.JSONObject
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Polygon
import java.io.File

class GeoJSONCoastline(fileName: String) : CoastlineService {
    private val basePath = "./data/coastlines/%s"
    private val coastline: Polygon

    init {
        val json = File(basePath.format(fileName)).readText()
        coastline = parseCoastline(JSONObject(json))
    }

    override fun getCoastline() = coastline

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
