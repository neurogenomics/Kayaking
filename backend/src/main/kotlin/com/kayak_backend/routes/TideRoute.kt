package com.kayak_backend.routes

import com.kayak_backend.models.Location
import com.kayak_backend.services.tides.GribTideFetcher
import com.kayak_backend.services.tides.TideService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.datetime.toJavaLocalDateTime
import java.time.LocalDateTime

fun Route.tide(tide: TideService = GribTideFetcher()) {
    // TODO: Get getOrFail to serialize so this can be done implicitly
    fun getDateParameter(parameters: Parameters, name: String): LocalDateTime {
        try {
            val maybeDateStr = parameters[name] ?: return LocalDateTime.now();
            return kotlinx.datetime.LocalDateTime.parse(maybeDateStr).toJavaLocalDateTime();
        } catch (e: IllegalArgumentException){
            throw ParameterConversionException(name, "Date in format YYYY-MM-DD")
        }
    }

    route("/tide") {
        get {
            val lat = call.parameters.getOrFail<Double>("lat");
            val lon = call.parameters.getOrFail<Double>("lon");
            val dateTime = getDateParameter(call.parameters, "datetime");
            val location = Location(lat, lon);
            call.respond(tide.getTide(location, dateTime));
        }
    }
}