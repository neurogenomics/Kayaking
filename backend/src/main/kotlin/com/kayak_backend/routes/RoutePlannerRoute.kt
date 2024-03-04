package com.kayak_backend.routes

import com.kayak_backend.models.Location
import com.kayak_backend.services.route.*
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import io.ktor.server.routing.Route
import io.ktor.server.util.getOrFail

fun Route.planRoute(
    routePlanner: RoutePlanner,
    legTimer: LegTimer,
    legDifficulty: LegDifficulty,
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
                ).take(20).toList()

            val timedRoutes = routes.zip(routes.map { legTimer.getCheckpoints(it, startTime) })
            call.respond(
                timedRoutes.map {
                        (route, checkpoints) ->
                    TimedRankedRoute(
                        route.name,
                        route.length,
                        route.locations,
                        checkpoints,
                        legDifficulty.getDifficulty(route, startTime, checkpoints),
                    )
                },
            )
        }
    }
}
