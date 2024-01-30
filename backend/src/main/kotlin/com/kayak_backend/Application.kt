package com.kayak_backend

import com.kayak_backend.plugins.configureRouting
import com.kayak_backend.plugins.configureSerialization
import com.kayak_backend.plugins.configureStatusPages
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureRouting(getConf("./config.yaml"))
    configureStatusPages()
}

fun Application.testModule() {
    configureStatusPages()
}
