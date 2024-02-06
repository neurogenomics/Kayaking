package com.kayak_backend.services.slipways
import com.kayak_backend.models.Location
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

private const val API_ENDPOINT = "overpass-api.de"

class SlipwaysGetter(private val client: OkHttpClient = OkHttpClient(), private val loc1: Location, private val loc2: Location) {
    fun getSlipways(): List<Location> {
        val request = buildRequest()
        val response = client.newCall(request).execute()
        val str = response.body?.string()
        val json = JSONObject(str)
        return parseSlipwayInfo(json)
    }

    private fun parseSlipwayInfo(json: JSONObject): List<Location> {
        val elements = json.getJSONArray("elements")

        val slipways: MutableList<Location> = mutableListOf()
        for (i in 0 until elements.length()) {
            val slipway = elements.getJSONObject(i)
            slipways.add(Location(slipway.getDouble("lat"), slipway.getDouble("lon")))
        }

        return slipways
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
