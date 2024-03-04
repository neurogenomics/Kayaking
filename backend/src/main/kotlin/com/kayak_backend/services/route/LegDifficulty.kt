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

    // either this or need to get list of locations from legs and match them up with checkpoints
    private var index = 0

    // source: https://www.metoffice.gov.uk/weather/guides/coast-and-sea/beaufort-scale
    private val waveLevels = listOf(0.0, 0.1, 0.3, 1.0, 1.5, 2.5, 4.0, 5.5, 7.5, 10.0, 12.5, 16.0)
    private val windLevels = listOf(1.0, 2.0, 3.0, 5.0, 8.0, 11.0, 14.0, 17.0, 21.0, 24.0, 28.0, 32.0)

    fun getDifficulty(
        route: TimedRoute,
        dateTime: LocalDateTime,
    ): Int {
        index = 0
        return getLegDifficulty(route.locations, dateTime, route.checkpoints)
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

        val windLevel = windLevels.indexOfFirst { it >= (getWindMagnitude(windInfo)) }
        val waveLevel = waveLevels.indexOfFirst { it >= (waveInfo.height) }

        return if (windLevel == -1 || waveLevel == -1) 12 else max(windLevel, waveLevel)
    }
}
