package com.kayak_backend.services.slipways

import com.kayak_backend.services.isleOfWightLocation1
import com.kayak_backend.services.isleOfWightLocation2
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

private const val API_ENDPOINT = "nominatim.openstreetmap.org"

class OsmNamer(private val client: OkHttpClient = OkHttpClient()) {
    fun nameOsmIds(osmIds: List<String>): List<String> {
        val chunkedOsmIds = osmIds.chunked(50) // API allows max of 50 lookups in one request
        val result = mutableListOf<String>()

        for (chunk in chunkedOsmIds) {
            val request = buildRequest(chunk)
            val response = client.newCall(request).execute()
            val str = response.body?.string()
            val json = JSONArray(str)
            result.addAll(parseDisplayName(json))
        }
        return result
    }

    private fun parseDisplayName(jsonArray: JSONArray): List<String> {
        val displayNames: MutableList<String> = mutableListOf()
        for (i in 0 until jsonArray.length()) {
            val place = jsonArray.getJSONObject(i)
            displayNames.add(place.getString("display_name"))
        }
        return displayNames
    }

    private fun buildRequest(osmIds: List<String>): Request {
        val urlBuilder =
            HttpUrl.Builder()
                .scheme("https")
                .host(API_ENDPOINT)
                .addPathSegment("lookup")
                .addQueryParameter("format", "json")
                .addQueryParameter("osm_ids", osmIds.joinToString(","))
        return Request.Builder()
            .url(urlBuilder.build())
            .build()
    }
}

fun main() {
    val slipwayGetter = SlipwaysGetter(OkHttpClient(), isleOfWightLocation1, isleOfWightLocation2)
    slipwayGetter.getSlipways()
}
