package com.kayak_backend.services.dangerousWindWarning

import com.kayak_backend.models.Location
import com.kayak_backend.models.WindInfo
import com.kayak_backend.services.wind.WindService
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DangerousWindServiceTest {
    // Please make sure this matches BAD_WIND_MAGNITUDE_LIMIT in DangerousWindService so the tests don't break updating that value
    private val badWindMagnitude = 2.0

    // Same with this and BAD_WIND_ANGLE
    private val badWindAngle = 90.0

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
        val magnitude = badWindMagnitude * 3
        val angle = Math.toRadians(seaBearings[loc1]!! + (min(badWindAngle / 2, 89.0)))
        every {
            windServiceMock.getWind(loc1, any())
        } returns WindInfo(magnitude * sin(angle), magnitude * cos(angle))

        val result = dangerousWindService.findBadWinds(listOf(loc1), listOf(0), time)
        assert(result[0])
    }

    // will not test anything new if the BAD_WIND_ANGLE_LIMIT is below 10
    @Test
    fun markingGoodAndBadZonesWrapsAroundAt360() {
        // an angle for the wind that ideally wraps 360 (unless badWindAngle too small)
        val angle = Math.toRadians((seaBearings[loc2]!! + (min(badWindAngle - 1, 360 - seaBearings[loc2]!!))) + 360 % 360)

        val difference = min(abs(seaBearings[loc2]!! - Math.toDegrees(angle)), 360 - abs(seaBearings[loc2]!! - Math.toDegrees(angle)))
        // cos is used to make the wind component parallel to the seaBearing = 3 * badWindMagnitude
        val cos = cos(Math.toRadians(difference))

        val magnitude = badWindMagnitude * 3 / cos

        every {
            windServiceMock.getWind(loc2, any())
        } returns WindInfo(magnitude * sin(angle), magnitude * cos(angle))

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
        every {
            windServiceMock.getWind(loc1, time)
        } returns WindInfo(0.0, badWindMagnitude / 2)
        every {
            windServiceMock.getWind(loc1, time.plusSeconds(1))
        } returns WindInfo(0.0, badWindMagnitude * 3)
        every {
            windServiceMock.getWind(loc3, any())
        } returns WindInfo(badWindMagnitude * 3, 0.0)

        val result = dangerousWindService.findBadWinds(listOf(loc1, loc1, loc3), listOf(0, 1, 2), time)
        assertEquals(3, result.size)
        assertEquals(listOf(false, true, false), result)
    }

    @Test
    fun locationMarkedGoodWhenStrongWindButNotComponentOutToSea() {
        every {
            windServiceMock.getWind(loc1, any())
        } returns WindInfo(badWindMagnitude * 3, badWindMagnitude / 2)

        val result = dangerousWindService.findBadWinds(listOf(loc1), listOf(0), time)
        assertFalse(result[0])
    }

    @Test
    fun markedForTheTimeReachedInRoute() {
        every {
            windServiceMock.getWind(loc1, time)
        } returns WindInfo(0.0, badWindMagnitude / 2)
        every {
            windServiceMock.getWind(loc1, time.plusSeconds(1))
        } returns WindInfo(0.0, badWindMagnitude * 3)

        val result = dangerousWindService.findBadWinds(listOf(loc1, loc1), listOf(0, 1), time)
        assertEquals(listOf(false, true), result)
    }
}
