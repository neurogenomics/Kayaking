package com.kayak_backend.routes
import com.kayak_backend.models.Location
import com.kayak_backend.services.sunset.SunsetService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.datetime.toJavaLocalDate
import java.time.LocalDate

fun Route.sunset(sunset: SunsetService = SunsetService()) {
    // TODO: Get getOrFail to serialize LocalDates so this can be done implicitly
    fun getDateParameter(parameters: Parameters, name: String): LocalDate? {
        try {
            val maybeDateStr = parameters[name] ?: return null;
            return kotlinx.datetime.LocalDate.parse(maybeDateStr).toJavaLocalDate();
        } catch (e: IllegalArgumentException){
            throw ParameterConversionException(name, "Date in format YYYY-MM-DD")
        }
    }

    route("/sunset") {
        get {
            val lat = call.parameters.getOrFail<Double>("lat");
            val lng = call.parameters.getOrFail<Double>("lng");
            val maybeDate = getDateParameter(call.parameters, "date");
            val location = Location(lat, lng);
            if (maybeDate != null){
                return@get call.respond(sunset.getSunset(location, maybeDate));
            }
            call.respond(sunset.getSunset(location));
        }
    }
}