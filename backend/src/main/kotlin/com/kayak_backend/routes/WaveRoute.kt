package com.kayak_backend.routes

import com.kayak_backend.gribReader.GribFileError
import com.kayak_backend.gribReader.GribIndexError
import com.kayak_backend.models.Location
import com.kayak_backend.services.waves.WaveService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlin.math.max
import kotlin.math.min

fun Route.wave(wave: WaveService) {
    // TODO: Get getOrFail to serialize so this can be done implicitly

    route("/wave") {
        get {
            val lat = call.parameters.getOrFail<Double>("lat")
            val lon = call.parameters.getOrFail<Double>("lon")
            val dateTime = getDateTimeParameter(call.parameters, "datetime")
            val location = Location(lat, lon)
            try {
                call.respond(wave.getWave(location, dateTime))
            } catch (e: GribFileError) {
                call.response.status(HttpStatusCode.InternalServerError)
                call.respondText(e.message ?: "Unknown Grib File Error")
            } catch (e: GribIndexError) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respondText(e.message ?: "Grib Index Error - Request may be out of bounds")
            }
        }
    }

    route("/waveGrid") {
        get {
            val lat1 = call.parameters.getOrFail<Double>("latFrom")
            val lon1 = call.parameters.getOrFail<Double>("lonFrom")
            val lat2 = call.parameters.getOrFail<Double>("latTo")
            val lon2 = call.parameters.getOrFail<Double>("lonTo")
            val latRes = call.parameters.getOrFail<Double>("latRes")
            val lonRes = call.parameters.getOrFail<Double>("lonRes")

            val dateTime = getDateTimeParameter(call.parameters, "datetime")

            val corner1 = Location(min(lat1, lat2), min(lon1, lon2))
            val corner2 = Location(max(lat2, lat2), max(lon1, lon2))
            try {
                call.respond(wave.getWaveGrid(corner1, corner2, dateTime, Pair(latRes, lonRes)))
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
