package com.kayak_backend

import com.charleskorn.kaml.Yaml
import com.kayak_backend.gribReader.GribReader
import com.kayak_backend.gribReader.NetCDFGribReader
import com.kayak_backend.services.tideTimes.AdmiraltyTideTimeService
import com.kayak_backend.services.tideTimes.TideTimeService
import com.kayak_backend.services.tides.GribTideFetcher
import com.kayak_backend.services.tides.TideService
import com.kayak_backend.services.wind.GribWindFetcher
import com.kayak_backend.services.wind.WindService
import io.github.cdimascio.dotenv.dotenv
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
    val vTideVarName: String,
)

@Serializable
data class Conf(
    val tideService: String,
    val windService: String,
    val tideTimeService: String,
    val tideGribConf: TideGribConf? = null,
    val windGribConf: WindGribConf? = null,
)

private val dotenv = dotenv()

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
        else -> throw UnsupportedOperationException("Tide Conf required")
    }
}

fun getWindService(conf: Conf): WindService {
    return when (conf.windService) {
        "grib" -> {
            conf.windGribConf ?: throw UnsupportedOperationException("Wind Grib Config not Provided")
            GribWindFetcher(conf.windGribConf, getGribReader(conf.windGribConf.gribReader))
        }
        else -> throw UnsupportedOperationException("Tide Conf required")
    }
}

fun getTideTimeService(conf: Conf): TideTimeService  {
    return when (conf.tideTimeService) {
        "admiralty" -> {
            val apiKey = dotenv["ADMIRALTY_API_KEY"]
            if (apiKey == null || apiKey.isEmpty())
                {
                    throw IllegalStateException("Admiralty API key is missing or empty in .env")
                }
            AdmiraltyTideTimeService(apiKey)
        }
        else -> throw UnsupportedOperationException("TideTime Conf required")
    }
}
