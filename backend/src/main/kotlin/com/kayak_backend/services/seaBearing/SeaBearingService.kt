package com.kayak_backend.services.seaBearing

import com.kayak_backend.services.coastline.IsleOfWightCoastline

//imports for the main function and manual testing
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter

class SeaBearingService {

    private var seaBearings : List<SeaBearingInfo> = listOf()
    //TODO decide if coastline service should be a field in SeaBearingService/ SeaBearingGetter - surely field better to then decouple from isle of white coast
    private val seaBearingsGetter = SeaBearingsGetter(IsleOfWightCoastline())

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
