package org.dimdev.dimcore.client.errata.element

import imgui.ImGui
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import org.dimdev.dimcore.api.errata.schema.StructNode

object ColorElement : SchemaElement {
    override fun edit(context: SchemaElement.Context): Tag? {
        val node = context.node as? StructNode ?: return context.fallback()
        val compound = context.value as? CompoundTag ?: CompoundTag()
        val channels = node.fields.take(4)
        val rgba = FloatArray(4) { index -> channels.getOrNull(index)?.let { compound.getFloat(it.name) } ?: 1f }

        if (!ImGui.colorEdit4(context.label, rgba)) return null
        return compound.copy().apply { channels.forEachIndexed { index, field -> putFloat(field.name, rgba[index]) } }
    }
}
