package com.kayak_backend.services.timeRange

import com.kayak_backend.gribReader.GribReader
import com.kayak_backend.services.times.GribTimeService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyAll
import kotlin.test.Test

class GribTimeServiceTest {
    private val gribReaderMock = mockk<GribReader>()
    private val filePath = "filepath"

    @Test
    fun serviceCallsGribReader() {
        every { gribReaderMock.getTimeRange(filePath) } returns emptyList()

        val timeService = GribTimeService(gribReaderMock, filePath)
        timeService.getTimes()
        verifyAll {
            gribReaderMock.getTimeRange(filePath)
        }
    }
}
