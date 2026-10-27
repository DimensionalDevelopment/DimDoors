package org.dimdev.dimcore.client.errata.element

import imgui.ImGui
import net.minecraft.client.Minecraft
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation

object LevelResourceKeyElement : SchemaElement {
    override fun edit(context: SchemaElement.Context): Tag? {
        val levels = Minecraft.getInstance().connection?.levels() ?: return context.fallback()
        val current = context.value?.asString ?: "-"
        var picked: ResourceLocation? = null

        if (ImGui.beginCombo("${context.label}###value", current)) {
            levels.map { it.location() }.sorted().forEach { if (ImGui.selectable(it.toString(), it.toString() == current)) picked = it }
            ImGui.endCombo()
        }

        return picked?.let { StringTag.valueOf(it.toString()) }
    }
}
