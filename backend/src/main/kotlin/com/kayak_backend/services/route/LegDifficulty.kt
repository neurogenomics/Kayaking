package com.kayak_backend.services.route

import com.kayak_backend.getConf
import com.kayak_backend.getWaveService
import com.kayak_backend.getWindService
import com.kayak_backend.services.waves.WaveService
import com.kayak_backend.services.wind.WindService
import java.time.LocalDateTime
import java.time.ZoneOffset

class LegDifficulty {
    // maps leg to (time, difficulty)
    private val difficultyCache = mutableMapOf<Leg, MutableMap<Long, Int>>()

    fun getDifficulty(
        route: Route,
        windService: WindService = getWindService(getConf("./config.yaml")),
        waveService: WaveService = getWaveService(getConf("./config.yaml")),
        dateTime: LocalDateTime,
    ): Int {
        return getLegDifficulty(route.locations, windService, waveService, dateTime)
    }

    private fun getLegDifficulty(
        leg: Leg,
        windService: WindService,
        waveService: WaveService,
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
        if (leg is Leg.SingleLeg) {
            return 0
        } else {
            return 1
        }
    }
}
