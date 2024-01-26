import com.kayak_backend.models.Location
import com.kayak_backend.routes.slipway
import com.kayak_backend.services.slipways.SlipwayService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.*

class SlipwayRouteTest {

    private val slipwayMock = mockk<SlipwayService>()
    //private val sunsetInfoMock = SunsetInfo(LocalTime.of(10, 30), LocalTime.of(6,30))

    init {
        every { slipwayMock.closestSlipway(any()) } returns Location(0.0, 0.0);
    }

    // TODO: Reuse original setup or move to separate file
    private fun TestApplicationBuilder.commonSetup() {
        environment {
            config = MapApplicationConfig()
        }
        install(StatusPages) {
            exception<MissingRequestParameterException> { call, cause ->
                call.respondText("Missing \"${cause.parameterName}\" parameter.", status = HttpStatusCode.BadRequest)
            }
            exception<ParameterConversionException> { call, cause ->
                call.respondText(
                    "Parameter \"${cause.parameterName}\" should be ${cause.type}.",
                    status = HttpStatusCode.BadRequest
                )
            }
        }
        install(ContentNegotiation) {
            json()
        }
        routing { slipway(slipwayMock) }
    }

    // TODO: Check error messages instead of error pages? Not sure how to
    @Test
    fun returnsLocation() = testApplication {
        commonSetup()
        val response = client.get("/slipway?lat=50.64&lng=60")
        assertEquals(HttpStatusCode.OK, response.status)
        val encoded = Json.encodeToString(slipwayMock.closestSlipway(Location(0.0, 0.0)))
        assertEquals(encoded, response.bodyAsText())
    }
    @Test
    fun requiresLatParameter() = testApplication {
        commonSetup()
        val response = client.get("/slipway?lng=20")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Missing \"lat\" parameter.", response.bodyAsText())
    }

    @Test
    fun requiresLatToBeDouble() = testApplication {
        commonSetup()
        val response = client.get("/slipway?lat=dog&lng=40")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Parameter \"lat\" should be Double.", response.bodyAsText())
    }

    @Test
    fun requiresLngParameter() = testApplication {
        commonSetup()
        val response = client.get("/slipway?lat=50")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Missing \"lng\" parameter.", response.bodyAsText())
    }

    @Test
    fun requiresLngToBeDouble() = testApplication {
        commonSetup()
        val response = client.get("/slipway?lat=25.45&lng=yoel")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Parameter \"lng\" should be Double.", response.bodyAsText())
    }

}