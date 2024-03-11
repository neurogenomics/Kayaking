package com.kayak_backend.routes

import com.kayak_backend.models.WaveGrid
import com.kayak_backend.models.WaveInfo
import com.kayak_backend.services.waves.WaveService
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

class WaveRouteTest {
    private val waveServiceMock = mockk<WaveService>()
    private val waveInfoMock = WaveInfo(1.0, -1.0)
    private val waveGridMock =
        WaveGrid(
            listOf(
                listOf(WaveInfo(0.5, -0.5), WaveInfo(1.0, -1.0)),
                listOf(WaveInfo(1.5, -1.5), WaveInfo(2.0, -2.0)),
            ),
            listOf(50.0, 51.0),
            listOf(-1.2, -1.1),
        )

    init {
        every { waveServiceMock.getWave(any(), any()) } returns waveInfoMock
        every { waveServiceMock.getWaveGrid(any(), any(), any(), any()) } returns waveGridMock
    }

    // TODO: Check error messages instead of error pages? Not sure how to
    @Test
    fun returnsWaveInfo() =
        testApplication {
            commonSetup { wave(waveServiceMock) }
            val response = client.get("/wave?lat=50.64&lon=60&date=2024-01-01")
            assertEquals(HttpStatusCode.OK, response.status)
            val encoded = Json.encodeToString(waveInfoMock)
            assertEquals(encoded, response.bodyAsText())
        }

    @Test
    fun requiresLatParameter() =
        testApplication {
            commonSetup { wave(waveServiceMock) }
            val response = client.get("/wave?lon=20")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Missing \"lat\" parameter.", response.bodyAsText())
        }

    @Test
    fun requiresLatToBeDouble() =
        testApplication {
            commonSetup { wave(waveServiceMock) }
            val response = client.get("/wave?lat=dog&lon=40")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Parameter \"lat\" should be Double.", response.bodyAsText())
        }

    @Test
    fun requireslonParameter() =
        testApplication {
            commonSetup { wave(waveServiceMock) }
            val response = client.get("/wave?lat=50")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Missing \"lon\" parameter.", response.bodyAsText())
        }

    @Test
    fun requireslonToBeDouble() =
        testApplication {
            commonSetup { wave(waveServiceMock) }
            val response = client.get("/wave?lat=25.45&lon=yoel")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Parameter \"lon\" should be Double.", response.bodyAsText())
        }

    @Test
    fun doesNotRequireDate() =
        testApplication {
            commonSetup { wave(waveServiceMock) }
            val response = client.get("/wave?lat=50.64&lon=60")
            assertEquals(HttpStatusCode.OK, response.status)
            val encoded = Json.encodeToString(waveInfoMock)
            assertEquals(encoded, response.bodyAsText())
        }

    @Test
    fun returnsWaveGrid() =
        testApplication {
            commonSetup { wave(waveServiceMock) }
            val response = client.get("/waveGrid?latFrom=50.0&latTo=51.0&lonFrom=-1.2&lonTo=-1.1&latRes=1.0&lonRes=0.1")
            assertEquals(HttpStatusCode.OK, response.status)
            val encoded = Json.encodeToString(waveGridMock)
            assertEquals(encoded, response.bodyAsText())
        }
}
