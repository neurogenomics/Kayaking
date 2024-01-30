package com.kayak_backend.services.tideTimes

import com.kayak_backend.models.Location
import com.kayak_backend.models.TideEvent
import com.kayak_backend.models.TideEvents
import com.kayak_backend.models.TideStation
import com.kayak_backend.services.slipways.SlipwaysGetter
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.time.LocalDateTime
import io.github.cdimascio.dotenv.dotenv

class TideTimeService(private val client: OkHttpClient = OkHttpClient()) {

    private val dotenv = dotenv()
    private val ADMIRALTY_API_KEY = dotenv["ADMIRALTY_API_KEY"]
    private var stations : List<TideStation>  = listOf()
    private val tideStationService = TideStationService()

    fun getTideTimes(location: Location): TideEvents {
        val station = getClosestTideStation(location)
        val request = buildRequest(station.id)
        val response = client.newCall(request).execute()
        val jsonStr = response.body?.string() ?: throw Exception()
        val events = parseTideEvents(jsonStr)
        return TideEvents(events, station)
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

    private fun getClosestTideStation(location: Location): TideStation {
        if (stations.isEmpty()){
            stations = tideStationService.getTideStations()
        }
        return stations.reduce { closest, current ->
            val distanceToClosest = location.distance(closest.location)
            val distanceToCurrent = location.distance(current.location)
            if (distanceToCurrent < distanceToClosest) {
                current
            } else {
                closest
            }
        }
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
            .url(urlBuilder.build())
            .addHeader("Ocp-Apim-Subscription-Key", ADMIRALTY_API_KEY)
            .build()
    }
}



