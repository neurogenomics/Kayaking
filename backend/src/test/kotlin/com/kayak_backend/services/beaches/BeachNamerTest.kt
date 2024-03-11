package com.kayak_backend.services.beaches

import BeachNamer
import com.kayak_backend.models.Location
import org.junit.Test
import kotlin.test.assertEquals

class BeachNamerTest {
    @Test
    fun getsBeachNameForALocationThatIsExactlyThatBeach()  {
        val beachNamer = BeachNamer()
        val result = beachNamer.getClosestBeachName(Location(50.7089448, -1.0997663))
        assertEquals("Priory Bay", result)
    }

    @Test
    fun getsClosestBeachNameForALocation()  {
        val beachNamer = BeachNamer()
        val result = beachNamer.getClosestBeachName(Location(50.7089446, -1.0997665))
        assertEquals("Priory Bay", result)
    }
}
