package com.kayak_backend.routes

import com.kayak_backend.models.TideGrid
import com.kayak_backend.models.TideInfo
import com.kayak_backend.services.tides.TideService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class TideRouteTest {
    private val tideServiceMock = mockk<TideService>()
    private val tideInfoMock = TideInfo(1.0, -1.0)
    private val tideGridMock = TideGrid(
        listOf(
            listOf(TideInfo(0.5, -0.5), TideInfo(1.0, -1.0)),
            listOf(TideInfo(1.5, -1.5), TideInfo(2.0, -2.0)),
        ),
        listOf(50.0, 51.0),
        listOf(-1.2, -1.1),
    )

    init {
        every { tideServiceMock.getTide(any(), any()) } returns tideInfoMock
        every { tideServiceMock.getTideGrid(any(), any(), any(), any()) } returns tideGridMock
    }

    // TODO: Check error messages instead of error pages? Not sure how to
    @Test
    fun returnsTideInfo() =
        testApplication {
            commonSetup { tide(tideServiceMock) }
            val response = client.get("/tide?lat=50.64&lon=60&date=2024-01-01")
            assertEquals(HttpStatusCode.OK, response.status)
            val encoded = Json.encodeToString(tideInfoMock)
            assertEquals(encoded, response.bodyAsText())
        }

    @Test
    fun requiresLatParameter() =
        testApplication {
            commonSetup { tide(tideServiceMock) }
            val response = client.get("/tide?lon=20")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Missing \"lat\" parameter.", response.bodyAsText())
        }

    @Test
    fun requiresLatToBeDouble() =
        testApplication {
            commonSetup { tide(tideServiceMock) }
            val response = client.get("/tide?lat=dog&lon=40")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Parameter \"lat\" should be Double.", response.bodyAsText())
        }

    @Test
    fun requireslonParameter() =
        testApplication {
            commonSetup { tide(tideServiceMock) }
            val response = client.get("/tide?lat=50")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Missing \"lon\" parameter.", response.bodyAsText())
        }

    @Test
    fun requireslonToBeDouble() =
        testApplication {
            commonSetup { tide(tideServiceMock) }
            val response = client.get("/tide?lat=25.45&lon=yoel")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Parameter \"lon\" should be Double.", response.bodyAsText())
        }

    @Test
    fun doesNotRequireDate() =
        testApplication {
            commonSetup { tide(tideServiceMock) }
            val response = client.get("/tide?lat=50.64&lon=60")
            assertEquals(HttpStatusCode.OK, response.status)
            val encoded = Json.encodeToString(tideInfoMock)
            assertEquals(encoded, response.bodyAsText())
        }

    @Test
    fun returnsTideGrid() =
        testApplication {
            commonSetup { tide(tideServiceMock) }
            val response = client.get("/tideGrid?latFrom=50.0&latTo=51.0&lonFrom=-1.2&lonTo=-1.1&latRes=1.0&lonRes=0.1")
            assertEquals(HttpStatusCode.OK, response.status)
            val encoded = Json.encodeToString(tideGridMock)
            assertEquals(encoded, response.bodyAsText())
        }
}
