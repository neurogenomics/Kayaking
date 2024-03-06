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

    fun getDifficulty(
        route: Route,
        dateTime: LocalDateTime,
        checkpoints: List<Long>,
    ): Int {
        return getLegDifficulty(route.locations, dateTime, checkpoints, 0).first
    }

    private fun getLegDifficulty(
        leg: Leg,
        dateTime: LocalDateTime,
        checkpoints: List<Long>,
        index: Int,
    ): Pair<Int, Int> {
        val epoch = dateTime.toEpochSecond(ZoneOffset.UTC) + checkpoints[index]
        val times = difficultyCache.getOrPut(leg) { mutableMapOf() }

        return if (times.containsKey(epoch)) {
            Pair(times.getValue(epoch), index + leg.locations.size - 1)
        } else {
            calculateDifficulty(leg, dateTime, checkpoints, index).also { times[epoch] = it.first }
        }
    }

    private fun calculateDifficulty(
        leg: Leg,
        dateTime: LocalDateTime,
        checkpoints: List<Long>,
        index: Int,
    ): Pair<Int, Int> {
        return when (leg) {
            is Leg.SingleLeg -> {
                val midpoint =
                    Location(
                        (leg.start.latitude + leg.end.latitude) / 2,
                        (leg.start.longitude + leg.end.longitude) / 2,
                    )
                Pair(classifyConditions(dateTime.plusSeconds(checkpoints[index]), midpoint), index + 1)
            }
            is Leg.MultipleLegs -> {
                val difficulty =
                    leg.legs.fold(0 to index) { (currentMax, currentIndex), aLeg ->
                        val (difficulty, newIndex) = getLegDifficulty(aLeg, dateTime, checkpoints, currentIndex)
                        maxOf(currentMax, difficulty) to newIndex
                    }
                difficulty
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
