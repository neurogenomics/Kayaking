package com.kayak_backend.routes

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Assert.assertEquals
import org.junit.Test

class TestRouteTest {
    // to check app runs
    @Test
    fun testTestRoot() = testApplication {
        commonSetup { testRouting() }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello, this is a kayak app", response.bodyAsText())
    }

    @Test
    fun testTestTest() = testApplication {
        commonSetup { testRouting() }
        val response = client.get("/test")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello, this is a kayak app", response.bodyAsText())
    }
}