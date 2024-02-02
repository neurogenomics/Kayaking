package com.kayak_backend.routes

import com.kayak_backend.models.WindInfo
import com.kayak_backend.services.wind.WindService
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

class WindRouteTest {
    private val windServiceMock = mockk<WindService>()
    private val windInfoMock = WindInfo(1.0, -1.0)

    init {
        every { windServiceMock.getWind(any(), any()) } returns windInfoMock
    }

    // TODO: Check error messages instead of error pages? Not sure how to
    @Test
    fun returnswindInfo() =
        testApplication {
            commonSetup { wind(windServiceMock) }
            val response = client.get("/wind?lat=50.64&lon=60&date=2024-01-01")
            assertEquals(HttpStatusCode.OK, response.status)
            val encoded = Json.encodeToString(windInfoMock)
            assertEquals(encoded, response.bodyAsText())
        }

    @Test
    fun requiresLatParameter() =
        testApplication {
            commonSetup { wind(windServiceMock) }
            val response = client.get("/wind?lon=20")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Missing \"lat\" parameter.", response.bodyAsText())
        }

    @Test
    fun requiresLatToBeDouble() =
        testApplication {
            commonSetup { wind(windServiceMock) }
            val response = client.get("/wind?lat=dog&lon=40")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Parameter \"lat\" should be Double.", response.bodyAsText())
        }

    @Test
    fun requireslonParameter() =
        testApplication {
            commonSetup { wind(windServiceMock) }
            val response = client.get("/wind?lat=50")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Missing \"lon\" parameter.", response.bodyAsText())
        }

    @Test
    fun requireslonToBeDouble() =
        testApplication {
            commonSetup { wind(windServiceMock) }
            val response = client.get("/wind?lat=25.45&lon=yoel")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Parameter \"lon\" should be Double.", response.bodyAsText())
        }

    @Test
    fun doesNotRequireDate() =
        testApplication {
            commonSetup { wind(windServiceMock) }
            val response = client.get("/wind?lat=50.64&lon=60")
            assertEquals(HttpStatusCode.OK, response.status)
            val encoded = Json.encodeToString(windInfoMock)
            assertEquals(encoded, response.bodyAsText())
        }
}
