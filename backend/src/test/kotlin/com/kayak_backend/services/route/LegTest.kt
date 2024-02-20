package com.kayak_backend.services.route

import com.kayak_backend.models.Location
import org.junit.Test
import kotlin.test.assertEquals

class LegTest {
    @Test
    fun correctlyCreatesMultipleLegs() {
        val loc1 = Location(0.0, 5.0)
        val loc2 = Location(1.0, 4.0)
        val loc3 = Location(2.0, 3.0)
        assertEquals(
            Leg.create(listOf(loc1, loc2, loc3)),
            Leg.MultipleLegs(
                listOf(
                    Leg.SingleLeg(loc1, loc2),
                    Leg.SingleLeg(loc2, loc3),
                ),
            ),
        )
    }

    @Test
    fun returnsEmptyListIfGivenZeroLocation() {
        assertEquals(Leg.create(emptyList()), Leg.MultipleLegs(emptyList()))
    }

    @Test
    fun returnsEmptyListIfGivenOneLocation() {
        assertEquals(Leg.create(listOf(Location(0.0, 0.0))), Leg.MultipleLegs(emptyList()))
    }
}
