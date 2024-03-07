// package com.kayak_backend.services.dangerousWindWarning.seaBearing
// import com.kayak_backend.services.coastline.CoastlineService
// import com.kayak_backend.services.route.BaseRoute
// import io.mockk.every
// import io.mockk.mockk
// import org.locationtech.jts.geom.Coordinate
// import kotlin.math.abs
// import kotlin.test.Test
//
// private const val ALLOWED_ROUNDING_ERROR = 2.0
//
// class SeaBearingGetterTest {
//    private val coastlineMock = mockk<CoastlineService>()
//    private val routeMock = mockk<BaseRoute>()
//    private val seaBearingsGetter = SeaBearingsGetter(coastlineMock, routeMock, 0.0)
//
//    @Test
//    fun findCorrectBearingToSea() {
//        // this has not taken into account spherical factors
//        val expectedBearings = listOf(45.0, 135.0, 225.0, 315.0)
//
//        val mockOut =
//            arrayOf(
//                Coordinate(1.0, 1.0),
//                Coordinate(2.0, 0.0),
//                Coordinate(1.0, -1.0),
//                Coordinate(0.0, 0.0),
//                Coordinate(1.0, 1.0),
//            )
//
//        every { routeMock.createBaseRoute(coastlineMock.getCoastline(), 0.0).coordinates } returns mockOut
//
//        val result = seaBearingsGetter.getSeaBearings()
//
//        for (i in result.indices) {
//            assert(abs(result[i].bearing - expectedBearings[i]) < ALLOWED_ROUNDING_ERROR)
//        }
//    }
//
//    @Test
//    fun emptyCoastlineReturnsEmptyBearings() {
//        every { routeMock.createBaseRoute(coastlineMock.getCoastline(), 0.0).coordinates } returns arrayOf()
//
//        val result = seaBearingsGetter.getSeaBearings()
//
//        assert(result.isEmpty())
//    }
//
//    @Test
//    fun oneCoordinateCoastlineReturnsEmptyBearings() {
//        val mockOut = arrayOf(Coordinate(1.0, 1.0))
//        every { routeMock.createBaseRoute(coastlineMock.getCoastline(), 0.0).coordinates } returns mockOut
//
//        val result = seaBearingsGetter.getSeaBearings()
//
//        assert(result.isEmpty())
//    }
//
//    @Test
//    fun repeatedConsecutiveCoordinateInCoastlineIgnored() {
//        val expectedBearings = listOf(0.0, 180.0)
//        val mockOut =
//            arrayOf(
//                Coordinate(1.0, 1.0),
//                Coordinate(1.0, 1.0),
//                Coordinate(2.0, 1.0),
//                Coordinate(1.0, 1.0),
//            )
//
//        every { routeMock.createBaseRoute(coastlineMock.getCoastline(), 0.0).coordinates } returns mockOut
//
//        val result = seaBearingsGetter.getSeaBearings()
//
//        assert(result.size == 2)
//        for (i in expectedBearings.indices) {
//            assert(abs(expectedBearings[i] - result[i].bearing) < ALLOWED_ROUNDING_ERROR)
//        }
//    }
// }
