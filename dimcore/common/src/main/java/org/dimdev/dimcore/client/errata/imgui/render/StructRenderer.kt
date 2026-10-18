package org.dimdev.dimcore.client.errata.imgui.render

import imgui.ImGui
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import org.dimdev.dimcore.api.errata.schema.StructNode
import org.dimdev.dimcore.client.errata.imgui.SchemaImGui.Editor

object StructRenderer : SchemaRenderer<StructNode> {
    override fun edit(editor: Editor, node: StructNode, label: String, path: List<String>, value: Tag?): Tag? {
        if (!ImGui.treeNode("$label###value")) return null
        val edited = editor.editFields(node, path, value as? CompoundTag ?: CompoundTag())
        ImGui.treePop()
        return edited
    }
}
