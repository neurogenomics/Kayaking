package com.kayak_backend.services.dangerousWindWarning

import com.kayak_backend.models.Location
import com.kayak_backend.models.WindInfo
import com.kayak_backend.services.wind.WindService
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DangerousWindServiceTest {
    // Please make sure this matches BAD_WIND_MAGNITUDE_LIMIT in DangerousWindService so the tests don't break updating that value
    private val badWindMagnitude = 2.0
    // These tests are based on the fact BAD_WIND_ANGLE_LIMIT = 90 in DangerousWindService

    private val loc1 = Location(0.0, 0.0)
    private val loc2 = Location(1.0, 0.0)
    private val loc3 = Location(3.0, 0.0)
    private val time = LocalDateTime.now()

    private val windServiceMock = mockk<WindService>()
    private val seaBearings: Map<Location, Double> =
        mapOf(
            (loc1 to 0.0),
            (loc2 to 350.0),
        )
    private val dangerousWindService = DangerousWindService(windServiceMock, seaBearings)

    @Test
    fun perpendicularBearingAndWindMarkedGood() {
        every {
            windServiceMock.getWind(loc1, any())
        } returns WindInfo(badWindMagnitude * 3, 0.0)

        val result = dangerousWindService.findBadWinds(listOf(loc1), listOf(0), time)
        assertEquals(1, result.size)
        assertFalse(result[0])
    }

    @Test
    fun obtuseBearingAndWindMarkedGood() {
        every {
            windServiceMock.getWind(loc1, any())
        } returns WindInfo(1.0, -badWindMagnitude * 3)

        val result = dangerousWindService.findBadWinds(listOf(loc1), listOf(0), time)
        assertFalse(result[0])
    }

    @Test
    fun equalBearingAndWindMarkedBad() {
        every {
            windServiceMock.getWind(loc1, any())
        } returns WindInfo(0.0, badWindMagnitude * 3)

        val result = dangerousWindService.findBadWinds(listOf(loc1), listOf(0), time)
        assert(result[0])
    }

    @Test
    fun acuteBearingAndWindMarkedBad() {
        every {
            windServiceMock.getWind(loc1, any())
        } returns WindInfo(badWindMagnitude * 3, badWindMagnitude * 3)

        val result = dangerousWindService.findBadWinds(listOf(loc1), listOf(0), time)
        assert(result[0])
    }

    @Test
    fun markingGoodAndBadZonesWrapsAroundAt360() {
        every {
            windServiceMock.getWind(loc2, any())
        } returns WindInfo(badWindMagnitude * 3, badWindMagnitude * 3)

        val result = dangerousWindService.findBadWinds(listOf(loc2), listOf(0), time)
        assert(result[0])
    }

    @Test
    fun ifLocationsIsEmptyReturnEmptyList() {
        val result = dangerousWindService.findBadWinds(listOf(), listOf(), time)
        assert(result.isEmpty())
    }

    @Test
    fun weakOutToSeaWindMarkedGood() {
        every {
            windServiceMock.getWind(loc1, any())
        } returns WindInfo(0.0, badWindMagnitude / 2)

        val result = dangerousWindService.findBadWinds(listOf(loc1), listOf(0), time)
        assertFalse(result[0])
    }

    @Test
    fun locationNotInSeaBearingsAssumedGood() {
        every {
            windServiceMock.getWind(loc3, any())
        } returns WindInfo(badWindMagnitude * 3, 0.0)

        val result = dangerousWindService.findBadWinds(listOf(loc3), listOf(0), time)
        assertEquals(1, result.size)
        assertFalse(result[0])
    }

    @Test
    fun returnsListOfBooleansMatchingIndexesToLocationsInput() {
        val expected = listOf(false, true, false)

        every {
            windServiceMock.getWind(loc1, any())
        } returns WindInfo(0.0, badWindMagnitude / 2)
        every {
            windServiceMock.getWind(loc2, any())
        } returns WindInfo(0.0, badWindMagnitude * 3)
        every {
            windServiceMock.getWind(loc3, any())
        } returns WindInfo(badWindMagnitude * 3, 0.0)

        val result = dangerousWindService.findBadWinds(listOf(loc1, loc2, loc3), listOf(0, 1, 2), time)
        assertEquals(3, result.size)
        for (i in expected.indices) {
            assertEquals(expected[i], result[i])
        }
    }
}
