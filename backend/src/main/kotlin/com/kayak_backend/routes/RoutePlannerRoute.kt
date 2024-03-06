package com.kayak_backend.routes

import com.kayak_backend.models.Location
import com.kayak_backend.services.route.CircularRoutePlanner
import com.kayak_backend.services.route.LegTimer
import com.kayak_backend.services.route.RoutePlanner
import com.kayak_backend.services.route.TimedRoute
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import java.time.Duration

fun Route.planRoute(
    routePlanner: RoutePlanner,
    circularRoutePlanner: CircularRoutePlanner,
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
                ).take(20).toList()

            call.respond(
                routes.map { route ->
                    TimedRoute(
                        route.name,
                        route.length,
                        route.locations,
                        legTimer.getCheckpoints(route, startTime),
                    )
                },
            )
        }
    }

    route("/planCircularRoute") {
        get {
            val duration = call.parameters.getOrFail<Double>("duration")
            val date = getDateParameter(call.parameters, "date")
            val routes =
                circularRoutePlanner.generateRoutes(
                    { true },
                    date.toLocalDate(),
                    minTime = Duration.ofMinutes(duration.toLong()),
                ).take(10).toList()
            call.respond(
                routes.map {
                    TimedRoute(
                        it.name,
                        it.length,
                        it.locations,
                        legTimer.getCheckpoints(it, it.startTime!!),
                    )
                },
            )
        }
    }
}
