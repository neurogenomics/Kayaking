package com.kayak_backend.interpolator

import com.kayak_backend.models.Range

interface Interpolator {
    fun interpolate(
        data: List<List<Double>>,
        indices: Pair<List<Double>, List<Double>>,
        // Length should be equal to the lengths of the list
        ranges: Pair<Range, Range>,
        // ranges must be in ascending order
        resolutions: Pair<Double, Double>,
    ): Triple<List<List<Double>>, List<Double>, List<Double>>
}
