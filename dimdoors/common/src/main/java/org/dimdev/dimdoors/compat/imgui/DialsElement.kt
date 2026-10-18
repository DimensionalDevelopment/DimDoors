package org.dimdev.dimdoors.compat.imgui

import imgui.ImGui
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.NumericTag
import net.minecraft.nbt.Tag
import org.dimdev.dimcore.client.errata.element.SchemaElement
import org.dimdev.dimdoors.rift.registry.DialingAddress

object DialsElement : SchemaElement {
    override fun edit(context: SchemaElement.Context): Tag? {
        val address = DialingAddress.from((context.value as? NumericTag)?.asInt ?: 0)
        val dials = intArrayOf(address.dial1.toInt(), address.dial2.toInt(), address.dial3.toInt())
        val changed = ImGui.sliderInt3(context.label, dials, 0, 9)
        return if (changed) IntTag.valueOf(DialingAddress(dials[0].toByte(), dials[1].toByte(), dials[2].toByte()).to()) else null
    }
}
