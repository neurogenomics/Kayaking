package com.kayak_backend.services.tideTimes

import com.kayak_backend.models.TideEvent
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.time.LocalDateTime

class TideTimeService(private val client: OkHttpClient = OkHttpClient()) {
    fun getTideTimes(): List<TideEvent> {
        // TODO get nearest station to provided location
        val request = buildRequest("0062")
        val response = client.newCall(request).execute()
        val jsonStr = response.body?.string() ?: throw Exception()
        return parseTideEvents(jsonStr)
    }

    private fun parseTideEvents(jsonStr: String): List<TideEvent> {
        val jsonArr = JSONArray(jsonStr)
        val events: List<TideEvent> = mutableListOf()
        for (i in 0 until jsonArr.length()) {
            val jsonObj = jsonArr.getJSONObject(i)
            val isHighTide = jsonObj.getString("EventType").equals("HighWater");
            val height = jsonObj.getDouble("Height");
            val dateTime = LocalDateTime.parse(jsonObj.getString("Date"))
            val tideEvent = TideEvent(isHighTide, dateTime, height);
            events.addLast(tideEvent)
        }
        return events;
    }

    private fun buildRequest(stationID: String): Request {
        val urlBuilder = HttpUrl.Builder()
            .scheme("https")
            .host("admiraltyapi.azure-api.net")
            .addPathSegment("uktidalapi")
            .addPathSegment("api")
            .addPathSegment("v1")
            .addPathSegment("stations")
            .addPathSegment(stationID)
            .addPathSegment("tidalEvents")
        return Request.Builder()
            //"fe25fe4a8a3a442bb9e3c884feb4051c"
            .url(urlBuilder.build())
            .addHeader("Ocp-Apim-Subscription-Key", System.getenv(""))
            .build()
    }
}



