package com.kayak_backend.routes

import com.kayak_backend.services.route.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertEquals

class RoutePlannerRouteTest {
    private val routePlannerMock = mockk<RoutePlanner>()

    private val slowLegTimer = mockk<LegTimer>()
    private val normalLegTimer = mockk<LegTimer>()
    private val fastLegTimer = mockk<LegTimer>()
    private val legTimersMock = LegTimers(slowLegTimer, normalLegTimer, fastLegTimer)
    private val circularRoutePlannerMock = mockk<CircularRoutePlanner>()
    private val legDifficultyMock = mockk<LegDifficulty>()

    init {
        every { routePlannerMock.generateRoutes(any(), any(), any(), any()).take(5).toList() } returns
            listOf()

        every { slowLegTimer.getDuration(any(), any()) } returns 0L
        every { normalLegTimer.getDuration(any(), any()) } returns 0L
        every { fastLegTimer.getDuration(any(), any()) } returns 0L
    }

    @Test
    fun requiresLatFromParameter() =
        testApplication {
            commonSetup { planRoute(routePlannerMock, circularRoutePlannerMock, legTimersMock, legDifficultyMock) }
            val response = client.get("/planRoute?latTo=20&lonFrom=30&lonTo=40&duration=10.0")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Missing \"latFrom\" parameter.", response.bodyAsText())
        }

    @Test
    fun requiresLatFromToBeDouble() =
        testApplication {
            commonSetup { planRoute(routePlannerMock, circularRoutePlannerMock, legTimersMock, legDifficultyMock) }
            val response = client.get("/planRoute?latFrom=dog&latTo=20&lonFrom=30&lonTo=40&duration=10.0")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Parameter \"latFrom\" should be Double.", response.bodyAsText())
        }

    @Test
    fun requiresLatToParameter() =
        testApplication {
            commonSetup { planRoute(routePlannerMock, circularRoutePlannerMock, legTimersMock, legDifficultyMock) }
            val response = client.get("/planRoute?latFrom=20&lonFrom=30&lonTo=40&duration=10.0")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Missing \"latTo\" parameter.", response.bodyAsText())
        }

    @Test
    fun requiresLatToToBeDouble() =
        testApplication {
            commonSetup { planRoute(routePlannerMock, circularRoutePlannerMock, legTimersMock, legDifficultyMock) }
            val response = client.get("/planRoute?latFrom=20&latTo=dog&lonFrom=30&lonTo=40&duration=10.0")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Parameter \"latTo\" should be Double.", response.bodyAsText())
        }

    @Test
    fun requiresLonFromParameter() =
        testApplication {
            commonSetup { planRoute(routePlannerMock, circularRoutePlannerMock, legTimersMock, legDifficultyMock) }
            val response = client.get("/planRoute?latFrom=20&latTo=30&lonTo=40&duration=10.0")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Missing \"lonFrom\" parameter.", response.bodyAsText())
        }

    @Test
    fun requiresLonFromToBeDouble() =
        testApplication {
            commonSetup { planRoute(routePlannerMock, circularRoutePlannerMock, legTimersMock, legDifficultyMock) }
            val response = client.get("/planRoute?latFrom=20&latTo=30&lonFrom=frog&lonTo=40&duration=10.0")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Parameter \"lonFrom\" should be Double.", response.bodyAsText())
        }

    @Test
    fun requiresLonToParameter() =
        testApplication {
            commonSetup { planRoute(routePlannerMock, circularRoutePlannerMock, legTimersMock, legDifficultyMock) }
            val response = client.get("/planRoute?latFrom=20&latTo=30&lonFrom=40&duration=10.0")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Missing \"lonTo\" parameter.", response.bodyAsText())
        }

    @Test
    fun requiresLonToToBeDouble() =
        testApplication {
            commonSetup { planRoute(routePlannerMock, circularRoutePlannerMock, legTimersMock, legDifficultyMock) }
            val response = client.get("/planRoute?latFrom=20&latTo=30&lonFrom=40&lonTo=frog&duration=10.0")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Parameter \"lonTo\" should be Double.", response.bodyAsText())
        }

    @Test
    fun requiresDurationParameter() =
        testApplication {
            commonSetup { planRoute(routePlannerMock, circularRoutePlannerMock, legTimersMock, legDifficultyMock) }
            val response = client.get("/planRoute?latFrom=20&latTo=30&lonFrom=40&lonTo=50")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Missing \"duration\" parameter.", response.bodyAsText())
        }

    @Test
    fun requiresDurationToBeDouble() =
        testApplication {
            commonSetup { planRoute(routePlannerMock, circularRoutePlannerMock, legTimersMock, legDifficultyMock) }
            val response = client.get("/planRoute?latFrom=20&latTo=30&lonFrom=40&lonTo=50&duration=frog")
            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Parameter \"duration\" should be Double.", response.bodyAsText())
        }
}
