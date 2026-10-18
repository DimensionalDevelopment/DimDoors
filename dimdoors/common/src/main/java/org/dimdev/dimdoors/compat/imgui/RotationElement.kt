package org.dimdev.dimdoors.compat.imgui

import imgui.ImGui
import net.minecraft.nbt.FloatTag
import net.minecraft.nbt.NumericTag
import net.minecraft.nbt.Tag
import org.dimdev.dimcore.api.ext.cast
import org.dimdev.dimcore.client.errata.element.SchemaElement

object RotationElement : SchemaElement {
    override fun edit(context: SchemaElement.Context): Tag? {
        val rotation = (context.value?.cast<NumericTag>()?.asFloat ?: 0f).toRadians().let(::floatArrayOf)
        val changed = ImGui.sliderAngle(context.label, rotation, 0f, 360f)
        return if (changed) FloatTag.valueOf(rotation[0].toDegrees()) else null
    }
}

private fun Float.toRadians() = Math.toRadians(this.toDouble()).toFloat()
private fun Float.toDegrees() = Math.toDegrees(this.toDouble()).toFloat()
