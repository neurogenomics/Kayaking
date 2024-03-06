package com.kayak_backend

import com.charleskorn.kaml.Yaml
import com.kayak_backend.gribFetcher.GribFetcher
import com.kayak_backend.gribFetcher.OpenSkironGribFetcher
import com.kayak_backend.gribReader.GribReader
import com.kayak_backend.gribReader.NetCDFGribReader
import com.kayak_backend.interpolator.SimpleInterpolator
import com.kayak_backend.services.coastline.IsleOfWightCoastline
import com.kayak_backend.services.route.*
import com.kayak_backend.services.route.kayak.BasicKayak
import com.kayak_backend.services.slipways.BeachesService
import com.kayak_backend.services.slipways.SlipwayService
import com.kayak_backend.services.tideTimes.AdmiraltyTideTimeService
import com.kayak_backend.services.tideTimes.TideTimeService
import com.kayak_backend.services.tides.GribTideFetcher
import com.kayak_backend.services.tides.TideService
import com.kayak_backend.services.times.GribTimeService
import com.kayak_backend.services.times.TimeService
import com.kayak_backend.services.waves.GribWaveFetcher
import com.kayak_backend.services.waves.WaveService
import com.kayak_backend.services.wind.GribWindFetcher
import com.kayak_backend.services.wind.WindService
import kotlinx.serialization.Serializable
import java.nio.file.Files
import java.nio.file.Path

@Serializable
data class TideGribConf(
    val gribReader: String,
    val filePath: String,
    val uTideVarName: String,
    val vTideVarName: String,
)

@Serializable
data class WindGribConf(
    val gribReader: String,
    val filePath: String,
    val uWindVarName: String,
    val vWindVarName: String,
)

@Serializable
data class WaveGribConf(
    val gribReader: String,
    val filePath: String,
    val waveHeightVarName: String,
    val waveDirectionVarName: String,
)

@Serializable
data class GribTimeServiceConf(
    val gribReader: String,
    val filePaths: List<String>,
)

@Serializable
data class Conf(
    val tideService: String,
    val windService: String,
    val timeService: String,
    val waveService: String,
    val tideTimeService: String,
    val tideGribConf: TideGribConf? = null,
    val windGribConf: WindGribConf? = null,
    val waveGribConf: WaveGribConf? = null,
    val gribTimeServiceConf: GribTimeServiceConf? = null,
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
            GribTideFetcher(conf.tideGribConf, getGribReader(conf.tideGribConf.gribReader), SimpleInterpolator())
        }

        else -> throw UnsupportedOperationException("Tide service type non existent")
    }
}

fun getWindService(conf: Conf): WindService {
    return when (conf.windService) {
        "grib" -> {
            conf.windGribConf ?: throw UnsupportedOperationException("Wind Grib Config not Provided")
            GribWindFetcher(conf.windGribConf, getGribReader(conf.windGribConf.gribReader), SimpleInterpolator())
        }

        else -> throw UnsupportedOperationException("Wind service type non existent")
    }
}

fun getWaveService(conf: Conf): WaveService {
    return when (conf.waveService) {
        "grib" -> {
            conf.waveGribConf ?: throw UnsupportedOperationException("Wave Grib Config not Provided")
            GribWaveFetcher(conf.waveGribConf, getGribReader(conf.waveGribConf.gribReader), SimpleInterpolator())
        }

        else -> throw UnsupportedOperationException("Wave service type non existent")
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

fun getTideTimeService(
    conf: Conf,
    sysEnv: Map<String, String>,
): TideTimeService {
    return when (conf.tideTimeService) {
        "admiralty" -> {
            val apiKey = sysEnv["ADMIRALTY_API_KEY"]
            if (apiKey.isNullOrEmpty()) {
                throw IllegalStateException("Admiralty API key is missing or empty in .env")
            }
            AdmiraltyTideTimeService(apiKey)
        }

        else -> throw UnsupportedOperationException("TideTime Conf required")
    }
}

fun getTimeService(conf: Conf): TimeService {
    return when (conf.timeService) {
        "grib" -> {
            with(conf.gribTimeServiceConf) {
                this ?: throw IllegalStateException("Grib file time service but no conf provided")
                GribTimeService(getGribReader(this.gribReader), this.filePaths)
            }
        }

        else -> {
            throw IllegalStateException("Time service provided not supported")
        }
    }
}

// Once we have the weather kayak, may want to use conf to determine which kayak
fun getLegTimer(): LegTimer {
    return LegTimer(BasicKayak())
}

fun getRoutePlanner(): RoutePlanner {
    val distanceFromCoast = 500.0
    val coast = IsleOfWightCoastline().getCoastline()
    val route = BaseRoute().createBaseRoute(coast, distanceFromCoast)
    val slipways = SlipwayService().getAllSlipways()
    val beaches = BeachesService().getAllBeaches()
    val slipwayStarts = slipways.mapIndexed { index, location -> StartPos(location, "Slipway $index") }
    val beachStarts =
        beaches.map { beachInfo ->
            StartPos(
                beachInfo.avergeLocation,
                beachInfo.name ?: "Unnamed beach",
            )
        }
    val startPositions = slipwayStarts.plus(beachStarts)

    return RoutePlanner(route, startPositions)
}
