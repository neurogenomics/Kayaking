package com.kayak_backend.services.times

import com.kayak_backend.gribReader.GribReader
import java.time.LocalDateTime

class GribTimeService(
    private val gribReader: GribReader,
    private val filePath: String,
) : TimeService {
    override fun getTimes(): List<LocalDateTime> {
        return gribReader.getTimeRange(filePath)
    }
}
