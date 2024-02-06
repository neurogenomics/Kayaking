package com.kayak_backend

import com.kayak_backend.gribReader.NetCDFGribReader
import com.kayak_backend.services.tides.GribTideFetcher
import com.kayak_backend.services.wind.GribWindFetcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

val testTideGribConf =
    TideGribConf(
        gribReader = "NetCDFGribReader",
        filePath = "src/test/gribFiles/testGrib.grb",
        latVarName = "TwoD/LatLon_100X120-49p74N-1p333W/lat",
        lonVarName = "TwoD/LatLon_100X120-49p74N-1p333W/lon",
        timeVarName = "TwoD/LatLon_100X120-49p74N-1p333W/time",
        uTideVarName = "TwoD/LatLon_100X120-49p74N-1p333W/u-component_of_current_surface",
        vTideVarName = "TwoD/LatLon_100X120-49p74N-1p333W/v-component_of_current_surface",
    )

val testWindGribConf =
    WindGribConf(
        gribReader = "NetCDFGribReader",
        filePath = "src/test/gribFiles/testGrib.grb",
        latVarName = "TwoD/LatLon_76X92-49p73N-1p324W/lat",
        lonVarName = "TwoD/LatLon_76X92-49p73N-1p324W/lon",
        timeVarName = "TwoD/LatLon_76X92-49p73N-1p324W/time",
        uWindVarName = "TwoD/LatLon_76X92-49p73N-1p324W/u-component_of_wind_height_above_ground",
        vWindVarName = "TwoD/LatLon_76X92-49p73N-1p324W/v-component_of_wind_height_above_ground",
    )

val testConfig =
    Conf(
        tideService = "grib",
        windService = "grib",
        gribFetcher = "OpenSkiron",
        tideGribConf = testTideGribConf,
        windGribConf = testWindGribConf,
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
}
