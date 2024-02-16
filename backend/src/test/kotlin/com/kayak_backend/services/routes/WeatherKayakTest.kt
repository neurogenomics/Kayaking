package com.kayak_backend.services.routes

import com.kayak_backend.models.*
import com.kayak_backend.services.route.kayak.WeatherKayak
import com.kayak_backend.services.tides.TideService
import com.kayak_backend.services.wind.WindService
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class WeatherKayakTest {
    private val windServiceMock: WindService = mockk<WindService>()
    private val tideServiceMock: TideService = mockk<TideService>()
    private val kayak: WeatherKayak = WeatherKayak(windServiceMock, tideServiceMock)

    // make sure these match those in WeatherKayak.kt
    private val windMult = 0.2
    private val tideMult = 0.5

    // to allow for small errors with sin/cos/PI
    private val roundingAllowance = 0.005

    private val loc = Location(0.0, 0.0)
    private val date = LocalDateTime.of(2024, 10, 10, 10, 10)
    private val kayakerSpeed = 3.0

    @Test
    fun weatherOppositeToGoalBearing() {
        val bearing = 90.0

        every {
            windServiceMock.getWind(loc, date)
        } returns WindInfo(-1.0 / windMult, 0.0 / windMult)
        every {
            tideServiceMock.getTide(loc, date)
        } returns TideInfo(0.0 / tideMult, 0.0 / tideMult)

        assertEquals(kayak.getSpeed(date, loc, bearing, kayakerSpeed), 2.0)
    }

    @Test
    fun weatherParallelToGoalBearing() {
        val bearing = 90.0

        every {
            windServiceMock.getWind(loc, date)
        } returns WindInfo(1.0 / windMult, 0.0 / windMult)
        every {
            tideServiceMock.getTide(loc, date)
        } returns TideInfo(0.0 / tideMult, 0.0 / tideMult)

        assertEquals(kayak.getSpeed(date, loc, bearing, kayakerSpeed), 4.0)
    }

    @Test
    fun weatherPerpendicularToGoalBearing() {
        val bearing = 45.0

        every {
            windServiceMock.getWind(loc, date)
        } returns WindInfo(1.0 / (sqrt(2.0) * windMult), -1.0 / (sqrt(2.0) * windMult))
        every {
            tideServiceMock.getTide(loc, date)
        } returns TideInfo(0.0 / tideMult, 0.0 / tideMult)

        val expected = sqrt(8.0)
        assert(abs(kayak.getSpeed(date, loc, bearing, kayakerSpeed) - expected) < roundingAllowance)
    }

    @Test
    fun weatherAcuteToGoalBearing() {
        val bearing = 45.0

        every {
            windServiceMock.getWind(loc, date)
        } returns WindInfo(0.0 / windMult, 1.0 / windMult)
        every {
            tideServiceMock.getTide(loc, date)
        } returns TideInfo(0.0 / tideMult, 0.0 / tideMult)

        val expected = 3.623
        assert(abs(kayak.getSpeed(date, loc, bearing, kayakerSpeed) - expected) < roundingAllowance)
    }

    @Test
    fun weatherObtuseToGoalBearing() {
        val bearing = 0.0

        every {
            windServiceMock.getWind(loc, date)
        } returns WindInfo(1.0 / (sqrt(2.0) * windMult), 0.0 / windMult)
        every {
            tideServiceMock.getTide(loc, date)
        } returns TideInfo(0.0 / tideMult, -1.0 / (sqrt(2.0) * tideMult))

        val expected = 2.208
        assert(abs(kayak.getSpeed(date, loc, bearing, kayakerSpeed) - expected) < roundingAllowance)
    }

    @Test
    fun weatherReflexToGoalBearingUnder270() {
        val bearing = 0.0

        every {
            windServiceMock.getWind(loc, date)
        } returns WindInfo(0.0 / windMult, 0.0 / windMult)
        every {
            tideServiceMock.getTide(loc, date)
        } returns TideInfo(-1.0 / (sqrt(2.0) * tideMult), -1.0 / (sqrt(2.0) * tideMult))

        val expected = 2.208
        assert(abs(kayak.getSpeed(date, loc, bearing, kayakerSpeed) - expected) < roundingAllowance)
    }

    @Test
    fun weatherReflexToGoalBearingOver270() {
        val bearing = 30.0

        every {
            windServiceMock.getWind(loc, date)
        } returns WindInfo(0.0 / windMult, 0.0 / windMult)
        every {
            tideServiceMock.getTide(loc, date)
        } returns TideInfo(-1.0 / (sqrt(2.0) * tideMult), 1.0 / (sqrt(2.0) * tideMult))

        val expected = 3.099
        assert(abs(kayak.getSpeed(date, loc, bearing, kayakerSpeed) - expected) < roundingAllowance)
    }
}
