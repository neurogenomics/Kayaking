package com.kayak_backend.services.timeRange

import com.kayak_backend.gribReader.GribReader
import com.kayak_backend.services.times.GribTimeService
import com.kayak_backend.testGribTimeServiceConf
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class GribTimeServiceTest {
    private val gribReaderMock = mockk<GribReader>()
    private val datetime1 = LocalDateTime.of(2024, 12, 10, 12, 0)
    private val datetime2 = LocalDateTime.of(2024, 12, 10, 13, 0)
    private val datetime3 = LocalDateTime.of(2024, 12, 10, 14, 0)
    private val datetime4 = LocalDateTime.of(2024, 12, 10, 15, 0)

    @Test
    fun serviceCallsGribReader() {
        every { gribReaderMock.getTimeRange(testGribTimeServiceConf.filePaths[0]) } returns
            listOf(
                datetime1,
                datetime2,
                datetime3,
            )
        every { gribReaderMock.getTimeRange(testGribTimeServiceConf.filePaths[1]) } returns
            listOf(
                datetime2,
                datetime3,
                datetime4,
            )
        val timeService = GribTimeService(gribReaderMock, testGribTimeServiceConf.filePaths)
        assertEquals(listOf(datetime2, datetime3), timeService.getTimes())
    }
}
