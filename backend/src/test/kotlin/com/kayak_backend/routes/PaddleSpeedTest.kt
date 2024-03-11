package com.kayak_backend.routes

import com.kayak_backend.services.route.DifficultyLegTimers
import com.kayak_backend.services.route.LegTimer
import com.kayak_backend.services.route.kayak.WeatherKayak
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PaddleSpeedTest {
    @Test
    fun correctlyTranslatesStringsIntoPaddleSpeeds() {
        assertEquals(PaddleSpeed.FAST, parsePaddleSpeed("fast"))
        assertEquals(PaddleSpeed.NORMAL, parsePaddleSpeed("normal"))
        assertEquals(PaddleSpeed.SLOW, parsePaddleSpeed("slow"))
    }

    @Test
    fun ignoresCaseForValidStringsForPaddleSpeed() {
        assertEquals(PaddleSpeed.FAST, parsePaddleSpeed("FAST"))
        assertEquals(PaddleSpeed.NORMAL, parsePaddleSpeed("NORmal"))
        assertEquals(PaddleSpeed.SLOW, parsePaddleSpeed("SlOw"))
    }

    @Test
    fun throwsErrorForInvalidStringForPaddleSpeed() {
        assertFailsWith<IllegalArgumentException> { parsePaddleSpeed("NotASpeed") }
    }

    @Test
    fun convertsPaddleSpeedToLegTimer() {
        val slow = LegTimer(WeatherKayak(1.0))
        val normal = LegTimer(WeatherKayak(2.0))
        val fast = LegTimer(WeatherKayak(3.0))
        val difficultyLegTimers = DifficultyLegTimers(slow, normal, fast)
        assertEquals(slow, paddleSpeedToLegTimer(PaddleSpeed.SLOW, difficultyLegTimers))
        assertEquals(normal, paddleSpeedToLegTimer(PaddleSpeed.NORMAL, difficultyLegTimers))
        assertEquals(fast, paddleSpeedToLegTimer(PaddleSpeed.FAST, difficultyLegTimers))
    }
}
