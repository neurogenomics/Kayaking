package com.kayak_backend.services.route

import com.kayak_backend.models.Location

class SectionIterator(private val sections: List<Leg>, private var currentIndex: Int = 0, private var step: Int = 1) :
    Iterator<Leg> {
    override fun hasNext(): Boolean = true

    override fun next(): Leg {
        val leg = sections[currentIndex]
        currentIndex = (currentIndex + step) % sections.size
        return leg
    }
}

class SectionCombiner(sections: List<Leg>, currentIndex: Int = 0, step: Int = 1) :
    Iterator<Leg> {
    private val sectionIterator = SectionIterator(sections, currentIndex, step)
    private var current: Leg? = null

    override fun hasNext(): Boolean = true

    override fun next(): Leg {
        var temp = current
        val newLeg = sectionIterator.next()
        if (temp == null) {
            current = newLeg
            return newLeg
        }
        temp = Leg.MultipleLegs(listOf(temp, newLeg))
        current = temp
        return temp
    }
}

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
