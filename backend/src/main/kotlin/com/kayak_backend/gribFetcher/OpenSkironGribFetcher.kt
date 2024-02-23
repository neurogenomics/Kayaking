package com.kayak_backend.gribFetcher

import com.kayak_backend.services.GRIB_FILE_PATH
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class OpenSkironGribFetcher(private val client: OkHttpClient = OkHttpClient()) : GribFetcher {
    private val filePathBz2 = "$GRIB_FILE_PATH.bz2"
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

        val buffer = ByteArray(4096)
        var bytesRead: Int

        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }

        inputStream.close()
        outputStream.close()
    }

    private fun downloadForecast(
        dateTime: LocalDateTime,
        client: OkHttpClient,
    ): Boolean {
        val url = formatBaseString(baseUrl, dateTime)
        val request =
            Request.Builder()
                .url(url)
                .build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            FileOutputStream(filePathBz2).use { output ->
                output.write(response.body!!.bytes())
            }
        } else {
            return false
        }
        response.close()
        return true
    }

    private fun isUptoDate(dateTime: LocalDateTime): Boolean {
        val file = File(GRIB_FILE_PATH)
        if (file.exists()) {
            val lastModifiedInstant = Instant.ofEpochMilli(file.lastModified())
            val lastModifiedDateTime = LocalDateTime.ofInstant(lastModifiedInstant, ZoneId.systemDefault())
            return dateTime.hour / 6 == lastModifiedDateTime.hour / 6
        }
        return false
    }

    override fun fetchGrib(): Boolean {
        val dateTime = LocalDateTime.now()
        val bz2GribPath = formatBaseString(filePathBz2, dateTime)
        val gribPath = formatBaseString(GRIB_FILE_PATH, dateTime)
        if (isUptoDate(dateTime)) {
            return false
        }
        // To handle the variable delay with openskiron publishing their gribfiles
        var success = false
        for (i in 0..10) {
            if (downloadForecast(dateTime.minusHours(6L * i), client)) {
                success = true
                break
            }
        }
        if (!success) return false

        decompressBz2(bz2GribPath, gribPath)
        val originalFile = File(bz2GribPath)
        if (originalFile.exists()) {
            originalFile.delete()
        }
        return true
    }
}
