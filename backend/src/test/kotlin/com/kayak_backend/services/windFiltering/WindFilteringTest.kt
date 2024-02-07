package com.kayak_backend.services.windFiltering

import com.kayak_backend.models.Location
import com.kayak_backend.models.WindInfo
import com.kayak_backend.services.seaBearing.SeaBearingInfo
import com.kayak_backend.services.seaBearing.SeaBearingService
import com.kayak_backend.services.wind.WindService
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import java.time.LocalDateTime

class WindFilteringTest {

    private val windServiceMock = mockk<WindService>()
    private val seaBearingMock = mockk<SeaBearingService>()
    private val windFiltering = WindFiltering(windServiceMock, seaBearingMock)

    //TODO will need to update when sorted out in WindFiltering
    private val dateTime = LocalDateTime.of(2024,1,25,14,0)

    @Test
    fun perpendicularBearingAndWindMarkedGood () {

        every { seaBearingMock.getSeaBearings() } returns listOf(
            SeaBearingInfo(0.0, Location(0.0,0.0))
        )
        every { windServiceMock.getWind(Location(0.0,0.0), dateTime)
        } returns WindInfo(1.0,0.0)

        val result = windFiltering.classifyAreas()
        assert(!result[0].bad)
    }

    @Test
    fun obtuseBearingAndWindMarkedGood () {

        every { seaBearingMock.getSeaBearings() } returns listOf(
            SeaBearingInfo(0.0, Location(0.0,0.0))
        )
        every { windServiceMock.getWind(Location(0.0,0.0), dateTime)
        } returns WindInfo(1.0,-1.0)

        val result = windFiltering.classifyAreas()
        assert(!result[0].bad)
    }

    @Test
    fun equalBearingAndWindMarkedBad () {
        every { seaBearingMock.getSeaBearings() } returns listOf(
            SeaBearingInfo(0.0, Location(0.0,0.0))
        )
        every { windServiceMock.getWind(Location(0.0,0.0), dateTime)
        } returns WindInfo(0.0,1.0)

        val result = windFiltering.classifyAreas()
        assert(result[0].bad)
    }

    @Test
    fun acuteBearingAndWindMarkedBad () {
        every { seaBearingMock.getSeaBearings() } returns listOf(
            SeaBearingInfo(0.0, Location(0.0,0.0))
        )
        every { windServiceMock.getWind(Location(0.0,0.0), dateTime)
        } returns WindInfo(1.0,1.0)

        val result = windFiltering.classifyAreas()
        assert(result[0].bad)
    }

    @Test
    fun markingGoodAndBadZonesWrapsAroundAt360 () {
        every { seaBearingMock.getSeaBearings() } returns listOf(
            SeaBearingInfo(350.0, Location(0.0,0.0))
        )
        every { windServiceMock.getWind(Location(0.0,0.0), dateTime)
        } returns WindInfo(1.0,1.0)

        val result = windFiltering.classifyAreas()
        assert(result[0].bad)
    }

    @Test
    fun ifSeaBearingServiceReturnsNoInfoReturnEmptyList () {
        every { seaBearingMock.getSeaBearings() } returns listOf()

        val result = windFiltering.classifyAreas()
        assert(result.isEmpty())
    }
}