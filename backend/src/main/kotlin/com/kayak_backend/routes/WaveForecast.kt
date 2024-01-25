package com.kayak_backend.routes
import java.time.LocalDateTime
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class WaveForecast {

    private val hour = String.format("%02d", (LocalDateTime.now().hour / 6) * 6)
    private val currentDate = SimpleDateFormat("yyMMdd").format(Date())

    fun checkAndRenewForecast() {
        if (newForecastExists()) {
            this.renewForecast()
            println("New forecast exists. Renewing forecast...")
        } else {
            println("No new forecast exists.")
        }
    }

    private fun getFilePath(isBz2: Boolean) : String {
        // TODO decide where forecast is actually stored
        val baseFilePath = "./src/main/kotlin/com/kayak_backend/routes/waveForecast_%s-%s.grib2"
        val filePath = String.format(baseFilePath, currentDate, hour)
        if (isBz2) {
            return filePath.plus(".bz2")
        }
        return filePath
    }

    private fun getDownloadURL(): String {

        val baseUrl = "https://openskiron.org/gribs_wrf_4km/Cherbourg_4km_WRF_WAM_%s-%s.grb.bz2"
        val fileUrl = String.format(baseUrl, currentDate, hour)
        println(fileUrl)

        return fileUrl
    }

    private fun decompressBz2Forecast(): Boolean {
        try {
            val inputStream = BZip2CompressorInputStream(BufferedInputStream(FileInputStream(getFilePath(isBz2 = true))))
            val outputStream = FileOutputStream(File(getFilePath(isBz2 = false)))

            // buffer for reading from the compressed stream
            val buffer = ByteArray(4096)
            var bytesRead: Int

            // read from the compressed stream and write to the output stream
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            // close the streams
            inputStream.close()
            outputStream.close()

            // delete the .bz2 file
            val originalFile = File(getFilePath(isBz2 = true))
            if (originalFile.exists()) {
                originalFile.delete()
                println("Original .bz2 file deleted.")
            }

            println("File decompressed successfully.")
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun renewForecast(): Boolean {
        val url = getDownloadURL()
        val client = OkHttpClient()

        try {
            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {

                FileOutputStream(getFilePath(isBz2 = true)).use { output ->
                    output.write(response.body!!.bytes())
                }
                // TODO actually do things with the forecast

                if (this.decompressBz2Forecast()) {
                    println("Downloaded successfully.")
                    return true
                }
            } else {
                println("Failed to download. Status code: ${response.code}")
            }
            response.close()
        } catch (e: Exception) {
            println("Error during download: ${e.message}")
        }
        return false
    }

    private fun newForecastExists(): Boolean {
        // TODO find out when the forecasts refresh and change this function appropriately
        val newForecastMayExist = !File(getFilePath(isBz2 = false)).exists()

        if (newForecastMayExist) {
            return renewForecast()
        }
        return false
    }
}

fun main() {

    // TODO change according to how often we need to check the forecast
    val waveForecast = WaveForecast()

    // Create a scheduled executor service
    val scheduler = Executors.newScheduledThreadPool(1)

    // Schedule the task to run every hour
    val initialDelay = 0L
    val period = 1L // Repeat every 1 hour
    val timeUnit = TimeUnit.HOURS

    val task = Runnable { waveForecast.checkAndRenewForecast() }

    scheduler.scheduleAtFixedRate(task, initialDelay, period, timeUnit)
}
