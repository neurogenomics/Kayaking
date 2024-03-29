package com.kayak_backend.services.tideTimes

import com.kayak_backend.models.Location
import com.kayak_backend.models.TideEvent
import com.kayak_backend.models.TideStation
import com.kayak_backend.models.TideTimes
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

class AdmiraltyTideTimeService(
    private val apiKey: String,
    private val client: OkHttpClient = OkHttpClient(),
    tideStationService: TideStationService = AdmiraltyTideStationService(apiKey),
) : TideTimeService {
    private var stations: List<TideStation> = tideStationService.getTideStations()

    private val stationTimes = ConcurrentHashMap<TideStation, Pair<TideTimes, LocalDateTime>>()

    override fun getTideTimes(location: Location): TideTimes {
        val station = getClosestTideStation(location)

        val res =
            stationTimes.compute(station) { tideStation, value ->
                if (value == null || (Duration.between(value.second, LocalDateTime.now()) >= Duration.ofDays(1))) {
                    val request = buildRequest(tideStation.id)
                    val response = client.newCall(request).execute()
                    val jsonStr = response.body?.string() ?: throw IOException("No response body from Admiralty API")
                    val events = parseTideEvents(jsonStr)
                    Pair(TideTimes(events, station), LocalDateTime.now())
                } else {
                    value
                }
            }!!

        return res.first
    }

    private fun parseTideEvents(jsonStr: String): List<TideEvent> {
        val jsonArr = JSONArray(jsonStr)
        val events: MutableList<TideEvent> = mutableListOf()
        for (i in 0 until jsonArr.length()) {
            val jsonObj = jsonArr.getJSONObject(i)
            val isHighTide = jsonObj.getString("EventType").equals("HighWater")
            var height: Double? = null
            if (jsonObj.has("Height")) {
                height = jsonObj.getDouble("Height")
            }

            val dateTime = LocalDateTime.parse(jsonObj.getString("DateTime"))
            val tideEvent = TideEvent(isHighTide, dateTime, height)
            events.add(tideEvent)
        }
        return events
    }

    private fun getClosestTideStation(location: Location): TideStation {
        return stations.reduce { closest, current ->
            val distanceToClosest = location distanceTo closest.location
            val distanceToCurrent = location distanceTo current.location
            if (distanceToCurrent < distanceToClosest) {
                current
            } else {
                closest
            }
        }
    }

    private fun buildRequest(stationID: String): Request {
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
                .addPathSegment(stationID)
                .addPathSegment("tidalEvents")
        return Request.Builder()
            .url(urlBuilder.build())
            .addHeader("Ocp-Apim-Subscription-Key", apiKey)
            .build()
    }
}
