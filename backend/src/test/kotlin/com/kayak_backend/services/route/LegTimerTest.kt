package com.kayak_backend.services.route

import com.kayak_backend.models.Location
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class LegTimerTest {
    private val legTimer = LegTimer(BasicKayak())
    private val name = "name"
    private val loc1 = Location(0.0, 0.0)
    private val loc2 = Location(0.0, 1.0)
    private val loc3 = Location(0.0, 2.0)
    private val loc4 = Location(0.0, 3.0)

    private val leg1 = Leg.SingleLeg(loc1, loc2)
    private val leg2 = Leg.SingleLeg(loc2, loc3)
    private val leg3 = Leg.SingleLeg(loc3, loc4)
    private val multiLeg =
        Leg.MultipleLegs(
            listOf(
                leg1,
                Leg.MultipleLegs(
                    listOf(
                        leg2,
                        leg3,
                    ),
                ),
            ),
        )

    @Test
    fun findsDurationForSingleLeg() {
        assertEquals(legTimer.getDuration(Leg.SingleLeg(loc1, loc2), LocalDateTime.now()), 72204)
    }

    @Test
    fun findsCompoundDurationForMultipleLegs() {
        val res1 = legTimer.getDuration(leg1, LocalDateTime.now())
        val res2 = legTimer.getDuration(leg2, LocalDateTime.now())
        val res3 = legTimer.getDuration(leg3, LocalDateTime.now())
        val res =
            legTimer.getDuration(
                multiLeg,
                LocalDateTime.now(),
            )
        assertEquals(res, res1 + res2 + res3)
    }

    @Test
    fun findsCheckPointsForASingleLegRoute() {
        val time = LocalDateTime.now()
        val route = Route(name, 0.0, leg1)
        val result = legTimer.getCheckpoints(route, time)
        assertEquals(2, result.size)
        assertEquals(result[0], 0)
        assertEquals(result[1], 72204L)
    }

    @Test
    fun findsCheckPointsForAMultipleLegRoute() {
        val time = LocalDateTime.now()
        val route = Route(name, 0.0, multiLeg)
        val result = legTimer.getCheckpoints(route, time)
        assertEquals(4, result.size)
        assertEquals(result[0], 0)
        assertEquals(result[1], 72204L)
        assertEquals(result[2], 72204L * 2)
        assertEquals(result[3], 72204L * 3)
    }
}
