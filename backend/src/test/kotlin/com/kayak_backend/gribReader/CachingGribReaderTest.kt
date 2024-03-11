package com.kayak_backend.gribReader

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import java.time.LocalDateTime

class CachingGribReaderTest {
    private val baseGribReader = mockk<GribReader>()

    private val time = LocalDateTime.of(2024, 3, 11, 10, 10)

    @Test
    fun singleVarCaches() {
        val cachingGribReader = CachingGribReader(baseGribReader)
        every { baseGribReader.getSingleVar(1.0, 1.0, time, "var", "file") } returns 1.0
        cachingGribReader.getSingleVar(1.0, 1.0, time, "var", "file")
        cachingGribReader.getSingleVar(1.0, 1.0, time, "var", "file")
        verify(exactly = 1) { baseGribReader.getSingleVar(1.0, 1.0, time, "var", "file") }
    }

    @Test
    fun varPairCaches() {
        val cachingGribReader = CachingGribReader(baseGribReader)
        every { baseGribReader.getVarPair(1.0, 1.0, time, "var1", "var2", "file") } returns Pair(1.0, -1.0)
        cachingGribReader.getVarPair(1.0, 1.0, time, "var1", "var2", "file")
        cachingGribReader.getVarPair(1.0, 1.0, time, "var1", "var2", "file")
        verify(exactly = 1) { baseGribReader.getVarPair(1.0, 1.0, time, "var1", "var2", "file") }
    }

    @Test
    fun singleVarCachesNearby() {
        val cachingGribReader = CachingGribReader(baseGribReader)
        every { baseGribReader.getSingleVar(1.0, 1.0, time, "var", "file") } returns 1.0
        cachingGribReader.getSingleVar(1.0, 1.0, time, "var", "file")
        cachingGribReader.getSingleVar(1.001, 1.001, time, "var", "file")
        verify(exactly = 1) { baseGribReader.getSingleVar(1.0, 1.0, time, "var", "file") }
    }
}
