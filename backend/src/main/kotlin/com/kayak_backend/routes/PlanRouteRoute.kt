package com.kayak_backend.routes

import com.kayak_backend.models.Location
import com.kayak_backend.services.coastline.IsleOfWightCoastline
import com.kayak_backend.services.route.*
import com.kayak_backend.services.route.RoutePlanner
import com.kayak_backend.services.route.StartPos
import com.kayak_backend.services.route.createBaseRoute
import com.kayak_backend.services.slipways.BeachesService
import com.kayak_backend.services.slipways.SlipwayService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.Route
import io.ktor.server.util.*

fun Route.planRoute() {
    val coast = IsleOfWightCoastline().getCoastline()
    val route = createBaseRoute(coast, 500.0)
    val slipways = SlipwayService().getAllSlipways()
    val beaches = BeachesService().getAllBeaches()
    val slipwayStarts = slipways.mapIndexed { index, location -> StartPos(location, "Slipway $index") }
    val beachStarts = beaches.map { beachInfo -> StartPos(beachInfo.avergeLocation, beachInfo.name ?: "Unnamed beach") }
    val startPositions = slipwayStarts.plus(beachStarts)
    val routePlanner = RoutePlanner(route, startPositions)
    val legTimer = LegTimer(BasicKayak())

    route("/planRoute") {
        get {
            val lat = call.parameters.getOrFail<Double>("lat")
            val lng = call.parameters.getOrFail<Double>("lon")
            val duration = call.parameters.getOrFail<Double>("duration")
            val startTime = getDateParameter(call.parameters, "startDateTime")
            val location = Location(lat, lng)
            val routes =
                routePlanner.generateRoutes(
                    { location.distance(it.location) < 5000 },
                    { legTimer.getDuration(it, startTime) < duration * 60 },
                ).take(5).toList()
            call.respond(routes)
        }
    }
}
