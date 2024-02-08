package com.kayak_backend.gribReader

import com.kayak_backend.models.Range
import com.kayak_backend.testTideGribConf
import com.kayak_backend.testWindGribConf
import org.junit.Assert
import java.time.LocalDateTime
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
                testWindGribConf.latVarName,
                testWindGribConf.lonVarName,
                testWindGribConf.timeVarName,
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
                testWindGribConf.latVarName,
                testWindGribConf.lonVarName,
                testWindGribConf.timeVarName,
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
                testTideGribConf.latVarName,
                testTideGribConf.lonVarName,
                testTideGribConf.timeVarName,
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
                testTideGribConf.latVarName,
                testTideGribConf.lonVarName,
                testTideGribConf.timeVarName,
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
                testWindGribConf.latVarName,
                testWindGribConf.lonVarName,
                testWindGribConf.timeVarName,
            )
        }

        assertFailsWith<GribIndexError> {
            netCDFGribReader.getSingleVar(
                latTest,
                lonTest,
                timeTestLate,
                testWindGribConf.uWindVarName,
                testWindGribConf.filePath,
                testWindGribConf.latVarName,
                testWindGribConf.lonVarName,
                testWindGribConf.timeVarName,
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
                testWindGribConf.latVarName,
                testWindGribConf.lonVarName,
                testWindGribConf.timeVarName,
            )
        }

        assertFailsWith<GribIndexError> {
            netCDFGribReader.getSingleVar(
                latTestSouth,
                lonTest,
                timeTest,
                testWindGribConf.uWindVarName,
                testWindGribConf.filePath,
                testWindGribConf.latVarName,
                testWindGribConf.lonVarName,
                testWindGribConf.timeVarName,
            )
        }
    }

    @Test
    fun rejectsOutOfBoundLongitude() {
        val latTest = 50.7499
        val lonTestWest = -2.944
        val lonTestEast = 0.313
        val timeTest: LocalDateTime = LocalDateTime.of(2024, 1, 25, 14, 0)
        assertFailsWith<GribIndexError> {
            netCDFGribReader.getSingleVar(
                latTest,
                lonTestWest,
                timeTest,
                testWindGribConf.uWindVarName,
                testWindGribConf.filePath,
                testWindGribConf.latVarName,
                testWindGribConf.lonVarName,
                testWindGribConf.timeVarName,
            )
        }

        assertFailsWith<GribIndexError> {
            netCDFGribReader.getSingleVar(
                latTest,
                lonTestEast,
                timeTest,
                testWindGribConf.uWindVarName,
                testWindGribConf.filePath,
                testWindGribConf.latVarName,
                testWindGribConf.lonVarName,
                testWindGribConf.timeVarName,
            )
        }
    }
}
