package org.dimdev.dimcore.client.errata.imgui.render

import imgui.ImGui
import net.minecraft.nbt.CollectionTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import org.dimdev.dimcore.api.errata.schema.ListNode
import org.dimdev.dimcore.client.errata.imgui.SchemaImGui.Editor

object ListRenderer : SchemaRenderer<ListNode> {
    override fun edit(editor: Editor, node: ListNode, label: String, path: List<String>, value: Tag?): Tag? {
        if (!ImGui.treeNode("$label###value")) return null

        val elements: List<Tag> = (value as? CollectionTag<*>).orEmpty()
        var edits: MutableMap<Int, Tag>? = null
        var remove = -1

        for (index in elements.indices) {
            ImGui.pushID(index)
            editor.edit(node.element, index.toString(), path + index.toString(), elements[index])?.let { tag -> edits = (edits ?: mutableMapOf()).also { it[index] = tag } }
            ImGui.sameLine()
            if (ImGui.button("x")) remove = index
            ImGui.popID()
        }

        val added = if (ImGui.button("+")) node.element.defaultTag(editor.session.schema) else null

        ImGui.treePop()
        if (edits == null && remove < 0 && added == null) return null

        return ListTag().apply {
            for (index in elements.indices) if (index != remove) add(edits?.get(index) ?: elements[index].copy())
            if (added != null) add(added)
        }
    }
}
