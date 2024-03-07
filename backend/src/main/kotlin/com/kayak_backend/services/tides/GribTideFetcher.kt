package com.kayak_backend.services.tides

import com.kayak_backend.TideGribConf
import com.kayak_backend.gribReader.GribFileError
import com.kayak_backend.gribReader.GribReader
import com.kayak_backend.interpolator.Interpolator
import com.kayak_backend.models.Location
import com.kayak_backend.models.Range
import com.kayak_backend.models.TideGrid
import com.kayak_backend.models.TideInfo
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class GribTideFetcher(
    private val conf: TideGribConf,
    private val gribReader: GribReader,
    private val interpolator: Interpolator,
) : TideService {
    override fun getTide(
        loc: Location,
        time: LocalDateTime,
    ): TideInfo {
        val pair =
            gribReader.getVarPair(
                loc.latitude,
                loc.longitude,
                time,
                conf.uTideVarName,
                conf.vTideVarName,
                conf.filePath,
            )
        return TideInfo(u = pair.first, v = pair.second)
    }

    override fun getTideGrid(
        cornerSW: Location,
        cornerNE: Location,
        time: LocalDateTime,
        resolutions: Pair<Double, Double>,
    ): TideGrid {
        val latRange = Range(cornerSW.latitude, cornerNE.latitude)
        val lonRange = Range(cornerSW.longitude, cornerNE.longitude)
        val (uData, latIndexU, lonIndexU) =
            gribReader.getVarGrid(
                latRange,
                lonRange,
                time,
                conf.uTideVarName,
                conf.filePath,
            )
        val (vData, latIndexV, lonIndexV) =
            gribReader.getVarGrid(
                latRange,
                lonRange,
                time,
                conf.vTideVarName,
                conf.filePath,
            )
        if (latIndexU != latIndexV || lonIndexU != lonIndexV) throw GribFileError("Tide Values uneven! Check variable names")
        val (interpolatedUData, newLatIndexU, newLonIndexU) =
            interpolator.interpolate(uData, Pair(latIndexU, lonIndexU), Pair(latRange, lonRange), resolutions)
        val (interpolatedVData, newLatIndexV, newLonIndexV) =
            interpolator.interpolate(vData, Pair(latIndexV, lonIndexV), Pair(latRange, lonRange), resolutions)
        if (newLatIndexU != newLatIndexV || newLonIndexU != newLonIndexV) throw GribFileError("Something went wrong with interpolation")
        val grid =
            interpolatedUData.zip(interpolatedVData) { us, vs ->
                us.zip(vs) { u, v ->
                    if (u.isNaN() || v.isNaN()) {
                        null
                    } else {
                        TideInfo(u, v)
                    }
                }
            }
        return TideGrid(grid, newLatIndexU, newLonIndexU)
    }

    override fun getTideAllDay(
        loc: Location,
        date: LocalDate,
    ): Map<LocalTime, TideInfo> {
        val data =
            gribReader.getDayData(
                loc.latitude,
                loc.longitude,
                date,
                conf.uTideVarName,
                conf.vTideVarName,
                conf.filePath,
            )
        return data.mapValues { TideInfo(u = it.value.first, v = it.value.second) }
    }
}
