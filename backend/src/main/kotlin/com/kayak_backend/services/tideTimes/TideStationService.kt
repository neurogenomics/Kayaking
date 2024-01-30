package com.kayak_backend.services.tideTimes

import com.kayak_backend.models.Location
import com.kayak_backend.models.TideEvent
import com.kayak_backend.models.TideStation
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.time.LocalDateTime
import io.github.cdimascio.dotenv.dotenv
import org.json.JSONObject

class TideStationService(private val client: OkHttpClient = OkHttpClient()) {

    private val dotenv = dotenv()
    private val ADMIRALTY_API_KEY = dotenv["ADMIRALTY_API_KEY"]
    fun getTideStations(): List<TideStation> {
        val request = buildRequest()
        val response = client.newCall(request).execute()
        val jsonStr = response.body?.string() ?: throw Exception()
        return parseTideEvents(jsonStr)
    }

    private fun parseTideEvents(jsonStr: String): List<TideStation> {
        val json = JSONObject(jsonStr)
        val features = json.getJSONArray("features");
        val stations: List<TideStation> = mutableListOf()
        for (i in 0 until features.length()) {
            val feature = features.getJSONObject(i)
            val coordinates = feature.getJSONObject("geometry").getJSONArray("coordinates");
            val location = Location(coordinates.getDouble(1), coordinates.getDouble(0))
            val properties = feature.getJSONObject("properties")
            val id = properties.getString("Id")
            val name = properties.getString("Name")
            stations.addLast(TideStation(id, name, location))
        }
        return stations;
    }

    private fun buildRequest(): Request {
        val urlBuilder = HttpUrl.Builder()
            .scheme("https")
            .host("admiraltyapi.azure-api.net")
            .addPathSegment("uktidalapi")
            .addPathSegment("api")
            .addPathSegment("v1")
            .addPathSegment("stations")
        return Request.Builder()
            .url(urlBuilder.build())
            .addHeader("Ocp-Apim-Subscription-Key", ADMIRALTY_API_KEY)
            .build()
    }
}



