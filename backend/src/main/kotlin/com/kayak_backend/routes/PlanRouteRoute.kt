package com.kayak_backend.routes

import com.kayak_backend.models.Location
import com.kayak_backend.services.coastline.IsleOfWightCoastline
import com.kayak_backend.services.route.RoutePlanner
import com.kayak_backend.services.route.StartPos
import com.kayak_backend.services.route.createBaseRoute
import com.kayak_backend.services.slipways.SlipwayService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Route.planRoute() {
    val coast = IsleOfWightCoastline().getCoastline()
    val route = createBaseRoute(coast, 500.0)
    val slipways = SlipwayService().getAllSlipways()
    val startPos = slipways.mapIndexed { index, location -> StartPos(location, "Slipway $index") }
    val routePlanner = RoutePlanner(route, startPos)

    route("/planRoute") {
        get {
            val lat = call.parameters.getOrFail<Double>("lat")
            val lng = call.parameters.getOrFail<Double>("lon")
            val location = Location(50.642384, -1.167471)
            val routes = routePlanner.generateRoutes(location, 5000.0, { it.length < 5000 }).take(300).toList()
            call.respond(routes)
        }
    }
}
