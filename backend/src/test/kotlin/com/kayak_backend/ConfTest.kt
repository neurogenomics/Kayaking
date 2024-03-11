package com.kayak_backend

import com.kayak_backend.gribReader.NetCDFGribReader
import com.kayak_backend.services.tideTimes.TideStationService
import com.kayak_backend.services.tideTimes.TideTimeService
import com.kayak_backend.services.tides.GribTideFetcher
import com.kayak_backend.services.times.GribTimeService
import com.kayak_backend.services.wind.GribWindFetcher
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

val testTideGribConf =
    TideGribConf(
        gribReader = "NetCDFGribReader",
        filePath = "src/test/gribFiles/testGrib.grb",
        uTideVarName = "(.+/)(u[_|-].*current.*)",
        vTideVarName = "(.+/)(v[_|-].*current.*)",
    )

val testWindGribConf =
    WindGribConf(
        gribReader = "NetCDFGribReader",
        filePath = "src/test/gribFiles/testGrib.grb",
        uWindVarName = "(.+/)(u[_|-].*wind.*)",
        vWindVarName = "(.+/)(v[_|-].*wind.*)",
    )

val testWaveGribConf =
    WaveGribConf(
        gribReader = "NetCDFGribReader",
        filePath = "gribFiles/Cherbourg_4km_WRF_WAM.grb",
        waveHeightVarName = "(.+/)(.*combined.*waves.*)",
        waveDirectionVarName = "(.+/)(.*Direction.*swell_waves.*)",
    )

val testGribTimeServiceConf =
    GribTimeServiceConf(
        gribReader = "NetCDFGribReader",
        filePaths = listOf("file1", "file2"),
    )

val testConfig =
    Conf(
        tideService = "grib",
        windService = "grib",
        timeService = "grib",
        waveService = "grib",
        gribFetcher = "OpenSkiron",
        waveGribConf = testWaveGribConf,
        tideGribConf = testTideGribConf,
        windGribConf = testWindGribConf,
        gribTimeServiceConf = testGribTimeServiceConf,
        tideTimeService = "admiralty",
    )

class ConfTest {
    @Test
    fun getConfSerializesYaml() {
        assertEquals(
            getConf("./src/test/kotlin/com/kayak_backend/testConfig.yaml"),
            testConfig,
        )
    }

    @Test
    fun getGribReaderReturnsCorrectType() {
        assertIs<NetCDFGribReader>(getGribReader("NetCDFGribReader"))
    }

    @Test
    fun getGribReaderThrowsUnsupportedError() {
        assertFailsWith<UnsupportedOperationException> { getGribReader("InexistentGribReader") }
    }

    @Test
    fun getTideServiceReturnsCorrectType() {
        assertIs<GribTideFetcher>(getTideService(testConfig))
    }

    @Test
    fun getTideServiceThrowsUnsupportedErrorWithNoGribConfig() {
        val configTideNull = testConfig.copy(tideGribConf = null)
        assertFailsWith<UnsupportedOperationException> {
            getTideService(configTideNull)
        }
    }

    @Test
    fun getTideServiceThrowsUnsupportedErrorWithNonexistentService() {
        val configTideNonexistent = testConfig.copy(tideService = "InexistentService")
        assertFailsWith<UnsupportedOperationException> {
            getTideService(configTideNonexistent)
        }
    }

    @Test
    fun getWindServiceReturnsCorrectType() {
        assertIs<GribWindFetcher>(getWindService(testConfig))
    }

    @Test
    fun getWindServiceThrowsUnsupportedErrorWithNoGribConfig() {
        val configWindNull = testConfig.copy(windGribConf = null)
        assertFailsWith<UnsupportedOperationException> {
            getWindService(configWindNull)
        }
    }

    @Test
    fun getWindServiceThrowsUnsupportedErrorWithNonexistentService() {
        val configWindNonexistent = testConfig.copy(windService = "InexistentService")
        assertFailsWith<UnsupportedOperationException> {
            getWindService(configWindNonexistent)
        }
    }

    @Test
    fun getTideTimeServiceReturnsCorrectType() {
        val mockTideStationService = mockk<TideStationService>()
        every { mockTideStationService.getTideStations() } returns listOf()
        val env = mapOf(Pair("ADMIRALTY_API_KEY", "apiKey"))
        assertIs<TideTimeService>(getTideTimeService(testConfig, env, tideStationService = mockTideStationService))
    }

    @Test
    fun getTideTimeServiceThrowIllegalArgumentWhenNoApiKeyProvided() {
        val env = mapOf(Pair("ADMIRALTY_API_KEY", ""))
        assertFailsWith<IllegalStateException> { getTideTimeService(testConfig, env) }
    }

    @Test
    fun getTimeServiceReturnsCorrectType() {
        assertIs<GribTimeService>(getTimeService(testConfig))
    }
}
