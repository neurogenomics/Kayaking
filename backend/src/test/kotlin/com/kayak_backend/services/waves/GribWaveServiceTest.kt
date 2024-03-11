package com.kayak_backend.services.waves

import com.kayak_backend.gribReader.GribReader
import com.kayak_backend.interpolator.Interpolator
import com.kayak_backend.models.Location
import com.kayak_backend.models.Range
import com.kayak_backend.models.WaveGrid
import com.kayak_backend.models.WaveInfo
import com.kayak_backend.testWaveGribConf
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class GribWaveServiceTest {
    private val gribReader = mockk<GribReader>()
    private val interpolator = mockk<Interpolator>()
    private val gribWaveFetcher = GribWaveFetcher(testWaveGribConf, gribReader, interpolator)
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
        WaveGrid(
            listOf(listOf(WaveInfo(1.0, 4.0), WaveInfo(1.0, 4.0)), listOf(WaveInfo(2.0, 4.0), WaveInfo(2.0, 4.0))),
            mockLatIndex,
            mockLonIndex,
        )

    @Test
    fun gribWaveServiceTestCallsFileReader() {
        val lat = 5.0
        val lon = 4.0
        val time = LocalDateTime.of(2024, 2, 1, 12, 0)
        every { gribReader.getVarPair(lat, lon, time, any(), any(), any()) } returns mockResponse
        assertEquals(WaveInfo(1.0, -1.0), gribWaveFetcher.getWave(Location(lat, lon), time))
    }

    @Test
    fun gribWaveGridServiceTest() {
        every {
            gribReader.getVarGrid(
                any(),
                any(),
                time,
                testWaveGribConf.waveHeightVarName,
                any(),
            )
        } returns Triple(mockGrid1, mockLatIndex, mockLonIndex)

        every {
            gribReader.getVarGrid(
                any(),
                any(),
                time,
                testWaveGribConf.waveDirectionVarName,
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
        val response = gribWaveFetcher.getWaveGrid(corner1, corner2, time, resolutions)
        assertEquals(expectedGrid, response)
    }

    @Test
    fun waveGridServiceReplacesNaNs() {
        val mockGrid1 = listOf(listOf(1.0, Double.NaN))
        val mockGrid2 = listOf(listOf(Double.NaN, 4.0))
        val mockLatIndex = listOf(50.0)
        val mockLonIndex = listOf(-1.0, -1.1)
        every {
            gribReader.getVarGrid(
                any(),
                any(),
                time,
                testWaveGribConf.waveHeightVarName,
                any(),
            )
        } returns Triple(mockGrid1, mockLatIndex, mockLonIndex)

        every {
            gribReader.getVarGrid(
                any(),
                any(),
                time,
                testWaveGribConf.waveDirectionVarName,
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

        val expectedGrid: List<List<WaveInfo?>> = listOf(listOf(null, null))
        val expectedResponse = WaveGrid(expectedGrid, mockLatIndex, mockLonIndex)
        val response = gribWaveFetcher.getWaveGrid(corner1, corner2, time, resolutions)
        assertEquals(expectedResponse, response)
    }
}
