package com.kayak_backend.services.tideTimes

import com.kayak_backend.models.Location
import com.kayak_backend.models.TideStation
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.json.JSONObject

class AdmiraltyTideStationService(private val apiKey: String, private val client: OkHttpClient = OkHttpClient()) : TideStationService {
    override fun getTideStations(): List<TideStation> {
        val request = buildRequest()
        val response = client.newCall(request).execute()
        val jsonStr = response.body?.string() ?: throw IOException("No response body from Admiralty API")
        return parseTideEvents(jsonStr)
    }

    private fun parseTideEvents(jsonStr: String): List<TideStation> {
        val json = JSONObject(jsonStr)
        val features = json.getJSONArray("features")
        val stations: MutableList<TideStation> = mutableListOf()
        for (i in 0 until features.length()) {
            val feature = features.getJSONObject(i)
            val coordinates = feature.getJSONObject("geometry").getJSONArray("coordinates")
            val location = Location(coordinates.getDouble(1), coordinates.getDouble(0))
            val properties = feature.getJSONObject("properties")
            val id = properties.getString("Id")
            val name = properties.getString("Name")
            stations.add(TideStation(id, name, location))
        }
        return stations
    }

    private fun buildRequest(): Request {
        if (apiKey.isEmpty()) {
            throw IllegalStateException("Admiralty API key is missing or empty")
        }
        val urlBuilder =
            HttpUrl.Builder()
                .scheme("https")
                .host("admiraltyapi.azure-api.net")
                .addPathSegment("uktidalapi")
                .addPathSegment("api")
                .addPathSegment("v1")
                .addPathSegment("stations")
        return Request.Builder()
            .url(urlBuilder.build())
            .addHeader("Ocp-Apim-Subscription-Key", apiKey)
            .build()
    }
}
