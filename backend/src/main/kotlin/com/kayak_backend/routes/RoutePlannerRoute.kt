package com.kayak_backend.routes

import com.kayak_backend.models.Location
import com.kayak_backend.services.coastline.IsleOfWightCoastline
import com.kayak_backend.services.route.LegTimer
import com.kayak_backend.services.route.RoutePlanner
import com.kayak_backend.services.route.TimedRoute
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import io.ktor.server.util.getOrFail

val coastline = IsleOfWightCoastline()

fun Route.planRoute(
    routePlanner: RoutePlanner,
    legTimer: LegTimer,
    startPositionFilterDistance: Double = 1000.0,
) {
    val coastlineLocations = routePlanner.getBaseRoute().coordinates.map { Location(it.x, it.y) }
    route("/planRoute") {
        get {
            val lat = call.parameters.getOrFail<Double>("lat")
            val lng = call.parameters.getOrFail<Double>("lon")
            val duration = call.parameters.getOrFail<Double>("duration")
            val startTime = getDateParameter(call.parameters, "startDateTime")
            val location = Location(lat, lng)

            val coastlineLocation = coastlineLocations.minBy { it distanceTo location }

            val routes =
                routePlanner.generateRoutes(
                    { coastlineLocation distanceTo it.location < startPositionFilterDistance },
                    { legTimer.getDuration(it, startTime) < duration * 60 },
                ).take(20).toList()

            call.respond(
                routes.map { route -> TimedRoute(route.name, route.length, route.locations, legTimer.getCheckpoints(route, startTime)) },
            )
        }
    }
}
