package com.kayak_backend.routes
import com.kayak_backend.gribReader.GribFileError
import com.kayak_backend.gribReader.GribIndexError
import com.kayak_backend.models.Location
import com.kayak_backend.services.wind.WindService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.datetime.toJavaLocalDateTime
import org.json.JSONArray
import java.time.LocalDateTime
import kotlin.math.max
import kotlin.math.min

fun Route.wind(wind: WindService) {
    // TODO: Get getOrFail to serialize so this can be done implicitly
    fun getDateParameter(
        parameters: Parameters,
        name: String,
    ): LocalDateTime {
        try {
            val maybeDateStr = parameters[name] ?: return LocalDateTime.now()
            return kotlinx.datetime.LocalDateTime.parse(maybeDateStr).toJavaLocalDateTime()
        } catch (e: IllegalArgumentException) {
            throw ParameterConversionException(name, "Date in format YYYY-MM-DD")
        }
    }

    route("/wind") {
        get {
            val lat = call.parameters.getOrFail<Double>("lat")
            val lon = call.parameters.getOrFail<Double>("lon")
            val dateTime = getDateParameter(call.parameters, "datetime")
            val location = Location(lat, lon)
            try {
                call.respond(wind.getWind(location, dateTime))
            } catch (e: GribFileError) {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respondText(e.message ?: "Unknown Grib File Error")
            } catch (e: GribIndexError) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respondText(e.message ?: "Grib Index Error - Request may be out of bounds")
            }
        }
    }

    route("/winds") {
        get {
            //val locs = emptyArray<List<Double>>() //call.parameters.getOrFail<List<List<Double>>>("locs")
            //val checkpoints = emptyArray<Int>().toList()
            //(JSONArray((call.parameters["checkpoints"])) )

            val locsArray = JSONArray(call.parameters.getOrFail<String>("locs"))
            val checkpointArray = JSONArray(call.parameters.getOrFail<String>("checkpoints"))

            val locs = Array(locsArray.length()) { val json = JSONArray(locsArray.getJSONArray(it)); Array(json.length()) { json.getDouble(it) }}.toList()
            val checkpoints = Array(checkpointArray.length()) { checkpointArray.getInt(it) }.toList()

            val start = getDateParameter(call.parameters, "start")
            val locations = locs.map { (lat, long) ->
                Location(lat, long)
            }

            try {
                call.respond(wind.getWindRoute(locations, checkpoints, start))
            } catch (e: GribFileError) {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respondText(e.message ?: "Unknown Grib File Error")
            } catch (e: GribIndexError) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respondText(e.message ?: "Grib Index Error - Request may be out of bounds")
            }
        }
    }

    route("/windGrid") {
        get {
            val lat1 = call.parameters.getOrFail<Double>("latFrom")
            val lon1 = call.parameters.getOrFail<Double>("lonFrom")
            val lat2 = call.parameters.getOrFail<Double>("latTo")
            val lon2 = call.parameters.getOrFail<Double>("lonTo")
            val latRes = call.parameters.getOrFail<Double>("latRes")
            val lonRes = call.parameters.getOrFail<Double>("lonRes")

            val dateTime = getDateParameter(call.parameters, "datetime")

            val corner1 = Location(min(lat1, lat2), min(lon1, lon2))
            val corner2 = Location(max(lat2, lat2), max(lon1, lon2))
            try {
                call.respond(wind.getWindGrid(corner1, corner2, dateTime, Pair(latRes, lonRes)))
            } catch (e: GribFileError) {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respondText(e.message ?: "Unknown Grib File Error")
            } catch (e: GribIndexError) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respondText(e.message ?: "Grib Index Error - Request may be out of bounds")
            }
        }
    }
}
