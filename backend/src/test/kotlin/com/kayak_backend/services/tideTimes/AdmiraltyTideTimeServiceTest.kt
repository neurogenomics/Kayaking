import com.kayak_backend.models.Location
import com.kayak_backend.models.TideEvent
import com.kayak_backend.models.TideStation
import com.kayak_backend.models.TideTimes
import com.kayak_backend.services.tideTimes.AdmiraltyTideTimeService
import com.kayak_backend.services.tideTimes.TideStationService
import io.ktor.network.sockets.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import java.time.LocalDateTime
import kotlin.test.*

class AdmiraltyTideTimeServiceTest {

    private val httpClientMock = mockk<OkHttpClient>()
    private val tideStationServiceMock = mockk<TideStationService>()
    private val tideTimeService = AdmiraltyTideTimeService("TEST_KEY", httpClientMock, tideStationServiceMock)

    @Test
    fun parsesJSONCorrectly() = testApplication {
        every { httpClientMock.newCall(any()).execute() } returns createMockResponse();
        val testLocation = Location(0.0, 0.0)
        val stationMock = TideStation("0", "Test name", Location(0.0, 0.0))
        every { tideStationServiceMock.getTideStations() } returns listOf(stationMock)
        val times = tideTimeService.getTideTimes(testLocation)
        val expectedTimes = TideTimes(
            listOf(
                TideEvent(true, LocalDateTime.parse("2024-01-30T01:10:00"), 2.58),
                TideEvent(false, LocalDateTime.parse("2024-01-31T06:43:00"), null)
            ),
            stationMock
        )
        assertEquals(expectedTimes, times)
    }

    private fun createMockResponse(): Response {
        val bodyString = """[ {
        "EventType": "HighWater",
        "DateTime": "2024-01-30T01:10:00",
        "IsApproximateTime": false,
        "Height": 2.58,
        "IsApproximateHeight": false,
        "Filtered": false,
        "Date": "2024-01-31T00:00:00"
    },
    {
        "EventType": "LowWater",
        "DateTime": "2024-01-31T06:43:00",
        "IsApproximateTime": false,
        "IsApproximateHeight": false,
        "Filtered": false,
        "Date": "2024-01-31T00:00:00"
    }]"""
        return Response.Builder()
            .request(Request.Builder().url("https://admiraltyapi.azure-api.net").build())
            .protocol(okhttp3.Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(bodyString.toResponseBody())
            .build()
    }

    @Test
    fun usesClosestStation() {
        every { httpClientMock.newCall(any()).execute() } returns createMockResponse();
        val stationMock1 = TideStation("1", "Test1", Location(5.0, 4.0))
        val stationMock2 = TideStation("ExpectedID", "Test2", Location(2.0, 1.0))
        every { tideStationServiceMock.getTideStations() } returns listOf(stationMock1, stationMock2)
        tideTimeService.getTideTimes(Location(1.0,1.0))
        verify { httpClientMock.newCall(match { it.url.pathSegments.contains("ExpectedID") }) }
    }

    @Test
    fun handlesNetworkError() {
        val stationMock = TideStation("0", "Test name", Location(0.0, 0.0))
        every { tideStationServiceMock.getTideStations() } returns listOf(stationMock)
        every { httpClientMock.newCall(any()).execute() } throws SocketTimeoutException()
        assertFailsWith<IOException> {
            tideTimeService.getTideTimes(Location(0.0, 0.0))
        }
    }

}