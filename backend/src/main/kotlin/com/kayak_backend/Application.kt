package com.kayak_backend
import com.kayak_backend.plugins.configureRouting
import com.kayak_backend.plugins.configureSerialization
import com.kayak_backend.plugins.configureStatusPages
import io.ktor.server.application.*
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

fun scheduleGribFetcher(conf: Conf) {
    val timer = Timer()
    val millisecondsInHour: Long = 3600000
    val millisecondsTillNextHour: Long = millisecondsInHour - (System.currentTimeMillis() % millisecondsInHour)
    timer.scheduleAtFixedRate(millisecondsTillNextHour, millisecondsInHour) {
        getGribFetcher(conf).fetchGrib()
    }
}

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val conf: Conf = getConf("./config.yaml")
    configureSerialization()
    configureRouting(conf)
    configureStatusPages()
    when (System.getenv("ENVIRONMENT")) {
        "dev" -> {
            getGribFetcher(conf).fetchGrib()
        }
        "prod" -> {
            scheduleGribFetcher(conf)
        }
        else -> throw IllegalStateException("ENVIRONMENT must be dev or prod in .env")
    }
}
