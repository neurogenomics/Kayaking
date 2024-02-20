package com.kayak_backend.routes

import com.kayak_backend.models.Location
import com.kayak_backend.services.route.LegTimer
import com.kayak_backend.services.route.RoutePlanner
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import io.ktor.server.util.getOrFail
import kotlin.text.get

fun Route.planRoute(
    routePlanner: RoutePlanner,
    legTimer: LegTimer,
    startPositionFilterDistance: Double = 1000.0,
) {
    route("/planRoute") {
        get {
            val lat = call.parameters.getOrFail<Double>("lat")
            val lng = call.parameters.getOrFail<Double>("lon")
            val duration = call.parameters.getOrFail<Double>("duration")
            val startTime = getDateParameter(call.parameters, "startDateTime")
            val location = Location(lat, lng)
            val routes =
                routePlanner.generateRoutes(
                    { location distanceTo it.location < startPositionFilterDistance },
                    { legTimer.getDuration(it, startTime) < duration * 60 },
                ).take(5).toList()
            call.respond(routes)
        }
    }
}
