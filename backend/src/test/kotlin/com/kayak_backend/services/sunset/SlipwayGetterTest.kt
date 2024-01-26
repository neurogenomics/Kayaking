package com.kayak_backend.services.sunset;
import com.kayak_backend.models.Location
import com.kayak_backend.services.slipways.SlipwaysGetter
import com.kayak_backend.services.sunset.SunsetInfo
import com.kayak_backend.services.sunset.SunsetService
import io.ktor.network.sockets.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import java.time.LocalTime
import kotlin.test.*
public class SlipwayGetterTest {

    val ISLEOFWIGHTLOCATION1 = Location(50.564485309567644, -1.6005677025384493)
    val ISLEOFWIGHTLOCATION2 = Location(50.8605772841442, -1.0457581322259493)

    private val httpClientMock = mockk<OkHttpClient>()
    private val slipwaysGetter = SlipwaysGetter(httpClientMock, ISLEOFWIGHTLOCATION1, ISLEOFWIGHTLOCATION2)
    private fun createMockResponse(): Response {
        val bodyString = """{"elements": {"lon":-1.0721198,"id":12711438,"type":"node","lat":50.688823,"tags":{"seamark:small_craft_facility:category":"slipway","leisure":"slipway","seamark:type":"small_craft_facility"}},
            {"lon":-1.2345678,"id":12711439,"type":"node","lat":50.7890123,"tags":{"leisure":"slipway","seamark:type":"small_craft_facility"}}"""
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
            slipwaysGetter.getSlipways();
        }
    }
}
