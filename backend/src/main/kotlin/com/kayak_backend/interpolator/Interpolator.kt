package com.kayak_backend.interpolator

interface Interpolator {
    fun interpolate(
        data: Array<Array<Double>>,
        indices: Pair<Array<Double>, Array<Double>>,
        // Length should be equal to the lengths of the array
        ranges: Pair<Pair<Double, Double>, Pair<Double, Double>>,
        // ranges must be in ascending order
        resolutions: Pair<Double, Double>,
    ): Triple<Array<Array<Double>>, List<Double>, List<Double>>
}
