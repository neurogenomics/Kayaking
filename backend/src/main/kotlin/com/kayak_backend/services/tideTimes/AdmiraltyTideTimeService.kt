package com.kayak_backend.services.tideTimes

import com.kayak_backend.models.Location
import com.kayak_backend.models.TideEvent
import com.kayak_backend.models.TideTimes
import com.kayak_backend.models.TideStation
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.time.LocalDateTime
import io.github.cdimascio.dotenv.dotenv
import java.io.IOException

class AdmiraltyTideTimeService(
    private val client: OkHttpClient = OkHttpClient(),
    private val tideStationService: TideStationService = AdmiraltyTideStationService(),
) : TideTimeService {

    private val dotenv = dotenv()
    private val apikey = dotenv["ADMIRALTY_API_KEY"]
    private var stations: List<TideStation> = listOf()

    override fun getTideTimes(location: Location): TideTimes {
        val station = getClosestTideStation(location)
        val request = buildRequest(station.id)
        val response = client.newCall(request).execute()
        val jsonStr = response.body?.string() ?: throw IOException("No response body from Admiralty API")
        val events = parseTideEvents(jsonStr)
        return TideTimes(events, station)
    }

    private fun parseTideEvents(jsonStr: String): List<TideEvent> {
        val jsonArr = JSONArray(jsonStr)
        val events: MutableList<TideEvent> = mutableListOf()
        for (i in 0 until jsonArr.length()) {
            val jsonObj = jsonArr.getJSONObject(i)
            val isHighTide = jsonObj.getString("EventType").equals("HighWater");
            var height: Double? = null;
            if (jsonObj.has("Height")){
                height = jsonObj.getDouble("Height")
            }
            val dateTime = LocalDateTime.parse(jsonObj.getString("DateTime"))
            val tideEvent = TideEvent(isHighTide, dateTime, height);
            events.add(tideEvent)
        }
        return events;
    }

    private fun getClosestTideStation(location: Location): TideStation {
        if (stations.isEmpty()) {
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
        if (apikey.isEmpty()){
            throw IllegalStateException("Admiralty API key is missing or empty");
        }
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
            .addHeader("Ocp-Apim-Subscription-Key", apikey)
            .build()
    }
}



