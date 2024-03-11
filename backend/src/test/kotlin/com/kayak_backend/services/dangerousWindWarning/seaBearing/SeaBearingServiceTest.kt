package com.kayak_backend.services.dangerousWindWarning.seaBearing

import com.kayak_backend.models.Location
import com.kayak_backend.services.coastline.CoastlineService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test

class SeaBearingServiceTest {
    private val coastlineMock = mockk<CoastlineService>()
    private val routeBuffer = 500.0
    private val seaBearingGetterMock = mockk<SeaBearingsGetter>()
    private val seaBearingService = SeaBearingService(coastlineMock, routeBuffer, seaBearingGetterMock)

    @Test
    fun getSeaBearingsCallsSeaBearingsGetterOnlyWhenMapIsEmpty() {
        every { seaBearingGetterMock.getSeaBearings() } returns mapOf(Location(1.0, 1.0) to 1.0)
        seaBearingService.getSeaBearings()
        seaBearingService.getSeaBearings()
        verify(exactly = 1) { seaBearingGetterMock.getSeaBearings() }
    }
}
