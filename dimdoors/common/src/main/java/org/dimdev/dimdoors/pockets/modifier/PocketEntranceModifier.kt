package org.dimdev.dimdoors.pockets.modifier

import com.google.common.base.MoreObjects
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import org.dimdev.dimdoors.pockets.PocketGenerationContext
import org.dimdev.dimdoors.rift.targets.PocketEntranceMarker
import org.dimdev.dimdoors.rift.targets.PocketExitMarker
import org.dimdev.dimdoors.world.pocket.type.Pocket

data class PocketEntranceModifier(val id: Int) : Modifier {
    override fun toString(): String = MoreObjects.toStringHelper(this).add("id", id).toString()

    override val type get() = Modifiers.PUBLIC

    override fun apply(parameters: PocketGenerationContext, manager: RiftManager) {
        manager.consume(id) { rift ->
            rift.setDestination(PocketEntranceMarker.builder().ifDestination(PocketExitMarker).weight(1.0f).build())
            true
        }
    }

    override fun apply(parameters: PocketGenerationContext, builder: Pocket.PocketBuilder<*, *>) {
    }

    companion object {
        const val KEY: String = "pocket_entrance"

        val CODEC =
            RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    Codec.INT.fieldOf("id").forGetter(PocketEntranceModifier::id)
                ).apply(instance, ::PocketEntranceModifier)
            }
    }
}