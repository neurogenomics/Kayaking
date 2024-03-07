package com.kayak_backend.services.route

import com.kayak_backend.models.Location

class StartPos(val location: Location, val name: String)

fun splitRouteIntoSections(
    route: List<Location>,
    startPosOnRoute: Set<Location>,
): List<Leg> {
    val sections = mutableListOf<Leg>()
    var currentLegLocations = mutableListOf<Location>()
    for (location in route) {
        currentLegLocations.add(location)
        if (startPosOnRoute.contains(location)) {
            val leg = Leg.create(currentLegLocations)
            if (leg.locations.isNotEmpty()) sections.add(leg)
            currentLegLocations = mutableListOf(location)
        }
    }

    // Connect first and last section
    if (route.first() !in startPosOnRoute) {
        if (sections.isNotEmpty()) {
            currentLegLocations.addAll(sections.removeFirst().locations)
        }
        sections.add(0, Leg.create(currentLegLocations))
    }
    return sections
}

// Given a leg, create a longer leg that connects to the start and end slipways
fun connectToStart(
    route: SectionedRoute,
    leg: Leg,
): Pair<Leg, String> {
    // TODO allow route to connect to multiple start locations
    val start = route.getStartPos(leg.start)
    val end = route.getStartPos(leg.end)
    val combinedLeg =
        Leg.MultipleLegs(
            listOf(
                Leg.SingleLeg(start.location, leg.start),
                leg,
                Leg.SingleLeg(leg.end, end.location),
            ),
        )
    val name = "${start.name} to ${end.name}"
    return Pair(combinedLeg, name)
}
