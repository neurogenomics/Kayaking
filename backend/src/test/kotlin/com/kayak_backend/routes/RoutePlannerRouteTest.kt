package com.kayak_backend.routes

import com.kayak_backend.services.route.LegDifficulty
import com.kayak_backend.services.route.LegTimer
import com.kayak_backend.services.route.RoutePlanner
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertEquals

class RoutePlannerRouteTest {
    private val routePlannerMock = mockk<RoutePlanner>()
    private val legTimerMock = mockk<LegTimer>()
    private val legDifficultyMock = mockk<LegDifficulty>()

    init {
        every { routePlannerMock.generateRoutes(any(), any()).take(5).toList() } returns
            listOf()
        every { legTimerMock.getDuration(any(), any()) } returns 0L
    }

    @Test
    fun requiresLatParameter() =
        testApplication {
            commonSetup { planRoute(routePlannerMock, legTimerMock, legDifficultyMock) }
            val response = client.get("/planRoute?lng=20&duration=10.0")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Missing \"lat\" parameter.", response.bodyAsText())
        }

    @Test
    fun requiresLatToBeDouble() =
        testApplication {
            commonSetup { planRoute(routePlannerMock, legTimerMock, legDifficultyMock) }
            val response = client.get("/planRoute?lat=dog&lon=40&duration=10.0")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Parameter \"lat\" should be Double.", response.bodyAsText())
        }

    @Test
    fun requiresLonParameter() =
        testApplication {
            commonSetup { planRoute(routePlannerMock, legTimerMock, legDifficultyMock) }
            val response = client.get("/planRoute?lat=50&duration=10.0")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Missing \"lon\" parameter.", response.bodyAsText())
        }

    @Test
    fun requiresLonToBeDouble() =
        testApplication {
            commonSetup { planRoute(routePlannerMock, legTimerMock, legDifficultyMock) }

            val response = client.get("/planRoute?lat=25.45&lon=frog&duration=10.0")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Parameter \"lon\" should be Double.", response.bodyAsText())
        }

    @Test
    fun requiresDurationParameter() =
        testApplication {
            commonSetup { planRoute(routePlannerMock, legTimerMock, legDifficultyMock) }
            val response = client.get("/planRoute?lat=50&lon=10")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Missing \"duration\" parameter.", response.bodyAsText())
        }

    @Test
    fun requiresDurationToBeDouble() =
        testApplication {
            commonSetup { planRoute(routePlannerMock, legTimerMock, legDifficultyMock) }
            val response = client.get("/planRoute?lat=25.45&lon=10.0&duration=frog")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Parameter \"duration\" should be Double.", response.bodyAsText())
        }
}
