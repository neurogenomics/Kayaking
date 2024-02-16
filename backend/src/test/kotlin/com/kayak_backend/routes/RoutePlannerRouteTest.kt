package com.kayak_backend.routes

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kayak_backend.services.route.Route
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.junit.Test
import kotlin.test.assertEquals

class RoutePlannerRouteTest {
    @Test
    fun returnsGeneratedRoutes() =
        testApplication {
            val response = client.get("/planRoute?lat=50.5898&lon=-1.2525&duration=20.0")
            assertEquals(HttpStatusCode.OK, response.status)

            val listType = object : TypeToken<List<Route>>() {}.type
            val res = Gson().fromJson<List<Route>>(response.bodyAsText(), listType)
            assert(res.size <= 5)
        }

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
