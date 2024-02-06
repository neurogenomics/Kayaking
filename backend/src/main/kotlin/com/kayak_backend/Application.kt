package com.kayak_backend

import com.kayak_backend.plugins.configureRouting
import com.kayak_backend.plugins.configureSerialization
import com.kayak_backend.plugins.configureStatusPages
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val conf: Conf = getConf("./config.yaml")
    configureSerialization()
    configureRouting(conf)
    configureStatusPages()
    when (dotenv()["ENVIRONMENT"]) {
        "dev" -> {
            getGribFetcher(conf).fetchGrib()
        }
        "prod" -> {
            val timer = Timer()
            val millisecondsInHour: Long = 3600000
            val millisecondsTillNextHour: Long = millisecondsInHour - (System.currentTimeMillis() % millisecondsInHour)
            timer.scheduleAtFixedRate(millisecondsTillNextHour, millisecondsInHour) {
                launch {
                    getGribFetcher(conf).fetchGrib()
                }
            }
        }
        else -> throw IllegalStateException("ENVIRONMENT must be dev or prod in .env")
    }
}
