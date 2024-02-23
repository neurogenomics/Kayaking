package com.kayak_backend.routes

import com.kayak_backend.services.times.TimeService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeRangeRouteTest {
    private val expected = listOf("2024-02-17T12:00", "2024-02-17T13:00")
    private val mockResponse =
        listOf(
            LocalDateTime.of(2024, 2, 17, 12, 0),
            LocalDateTime.of(2024, 2, 17, 13, 0),
        )
    private val timeServiceMock = mockk<TimeService>()

    @Test
    fun timeRangeReturnsData() =
        testApplication {
            commonSetup { times(timeServiceMock) }
            every { timeServiceMock.getTimes() } returns mockResponse
            val response = client.get("/times")
            assertEquals(response.status, HttpStatusCode.OK)
            assertEquals(Json.encodeToString<List<String>>(expected), response.bodyAsText())
        }
}
