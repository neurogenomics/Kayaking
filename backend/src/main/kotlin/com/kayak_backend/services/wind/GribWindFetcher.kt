package com.kayak_backend.services.wind

import com.kayak_backend.WindGribConf
import com.kayak_backend.gribReader.GribFileError
import com.kayak_backend.gribReader.GribReader
import com.kayak_backend.interpolator.Interpolator
import com.kayak_backend.models.Location
import com.kayak_backend.models.Range
import com.kayak_backend.models.WindGrid
import com.kayak_backend.models.WindInfo
import java.time.LocalDateTime

class GribWindFetcher(
    private val conf: WindGribConf,
    private val gribReader: GribReader,
    private val interpolator: Interpolator,
) : WindService {
    override fun getWind(
        loc: Location,
        time: LocalDateTime,
    ): WindInfo {
        val pair =
            gribReader.getVarPair(
                loc.latitude,
                loc.longitude,
                time,
                conf.uWindVarName,
                conf.vWindVarName,
                conf.filePath,
            )
        return WindInfo(u = pair.first, v = pair.second)
    }

    override fun getWindGrid(
        cornerSW: Location,
        cornerNE: Location,
        time: LocalDateTime,
        resolutions: Pair<Double, Double>,
    ): WindGrid {
        val latRange = Range(cornerSW.latitude, cornerNE.latitude)
        val lonRange = Range(cornerSW.longitude, cornerNE.longitude)
        val (uData, latIndexU, lonIndexU) =
            gribReader.getVarGrid(
                latRange,
                lonRange,
                time,
                conf.uWindVarName,
                conf.filePath,
            )
        val (vData, latIndexV, lonIndexV) =
            gribReader.getVarGrid(
                latRange,
                lonRange,
                time,
                conf.vWindVarName,
                conf.filePath,
            )
        if (latIndexU != latIndexV || lonIndexU != lonIndexV) throw GribFileError("Wind Values uneven! Check variable names")
        val (interpolatedUData, newLatIndexU, newLonIndexU) =
            interpolator.interpolate(uData, Pair(latIndexU, lonIndexU), Pair(latRange, lonRange), resolutions)
        val (interpolatedVData, newLatIndexV, newLonIndexV) =
            interpolator.interpolate(vData, Pair(latIndexV, lonIndexV), Pair(latRange, lonRange), resolutions)
        if (newLatIndexU != newLatIndexV || newLonIndexU != newLonIndexV) throw GribFileError("Something went wrong with interpolation")
        val grid =
            interpolatedUData.zip(interpolatedVData) { us, vs ->
                us.zip(vs) { u, v ->
                    WindInfo(u, v)
                }
            }
        return WindGrid(grid, newLatIndexU, newLonIndexU)
    }
}
