package com.kayak_backend.services.beaches
import com.kayak_backend.models.BeachInfo
import com.kayak_backend.models.Location
import com.kayak_backend.models.averageLocation
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.math.BigInteger

private const val API_ENDPOINT = "overpass-api.de"

class BeachesGetter(private val client: OkHttpClient = OkHttpClient(), private val loc1: Location, private val loc2: Location) {
    fun getBeaches(): List<BeachInfo> {
        val request = buildRequest()
        val response = client.newCall(request).execute()
        val str = response.body?.string()
        val json = JSONObject(str)
        return parseBeachInfo(json)
    }

    private fun parseBeachInfo(json: JSONObject): List<BeachInfo> {
        val beaches: MutableList<BeachInfo> = mutableListOf()
        val elements = json.getJSONArray("elements")

        val nodes: MutableMap<BigInteger, Location> = mutableMapOf()
        val beachJSONs: MutableList<JSONObject> = mutableListOf()

        for (i in 0 until elements.length()) {
            val e = elements.getJSONObject(i)
            if (e.getString("type").equals("node")) {
                val location = Location(e.getDouble("lat"), e.getDouble("lon"))
                nodes[e.getBigInteger("id")] = location
            } else if (e.getString("type").equals("way")) {
                beachJSONs.add(e)
            }
        }

        for (beach in beachJSONs) {
            val coordinates: MutableList<Location> = mutableListOf()
            val beachNodes = beach.getJSONArray("nodes")

            for (i in 0 until beachNodes.length()) {
                val id = beachNodes.getBigInteger(i)
                if (nodes.contains(id)) {
                    coordinates.add(nodes[id]!!)
                }
            }

            var name: String? = null

            if (beach.has("name")) {
                name = beach.getString("name")
            }
            /*if (beach.has("tags")){
                val tags = beach.getJSONObject("tags")
                if (tags.has("surface")){
                    val surface = beach.getString("surface");
                }
                if (tags.has("lifeguard")){
                    val lifeguard = beach.getString("lifeguard");
                }
                if (tags.has("supervised")){
                    val supervised = beach.getString("supervised");
                }
            }*/

            beaches.add(BeachInfo(name, coordinates, averageLocation(coordinates)))
        }

        return beaches
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
            (way["natural"="beach"]
            (""" + loc1.latitude + """,""" + loc1.longitude + """,""" + loc2.latitude + """,""" + loc2.longitude + """););
            (._;>;);
            out;""",
                )
        return Request.Builder()
            .url(urlBuilder.build())
            .build()
    }
}
