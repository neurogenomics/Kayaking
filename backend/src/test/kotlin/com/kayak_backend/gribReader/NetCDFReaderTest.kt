package com.kayak_backend.gribReader

import com.kayak_backend.models.Range
import com.kayak_backend.testTideGribConf
import com.kayak_backend.testWindGribConf
import org.junit.Assert
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NetCDFReaderTest {
    private val netCDFGribReader = NetCDFGribReader()

    @Test
    fun singleVarReadsValuesCorrectly() {
        val expectedAnswer = 3.94999
        val latTest = 50.6418
        val lonTest = -1.32174
        val timeTest: LocalDateTime = LocalDateTime.of(2024, 1, 25, 14, 0)
        val value =
            netCDFGribReader.getSingleVar(
                latTest,
                lonTest,
                timeTest,
                testWindGribConf.uWindVarName,
                testWindGribConf.filePath,
            )
        assertEquals(expectedAnswer, value, 1e-3)
    }

    @Test
    fun varPairReadsValuesCorrectly() {
        val expectedAnswer = Pair(3.94999, 4.73)
        val latTest = 50.6418
        val lonTest = -1.32174
        val timeTest: LocalDateTime = LocalDateTime.of(2024, 1, 25, 14, 0)
        val value =
            netCDFGribReader.getVarPair(
                latTest,
                lonTest,
                timeTest,
                testWindGribConf.uWindVarName,
                testWindGribConf.vWindVarName,
                testWindGribConf.filePath,
            )
        assertEquals(expectedAnswer.first, value.first, 1e-3)
        assertEquals(expectedAnswer.second, value.second, 1e-3)
    }

    @Test
    fun varGridReadsValuesCorrectly() {
        val latRange = Range(50.60582, 50.64034)
        val lonRange = Range(-1.16709, -1.1337)
        val timeTest: LocalDateTime = LocalDateTime.of(2024, 1, 25, 14, 0)

        val expected = listOf(listOf(-0.533999, -0.82), listOf(-0.21, -0.467999))
        val value =
            netCDFGribReader.getVarGrid(
                latRange,
                lonRange,
                timeTest,
                testTideGribConf.uTideVarName,
                testTideGribConf.filePath,
            )
        assertEquals(expected.size, value.first.size)
        assertEquals(expected[0].size, value.first[0].size)
        expected.forEachIndexed { i, arr ->
            arr.forEachIndexed { j, pt ->
                assertEquals(pt, value.first[i][j], 1e-3)
            }
        }
        assertEquals(expected.size, value.second.size)
        assertEquals(expected[0].size, value.third.size)
    }

    @Test
    fun varInterpolatesCorrectly() {
        val neighbors = listOf(-0.697, -0.742)
        val latTest = 50.7499
        val lonTest = -1.3053
        val timeTest: LocalDateTime = LocalDateTime.of(2024, 1, 25, 14, 0)
        val value =
            netCDFGribReader.getSingleVar(
                latTest,
                lonTest,
                timeTest,
                testTideGribConf.uTideVarName,
                testTideGribConf.filePath,
            )
        Assert.assertEquals(value, neighbors.average(), 1e-3)
    }

    @Test
    fun rejectsOutOfBoundTimes() {
        val latTest = 50.7499
        val lonTest = -1.3053
        val timeTestEarly: LocalDateTime = LocalDateTime.of(2024, 1, 25, 11, 0)
        val timeTestLate: LocalDateTime = LocalDateTime.of(2024, 1, 27, 13, 0)
        assertFailsWith<GribIndexError> {
            netCDFGribReader.getSingleVar(
                latTest,
                lonTest,
                timeTestEarly,
                testWindGribConf.uWindVarName,
                testWindGribConf.filePath,
            )
        }

        assertFailsWith<GribIndexError> {
            netCDFGribReader.getSingleVar(
                latTest,
                lonTest,
                timeTestLate,
                testWindGribConf.uWindVarName,
                testWindGribConf.filePath,
            )
        }
    }

    @Test
    fun rejectsOutOfBoundLatitude() {
        val latTestNorth = 51.084
        val latTestSouth = 48.366
        val lonTest = -1.3053
        val timeTest: LocalDateTime = LocalDateTime.of(2024, 1, 25, 14, 0)
        assertFailsWith<GribIndexError> {
            netCDFGribReader.getSingleVar(
                latTestNorth,
                lonTest,
                timeTest,
                testWindGribConf.uWindVarName,
                testWindGribConf.filePath,
            )
        }

        assertFailsWith<GribIndexError> {
            netCDFGribReader.getSingleVar(
                latTestSouth,
                lonTest,
                timeTest,
                testWindGribConf.uWindVarName,
                testWindGribConf.filePath,
            )
        }
    }

    @Test
    fun rejectsOutOfBoundLongitude() {
        val latTest = 50.7499
        val lonTestWest = -3.0
        val lonTestEast = 0.313
        val timeTest: LocalDateTime = LocalDateTime.of(2024, 1, 25, 14, 0)
        assertFailsWith<GribIndexError> {
            netCDFGribReader.getSingleVar(
                latTest,
                lonTestWest,
                timeTest,
                testWindGribConf.uWindVarName,
                testWindGribConf.filePath,
            )
        }

        assertFailsWith<GribIndexError> {
            netCDFGribReader.getSingleVar(
                latTest,
                lonTestEast,
                timeTest,
                testWindGribConf.uWindVarName,
                testWindGribConf.filePath,
            )
        }
    }

    @Test
    fun returnsCorrectTimeRange() {
        val startTime = LocalDateTime.of(2024, 1, 25, 12, 0, 0)
        val length = 49
        val timeRange =
            netCDFGribReader.getTimeRange(
                testTideGribConf.filePath,
            )
        assertEquals(length, timeRange.size)
        assertEquals(startTime, timeRange[0])
        assertEquals(startTime.plusHours(length.toLong() - 1), timeRange.last())
    }

    @Test
    fun returnsCorrectDayData() {
        val date = LocalDate.of(2024, 1, 26)
        val lat = 50.593
        val lon = -1.30
        val data =
            netCDFGribReader.getDayData(
                lat,
                lon,
                date,
                testTideGribConf.uTideVarName,
                testTideGribConf.vTideVarName,
                testTideGribConf.filePath,
            )
        assertEquals(24, data.keys.size)

        val expectedData =
            mapOf(
                LocalTime.of(0, 0) to Pair(-0.592, 0.115),
                LocalTime.of(1, 0) to Pair(-1.041, 0.184),
                LocalTime.of(2, 0) to Pair(-1.07, 0.177),
                LocalTime.of(3, 0) to Pair(-0.83, 0.129),
                LocalTime.of(4, 0) to Pair(-0.406, 0.0523),
                LocalTime.of(5, 0) to Pair(0.221, -0.058),
                LocalTime.of(6, 0) to Pair(0.913, -0.177),
                LocalTime.of(7, 0) to Pair(1.387, -0.253),
                LocalTime.of(8, 0) to Pair(1.526, -0.27),
                LocalTime.of(9, 0) to Pair(1.367, -0.238),
                LocalTime.of(10, 0) to Pair(0.935, -0.159),
                LocalTime.of(11, 0) to Pair(0.268, -0.038),
                LocalTime.of(12, 0) to Pair(-0.502, 0.099),
                LocalTime.of(13, 0) to Pair(-1.053, 0.188),
                LocalTime.of(14, 0) to Pair(-1.184, 0.199),
                LocalTime.of(15, 0) to Pair(-1.024, 0.164),
                LocalTime.of(16, 0) to Pair(-0.697, 0.105),
                LocalTime.of(17, 0) to Pair(-0.194, 0.016),
                LocalTime.of(18, 0) to Pair(0.476, -0.102),
                LocalTime.of(19, 0) to Pair(1.069, -0.201),
                LocalTime.of(20, 0) to Pair(1.352, -0.242),
                LocalTime.of(21, 0) to Pair(1.326, -0.231),
                LocalTime.of(22, 0) to Pair(1.028, -0.175),
                LocalTime.of(23, 0) to Pair(0.453, -0.07),
            )

        for ((key, value) in expectedData) {
            assert(key in data)
            assertEquals(value.first, data[key]!!.first, 1e-3)
            assertEquals(value.second, data[key]!!.second, 1e-3)
        }
    }

    @Test
    fun returnsIncompleteDayData() {
        val date = LocalDate.of(2024, 1, 25)
        val lat = 50.593
        val lon = -1.30
        val data =
            netCDFGribReader.getDayData(
                lat,
                lon,
                date,
                testTideGribConf.uTideVarName,
                testTideGribConf.vTideVarName,
                testTideGribConf.filePath,
            )
        assertEquals(12, data.keys.size)
        assertEquals(-0.903, data[LocalTime.of(12, 0)]!!.first, 1e-3)
        assertEquals(0.145, data[LocalTime.of(23, 0)]!!.first, 1e-3)
    }

    @Test
    fun interpolatesDayDataCorrectly() {
        val date = LocalDate.of(2024, 1, 26)
        val lat = 50.593
        val lon = -1.27
        val data =
            netCDFGribReader.getDayData(
                lat,
                lon,
                date,
                testTideGribConf.uTideVarName,
                testTideGribConf.vTideVarName,
                testTideGribConf.filePath,
            )

        assertEquals(24, data.keys.size)
        assert(data.values.none { it.first.isNaN() || it.second.isNaN() })

        val eightval1 = 2.263
        val eightval2 = 1.526
        val average = (eightval2 + eightval1) / 2
        assertEquals(average, data[LocalTime.of(8, 0)]!!.first, 1e-3)
    }
}
