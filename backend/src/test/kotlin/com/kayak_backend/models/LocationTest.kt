package com.kayak_backend.models

import org.junit.Test
import kotlin.test.assertEquals

class LocationTest {
    @Test
    fun findsCorrectBearingBetweenTwoLocations() {
        val loc1 = Location(0.0, 0.0)
        val loc2 = Location(0.0, 2.0)
        val loc3 = Location(2.0, 2.0)

        assertEquals(90.0, loc1 bearingTo loc2, 0.05)
        assertEquals(45.0, loc1 bearingTo loc3, 0.05)
        assertEquals(0.0, loc2 bearingTo loc3, 0.05)
    }

    @Test
    fun findsCorrectBearingBetweenTwoLocationsSplittingNorthAxis() {
        val loc1 = Location(1.0, 1.0)
        val loc2 = Location(1.0, -1.0)

        assertEquals(270.0, loc1 bearingTo loc2, 0.05)
        assertEquals(90.0, loc2 bearingTo loc1, 0.05)
    }
}
