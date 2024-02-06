package com.kayak_backend.plugins

import com.kayak_backend.Conf
import com.kayak_backend.getTideService
import com.kayak_backend.getWindService
import com.kayak_backend.routes.slipway
import com.kayak_backend.routes.sunset
import com.kayak_backend.routes.testRouting
import com.kayak_backend.routes.tide
import com.kayak_backend.routes.tideTimes
import com.kayak_backend.routes.wind
import com.kayak_backend.services.tideTimes.AdmiraltyTideTimeService
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(conf: Conf) {
    val dotenv = dotenv()

    routing {
        testRouting()
        sunset()
        slipway()
        tideTimes(AdmiraltyTideTimeService(dotenv["ADMIRALTY_API_KEY"]))

        tide(getTideService(conf))
        wind(getWindService(conf))
    }
}
