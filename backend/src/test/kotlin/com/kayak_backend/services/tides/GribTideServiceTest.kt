package com.kayak_backend.services.tides

import com.kayak_backend.gribReader.GribReader
import com.kayak_backend.models.Location
import com.kayak_backend.models.TideInfo
import com.kayak_backend.testTideGribConf
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class GribTideServiceTest {


    private val gribReader = mockk<GribReader>()
    private val gribTideFetcher = GribTideFetcher(testTideGribConf, gribReader)
    private val mockResponse = Pair<Double, Double>(1.0, -1.0)
    @Test
    fun gribTideServiceTestCallsFileReader() {
        val lat = 5.0
        val lon = 4.0
        val time = LocalDateTime.of(2024, 2, 1, 12, 0)
        every {gribReader.getVarPair(lat, lon, time, any(), any(), any(), any(), any(), any())} returns mockResponse
        assertEquals(TideInfo(1.0, -1.0), gribTideFetcher.getTide(Location(lat, lon), time))
    }

}