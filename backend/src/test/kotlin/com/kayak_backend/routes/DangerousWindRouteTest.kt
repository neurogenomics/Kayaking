package com.kayak_backend.routes

import com.kayak_backend.services.dangerousWindWarning.DangerousWindService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

class DangerousWindRouteTest {
    private val dangerousWindServiceMock = mockk<DangerousWindService>()

    @Test
    fun requiresLatParameter() =
        testApplication {
            commonSetup { dangerousWind(dangerousWindServiceMock) }
            val response = client.get("/dangerouswind")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Missing \"lat\" parameter.", response.bodyAsText())
        }
}
