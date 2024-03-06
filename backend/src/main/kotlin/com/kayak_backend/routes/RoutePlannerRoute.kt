package com.kayak_backend.routes

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
            val latFrom = call.parameters.getOrFail<Double>("latFrom")
            val lonFrom = call.parameters.getOrFail<Double>("lonFrom")
            val latTo = call.parameters.getOrFail<Double>("latTo")
            val lonTo = call.parameters.getOrFail<Double>("lonTo")
            val duration = call.parameters.getOrFail<Double>("duration")
            val startTime = getDateParameter(call.parameters, "startDateTime")
            val routes =
                routePlanner.generateRoutes(
                    { it.location.latitude in latFrom..latTo && it.location.longitude in lonFrom..lonTo },
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
                    date.toLocalDate().plusDays(1),
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
