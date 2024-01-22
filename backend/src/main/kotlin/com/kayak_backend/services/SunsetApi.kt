package com.kayak_backend.services
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

private const val API_ENDPOINT = "api.sunrisesunset.io"
class SunsetApi (private val client: OkHttpClient = OkHttpClient()){
    fun getSunset(lat: String, lng: String, date: String? = "today"): String {
        val request = buildRequest(lat, lng, date)
        val response = client.newCall(request).execute()
        val str = response.body?.string()
        val json = JSONObject(str)
        val results = json.optString("results")
        if (results.isNotEmpty()) {
            return results
        } else {
            throw NoSuchElementException("Sunset data could not be found")
        }
    }
    private fun buildRequest(lat: String, lng: String, date: String?): Request {
        val urlBuilder = HttpUrl.Builder()
            .scheme("https")
            .host(API_ENDPOINT)
            .addPathSegment("json")
            .addQueryParameter("lat", lat)
            .addQueryParameter("lng", lng)
            .addQueryParameter("date", date)
        return Request.Builder()
            .url(urlBuilder.build())
            .build()
    }
}