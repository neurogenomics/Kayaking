package com.kayak_backend.models

import com.kayak_backend.serialization.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class TideInfo(val u: Double, val v: Double)

@Serializable
data class TideEvent(
    val isHighTide: Boolean,
    @Serializable(with = LocalDateTimeSerializer::class)
    val datetime: LocalDateTime,
    val height: Double?
)
@Serializable
data class TideStation(
    val id: String,
    val name: String,
    val location: Location,
)

@Serializable
data class TideEvents(
    val events: List<TideEvent>,
    val source: TideStation
)