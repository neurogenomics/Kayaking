package com.kayak_backend.routes

import com.kayak_backend.models.Location
import com.kayak_backend.services.route.*
import com.kayak_backend.services.route.CircularRoutePlanner
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.Route
import io.ktor.server.util.*
import java.time.Duration

fun Route.planRoute(
    routePlanner: RoutePlanner,
    circularRoutePlanner: CircularRoutePlanner,
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

            val timedRoutes = routes.map { Pair(it, legTimer.getCheckpoints(it, startTime)) }
            call.respond(
                timedRoutes.map {
                        (route, checkpoints) ->
                    TimedRankedRoute(
                        route.name,
                        route.length,
                        route.locations,
                        checkpoints,
                        difficulty = legDifficulty.getDifficulty(route, startTime, checkpoints),
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
                    { it.startTime!! >= date },
                    date.toLocalDate(),
                    minTime = Duration.ofMinutes(duration.toLong()),
                ).take(10).toList()
            val timedRoutes = routes.map { Pair(it, legTimer.getCheckpoints(it, it.startTime!!)) }
            call.respond(
                timedRoutes.map {
                        (route, checkpoints) ->
                    TimedRankedRoute(
                        route.name,
                        route.length,
                        route.locations,
                        checkpoints,
                        difficulty = legDifficulty.getDifficulty(route, route.startTime!!, checkpoints),
                    )
                },
            )
        }
    }
}
