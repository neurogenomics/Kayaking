package com.kayak_backend.interpolator

import com.kayak_backend.models.Range

class SimpleInterpolator : Interpolator {
    override fun interpolate(
        data: List<List<Double>>,
        indices: Pair<List<Double>, List<Double>>,
        ranges: Pair<Range, Range>,
        resolutions: Pair<Double, Double>,
    ): Triple<List<List<Double>>, List<Double>, List<Double>> {
        val (range1, range2) = ranges
        val (resolution1, resolution2) = resolutions
        val (index1, index2) = indices

        val newIndex1 =
            generateSequence(seed = range1.start) { it + resolution1 }.takeWhile { it <= range1.end }
                .toList()

        val newIndex2 =
            generateSequence(seed = range2.start) { it + resolution2 }.takeWhile { it <= range2.end }
                .toList()

        var currentIndex1 = 0

        val result =
            newIndex1.map {
                while (currentIndex1 < index1.size - 1 && it >= index1[currentIndex1 + 1]) currentIndex1++
                var currentIndex2 = 0
                newIndex2.map { it2 ->
                    while (currentIndex2 < index2.size - 1 && it2 >= index2[currentIndex2 + 1]) currentIndex2++
                    data[currentIndex1][currentIndex2]
                }
            }

        return Triple(result, newIndex1, newIndex2)
    }
}
