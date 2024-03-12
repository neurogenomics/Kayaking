package com.kayak_backend.routes

import com.kayak_backend.services.route.*
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

fun parseDifficultyRange(input: String): IntRange {
    return when (input.lowercase()) {
        "any" -> 1..9
        "easy" -> 1..3
        "medium" -> 4..7
        "hard" -> 8..9
        else -> throw IllegalArgumentException("Invalid difficulty: $input")
    }
}

fun paddleSpeedToLegTimer(
    paddleSpeed: PaddleSpeed,
    difficultyLegTimers: DifficultyLegTimers,
): LegTimer {
    return when (paddleSpeed) {
        PaddleSpeed.SLOW -> difficultyLegTimers.slowLegTimer
        PaddleSpeed.NORMAL -> difficultyLegTimers.normalLegTimer
        PaddleSpeed.FAST -> difficultyLegTimers.fastLegTimer
    }
}

fun Route.planRoute(
    routePlanner: RoutePlanner,
    circularRoutePlanner: CircularRoutePlanner,
    difficultyLegTimers: DifficultyLegTimers,
    legDifficulty: LegDifficulty,
) {
    route("/planRoute") {
        get {
            val latFrom = call.parameters.getOrFail<Double>("latFrom")
            val lonFrom = call.parameters.getOrFail<Double>("lonFrom")
            val latTo = call.parameters.getOrFail<Double>("latTo")
            val lonTo = call.parameters.getOrFail<Double>("lonTo")
            val duration = call.parameters.getOrFail<Double>("duration")
            val startTime = getDateTimeParameter(call.parameters, "startDateTime")
            val paddleSpeed = parsePaddleSpeed(call.parameters["paddleSpeed"] ?: "normal")
            val difficulty = parseDifficultyRange(call.parameters["difficulty"] ?: "medium")

            val legTimer = paddleSpeedToLegTimer(paddleSpeed, difficultyLegTimers)

            val routes =
                routePlanner.generateRoutes(
                    { it.location.latitude in latFrom..latTo && it.location.longitude in lonFrom..lonTo },
                    { legTimer.getDuration(it, startTime) >= duration * 60 },
                    { legTimer.getDuration(it, startTime) <= duration * 60 * 1.5 },
                    startTime,
                )

            val timedRoutes =
                routes.map {
                    Pair(
                        it,
                        legTimer.getCheckpoints(it, startTime),
                    )
                }

            val rankedRoutes =
                timedRoutes.map { (route, checkpoints) ->
                    TimedRankedRoute(
                        route.name,
                        route.length,
                        route.locations,
                        checkpoints,
                        startTime,
                        legDifficulty.getDifficulty(route, startTime, checkpoints),
                    )
                }

            call.respond(
                rankedRoutes.filter { it.difficulty in difficulty }.take(10).sortedBy { it.length }.toList(),
            )
        }
    }

    route("/planCircularRoute") {
        get {
            val duration = call.parameters.getOrFail<Double>("duration")
            val date = getDateTimeParameter(call.parameters, "startDateTime")
            val paddleSpeed = parsePaddleSpeed(call.parameters["paddleSpeed"] ?: "normal")
            val difficulty = parseDifficultyRange(call.parameters["difficulty"] ?: "medium")

            val legTimer = paddleSpeedToLegTimer(paddleSpeed, difficultyLegTimers)

            val routes =
                circularRoutePlanner.generateRoutes(
                    legTimer,
                    date.toLocalDate(),
                    minTime = Duration.ofMinutes(duration.toLong()),
                )
            val timedRoutes =
                routes.map { Pair(it, difficultyLegTimers.normalLegTimer.getCheckpoints(it, it.startTime)) }
            call.respond(
                timedRoutes.map { (route, checkpoints) ->
                    TimedRankedRoute(
                        route.name,
                        route.length,
                        route.locations,
                        checkpoints,
                        route.startTime,
                        legDifficulty.getDifficulty(route, route.startTime, checkpoints),
                    )
                }.filter { it.difficulty in difficulty && it.startTime > date }.take(10)
                    .sortedBy { Duration.between(it.startTime, date) }
                    .toList(),
            )
        }
    }
}
