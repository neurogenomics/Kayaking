package com.kayak_backend.services.route

import com.kayak_backend.models.Location
import com.kayak_backend.serialization.LegSerializer
import kotlinx.serialization.Serializable

@Serializable(with = LegSerializer::class)
sealed class Leg {
    abstract val length: Double
    abstract val start: Location
    abstract val end: Location
    abstract val locations: List<Location>

    companion object {
        fun create(locations: List<Location>): Leg {
            val legs = mutableListOf<SingleLeg>()
            for (i in 1 until locations.size) {
                legs.add(SingleLeg(locations[i - 1], locations[i]))
            }
            return MultipleLegs(legs)
        }
    }

    abstract fun reverse(): Leg

    fun midpoint(): Location {
        return Location(
            (start.latitude + end.latitude) / 2,
            (start.longitude + end.longitude) / 2,
        )
    }

    fun bearing(): Double {
        return start bearingTo end
    }

    data class SingleLeg(override val start: Location, override val end: Location) : Leg() {
        override val length = start distanceTo end
        override val locations = listOf(start, end)

        override fun reverse(): Leg {
            return SingleLeg(start = end, end = start)
        }
    }

    data class MultipleLegs(val legs: List<Leg>) : Leg() {
        override val length = legs.sumOf { it.length }
        override val start = legs.first().start
        override val end = legs.last().end

        override val locations by lazy {
            // Remove last location from each leg as its equal to first location of next leg
            // Except for final leg
            legs.flatMapIndexed { index, leg ->
                if (index == legs.size - 1) {
                    leg.locations
                } else {
                    leg.locations.dropLast(1)
                }
            }
        }

        override fun reverse(): Leg {
            return MultipleLegs(legs.reversed().map(Leg::reverse))
        }
    }
}
