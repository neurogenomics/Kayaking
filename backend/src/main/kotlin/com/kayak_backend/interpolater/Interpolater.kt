package com.kayak_backend.interpolater

interface Interpolater {
    fun <T> interpolate(
        data: Array<T>,
        indices: Array<Double>,
        resolutions: Array<Double>,
    )
}
