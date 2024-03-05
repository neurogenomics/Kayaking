package com.kayak_backend.services.slipway
import com.kayak_backend.models.Location
import com.kayak_backend.services.route.NamedLocation
import com.kayak_backend.services.slipways.SlipwayService
import io.ktor.server.testing.*
import kotlin.test.*

class SlipwayServiceTest {
    private val slipwayService = SlipwayService()

    @Test
    fun returnsClosestSlipway() {
        val closestSlipway = slipwayService.getClosestSlipway(Location(50.6888230, -1.0721198))
        val actualClosestSlipway = NamedLocation(Location(50.6888230, -1.0721198), "Fishermans Walk Slipway")
        assertEquals(actualClosestSlipway.name, closestSlipway.name)
        assertEquals(actualClosestSlipway.location, closestSlipway.location)
    }
}
