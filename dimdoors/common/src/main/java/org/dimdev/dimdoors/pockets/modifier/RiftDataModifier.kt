package org.dimdev.dimdoors.pockets.modifier

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.block.entity.RiftData
import org.dimdev.dimdoors.pockets.PocketGenerationContext
import org.dimdev.dimdoors.rift.targets.VirtualTarget
import org.dimdev.dimdoors.api.util.nullableForGetter
import kotlin.jvm.optionals.getOrNull
import org.dimdev.dimdoors.world.pocket.type.Pocket

class RiftDataModifier(private val doorData: Holder<RiftData>?, private val ids: MutableList<Int>) : Modifier {
    override val type get() = Modifiers.RIFT_DATA

    override fun apply(parameters: PocketGenerationContext, manager: RiftManager) {
        val riftConsumer: (Rift) -> Unit = if (doorData == null) {
            { rift -> rift.setDestination(VirtualTarget.NoneTarget) }
        } else {
            { rift -> rift.data = doorData.value().copy() }
        }

        manager.foreachConsume { id, rift ->
            if (ids.contains(id)) {
                riftConsumer.invoke(rift)
                return@foreachConsume true
            } else {
                return@foreachConsume false
            }
        }
    }

    override fun apply(parameters: PocketGenerationContext, builder: Pocket.PocketBuilder<*, *>) {}

    companion object {
        val CODEC: MapCodec<RiftDataModifier> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                RiftData.HOLDER_CODEC.optionalFieldOf("rift_data").nullableForGetter(RiftDataModifier::doorData),
                Codec.INT_STREAM.xmap(
                    { a -> a.boxed().toList() },
                    { integers ->
                        integers.stream().mapToInt { obj -> obj.toInt() }
                    }).fieldOf("ids").forGetter(RiftDataModifier::ids)
            ).apply(instance) { doorData, ids -> RiftDataModifier(doorData.getOrNull(), ids) }
        }


        const val KEY: String = "rift_data"
    }
}
