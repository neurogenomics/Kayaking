package com.kayak_backend.services.route

import com.kayak_backend.models.Location

sealed class Leg {
    abstract val length: Double
    abstract val start: Location
    abstract val end: Location
    abstract val locations: List<Location>
    val bearing: Double by lazy { start bearingTo end }

    companion object {
        fun create(locations: List<Location>): Leg {
            val legs = mutableListOf<SingleLeg>()
            for (i in 1 until locations.size) {
                legs.add(SingleLeg(locations[i - 1], locations[i]))
            }
            return MultipleLegs(legs)
        }
    }

    data class SingleLeg(override val start: Location, override val end: Location) : Leg() {
        override val length: Double by lazy { start.distance(end) }
        override val locations by lazy { listOf(start, end) }
    }

    data class MultipleLegs(val legs: List<Leg>) : Leg() {
        override val length: Double by lazy { legs.sumOf { it.length } }
        override val start: Location by lazy { legs.first().start }
        override val end: Location by lazy { legs.last().end }
        override val locations by lazy { legs.flatMap { leg -> leg.locations } } // Need to fix
    }
}
