package com.kayak_backend

import com.kayak_backend.plugins.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}
fun Application.module() {
    configureSerialization()
    configureRouting()
    configureStatusPages()
}

fun Application.testModule() {
    configureStatusPages()
}


