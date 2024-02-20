package com.kayak_backend.services.tides

import com.kayak_backend.gribReader.GribReader
import com.kayak_backend.interpolator.Interpolator
import com.kayak_backend.models.Location
import com.kayak_backend.models.Range
import com.kayak_backend.models.TideGrid
import com.kayak_backend.models.TideInfo
import com.kayak_backend.testTideGribConf
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class GribTideServiceTest {
    private val gribReader = mockk<GribReader>()
    private val interpolator = mockk<Interpolator>()
    private val gribTideFetcher = GribTideFetcher(testTideGribConf, gribReader, interpolator)
    private val mockResponse = Pair(1.0, -1.0)

    private val mockGrid1 = listOf(listOf(1.0, 1.0), listOf(2.0, 2.0))
    private val mockGrid2 = listOf(listOf(4.0, 4.0), listOf(4.0, 4.0))
    private val mockLatIndex = listOf(50.0, 51.0)
    private val mockLonIndex = listOf(-1.2, -1.1)

    private val corner1 = Location(50.0, -1.2)
    private val corner2 = Location(51.0, -1.1)
    val time: LocalDateTime = LocalDateTime.of(2024, 2, 1, 12, 0)
    private val indices = Pair(listOf(50.0, 51.0), listOf(-1.2, -1.1))
    private val ranges = Pair(Range(50.0, 51.0), Range(-1.2, -1.1))
    private val resolutions = Pair(1.0, 0.1)

    private val expectedGrid =
        TideGrid(
            listOf(listOf(TideInfo(1.0, 4.0), TideInfo(1.0, 4.0)), listOf(TideInfo(2.0, 4.0), TideInfo(2.0, 4.0))),
            mockLatIndex,
            mockLonIndex,
        )

    @Test
    fun gribTideServiceTestCallsFileReader() {
        val lat = 5.0
        val lon = 4.0
        val time = LocalDateTime.of(2024, 2, 1, 12, 0)
        every { gribReader.getVarPair(lat, lon, time, any(), any(), any()) } returns mockResponse
        assertEquals(TideInfo(1.0, -1.0), gribTideFetcher.getTide(Location(lat, lon), time))
    }

    @Test
    fun gribTideGridServiceTest() {
        every {
            gribReader.getVarGrid(
                any(),
                any(),
                time,
                testTideGribConf.uTideVarName,
                any(),
            )
        } returns Triple(mockGrid1, mockLatIndex, mockLonIndex)

        every {
            gribReader.getVarGrid(
                any(),
                any(),
                time,
                testTideGribConf.vTideVarName,
                any(),
            )
        } returns Triple(mockGrid2, mockLatIndex, mockLonIndex)
        every { interpolator.interpolate(mockGrid1, indices, ranges, resolutions) } returns
            Triple(
                mockGrid1,
                mockLatIndex,
                mockLonIndex,
            )
        every { interpolator.interpolate(mockGrid2, indices, ranges, resolutions) } returns
            Triple(
                mockGrid2,
                mockLatIndex,
                mockLonIndex,
            )
        val response = gribTideFetcher.getTideGrid(corner1, corner2, time, resolutions)
        assertEquals(expectedGrid, response)
    }

    @Test
    fun tideGridServiceReplacesNaNs() {
        val mockGrid1 = listOf(listOf(1.0, Double.NaN))
        val mockGrid2 = listOf(listOf(Double.NaN, 4.0))
        val mockLatIndex = listOf(50.0)
        val mockLonIndex = listOf(-1.0, -1.1)
        every {
            gribReader.getVarGrid(
                any(),
                any(),
                time,
                testTideGribConf.uTideVarName,
                any(),
            )
        } returns Triple(mockGrid1, mockLatIndex, mockLonIndex)

        every {
            gribReader.getVarGrid(
                any(),
                any(),
                time,
                testTideGribConf.vTideVarName,
                any(),
            )
        } returns Triple(mockGrid2, mockLatIndex, mockLonIndex)
        every { interpolator.interpolate(mockGrid1, any(), any(), any()) } returns
            Triple(
                mockGrid1,
                mockLatIndex,
                mockLonIndex,
            )
        every { interpolator.interpolate(mockGrid2, any(), any(), any()) } returns
            Triple(
                mockGrid2,
                mockLatIndex,
                mockLonIndex,
            )

        val expectedGrid: List<List<TideInfo?>> = listOf(listOf(null, null))
        val expectedResponse = TideGrid(expectedGrid, mockLatIndex, mockLonIndex)
        val response = gribTideFetcher.getTideGrid(corner1, corner2, time, resolutions)
        assertEquals(expectedResponse, response)
    }
}
