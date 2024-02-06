package com.kayak_backend.services.seaBearing;
import com.kayak_backend.services.coastline.CoastlineService
import io.mockk.every
import io.mockk.mockk
import org.locationtech.jts.geom.Coordinate
import kotlin.math.abs
import kotlin.test.Test

private const val ALLOWED_ROUNDING_ERROR = 2.0
class SeaBearingGetterTest {

    private val coastlineMock = mockk<CoastlineService>()
    private val seaBearingsGetter = SeaBearingsGetter(coastlineMock)

    @Test
    fun findCorrectBearingToSea(){
        //this has not taken into account spherical factors
        val expectedBearings = listOf(45.0,135.0,225.0,315.0)

        every { coastlineMock.getCoastline().coordinates } returns arrayOf(
            Coordinate(1.0,1.0),
            Coordinate(2.0,0.0),
            Coordinate(1.0,-1.0),
            Coordinate(0.0,0.0),
            Coordinate(1.0,1.0)
        )

        val result = seaBearingsGetter.getSeaBearings()

        for (i in result.indices){
            assert(abs(result[i].bearing - expectedBearings[i]) < ALLOWED_ROUNDING_ERROR)
        }
    }

    @Test
    fun emptyCoastlineReturnsEmptyBearings(){
        every { coastlineMock.getCoastline().coordinates } returns arrayOf()

        val result = seaBearingsGetter.getSeaBearings()

        assert(result.isEmpty())
    }

    @Test
    fun oneCoordinateCoastlineReturnsEmptyBearings(){
        every { coastlineMock.getCoastline().coordinates } returns arrayOf(
            Coordinate(1.0,1.0)
        )

        val result = seaBearingsGetter.getSeaBearings()

        assert(result.isEmpty())
    }

    @Test
    fun repeatedConsecutiveCoordinateInCoastlineIgnored(){
        every { coastlineMock.getCoastline().coordinates } returns arrayOf(
            Coordinate(1.0,1.0),
            Coordinate(1.0,1.0)
        )

        val result = seaBearingsGetter.getSeaBearings()

        assert(result.isEmpty())
    }
}
