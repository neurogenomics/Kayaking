import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlin.test.*

class TestTest {
    @Test
    fun testTestRoot() = testApplication {
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello, this is a kayak app", response.bodyAsText())
    }

    @Test
    fun testTestTest() = testApplication {
        val response = client.get("/test")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello, this is a kayak app", response.bodyAsText())
    }
}