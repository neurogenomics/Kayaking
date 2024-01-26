package com.kayak_backend.models

import com.kayak_backend.serialization.LocalDateTimeSerializer
import com.kayak_backend.serialization.LocalTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class TideEvent(
    val isHighTide: Boolean,
    @Serializable(with = LocalDateTimeSerializer::class)
    val datetime: LocalDateTime,
    val height: Double
)