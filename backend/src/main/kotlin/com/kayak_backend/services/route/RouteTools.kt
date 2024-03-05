package com.kayak_backend.services.route

import com.kayak_backend.models.Location

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
    if (sections.isNotEmpty()) {
        currentLegLocations.addAll(sections.removeFirst().locations)
    }
    sections.add(0, Leg.create(currentLegLocations))
    return sections
}
