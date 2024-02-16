package com.kayak_backend.services.route

import com.kayak_backend.models.Location
import io.mockk.every
import io.mockk.mockk
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Polygon
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class RoutePlannerTest {
    private val legTimerMock = mockk<LegTimer>()
    private val polygonMaker = PolygonMaker()

    private val startPos: List<StartPos> =
        listOf(
            StartPos(Location(1.5, -1.5), "Start1"),
            StartPos(Location(1.0, -1.0), "Start2"),
            StartPos(Location(-1.5, -1.5), "Start3"),
            StartPos(Location(-1.0, 1.0), "Start4"),
        )

    @Test
    fun generatesRoutesWhichStartAndEndAtValidPoints() {
        val location = Location(0.0, 0.0)
        val startTime = LocalDateTime.now()
        val duration = 50L
        val polygon = polygonMaker.createPolygon(polygonMaker.polygon1)
        val routePlanner = RoutePlanner(polygon, startPos, 100000000)

        every { legTimerMock.getDuration(any(), any()) } returns 4L

        val result = (
            routePlanner.generateRoutes(
                { location distanceTo it.location < 1000000000.0 },
                { legTimerMock.getDuration(it, startTime) < duration * 60 },
            ).take(5).toList()
        )

        for (route in result) {
            assert(startPos.any { it.location == route.locations.first() })
            assert(startPos.any { it.location == route.locations.last() })
        }
    }

    /*
     * TODO:
     *  route planner seems to have timing bugs when the start/end point of the polygon is a startpoint
     * (i only say timing because the test would pass if i went through with the debugger but fail if i just ran it)
     * the problem might of also included the fact that i had a start point already on the base route?
     *
     * */

    @Test
    fun withOnlyOneStartPointDoesNotSplitRouteIntoSections() {}

    @Test
    fun returnsEmptySequenceWhenNoRoutesPossibleWithinDuration() {}

    @Test
    fun returnsEmptySequenceWhenNoStartingPointsCloseEnough() {}

    @Test
    fun returnsLongestPossibleRoutesOutOfThoseGenerated() {}

    @Test
    fun sectionCombinerHasNext() {
        val polygon = polygonMaker.createPolygon(polygonMaker.polygon1)
        val routePlanner = RoutePlanner(polygon, startPos, 10)
        val sectionCombiner = routePlanner.SectionCombiner()
        assert(sectionCombiner.hasNext())
    }

    // SectionCombiner tests
    @Test
    fun sectionCombinerGetNext() {
        val polygon = polygonMaker.createPolygon(polygonMaker.polygon1)
        val routePlanner = RoutePlanner(polygon, startPos, 10000)
        val sectionCombiner = routePlanner.SectionCombiner()

        val loc1 = Location(-1.5, -1.5)
        val loc2 = Location(-1.5, 1.5)
        val loc3 = Location(1.5, 1.5)
        val loc4 = Location(1.5, -1.5)

        val leg1 = Leg.SingleLeg(loc1, loc2)
        val leg2 = Leg.SingleLeg(loc2, loc3)
        // leg to itself because it's the start and end point of the polygon
        val leg3 = Leg.SingleLeg(loc3, loc3)
        val leg4 = Leg.SingleLeg(loc3, loc4)

        val expectedCombinedLeg = Leg.MultipleLegs(listOf(leg1, leg2, leg3, leg4))

        val actualLeg = sectionCombiner.next()

        assertEquals(expectedCombinedLeg, actualLeg)
    }

    class PolygonMaker() {
        private val geometryFactory = GeometryFactory()

        val polygon1 =
            arrayOf(
                Coordinate(1.5, 1.5),
                Coordinate(1.5, -1.5),
                Coordinate(-1.5, -1.5),
                Coordinate(-1.5, 1.5),
                Coordinate(1.5, 1.5),
            )

        val polygon2 =
            arrayOf(
                Coordinate(1.0, 1.0),
                Coordinate(1.5, -1.5),
                Coordinate(-1.5, -1.5),
                Coordinate(-1.5, 1.5),
                Coordinate(1.0, 1.0),
            )

        fun createPolygon(vertices: Array<Coordinate>): Polygon {
            val linearRing = geometryFactory.createLinearRing(vertices)

            return geometryFactory.createPolygon(linearRing)
        }
    }
}
