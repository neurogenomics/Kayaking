package com.kayak_backend.services.tides

import com.kayak_backend.TideGribConf
import com.kayak_backend.gribReader.GribReader
import com.kayak_backend.models.Location
import com.kayak_backend.models.TideInfo
import java.time.LocalDateTime

class GribTideFetcher(private val conf: TideGribConf, private val gribReader: GribReader) : TideService {
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
                conf.latVarName,
                conf.lonVarName,
                conf.timeVarName,
            )
        return TideInfo(u = pair.first, v = pair.second)
    }
}
