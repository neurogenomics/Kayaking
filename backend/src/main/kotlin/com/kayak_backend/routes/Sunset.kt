package com.kayak_backend.routes


import com.kayak_backend.services.SunsetApi
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import okhttp3.OkHttpClient
import java.io.IOException
fun getRequiredParam(parameters: Parameters, name: String): String {
    return parameters[name] ?: throw IllegalArgumentException("Missing '$name' parameter")
}
fun Route.sunset() {
    val sunsetApi = SunsetApi(OkHttpClient())

    route("/sunset") {
        get {
            try {
                val lat = getRequiredParam(call.parameters, "lat")
                val lng = getRequiredParam(call.parameters, "lng")
                val dateStr = call.parameters["date"]
                val sunsetInfo = sunsetApi.getSunset(lat, lng, dateStr)
                call.respondText { sunsetInfo }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Bad Request")
            } catch (e: NoSuchElementException) {
                call.respond(HttpStatusCode.InternalServerError, "Internal Server Error: ${e.message}")
            }
        }
    }
}
