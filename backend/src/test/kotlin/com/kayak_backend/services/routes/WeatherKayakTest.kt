package com.kayak_backend.services.routes

import com.kayak_backend.models.*
import com.kayak_backend.services.route.WeatherKayak
import com.kayak_backend.services.tides.TideService
import com.kayak_backend.services.wind.WindService
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class WeatherKayakTest {
    private val windServiceMock: WindService = mockk<WindService>()
    private val tideServiceMock: TideService = mockk<TideService>()
    private val kayak: WeatherKayak = WeatherKayak(windServiceMock, tideServiceMock)

    // may have to change once have factors for wind/tide
    @Test
    fun weatherOppositeToGoalBearing() {
        val loc = Location(0.0, 0.0)
        val date = LocalDateTime.of(2024, 10, 10, 10, 10)
        val bearing = 90.0

        every {
            windServiceMock.getWind(loc, date)
        } returns WindInfo(-1.0, 0.0)
        every {
            tideServiceMock.getTide(loc, date)
        } returns TideInfo(0.0, 0.0)

        println("result is ${kayak.getSpeed(date,loc,bearing)}")

        assertEquals(kayak.getSpeed(date, loc, bearing), 2.0)
    }

    @Test
    fun weatherParallelToGoalBearing() {
        val loc = Location(0.0, 0.0)
        val date = LocalDateTime.of(2024, 10, 10, 10, 10)
        val bearing = 90.0

        every {
            windServiceMock.getWind(loc, date)
        } returns WindInfo(1.0, 0.0)
        every {
            tideServiceMock.getTide(loc, date)
        } returns TideInfo(0.0, 0.0)

        println("result is ${kayak.getSpeed(date,loc,bearing)}")

        assertEquals(kayak.getSpeed(date, loc, bearing), 4.0)
    }

    @Test
    fun weatherPerpendicularToGoalBearing() {
        val loc = Location(0.0, 0.0)
        val date = LocalDateTime.of(2024, 10, 10, 10, 10)
        val bearing = 0.0

        every {
            windServiceMock.getWind(loc, date)
        } returns WindInfo(-1.0, 0.0)
        every {
            tideServiceMock.getTide(loc, date)
        } returns TideInfo(0.0, 0.0)

        println(kayak.getSpeed(date, loc, bearing))
        // what should result be
        // assertEquals(kayak.getSpeed(date,loc,bearing), )
    }
}
