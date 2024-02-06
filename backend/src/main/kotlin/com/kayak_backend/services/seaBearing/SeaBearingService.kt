package com.kayak_backend.services.seaBearing

import com.kayak_backend.models.SeaBearingInfo
import com.kayak_backend.services.coastline.IsleOfWightCoastline

//imports for the main function and manual testing
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter

class SeaBearingService {

    private var seaBearings : List<SeaBearingInfo> = listOf()
    private val seaBearingsGetter = SeaBearingsGetter()

    fun getSeaBearings() : List<SeaBearingInfo> {
        if (seaBearings.isEmpty()){
            seaBearings = seaBearingsGetter.getSeaBearings();
        }
        return seaBearings
    }

}

//prints the bearings and coordinates into seaBearings.csv (for manual testing)
fun main() {
    val service = SeaBearingService()
    val bearings = service.getSeaBearings()

    val coastlineService = IsleOfWightCoastline()
    val coastline = coastlineService.getCoastline().coordinates

    try {
        PrintWriter(FileWriter(File("src/main/resources/seaBearings.csv"))).use { writer ->
            for (i in 0..<coastline.size - 1) {
                writer.println("${bearings[i].bearing},${bearings[i].coor.latitude},${bearings[i].coor.longitude}")
            }
        }

    } catch (e: IOException) {
        e.printStackTrace()
    }
}
