package com.kayak_backend.services.route

import com.kayak_backend.getConf
import com.kayak_backend.getWaveService
import com.kayak_backend.getWindService
import com.kayak_backend.models.Location
import com.kayak_backend.services.waves.WaveService
import com.kayak_backend.services.wind.WindService
import java.time.LocalDateTime
import java.time.ZoneOffset

// should this be called routeDifficulty?
class LegDifficulty(
    windService: WindService = getWindService(getConf("./config.yaml")),
    waveService: WaveService = getWaveService(getConf("./config.yaml")),
) {
    // maps leg to (time, difficulty)
    private val difficultyCache = mutableMapOf<Leg, MutableMap<Long, Int>>()

    fun getDifficulty(
        route: Route,
        dateTime: LocalDateTime,
    ): Int {
        return getLegDifficulty(route.locations, dateTime)
    }

    private fun getLegDifficulty(
        leg: Leg,
        dateTime: LocalDateTime,
    ): Int {
        val epoch = dateTime.toEpochSecond(ZoneOffset.UTC)
        val times = difficultyCache.getOrPut(leg) { mutableMapOf() }
        return times.getOrPut(epoch) { calculateDifficulty(leg, dateTime) }
    }

    private fun calculateDifficulty(
        leg: Leg,
        dateTime: LocalDateTime,
    ): Int {
        return when (leg) {
            is Leg.SingleLeg -> {
                val midpoint =
                    Location(
                        (leg.start.latitude + leg.start.latitude) / 2,
                        (leg.start.longitude + leg.start.longitude) / 2,
                    )
                classifyConditions(dateTime, midpoint)
            }
            is Leg.MultipleLegs -> {
                leg.legs.maxOf { getLegDifficulty(leg, dateTime) }
            }
        }
    }

    private fun classifyConditions(
        dateTime: LocalDateTime,
        location: Location,
    ): Int  {
        return 0
    }
}
