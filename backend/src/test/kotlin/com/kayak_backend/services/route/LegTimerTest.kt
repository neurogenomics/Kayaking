package com.kayak_backend.services.route

import com.kayak_backend.models.Location
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

    @Test
    fun findsCheckPointsForASingleLegRoute() {
        val time = LocalDateTime.now()
        val route = Route(0.0, listOf(loc1, loc2))
        legTimer.getDuration(Leg.SingleLeg(loc1, loc2), time)
        val result = legTimer.getCheckpoints(route, time)
        assertEquals(2, result.size)
        assertEquals(result[0], Pair(loc1, 0L))
        assertEquals(result[1], Pair(loc2, 72204L))
    }

    @Test
    fun findsCheckPointsForAMultipleLegRoute() {
        val time = LocalDateTime.now()
        val route = Route(0.0, listOf(loc1, loc2, loc3, loc4))
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
            time,
        )
        val result = legTimer.getCheckpoints(route, time)
        assertEquals(4, result.size)
        assertEquals(result[0], Pair(loc1, 0L))
        assertEquals(result[1], Pair(loc2, 72204L))
        assertEquals(result[2], Pair(loc3, 72204L * 2))
        assertEquals(result[3], Pair(loc4, 72204L * 3))
    }

    @Test
    fun findsSingleCheckPointForRouteOfOnePoint() {
        val time = LocalDateTime.now()
        val route = Route(0.0, listOf(loc1))
        val result = legTimer.getCheckpoints(route, time)
        assertEquals(result.size, 1)
        assertEquals(result[0], Pair(loc1, 0L))
    }
}
