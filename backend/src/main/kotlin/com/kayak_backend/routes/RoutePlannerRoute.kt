package com.kayak_backend.routes

import com.kayak_backend.services.route.*
import com.kayak_backend.services.route.CircularRoutePlanner
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.Route
import io.ktor.server.util.*
import java.time.Duration

enum class PaddleSpeed {
    SLOW,
    NORMAL,
    FAST,
}

fun parsePaddleSpeed(input: String): PaddleSpeed {
    return when (input.lowercase()) {
        "slow" -> PaddleSpeed.SLOW
        "normal" -> PaddleSpeed.NORMAL
        "fast" -> PaddleSpeed.FAST
        else -> throw IllegalArgumentException("Invalid paddleSpeed: $input")
    }
}

fun paddleSpeedToLegTimer(
    paddleSpeed: PaddleSpeed,
    legTimers: LegTimers,
): LegTimer {
    return when (paddleSpeed) {
        PaddleSpeed.SLOW -> legTimers.slowLegTimer
        PaddleSpeed.NORMAL -> legTimers.normalLegTimer
        PaddleSpeed.FAST -> legTimers.fastLegTimer
    }
}

fun Route.planRoute(
    routePlanner: RoutePlanner,
    circularRoutePlanner: CircularRoutePlanner,
    legTimers: LegTimers,
    legDifficulty: LegDifficulty,
) {
    route("/planRoute") {
        get {
            val latFrom = call.parameters.getOrFail<Double>("latFrom")
            val lonFrom = call.parameters.getOrFail<Double>("lonFrom")
            val latTo = call.parameters.getOrFail<Double>("latTo")
            val lonTo = call.parameters.getOrFail<Double>("lonTo")
            val duration = call.parameters.getOrFail<Double>("duration")
            val startTime = getDateParameter(call.parameters, "startDateTime")
            val paddleSpeed = parsePaddleSpeed(call.parameters["paddleSpeed"] ?: "normal")
            val legTimer = paddleSpeedToLegTimer(paddleSpeed, legTimers)

            val routes =
                routePlanner.generateRoutes(
                    { it.location.latitude in latFrom..latTo && it.location.longitude in lonFrom..lonTo },
                    { legTimer.getDuration(it, startTime) >= duration * 60 },
                    startTime,
                ).take(10).sortedBy { it.length }.toList()

            val timedRoutes = routes.map { Pair(it, legTimer.getCheckpoints(it, startTime)) }
            call.respond(
                timedRoutes.map {
                        (route, checkpoints) ->
                    TimedRankedRoute(
                        route.name,
                        route.length,
                        route.locations,
                        checkpoints,
                        startTime,
                        legDifficulty.getDifficulty(route, startTime, checkpoints),
                    )
                },
            )
        }
    }

    route("/planCircularRoute") {
        get {
            val duration = call.parameters.getOrFail<Double>("duration")
            val date = getDateParameter(call.parameters, "startDateTime")
            val routes =
                circularRoutePlanner.generateRoutes(
                    { true },
                    { it.startTime >= date },
                    date.toLocalDate(),
                    minTime = Duration.ofMinutes(duration.toLong()),
                ).take(10).toList()
            val timedRoutes = routes.map { Pair(it, legTimers.normalLegTimer.getCheckpoints(it, it.startTime)) }
            call.respond(
                timedRoutes.map {
                        (route, checkpoints) ->
                    TimedRankedRoute(
                        route.name,
                        route.length,
                        route.locations,
                        checkpoints,
                        route.startTime,
                        legDifficulty.getDifficulty(route, route.startTime, checkpoints),
                    )
                },
            )
        }
    }
}
