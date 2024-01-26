import com.kayak_backend.models.Location
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
class SunsetService {

    private val httpClientMock = mockk<OkHttpClient>()
    private val sunsetService = SunsetService(httpClientMock)
  
    @Test
    fun returnsSunsetInfo() = testApplication {
        every { httpClientMock.newCall(any()).execute() } returns createMockResponse();
        val resultSunsetInfo = sunsetService.getSunset(Location(0.0,0.0))
        val sunsetInfo = SunsetInfo(LocalTime.of(10, 30), LocalTime.of(6,30))
        assertEquals(sunsetInfo, resultSunsetInfo)
    }
    private fun createMockResponse(): Response {
        val bodyString = """{"results": {"sunrise": "10:30:00 AM", "sunset": "6:30:00 AM"}}"""
        return Response.Builder()
            .request(Request.Builder().url("https://api.sunrisesunset.io").build())
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
            val location = Location(0.0, 0.0)
            sunsetService.getSunset(location)
        }
    }

}