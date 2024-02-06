package com.kayak_backend.services.wind

import com.kayak_backend.WindGribConf
import com.kayak_backend.gribReader.GribReader
import com.kayak_backend.models.Location
import com.kayak_backend.models.WindInfo
import java.time.LocalDateTime

class GribWindFetcher(private val conf: WindGribConf, private val gribReader: GribReader) : WindService {
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
                conf.vTideVarName,
                conf.filePath,
                conf.latVarName,
                conf.lonVarName,
                conf.timeVarName,
            )
        return WindInfo(u = pair.first, v = pair.second)
    }
}
