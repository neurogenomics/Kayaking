import com.kayak_backend.models.Location
import com.kayak_backend.models.TideEvent
import com.kayak_backend.models.TideStation
import com.kayak_backend.models.TideTimes
import com.kayak_backend.routes.commonSetup
import com.kayak_backend.routes.tideTimes
import com.kayak_backend.services.tideTimes.AdmiraltyTideTimeService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import kotlin.test.*

class TideTimesRouteTest {
    private val tideTimesMock = mockk<AdmiraltyTideTimeService>()

    init {
        every { tideTimesMock.getTideTimes(any()) } returns
            TideTimes(
                events =
                    listOf(
                        TideEvent(
                            true,
                            LocalDateTime.parse("2024-04-06T23:00:00.000"),
                            4.5,
                        ),
                    ),
                source = TideStation("A", "Example Station", Location(50.0, -2.0)),
            )
    }

    @Test
    fun returnsTideTimes() =
        testApplication {
            commonSetup { tideTimes(tideTimesMock) }
            val response = client.get("/tidetimes?lat=50.64&lon=60")
            assertEquals(HttpStatusCode.OK, response.status)
            val encoded = Json.encodeToString(tideTimesMock.getTideTimes(Location(0.0, 0.0)))
            assertEquals(encoded, response.bodyAsText())
        }

    @Test
    fun requiresLatParameter() =
        testApplication {
            commonSetup { tideTimes(tideTimesMock) }
            val response = client.get("/tidetimes?lng=20")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Missing \"lat\" parameter.", response.bodyAsText())
        }

    @Test
    fun requiresLatToBeDouble() =
        testApplication {
            commonSetup { tideTimes(tideTimesMock) }
            val response = client.get("/tidetimes?lat=dog&lon=40")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Parameter \"lat\" should be Double.", response.bodyAsText())
        }

    @Test
    fun requiresLonParameter() =
        testApplication {
            commonSetup { tideTimes(tideTimesMock) }
            val response = client.get("/tidetimes?lat=50")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Missing \"lon\" parameter.", response.bodyAsText())
        }

    @Test
    fun requiresLonToBeDouble() =
        testApplication {
            commonSetup { tideTimes(tideTimesMock) }
            val response = client.get("/tidetimes?lat=25.45&lon=frog")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Parameter \"lon\" should be Double.", response.bodyAsText())
        }
}
