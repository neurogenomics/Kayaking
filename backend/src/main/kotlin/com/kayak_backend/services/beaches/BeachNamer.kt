import com.kayak_backend.models.Location
import com.kayak_backend.services.route.NamedLocation
import org.json.JSONArray
import java.io.File

class BeachNamer {
    private val filePath = "data/beaches/isleOfWightBeaches.json"
    private val beaches = readBeachesFromFile()

    private fun readBeachesFromFile(): List<NamedLocation> {
        val jsonContent = File(filePath).readText()
        val jsonArray = JSONArray(jsonContent)
        val beaches = mutableListOf<NamedLocation>()
        for (i in 0 until jsonArray.length()) {
            val jsonObj = jsonArray.getJSONObject(i)
            val latitude = jsonObj.getDouble("latitude")
            val longitude = jsonObj.getDouble("longitude")
            val name = jsonObj.getString("name")
            beaches.add(NamedLocation(Location(latitude, longitude), name))
        }
        return beaches
    }

    fun getClosestBeachName(location: Location): String {
        return beaches.minBy { it.location distanceTo location }.name
    }
}
