package com.kayak_backend.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(RequestValidation) {
    }
    install(StatusPages) {
        exception<MissingRequestParameterException> { call, cause ->
            call.respondText("Missing \"${cause.parameterName}\" parameter.", status = HttpStatusCode.BadRequest)
        }
        exception<ParameterConversionException> { call, cause ->
            call.respondText("Parameter \"${cause.parameterName}\" should be ${cause.type}", status = HttpStatusCode.BadRequest)
        }
    }
}
