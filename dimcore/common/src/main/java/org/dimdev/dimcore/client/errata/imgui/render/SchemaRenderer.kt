package org.dimdev.dimcore.client.errata.imgui.render

import net.minecraft.nbt.Tag
import org.dimdev.dimcore.api.errata.schema.SchemaNode
import org.dimdev.dimcore.client.errata.imgui.SchemaImGui.Editor

fun interface SchemaRenderer<T : SchemaNode> {
    fun edit(editor: Editor, node: T, label: String, path: List<String>, value: Tag?): Tag?
}
