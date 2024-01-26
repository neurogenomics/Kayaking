package com.kayak_backend.services.sunset

import com.kayak_backend.models.Location
import com.kayak_backend.serialization.LocalTimeSerializer
import kotlinx.serialization.Serializable
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
@Serializable
data class SunsetInfo(
    @Serializable(with = LocalTimeSerializer::class)
    val sunrise: LocalTime,
    @Serializable(with = LocalTimeSerializer::class)
    val sunset: LocalTime
)

class SunsetService(private val client: OkHttpClient = OkHttpClient()) {
    fun getSunset(location: Location, date: LocalDate? = null): SunsetInfo {
        val today: LocalDate = LocalDate.now();
        val request = buildRequest(location, date ?: today)
        val response = client.newCall(request).execute()
        val jsonStr = response.body?.string() ?: throw Exception()
        return parseSunsetInfo(jsonStr)
    }

    private fun parseSunsetInfo(jsonStr: String): SunsetInfo {
        val json = JSONObject(jsonStr)
        val results = json.getJSONObject("results")
        val sunriseStr = results.get("sunrise").toString();
        val sunsetStr = results.get("sunset").toString();
        val formatter = DateTimeFormatter.ofPattern("h:mm:ss a");
        val sunrise = LocalTime.parse(sunriseStr,formatter)
        val sunset = LocalTime.parse(sunsetStr,formatter)
        return SunsetInfo(sunrise, sunset)
    }
    private fun buildRequest(location: Location, date: LocalDate): Request {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDateTime: String = date.format(dateFormatter)
        val urlBuilder = HttpUrl.Builder()
                .scheme("https")
                .host("api.sunrisesunset.io")
                .addPathSegment("json")
                .addQueryParameter("lat", location.lat.toString())
                .addQueryParameter("lng", location.lng.toString())
                .addQueryParameter("date", formattedDateTime)
        return Request.Builder()
                .url(urlBuilder.build())
                .build()
    }
}

