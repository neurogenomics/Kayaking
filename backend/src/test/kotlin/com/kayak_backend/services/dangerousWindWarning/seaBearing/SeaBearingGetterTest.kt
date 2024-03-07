package com.kayak_backend.services.dangerousWindWarning.seaBearing
import com.kayak_backend.models.Location
import com.kayak_backend.services.coastline.CoastlineService
import com.kayak_backend.services.route.BaseRoute
import io.mockk.every
import io.mockk.mockk
import org.locationtech.jts.geom.Coordinate
import kotlin.math.abs
import kotlin.test.Test

private const val ALLOWED_ROUNDING_ERROR = 2.0

// this has not taken into account spherical factors

class SeaBearingGetterTest {
    private val coastlineMock = mockk<CoastlineService>()
    private val routeMock = mockk<BaseRoute>()
    private val routeBuffer = 0.0
    private val seaBearingsGetter = SeaBearingsGetter(coastlineMock, routeMock, routeBuffer)

    @Test
    fun findCorrectBearingToSea() {
        val c1 = Coordinate(1.0, 1.0)
        val c2 = Coordinate(2.0, 0.0)
        val c3 = Coordinate(1.0, -1.0)
        val c4 = Coordinate(0.0, 0.0)
        val c5 = Coordinate(1.0, 1.0)
        val expectedBearings: Map<Coordinate, Double> = mapOf(c1 to 45.0, c2 to 135.0, c3 to 225.0, c4 to 315.0)

        val mockOut = arrayOf(c1, c2, c3, c4, c5)

        every { routeMock.createBaseRoute(coastlineMock.getCoastline(), routeBuffer).coordinates } returns mockOut

        val result = seaBearingsGetter.getSeaBearings()

        for (key in expectedBearings.keys) {
            val locKey = Location(key.x, key.y)
            assert(result.containsKey(locKey))
            assert(abs(result[locKey]!! - expectedBearings[key]!!) < ALLOWED_ROUNDING_ERROR)
        }
    }

    @Test
    fun emptyCoastlineReturnsEmptyBearings() {
        every { routeMock.createBaseRoute(coastlineMock.getCoastline(), routeBuffer).coordinates } returns arrayOf()

        val result = seaBearingsGetter.getSeaBearings()

        assert(result.isEmpty())
    }

    @Test
    fun oneCoordinateCoastlineReturnsEmptyBearings() {
        val mockOut = arrayOf(Coordinate(1.0, 1.0))
        every { routeMock.createBaseRoute(coastlineMock.getCoastline(), routeBuffer).coordinates } returns mockOut

        val result = seaBearingsGetter.getSeaBearings()

        assert(result.isEmpty())
    }

    @Test
    fun repeatedConsecutiveCoordinateInCoastlineIgnored() {
        val c1 = Coordinate(1.0, 1.0)
        val c2 = Coordinate(2.0, 1.0)
        val expectedBearings: Map<Coordinate, Double> = mapOf(c1 to 0.0, c2 to 180.0)

        val mockOut =
            arrayOf(c1, c1, c2, c1)

        every { routeMock.createBaseRoute(coastlineMock.getCoastline(), routeBuffer).coordinates } returns mockOut

        val result = seaBearingsGetter.getSeaBearings()

        assert(result.size == 2)
        for (i in expectedBearings.keys) {
            val locKey = Location(i.x, i.y)
            assert(result.containsKey(locKey))
            assert(abs(result[locKey]!! - expectedBearings[i]!!) < ALLOWED_ROUNDING_ERROR)
        }
    }
}
