package com.kayak_backend.routes

import com.kayak_backend.models.Location
import com.kayak_backend.services.dangerousWindWarning.DangerousWindService
import io.ktor.client.request.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test

class DangerousWindRouteTest {
    private val dangerousWindServiceMock = mockk<DangerousWindService>()
    private val locations = listOf(Location(37.7749, -122.4194), Location(40.7128, -74.0060))
    private val checkpoints = listOf(1234567890, 2345678901, 3456789012)
    private val date = java.time.LocalDateTime.of(2024, 3, 11, 12, 30)
    private val json = """
        {
          "locations": [
            {
              "latitude": 37.7749,
              "longitude": -122.4194
            },
            {
              "latitude": 40.7128,
              "longitude": -74.0060
            }
          ],
          "checkpoints": [1234567890, 2345678901, 3456789012],
          "date": "2024-03-11T12:30:00"
        }
    """
    private val request = buildRequest()

    @Test
    fun extractsWindPointsFromRequestBody() {
        every { dangerousWindServiceMock.findBadWinds(any(), any(), any()) } returns emptyList()
        testApplication {
            commonSetup { dangerousWind(dangerousWindServiceMock) }
            client.post(request)
            verify(exactly = 1) { dangerousWindServiceMock.findBadWinds(locations, checkpoints, date) }
        }
    }

    private fun buildRequest(): HttpRequestBuilder {
        val request = HttpRequestBuilder()

        request.url("/dangerouswind")
        request.setBody(json)
        return request
    }
}
