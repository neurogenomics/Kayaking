package com.kayak_backend.services.slipways
import com.kayak_backend.models.Location
import com.kayak_backend.services.route.NamedLocation
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

private const val API_ENDPOINT = "overpass-api.de"

class SlipwaysGetter(private val client: OkHttpClient = OkHttpClient(), private val loc1: Location, private val loc2: Location) {
    private val osmNamer = OsmNamer()

    fun getSlipways(): List<NamedLocation> {
        val request = buildRequest()
        val response = client.newCall(request).execute()
        val str = response.body?.string()
        val json = JSONObject(str)

        val slipwaysLocations = parseSlipwayLocations(json)
        val osmIds = parseOsmIds(json)
        // Get full address of slipway use first part
        val displayNames = osmNamer.nameOsmIds(osmIds).map { "${it.split(',').first()} Slipway" }
        return slipwaysLocations.zip(displayNames).map { NamedLocation(it.first, it.second) }
    }

    private fun parseSlipwayLocations(json: JSONObject): List<Location> {
        val elements = json.getJSONArray("elements")
        val slipways: MutableList<Location> = mutableListOf()
        for (i in 0 until elements.length()) {
            val slipway = elements.getJSONObject(i)
            slipways.add(Location(slipway.getDouble("lat"), slipway.getDouble("lon")))
        }
        return slipways
    }

    private fun parseOsmIds(json: JSONObject): List<String> {
        val elements = json.getJSONArray("elements")
        val osmIds: MutableList<String> = mutableListOf()
        for (i in 0 until elements.length()) {
            val node = elements.getJSONObject(i)
            // osmID = first letter of type + id
            osmIds.add("${node.getString("type")[0]}${node.getBigInteger("id")}")
        }
        return osmIds
    }

    private fun buildRequest(): Request {
        val urlBuilder =
            HttpUrl.Builder()
                .scheme("https")
                .host(API_ENDPOINT)
                .addPathSegment("api")
                .addPathSegment("interpreter")
                .addQueryParameter(
                    "data",
                    """[out:json];
            node
            [leisure=slipway]
            (""" + loc1.latitude + """,""" + loc1.longitude + """,""" + loc2.latitude + """,""" + loc2.longitude + """);
            out;""",
                )
        return Request.Builder()
            .url(urlBuilder.build())
            .build()
    }
}
