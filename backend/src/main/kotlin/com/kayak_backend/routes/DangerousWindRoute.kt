package com.kayak_backend.routes

import com.kayak_backend.models.Location
import com.kayak_backend.services.dangerousWindWarning.DangerousWindService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.json.JSONArray

fun Route.dangerousWind(dangerousWind: DangerousWindService) {
    route("/dangerouswind") {
        get {
            val startTime = getDateParameter(call.parameters, "startDateTime")

            val latsArray = JSONArray(call.parameters.getOrFail<String>("lats"))
            val longsArray = JSONArray(call.parameters.getOrFail<String>("longs"))
            val checkpointArray = JSONArray(call.parameters.getOrFail<String>("checkpoints"))

            val checkpoints = Array(checkpointArray.length()) { checkpointArray.getLong(it) }.toList()
            val lats = Array(latsArray.length()) { latsArray.getDouble(it) }.toList()
            val longs = Array(longsArray.length()) { longsArray.getDouble(it) }.toList()

            val locations =
                lats.zip(longs).map { (lat, long) ->
                    Location(lat, long)
                }

            call.respond(dangerousWind.findBadWinds(locations, checkpoints, startTime))
        }
    }
}
