package com.kayak_backend.services.route

import com.kayak_backend.getConf
import com.kayak_backend.getWaveService
import com.kayak_backend.getWindService
import com.kayak_backend.models.Location
import com.kayak_backend.models.getWindMagnitude
import com.kayak_backend.services.waves.WaveService
import com.kayak_backend.services.wind.WindService
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.max

// TODO in future: take into account when shielded from the wind by cliffs
class LegDifficulty(
    private val windService: WindService = getWindService(getConf("./config.yaml")),
    private val waveService: WaveService = getWaveService(getConf("./config.yaml")),
) {
    // maps leg to (time, difficulty)
    private val difficultyCache = mutableMapOf<Leg, MutableMap<Long, Int>>()

    // index of how many checkpoints we are through the route, increased each time we process a single leg
    private var index = 0

    fun getDifficulty(
        route: Route,
        dateTime: LocalDateTime,
        checkpoints: List<Long>,
    ): Int {
        index = 0
        return getLegDifficulty(route.locations, dateTime, checkpoints)
    }

    private fun getLegDifficulty(
        leg: Leg,
        dateTime: LocalDateTime,
        checkpoints: List<Long>,
    ): Int {
        val epoch = dateTime.toEpochSecond(ZoneOffset.UTC) + checkpoints[index]
        val times = difficultyCache.getOrPut(leg) { mutableMapOf() }
        return times.getOrPut(epoch) { calculateDifficulty(leg, dateTime, checkpoints) }
    }

    private fun calculateDifficulty(
        leg: Leg,
        dateTime: LocalDateTime,
        checkpoints: List<Long>,
    ): Int {
        return when (leg) {
            is Leg.SingleLeg -> {
                val midpoint =
                    Location(
                        (leg.start.latitude + leg.end.latitude) / 2,
                        (leg.start.longitude + leg.end.longitude) / 2,
                    )
                classifyConditions(dateTime.plusSeconds(checkpoints[index++]), midpoint)
            }
            is Leg.MultipleLegs -> {
                leg.legs.maxOf {
                    getLegDifficulty(it, dateTime, checkpoints)
                }
            }
        }
    }

    private fun classifyConditions(
        dateTime: LocalDateTime,
        location: Location,
    ): Int {
        val windInfo = windService.getWind(location, dateTime)
        val waveInfo = waveService.getWave(location, dateTime)

        val windLevel = windLimits.indexOfFirst { it >= (getWindMagnitude(windInfo)) }
        val waveLevel = waveLimits.indexOfFirst { it >= (waveInfo.height) }

        return if (windLevel == -1 || waveLevel == -1) 12 else max(windLevel, waveLevel)
    }

    companion object {
        // source: https://www.metoffice.gov.uk/weather/guides/coast-and-sea/beaufort-scale
        private val waveLimits = listOf(0.0, 0.1, 0.3, 1.0, 1.5, 2.5, 4.0, 5.5, 7.5, 10.0, 12.5, 16.0)
        private val windLimits = listOf(1.0, 2.0, 3.0, 5.0, 8.0, 11.0, 14.0, 17.0, 21.0, 24.0, 28.0, 32.0)
    }
}
