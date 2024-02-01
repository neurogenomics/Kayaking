package com.kayak_backend.plugins

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


fun Application.configureRouting() {
    val dotenv = dotenv()
    routing {
        testRouting()
        sunset()
        slipway()
        tide()
        tideTimes(AdmiraltyTideTimeService(dotenv["ADMIRALTY_API_KEY"]))
        wind()
    }
}
