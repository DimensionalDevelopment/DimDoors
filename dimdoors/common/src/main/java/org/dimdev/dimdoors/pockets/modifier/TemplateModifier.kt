package org.dimdev.dimdoors.pockets.modifier

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import org.dimdev.dimcore.api.errata.schema.Schemas
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.pockets.PocketGenerationContext
import org.dimdev.dimdoors.rift.targets.TemplateTarget
import org.dimdev.dimdoors.world.pocket.type.Pocket
import java.util.stream.IntStream
import java.util.stream.Stream

abstract class TemplateModifier<V, T: TemplateTarget<V, T>>(val ids: MutableList<Int>) : Modifier {
    abstract val template: ResourceKey<V>

    override fun apply(parameters: PocketGenerationContext, manager: RiftManager) {
        val template = createTarget(template)

        manager.foreachConsume { id, rift: Rift ->
            if (ids.contains(id)) {
                rift.setDestination(template.copy())
                return@foreachConsume true
            } else {
                return@foreachConsume false
            }
        }
    }

    abstract fun createTarget(key: ResourceKey<V>): T

    override fun apply(parameters: PocketGenerationContext, builder: Pocket.PocketBuilder<*, *>) {}

    companion object {
        val IDS_CODEC = Codec.INT_STREAM.xmap(
            IntStream::boxed,
            { integerStream -> integerStream.mapToInt(Int::toInt) })
            .xmap(
                Stream<Int>::toList,
                MutableList<Int>::stream
            ).fieldOf("ids")

        fun <V : Any, T : TemplateTarget<V, T>, R: TemplateModifier<V, T>> codec(key: ResourceKey<Registry<V>>, function: (MutableList<Int>, ResourceKey<V>) -> R): MapCodec<R> =
            RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    IDS_CODEC.forGetter { it.ids },
                    Schemas.registryKey(key).fieldOf("template").forGetter { it.template }
                ).apply(instance, function)
            }

        const val KEY: String = "template"
    }
}
