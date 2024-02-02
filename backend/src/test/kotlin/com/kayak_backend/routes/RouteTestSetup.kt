package com.kayak_backend.routes

import com.kayak_backend.models.TideInfo
import com.kayak_backend.services.tides.TideService
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk

fun TestApplicationBuilder.commonSetup(configuration : io.ktor.server.routing.Routing.() -> Unit){

    val tideServiceMock = mockk<TideService>()
    val tideInfoMock = TideInfo(1.0, -1.0)
    every { tideServiceMock.getTide(any(), any()) } returns tideInfoMock

    environment {
        config = MapApplicationConfig()
    }
    install(StatusPages) {
        exception<MissingRequestParameterException> { call, cause ->
            call.respondText("Missing \"${cause.parameterName}\" parameter.", status = HttpStatusCode.BadRequest)
        }
        exception<ParameterConversionException> { call, cause ->
            call.respondText(
                "Parameter \"${cause.parameterName}\" should be ${cause.type}.",
                status = HttpStatusCode.BadRequest
            )
        }
    }
    install(ContentNegotiation) {
        json()
    }
    routing(configuration)
}
