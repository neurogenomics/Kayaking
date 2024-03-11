package com.kayak_backend.services.route

import com.kayak_backend.models.Location
import com.kayak_backend.models.TideInfo
import com.kayak_backend.services.tides.TideService
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Polygon
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.assertEquals

class CircularRouteTest {
    private val legTimerMock = mockk<LegTimer>()
    private val tideServiceMock = mockk<TideService>()

    private val polygon: Polygon
    private val startPos: List<NamedLocation> =
        listOf(
            NamedLocation(Location(1.5, -1.5), "Start1"),
            NamedLocation(Location(1.5, 1.5), "Start2"),
            NamedLocation(Location(-1.5, -1.5), "Start3"),
            NamedLocation(Location(-1.5, 1.5), "Start4"),
        )

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

        every { tideServiceMock.getTideAllDay(any(), any()) } returns
            List(23) {
                Pair(LocalTime.of(it, 0), if (it <= 12) TideInfo(1.0, 1.0) else TideInfo(-1.0, -1.0))
            }.toMap()

        every { legTimerMock.getDuration(any(), any()) } returns 3600
    }

    @Test
    fun generatesRoutesWhichStartAndEndAtValidPoints() {
        val routePlanner = CircularRoutePlanner(polygon, startPos, tideServiceMock, 10000)

        val result = (
            routePlanner.generateRoutes(
                legTimerMock,
                date = LocalDate.of(2024, 7, 4),
                minTime = Duration.ofHours(4),
            ).take(5).toList()
        )
        assert(result.isNotEmpty())
        for (route in result) {
            assert(startPos.any { it.location == route.locations.start })
            assert(startPos.any { it.location == route.locations.end })
        }
    }

    @Test
    fun routeIsCircular() {
        val routePlanner = CircularRoutePlanner(polygon, startPos, tideServiceMock, 10000)

        val result = (
            routePlanner.generateRoutes(
                legTimerMock,
                date = LocalDate.of(2024, 7, 4),
                minTime = Duration.ofHours(4),
            ).take(5).toList()
        )
        assert(result.isNotEmpty())
        for (route in result) {
            assertEquals(route.locations.start, route.locations.end)
        }
    }

    @Test
    fun routeStartsBeforeSwitch() {
        val routePlanner = CircularRoutePlanner(polygon, startPos, tideServiceMock, 10000)

        val result = (
            routePlanner.generateRoutes(
                legTimerMock,
                date = LocalDate.of(2024, 7, 4),
                minTime = Duration.ofHours(4),
            ).take(5).toList()
        )
        assert(result.isNotEmpty())
        for (route in result) {
            println(route.startTime)
            assert(route.startTime.hour <= 12)
        }
    }

    @Test
    fun routeIsRightLength() {
        val routePlanner = CircularRoutePlanner(polygon, startPos, tideServiceMock, 10000)

        val result = (
            routePlanner.generateRoutes(
                legTimerMock,
                date = LocalDate.of(2024, 7, 4),
                minTime = Duration.ofHours(4),
            ).take(5).toList()
        )
        assert(result.isNotEmpty())
        for (route in result) {
            println(route.locations.locations)
            assertEquals(7, route.locations.locations.size)
        }
    }

    @Test
    fun routeIsSupportedByTide() {
        val routePlanner = CircularRoutePlanner(polygon, startPos, tideServiceMock, 10000)

        val result = (
            routePlanner.generateRoutes(
                legTimerMock,
                date = LocalDate.of(2024, 7, 4),
                minTime = Duration.ofHours(4),
            ).take(5).toList()
        )
        assert(result.isNotEmpty())
        for (route in result) {
            val loc1 = route.locations.locations[1]
            val loc2 = route.locations.locations[2]
            val bearing = loc1 bearingTo loc2
            assert(bearing in 0.0..90.0)
        }
    }
}
