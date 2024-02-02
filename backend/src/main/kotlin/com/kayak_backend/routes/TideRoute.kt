package com.kayak_backend.routes

import com.kayak_backend.gribReader.GribFileError
import com.kayak_backend.gribReader.GribIndexError
import com.kayak_backend.models.Location
import com.kayak_backend.services.tides.TideService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.datetime.toJavaLocalDateTime
import java.time.LocalDateTime

fun Route.tide(tide: TideService) {
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

    route("/tide") {
        get {
            val lat = call.parameters.getOrFail<Double>("lat")
            val lon = call.parameters.getOrFail<Double>("lon")
            val dateTime = getDateParameter(call.parameters, "datetime")
            val location = Location(lat, lon)
            try {
                call.respond(tide.getTide(location, dateTime))
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
