package com.kayak_backend.services.times

import com.kayak_backend.gribReader.GribReader
import java.time.LocalDateTime

class GribTimeService(
    private val gribReader: GribReader,
    private val filePaths: List<String>,
) : TimeService {
    override fun getTimes(): List<LocalDateTime> {
        return filePaths.map {
            gribReader.getTimeRange(it).toSet()
        }.reduce { accumulatedDates, newDates -> accumulatedDates intersect newDates }
            .toList()
            .sorted()
    }
}
