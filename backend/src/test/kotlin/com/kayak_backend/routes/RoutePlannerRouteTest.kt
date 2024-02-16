package com.kayak_backend.routes

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.junit.Test
import kotlin.test.assertEquals

// TODO make this work without admirality api key
class RoutePlannerRouteTest {
    @Test
    fun requiresLatParameter() =
        testApplication {
            val response = client.get("/planRoute?lng=20&duration=10.0")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Missing \"lat\" parameter.", response.bodyAsText())
        }

    @Test
    fun requiresLatToBeDouble() =
        testApplication {
            val response = client.get("/planRoute?lat=dog&lon=40&duration=10.0")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Parameter \"lat\" should be Double", response.bodyAsText())
        }

    @Test
    fun requiresLonParameter() =
        testApplication {
            val response = client.get("/planRoute?lat=50&duration=10.0")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Missing \"lon\" parameter.", response.bodyAsText())
        }

    @Test
    fun requiresLonToBeDouble() =
        testApplication {
            val response = client.get("/planRoute?lat=25.45&lon=frog&duration=10.0")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Parameter \"lon\" should be Double", response.bodyAsText())
        }

    @Test
    fun requiresDurationParameter() =
        testApplication {
            val response = client.get("/planRoute?lat=50&lon=10")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Missing \"duration\" parameter.", response.bodyAsText())
        }

    @Test
    fun requiresDurationToBeDouble() =
        testApplication {
            val response = client.get("/planRoute?lat=25.45&lon=10.0&duration=frog")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Parameter \"duration\" should be Double", response.bodyAsText())
        }
}
