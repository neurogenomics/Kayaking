package com.kayak_backend

import com.charleskorn.kaml.Yaml
import com.kayak_backend.gribFetcher.GribFetcher
import com.kayak_backend.gribFetcher.OpenSkironGribFetcher
import com.kayak_backend.gribReader.GribReader
import com.kayak_backend.gribReader.NetCDFGribReader
import com.kayak_backend.services.tides.GribTideFetcher
import com.kayak_backend.services.tides.TideService
import com.kayak_backend.services.wind.GribWindFetcher
import com.kayak_backend.services.wind.WindService
import kotlinx.serialization.Serializable
import java.nio.file.Files
import java.nio.file.Path

@Serializable
data class TideGribConf(
    val gribReader: String,
    val filePath: String,
    val latVarName: String,
    val lonVarName: String,
    val timeVarName: String,
    val uTideVarName: String,
    val vTideVarName: String,
)

@Serializable
data class WindGribConf(
    val gribReader: String,
    val filePath: String,
    val latVarName: String,
    val lonVarName: String,
    val timeVarName: String,
    val uWindVarName: String,
    val vWindVarName: String,
)

@Serializable
data class Conf(
    val tideService: String,
    val windService: String,
    val tideGribConf: TideGribConf? = null,
    val windGribConf: WindGribConf? = null,
    val gribFetcher: String,
)

fun getConf(filePath: String): Conf {
    val yaml = Files.readString(Path.of(filePath))
    return Yaml.default.decodeFromString(Conf.serializer(), yaml)
}

fun getGribReader(implementation: String): GribReader {
    return when (implementation) {
        "NetCDFGribReader" -> NetCDFGribReader()
        else -> throw UnsupportedOperationException("Grib Reader Implementation Non Existent")
    }
}

fun getTideService(conf: Conf): TideService {
    return when (conf.tideService) {
        "grib" -> {
            conf.tideGribConf ?: throw UnsupportedOperationException("Tide Grib Config not Provided")
            GribTideFetcher(conf.tideGribConf, getGribReader(conf.tideGribConf.gribReader))
        }
        else -> throw UnsupportedOperationException("Tide service type non existent")
    }
}

fun getWindService(conf: Conf): WindService {
    return when (conf.windService) {
        "grib" -> {
            conf.windGribConf ?: throw UnsupportedOperationException("Wind Grib Config not Provided")
            GribWindFetcher(conf.windGribConf, getGribReader(conf.windGribConf.gribReader))
        }
        else -> throw UnsupportedOperationException("Wind service type non existent")
    }
}

fun getGribFetcher(conf: Conf): GribFetcher {
    return when (conf.gribFetcher) {
        "OpenSkiron" -> {
            OpenSkironGribFetcher()
        }
        else -> throw UnsupportedOperationException("Grib Fetcher Conf not Provided")
    }
}
