package com.kayak_backend

import BeachNamer
import com.charleskorn.kaml.Yaml
import com.kayak_backend.gribFetcher.GribFetcher
import com.kayak_backend.gribFetcher.OpenSkironGribFetcher
import com.kayak_backend.gribReader.CachingGribReader
import com.kayak_backend.gribReader.GribReader
import com.kayak_backend.gribReader.NetCDFGribReader
import com.kayak_backend.interpolator.SimpleInterpolator
import com.kayak_backend.services.coastline.IsleOfWightCoastline
import com.kayak_backend.services.dangerousWindWarning.DangerousWindService
import com.kayak_backend.services.dangerousWindWarning.seaBearing.SeaBearingService
import com.kayak_backend.services.route.*
import com.kayak_backend.services.route.kayak.WeatherKayak
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
import org.locationtech.jts.geom.Polygon
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
        "CachingNetCDFGribReader" -> CachingGribReader(NetCDFGribReader())
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

fun getDangerousWindService(conf: Conf): DangerousWindService {
    return DangerousWindService((getWindService(conf)))
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

fun getDifficultyLegTimers(): DifficultyLegTimers {
    val slowLegTimer = LegTimer(WeatherKayak(kayakerSpeed = 0.7))
    val normalLegTimer = LegTimer(WeatherKayak(kayakerSpeed = 1.54))
    val fastLegTimer = LegTimer(WeatherKayak(kayakerSpeed = 2.0))
    return DifficultyLegTimers(slowLegTimer, normalLegTimer, fastLegTimer)
}

// separate to be consistent between the RoutePlanner and the SeaBearingService
private const val DISTANCE_FROM_COAST = 500.0
private val coastlineService = IsleOfWightCoastline()

fun getSeaBearingService(): SeaBearingService {
    return SeaBearingService(coastlineService, DISTANCE_FROM_COAST)
}

fun getLegDifficulty(): LegDifficulty {
    return LegDifficulty()
}

fun getRouteSetup(): Pair<Polygon, List<NamedLocation>> {
    val coast = coastlineService.getCoastline()
    val route = BaseRoute().createBaseRoute(coast, DISTANCE_FROM_COAST)
    val slipways = SlipwayService().getAllSlipways()
    val beaches = BeachesService().getAllBeaches()
    val beachNamer = BeachNamer()
    val beachStarts =
        beaches.map { beachInfo ->
            NamedLocation(
                beachInfo.avergeLocation,
                beachInfo.name ?: beachNamer.getClosestBeachName(beachInfo.avergeLocation),
            )
        }
    val startPositions = slipways.plus(beachStarts)
    return route to startPositions
}

fun getRoutePlanner(): RoutePlanner {
    val setup = getRouteSetup()
    return RoutePlanner(setup.first, setup.second)
}

fun getCircularRoutePlanner(
    tideService: TideService,
    legTimer: LegTimer,
): CircularRoutePlanner {
    val setup = getRouteSetup()
    return CircularRoutePlanner(setup.first, setup.second, legTimer, tideService)
}
