package com.kayak_backend.services.seaBearing

import com.kayak_backend.services.coastline.CoastlineService
import com.kayak_backend.services.coastline.IsleOfWightCoastline
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter

class SeaBearingService(coastlineService: CoastlineService = IsleOfWightCoastline()) {
    private var seaBearings: List<SeaBearingInfo> = listOf()

    private val seaBearingsGetter = SeaBearingsGetter(coastlineService)

    fun getSeaBearings(): List<SeaBearingInfo> {
        if (seaBearings.isEmpty()) {
            seaBearings = seaBearingsGetter.getSeaBearings()
        }

        return seaBearings
    }
}

fun main() {
    val service = SeaBearingService()
    val bearings = service.getSeaBearings()

    try {
        PrintWriter(FileWriter(File("src/main/resources/seaBearings.csv"))).use { writer ->
            for (i in 0..<bearings.size - 1) {
                writer.println("${bearings[i].bearing},${bearings[i].coor.latitude},${bearings[i].coor.longitude}")
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
