package com.kayak_backend.services.route

import com.kayak_backend.models.Location
import com.kayak_backend.services.route.kayak.BasicKayak
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class LegTimerTest {
    private val legTimer = LegTimer(BasicKayak())
    private val loc1 = Location(0.0, 0.0)
    private val loc2 = Location(0.0, 1.0)
    private val loc3 = Location(0.0, 2.0)
    private val loc4 = Location(0.0, 3.0)

    @Test
    fun findsDurationForSingleLeg() {
        assertEquals(legTimer.getDuration(Leg.SingleLeg(loc1, loc2), LocalDateTime.now()), 72204)
    }

    @Test
    fun findsCompoundDurationForMultipleLegs() {
        val res1 = legTimer.getDuration(Leg.SingleLeg(loc1, loc2), LocalDateTime.now())
        val res2 = legTimer.getDuration(Leg.SingleLeg(loc2, loc3), LocalDateTime.now())
        val res3 = legTimer.getDuration(Leg.SingleLeg(loc3, loc4), LocalDateTime.now())
        val res =
            legTimer.getDuration(
                Leg.MultipleLegs(
                    listOf(
                        Leg.SingleLeg(loc1, loc2),
                        Leg.MultipleLegs(
                            listOf(
                                Leg.SingleLeg(loc2, loc3),
                                Leg.SingleLeg(loc3, loc4),
                            ),
                        ),
                    ),
                ),
                LocalDateTime.now(),
            )
        assertEquals(res, res1 + res2 + res3)
    }
}
