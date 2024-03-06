package com.kayak_backend.services.waves

import com.kayak_backend.WaveGribConf
import com.kayak_backend.gribReader.GribFileError
import com.kayak_backend.gribReader.GribReader
import com.kayak_backend.interpolator.Interpolator
import com.kayak_backend.models.Location
import com.kayak_backend.models.Range
import com.kayak_backend.models.WaveGrid
import com.kayak_backend.models.WaveInfo
import java.time.LocalDateTime

class GribWaveFetcher(
    private val conf: WaveGribConf,
    private val gribReader: GribReader,
    private val interpolator: Interpolator,
) : WaveService {
    override fun getWave(
        loc: Location,
        time: LocalDateTime,
    ): WaveInfo {
        val pair =
            gribReader.getVarPair(
                loc.latitude,
                loc.longitude,
                time,
                conf.waveHeightVarName,
                conf.waveDirectionVarName,
                conf.filePath,
            )
        return WaveInfo(pair.first, pair.second)
    }

    override fun getWaveGrid(
        cornerSW: Location,
        cornerNE: Location,
        time: LocalDateTime,
        resolutions: Pair<Double, Double>,
    ): WaveGrid {
        val latRange = Range(cornerSW.latitude, cornerNE.latitude)
        val lonRange = Range(cornerSW.longitude, cornerNE.longitude)
        val (heightData, latIndexU, lonIndexU) =
            gribReader.getVarGrid(
                latRange,
                lonRange,
                time,
                conf.waveHeightVarName,
                conf.filePath,
            )
        val (directionData, latIndexV, lonIndexV) =
            gribReader.getVarGrid(
                latRange,
                lonRange,
                time,
                conf.waveDirectionVarName,
                conf.filePath,
            )
        if (latIndexU != latIndexV || lonIndexU != lonIndexV) throw GribFileError("Wave Values uneven! Check variable names")
        val (interpolatedUData, newLatIndexU, newLonIndexU) =
            interpolator.interpolate(heightData, Pair(latIndexU, lonIndexU), Pair(latRange, lonRange), resolutions)
        val (interpolatedVData, newLatIndexV, newLonIndexV) =
            interpolator.interpolate(directionData, Pair(latIndexV, lonIndexV), Pair(latRange, lonRange), resolutions)
        if (newLatIndexU != newLatIndexV || newLonIndexU != newLonIndexV) throw GribFileError("Something went wrong with interpolation")
        val grid =
            interpolatedUData.zip(interpolatedVData) { heights, dirs ->
                heights.zip(dirs) { height, dir ->
                    if (height.isNaN() || dir.isNaN()) {
                        null
                    } else {
                        WaveInfo(height, dir)
                    }
                }
            }
        return WaveGrid(grid, newLatIndexU, newLonIndexU)
    }
}
