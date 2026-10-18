package org.dimdev.dimcore.client.errata.imgui.render

import imgui.ImGui
import net.minecraft.nbt.Tag
import org.dimdev.dimcore.api.errata.schema.UntypedNode
import org.dimdev.dimcore.client.errata.imgui.SchemaImGui.Editor

object UntypedRenderer : SchemaRenderer<UntypedNode> {
    override fun edit(editor: Editor, node: UntypedNode, label: String, path: List<String>, value: Tag?): Tag? {
        ImGui.textDisabled("$label: ${value ?: "-"}")
        return null
    }
}
