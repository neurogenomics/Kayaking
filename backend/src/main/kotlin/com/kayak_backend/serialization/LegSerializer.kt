package com.kayak_backend.serialization

import com.kayak_backend.models.Location
import com.kayak_backend.services.route.Leg
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LegSerializer : KSerializer<Leg> {
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = SerialDescriptor("Locations", Location.serializer().descriptor)

    override fun serialize(
        encoder: Encoder,
        value: Leg,
    ) {
        encoder.encodeSerializableValue(ListSerializer(Location.serializer()), value.locations)
    }

    override fun deserialize(decoder: Decoder): Leg {
        // TODO not implemented
        // also would be very hard to do?
        return Leg.MultipleLegs(listOf())
    }
}
