import com.kayak_backend.models.BeachInfo
import com.kayak_backend.models.Location
import com.kayak_backend.routes.beaches
import com.kayak_backend.services.slipways.BeachesService
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

class BeachesRouteTest {
    private val beachesMock = mockk<BeachesService>()

    init {
        every { beachesMock.getAllBeaches() } returns listOf(BeachInfo("beach1", listOf(Location(1.0, 2.0))))
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
                    status = HttpStatusCode.BadRequest,
                )
            }
        }
        install(ContentNegotiation) {
            json()
        }
        routing { beaches(beachesMock) }
    }

    // TODO: Check error messages instead of error pages? Not sure how to
    @Test
    fun returnsBeachInfo() =
        testApplication {
            commonSetup()
            val response = client.get("/beaches")
            assertEquals(HttpStatusCode.OK, response.status)
            val encoded = Json.encodeToString(beachesMock.getAllBeaches())
            assertEquals(encoded, response.bodyAsText())
        }
}
