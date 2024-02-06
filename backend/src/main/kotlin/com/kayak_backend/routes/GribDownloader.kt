package com.kayak_backend.routes
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WaveForecast {
    private val baseFilePath = "./src/main/kotlin/com/kayak_backend/routes/waveForecast_%s-%s.grib2"
    private val baseUrl = "https://openskiron.org/gribs_wrf_4km/Cherbourg_4km_WRF_WAM_%s-%s.grb.bz2"

    private fun formatBaseString(
        baseStr: String,
        dateTime: LocalDateTime,
    ): String {
        val hourStr = String.format("%02d", (dateTime.hour / 6) * 6)
        val formatter = DateTimeFormatter.ofPattern("yyMMdd")
        val currentDateStr = dateTime.format(formatter)
        return String.format(baseStr, currentDateStr, hourStr)
    }

    private fun decompressBz2(
        inputFilePath: String,
        outputFilePath: String,
    ) {
        val inputStream = BZip2CompressorInputStream(BufferedInputStream(FileInputStream(inputFilePath)))
        val outputStream = FileOutputStream(File(outputFilePath))

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
    }

    private fun downloadForecast(
        url: String,
        outputFilePath: String,
        client: OkHttpClient,
    ) {
        val request =
            Request.Builder()
                .url(url)
                .build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            FileOutputStream(outputFilePath).use { output ->
                output.write(response.body!!.bytes())
            }
        } else {
            println("Failed to grib data download. Status code: ${response.code}")
        }
        response.close()
    }

    fun renewForecast(client: OkHttpClient): Boolean {
        val dateTime = LocalDateTime.now()
        val url = formatBaseString(baseUrl, dateTime)
        val bz2GribPath = formatBaseString("$baseFilePath.bz2", dateTime)
        val gribPath = formatBaseString(baseFilePath, dateTime)
        downloadForecast(url, bz2GribPath, client)
        decompressBz2(bz2GribPath, gribPath)
        val originalFile = File(bz2GribPath)
        if (originalFile.exists()) {
            originalFile.delete()
        }
        return true
    }
}

fun main() {
    // TODO change according to how often we need to check the forecast
    val waveForecast = WaveForecast()
    waveForecast.renewForecast(OkHttpClient())
}
