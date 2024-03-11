package com.kayak_backend.services.route

import com.kayak_backend.models.Location
import org.junit.Test
import kotlin.test.assertEquals

class LegTest {
    private val loc1 = Location(0.0, 5.0)
    private val loc2 = Location(1.0, 4.0)
    private val loc3 = Location(2.0, 3.0)
    private val loc4 = Location(3.0, 2.0)

    @Test
    fun correctlyCreatesMultipleLegs() {
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
    fun legReverseReversesSingleLeg() {
        val l = Leg.SingleLeg(loc1, loc2)
        assertEquals(l.reverse(), Leg.SingleLeg(loc2, loc1))
    }

    @Test
    fun legReverseReversesMultipleLeg() {
        val l = Leg.SingleLeg(loc1, loc2)
        val lr = Leg.SingleLeg(loc2, loc1)

        val l2 = Leg.SingleLeg(loc2, loc3)
        val l2r = Leg.SingleLeg(loc3, loc2)

        val l3 = Leg.SingleLeg(loc3, loc4)
        val l3r = Leg.SingleLeg(loc4, loc3)

        val ml = Leg.MultipleLegs(listOf(l, l2))
        val mlr = Leg.MultipleLegs(listOf(l2r, lr))

        val mll = Leg.MultipleLegs(listOf(ml, l3))
        val mllr = Leg.MultipleLegs(listOf(l3r, mlr))

        assertEquals(mll.reverse(), mllr)
    }
}
