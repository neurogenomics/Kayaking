package com.kayak_backend.interpolator

import kotlin.math.max

class SimpleInterpolator : Interpolator {
    override fun interpolate(
        data: Array<Array<Double>>,
        indices: Pair<Array<Double>, Array<Double>>,
        ranges: Pair<Pair<Double, Double>, Pair<Double, Double>>,
        resolutions: Pair<Double, Double>,
    ): Triple<Array<Array<Double>>, List<Double>, List<Double>> {
        val (range1, range2) = ranges
        val (resolution1, resolution2) = resolutions
        val (index1, index2) = indices

        val newIndex1 =
            generateSequence(seed = range1.first) { it + resolution1 }.takeWhile { it <= range1.second }
                .toList()

        val newIndex2 =
            generateSequence(seed = range2.first) { it + resolution2 }.takeWhile { it <= range2.second }
                .toList()

        var currentIndex1 = 0

        val result =
            newIndex1.map {
                while (currentIndex1 < index1.size - 1 && it >= index1[max(currentIndex1, 1)]) currentIndex1++
                var currentIndex2 = 0
                newIndex2.map { it2 ->
                    while (currentIndex2 < index2.size - 1 && it2 >= index2[max(currentIndex2, 1)]) currentIndex2++
                    data[currentIndex1][currentIndex2]
                }.toTypedArray()
            }.toTypedArray()

        return Triple(result, newIndex1, newIndex2)
    }
}
