import com.kayak_backend.models.Location
import com.kayak_backend.models.TideStation
import com.kayak_backend.services.tideTimes.AdmiraltyTideStationService
import io.ktor.network.sockets.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import kotlin.test.*

class AdmiraltyTideStationServiceTest {

    private val httpClientMock = mockk<OkHttpClient>()
    private val tideStationService = AdmiraltyTideStationService("TEST_KEY", httpClientMock)
    @Test
    fun parsesJSONCorrectly() {
        every { httpClientMock.newCall(any()).execute() } returns createMockResponse();
        val stations = tideStationService.getTideStations()
        val expectedStations = listOf(
            TideStation("0322", "Hirta (Bagh A' Bhaile)", Location(57.8, -8.566666)),
            TideStation("0324", "Rockall", Location(57.6, -13.683333))
        )
        assertEquals(expectedStations, stations)
    }

    private fun createMockResponse(): Response {
        val bodyString = """{
    "type": "FeatureCollection",
    "features": [
        {
            "type": "Feature",
            "geometry": {
                "type": "Point",
                "coordinates": [
                    -8.566666,
                    57.8
                ]
            },
            "properties": {
                "Id": "0322",
                "Name": "Hirta (Bagh A' Bhaile)",
                "Country": "Scotland",
                "ContinuousHeightsAvailable": true,
                "Footnote": null
            }
        },
        {
            "type": "Feature",
            "geometry": {
                "type": "Point",
                "coordinates": [
                    -13.683333,
                    57.6
                ]
            },
            "properties": {
                "Id": "0324",
                "Name": "Rockall",
                "Country": "Scotland",
                "ContinuousHeightsAvailable": true,
                "Footnote": "ML inferred"
            }
        }
        ]}"""
        return Response.Builder()
            .request(Request.Builder().url("https://admiraltyapi.azure-api.net").build())
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
            tideStationService.getTideStations()
        }
    }

}