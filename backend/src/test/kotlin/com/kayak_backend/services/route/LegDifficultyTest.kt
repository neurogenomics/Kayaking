package com.kayak_backend.services.route

import com.kayak_backend.models.Location
import com.kayak_backend.models.WaveInfo
import com.kayak_backend.models.WindInfo
import com.kayak_backend.services.waves.WaveService
import com.kayak_backend.services.wind.WindService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class LegDifficultyTest {
    private val waveMock = mockk<WaveService>()
    private val windMock = mockk<WindService>()
    private val legDifficulty = LegDifficulty(windMock, waveMock)
    private val dateTime = LocalDateTime.now()

    private val loc1 = Location(0.0, 0.0)
    private val loc2 = Location(0.0, 1.0)
    private val loc3 = Location(0.0, 2.0)
    private val loc4 = Location(0.0, 3.0)
    private val loc5 = Location(0.0, 4.0)

    // midpoint is loc2
    private val leg1 = Leg.SingleLeg(loc1, loc3)

    // midpoint is loc3
    private val leg2 = Leg.SingleLeg(loc2, loc4)

    // midpoint is loc4
    private val leg3 = Leg.SingleLeg(loc3, loc5)

    private val multipleLeg1 =
        Leg.MultipleLegs(listOf(leg2, leg3))

    private val multipleLeg2 =
        Leg.MultipleLegs(listOf(leg1, multipleLeg1))

    @Test
    fun calculatesDifficultyForSingleLeg() {
        val route = Route("name", 1.0, leg1)
        every { windMock.getWind(loc2, any()) } returns WindInfo(1.0, 1.0)
        every { waveMock.getWave(loc2, any()) } returns WaveInfo(1.0, 1.0)

        val result = legDifficulty.getDifficulty(route, dateTime, listOf(0, 1))
        // wind = 1, wave = 3
        assertEquals(3, result)
    }

    @Test
    fun retrievesValueFromCacheIfAlreadyThere() {
        val route = Route("name", 1.0, leg1)
        every { windMock.getWind(loc2, any()) } returns WindInfo(1.0, 1.0)
        every { waveMock.getWave(loc2, any()) } returns WaveInfo(1.0, 1.0)

        legDifficulty.getDifficulty(route, dateTime, listOf(0, 1))
        legDifficulty.getDifficulty(route, dateTime, listOf(0, 1))

        verify(exactly = 1) { windMock.getWind(loc2, any()) }
        verify(exactly = 1) { waveMock.getWave(loc2, any()) }
    }

    @Test
    fun recalculatesSameLegIfAtDifferentTime() {
        val route = Route("name", 1.0, leg1)
        every { windMock.getWind(loc2, any()) } returns WindInfo(1.0, 1.0)
        every { waveMock.getWave(loc2, any()) } returns WaveInfo(1.0, 1.0)

        legDifficulty.getDifficulty(route, dateTime, listOf(0, 1))
        legDifficulty.getDifficulty(route, dateTime.plusSeconds(10), listOf(0, 1))

        verify(exactly = 2) { windMock.getWind(loc2, any()) }
        verify(exactly = 2) { waveMock.getWave(loc2, any()) }
    }

    @Test
    fun whenWaveHeightGreaterThanWaveLevelsReturnsLevel12() {
        val route = Route("name", 1.0, leg1)
        every { windMock.getWind(loc2, any()) } returns WindInfo(1.0, 1.0)
        every { waveMock.getWave(loc2, any()) } returns WaveInfo(20.0, 1.0)

        val result = legDifficulty.getDifficulty(route, dateTime, listOf(0, 1))
        // wind = 1, wave = 12
        assertEquals(12, result)
    }

    @Test
    fun forMultipleLegReturnsMaxDifficultyOfLegsInside() {
        val route = Route("name", 1.0, multipleLeg2)
        println(dateTime)
        every { windMock.getWind(loc2, dateTime) } returns WindInfo(1.0, 1.0)
        every { waveMock.getWave(loc2, dateTime) } returns WaveInfo(1.0, 1.0)

        every { windMock.getWind(loc3, dateTime.plusSeconds(1)) } returns WindInfo(2.0, 2.0)
        every { waveMock.getWave(loc3, dateTime.plusSeconds(1)) } returns WaveInfo(5.0, 5.0)

        every { windMock.getWind(loc4, dateTime.plusSeconds(2)) } returns WindInfo(5.0, 5.0)
        every { waveMock.getWave(loc4, dateTime.plusSeconds(2)) } returns WaveInfo(2.0, 2.0)

        println(dateTime)
        val result = legDifficulty.getDifficulty(route, dateTime, listOf(0, 1, 2, 3))
        // wind = 3, wave = 7
        assertEquals(7, result)
    }
}
