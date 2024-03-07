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

    private val polygon: Polygon
    private val location = Location(0.0, 0.0)
    private val startTime = LocalDateTime.now()
    private val duration = 50L
    private val startPos: List<NamedLocation> =
        listOf(
            NamedLocation(Location(1.5, -1.5), "Start1"),
            NamedLocation(Location(1.0, -1.0), "Start2"),
            NamedLocation(Location(-1.5, -1.5), "Start3"),
            NamedLocation(Location(-1.0, 1.0), "Start4"),
        )

    private val loc1 = Location(-1.5, -1.5)
    private val loc2 = Location(-1.5, 1.5)
    private val loc3 = Location(1.5, 1.5)
    private val loc4 = Location(1.5, -1.5)

    private val leg1 = Leg.SingleLeg(loc1, loc2)
    private val leg2 = Leg.SingleLeg(loc2, loc3)

    // leg to itself because it's the start and end point of the polygon
    private val leg3 = Leg.SingleLeg(loc3, loc3)
    private val leg4 = Leg.SingleLeg(loc3, loc4)
    private val leg5 = Leg.SingleLeg(loc4, loc1)

    init {
        val geometryFactory = GeometryFactory()
        val linearRing =
            geometryFactory.createLinearRing(
                arrayOf(
                    Coordinate(1.5, 1.5),
                    Coordinate(1.5, -1.5),
                    Coordinate(-1.5, -1.5),
                    Coordinate(-1.5, 1.5),
                    Coordinate(1.5, 1.5),
                ),
            )

        polygon = geometryFactory.createPolygon(linearRing)
    }

    @Test
    fun generatesRoutesWhichStartAndEndAtValidPoints() {
        val routePlanner = RoutePlanner(polygon, startPos, 100000000)

        every { legTimerMock.getDuration(any(), any()) } returns 4L

        val result = (
            routePlanner.generateRoutes(
                { location distanceTo it.location < 1000000000.0 },
                { legTimerMock.getDuration(it, startTime) < duration },
            ).take(5).toList()
        )

        for (route in result) {
            assert(startPos.any { it.location == route.locations.start })
            assert(startPos.any { it.location == route.locations.end })
        }
    }

    @Test
    fun routesGeneratedWhenOnlyOneStartPoint() {
        val start = NamedLocation(Location(1.0, 1.0), "test")
        val routePlanner = RoutePlanner(polygon, listOf(start), 100000000)

        every { legTimerMock.getDuration(any(), any()) } returns 4L

        val result = (
            routePlanner.generateRoutes(
                { location distanceTo it.location < 1000000 },
                { legTimerMock.getDuration(it, startTime) < duration },
            ).take(5).toList()
        )

        for (route in result) {
            assertEquals(route.locations.start, start.location)
            assertEquals(route.locations.end, start.location)
        }
    }

    // this test will be redundant (and break) once we add circular routes
    @Test
    fun returnsEmptySequenceWhenNoRoutesPossibleWithinDuration() {
        val routePlanner = RoutePlanner(polygon, startPos, 100000000)
        val smallDuration = 1L

        every { legTimerMock.getDuration(any(), any()) } returns 4L

        val result = (
            routePlanner.generateRoutes(
                { location distanceTo it.location < 1000000000.0 },
                { legTimerMock.getDuration(it, startTime) < smallDuration },
            ).take(5).toList()
        )

        assert(result.isEmpty())
    }

    @Test
    fun returnsEmptySequenceWhenNoStartingPointsCloseEnough() {
        val routePlanner = RoutePlanner(polygon, startPos, 100000000)

        every { legTimerMock.getDuration(any(), any()) } returns 4L

        val result = (
            routePlanner.generateRoutes(
                { location distanceTo it.location < 10 },
                { legTimerMock.getDuration(it, startTime) < duration },
            ).take(5).toList()
        )

        assert(result.isEmpty())
    }

    @Test
    fun returnsLongestRoutesFirstOutOfThoseGenerated() {
        val routePlanner = RoutePlanner(polygon, startPos, 100000000)

        every { legTimerMock.getDuration(any(), any()) } returns 4L

        val result = (
            routePlanner.generateRoutes(
                { location distanceTo it.location < 1000000000.0 },
                { legTimerMock.getDuration(it, startTime) < duration },
            ).take(5).toList()
        )

        result.zipWithNext { a, b ->
            assert(a.length >= b.length)
        }
    }

    // SectionIterator tests
    @Test
    fun sectionIteratorHasNext() {
        val routePlanner = RoutePlanner(polygon, startPos, 10)
        val sectionIterator = routePlanner.SectionIterator()
        assert(sectionIterator.hasNext())
    }

    @Test
    fun sectionIteratorGetNext() {
        val routePlanner = RoutePlanner(polygon, startPos, 10)
        val sectionIterator = routePlanner.SectionIterator()

        val expectedCombinedLeg1 = Leg.MultipleLegs(listOf(leg1, leg2, leg3, leg4))
        val expectedCombinedLeg2 = Leg.MultipleLegs(listOf(leg5))

        assertEquals(sectionIterator.next(), expectedCombinedLeg1)
        assertEquals(sectionIterator.next(), expectedCombinedLeg2)
    }

    // SectionCombiner tests
    @Test
    fun sectionCombinerHasNext() {
        val routePlanner = RoutePlanner(polygon, startPos, 10)
        val sectionCombiner = routePlanner.SectionCombiner()
        assert(sectionCombiner.hasNext())
    }

    @Test
    fun sectionCombinerGetNext() {
        val routePlanner = RoutePlanner(polygon, startPos, 10000)
        val sectionCombiner = routePlanner.SectionCombiner()

        val expectedCombinedLeg = Leg.MultipleLegs(listOf(leg1, leg2, leg3, leg4))

        val combinedLeg1 = sectionCombiner.next()
        val combinedLeg2 = sectionCombiner.next()

        assertEquals(expectedCombinedLeg, combinedLeg1)
        assertEquals(combinedLeg2, Leg.MultipleLegs(listOf(combinedLeg1, Leg.MultipleLegs(listOf(leg5)))))
    }
}
