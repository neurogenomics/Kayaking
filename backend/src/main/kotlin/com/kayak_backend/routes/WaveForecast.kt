package com.kayak_backend.services
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.FileOutputStream

class WaveForecast() {

    private val filePath = "./src/main/kotlin/com/kayak_backend/routes/waveForecast.grib2"
    private val HOURS_BEFORE_UPDATE = 6
    fun checkAndRenewForecast() {
        if (isForecastOlderThanFourHours()) {
            this.renewForecast(getDownloadURL())
            println("File is older than four hours. Renewing forecast...")
        } else {
            println("File is not older than four hours.")
        }
    }

    private fun getDownloadURL(): String {
        val date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
        val baseUrl = "https://nomads.ncep.noaa.gov/cgi-bin/filter_gefs_wave_0p25.pl"
        val dynamicDate = "dir=%2Fgefs.$date%2F12%2Fwave%2Fgridded"
        val staticParams = "&file=gefs.wave.t12z.c00.global.0p25.f000.grib2&all_var=on&all_lev=on"

        val finalUrl = "$baseUrl?$dynamicDate$staticParams"
        return finalUrl
    }

    private fun renewForecast(url: String): Boolean {
        val client = OkHttpClient()

        try {
            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val downloadedFilePath = filePath

                FileOutputStream(downloadedFilePath).use { output ->
                    output.write(response.body!!.bytes())
                }
                // TODO actually do things with the forecast

                println("Downloaded successfully.")
                return true
            } else {
                println("Failed to download. Status code: ${response.code}")
            }
            response.close()
        } catch (e: Exception) {
            println("Error during download: ${e.message}")
        }
        return false
    }

    private fun isForecastOlderThanFourHours(): Boolean {
        val file = FileSystems.getDefault().getPath(filePath)
        println(file)

        if (Files.exists(file)) {
            val fileAttributes = Files.readAttributes(file, BasicFileAttributes::class.java)
            val lastModifiedTime = fileAttributes.lastModifiedTime().toInstant()

            val currentDateTime = LocalDateTime.now()
            val lastModifiedDateTime = LocalDateTime.ofInstant(lastModifiedTime, ZoneId.systemDefault())

            val hoursDifference = ChronoUnit.HOURS.between(lastModifiedDateTime, currentDateTime)

            return hoursDifference > HOURS_BEFORE_UPDATE
        } else {
            println("File not found.")

            // downloads the file if it is not found
            return true
        }
    }
}

fun main() {
    val waveForecast = WaveForecast()
    waveForecast.checkAndRenewForecast()
}
