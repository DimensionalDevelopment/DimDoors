package org.dimdev.dimdoors.pockets.modifier

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.pockets.PocketGenerationContext
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket
import org.dimdev.dimdoors.rift.targets.TemplateTarget
import org.dimdev.dimdoors.world.pocket.type.Pocket

data class TemplateModifier(val templateId: Holder<VirtualPocket>, val ids: MutableList<Int>) : Modifier {
    override val type get() = Modifiers.TEMPLATE

    override fun apply(parameters: PocketGenerationContext, manager: RiftManager) {
        val template = TemplateTarget(templateId)

        manager.foreachConsume { id, rift: Rift ->
            if (ids.contains(id)) {
                rift.setDestination(template.copy())
                return@foreachConsume true
            } else {
                return@foreachConsume false
            }
        }
    }

    override fun apply(parameters: PocketGenerationContext, builder: Pocket.PocketBuilder<*, *>) {}

    companion object {
        var CODEC = RecordCodecBuilder.mapCodec { instance -> instance.group(
                    VirtualPocket.HOLDER_CODEC.fieldOf("templateId").forGetter(TemplateModifier::templateId),
                    Codec.INT_STREAM.xmap(
                        { obj -> obj.boxed() },
                         { integerStream -> integerStream.mapToInt { obj -> obj.toInt() } })
                        .xmap(
                            { obj -> obj.toList() },
                            { obj -> obj.stream() }).fieldOf("ids")
                        .forGetter(TemplateModifier::ids)
                ).apply(
                    instance, ::TemplateModifier)
            }

        const val KEY: String = "template"
    }
}
