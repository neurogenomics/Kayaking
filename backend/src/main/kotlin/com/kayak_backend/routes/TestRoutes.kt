package com.kayak_backend.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.testRouting() {
    route("/") {
        get {
            call.respondText("Hello, this is a kayak app")
        }
    }
    route("/test") {
        get {
            call.respondText("Hello, this is a kayak app")
        }
    }
}