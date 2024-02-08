package com.kayak_backend.services.sunset
import com.kayak_backend.models.BeachInfo
import com.kayak_backend.models.Location
import com.kayak_backend.services.beaches.BeachesGetter
import com.kayak_backend.services.isleOfWightLocation1
import com.kayak_backend.services.isleOfWightLocation2
import io.ktor.network.sockets.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

public class BeachesGetterTest {
    private val httpClientMock = mockk<OkHttpClient>()
    private val beachesGetter = BeachesGetter(httpClientMock, isleOfWightLocation1, isleOfWightLocation2)

    private fun createMockResponse(): Response {
        val bodyString = """{ "elements": [

{
  "type": "node",
  "id": 4274005788,
  "lat": 50.5760726,
  "lon": -1.2901178
},
{
  "type": "node",
  "id": 4274005789,
  "lat": 50.5761325,
  "lon": -1.2900108
},
{
  "type": "way",
  "id": 1047801017,
  "nodes": [
    4274005788,
    4274005789,
  ],
  "tags": {
    "natural": "beach"
  }
}

  ]
}
"""
        return Response.Builder()
            .request(Request.Builder().url("https://overpass-api.de").build())
            .protocol(okhttp3.Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(bodyString.toResponseBody())
            .build()
    }

    @Test
    fun handlesNetworkError() {
        every { httpClientMock.newCall(any()).execute() } throws SocketTimeoutException()
        assertFailsWith<IOException> {
            beachesGetter.getBeaches()
        }
    }

    @Test
    fun returnsBeachesInfo() =
        testApplication {
            every { httpClientMock.newCall(any()).execute() } returns createMockResponse()
            val resultBeachesInfo = beachesGetter.getBeaches()
            println(resultBeachesInfo)
            val beachInfo = listOf(BeachInfo(null, listOf(Location(50.5760726, -1.2901178), Location(50.5761325, -1.2900108))))
            assertEquals(beachInfo, resultBeachesInfo)
        }
}
