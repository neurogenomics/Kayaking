package com.kayak_backend.interpolator

import com.kayak_backend.models.Range
import org.junit.Test
import kotlin.test.assertEquals

class SimpleInterpolatorTest {
    private val interpolator = SimpleInterpolator()

    @Test
    fun interpolatesSimpleListCorrectly() {
        val data = listOf(listOf(0.0, 1.0), listOf(1.0, 2.0))
        val indices = Pair(listOf(1.0, 2.0), listOf(1.0, 2.0))
        val ranges = Pair(Range(0.5, 2.5), Range(0.5, 2.5))
        val resolutions = Pair(0.5, 0.25)

        val expectedResult =
            listOf(
                listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
                listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
                listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
                listOf(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0),
                listOf(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0),
            )
        val expectedIndexes =
            Pair(listOf(0.5, 1.0, 1.5, 2.0, 2.5), listOf(0.5, 0.75, 1.0, 1.25, 1.5, 1.75, 2.0, 2.25, 2.5))
        val (res, newIndex1, newIndex2) = interpolator.interpolate(data, indices, ranges, resolutions)
        assertEquals(expectedIndexes.first, newIndex1)
        assertEquals(expectedIndexes.second, newIndex2)
        assertEquals(expectedResult.size, res.size)
        assertEquals(expectedResult[0].size, res[0].size)
        assertEquals(expectedIndexes.first, newIndex1)

        expectedResult.forEachIndexed { i, list ->
            assertEquals(list, res[i])
        }
    }
}
