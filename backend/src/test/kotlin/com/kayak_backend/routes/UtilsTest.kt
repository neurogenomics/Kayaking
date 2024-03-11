package com.kayak_backend.routes

import io.ktor.http.*
import io.ktor.server.plugins.*
import org.junit.Test
import java.time.Duration
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UtilsTest {
    @Test
    fun throwsErrorForInvalidDate() {
        val params = ParametersBuilder(0)
        params["testDateTime"] = "test"
        assertFailsWith<ParameterConversionException> {
            getDateTimeParameter(params.build(), "testDateTime")
        }
    }

    @Test
    fun defaultsToCurrentDateTimeIfNoParameter() {
        val params = ParametersBuilder(0)
        assert(
            Duration.between(
                LocalDateTime.now(),
                getDateTimeParameter(params.build(), "testDateTime"),
            ) < Duration.ofSeconds(5),
        )
    }

    @Test
    fun successfullyParsesDateTimeArgument() {
        val params = ParametersBuilder(0)
        params["testDateTime"] = "2024-01-24T00:00:00"
        assertEquals(getDateTimeParameter(params.build(), "testDateTime"), LocalDateTime.of(2024, 1, 24, 0, 0, 0))
    }
}
