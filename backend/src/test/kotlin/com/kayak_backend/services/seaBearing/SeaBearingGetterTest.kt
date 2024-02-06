package com.kayak_backend.services.seaBearing;
import com.kayak_backend.services.coastline.CoastlineService
import io.mockk.every
import io.mockk.mockk
import org.locationtech.jts.geom.Coordinate
import kotlin.math.abs
import kotlin.test.*

private const val ALLOWED_ROUNDING_ERROR = 2.0
class SeaBearingGetterTest {

    @Test
    fun findCorrectBearingToSea(){
        val coastlineMock = mockk<CoastlineService>()
        val seaBearingsGetter = SeaBearingsGetter(coastlineMock)

        val expectedBearings = listOf(45.0,135.0,225.0,315.0)

        every { coastlineMock.getCoastline().coordinates } returns arrayOf(
            Coordinate(1.0,1.0),
            Coordinate(2.0,0.0),
            Coordinate(1.0,-1.0),
            Coordinate(0.0,0.0),
            Coordinate(1.0,1.0)
        )

        val result = seaBearingsGetter.getSeaBearings()

        for (i in 0 until 4){
            assert(abs(result[i].bearing - expectedBearings[i]) < ALLOWED_ROUNDING_ERROR)
        }
    }
}
