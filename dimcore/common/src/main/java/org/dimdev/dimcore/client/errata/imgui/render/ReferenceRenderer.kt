package org.dimdev.dimcore.client.errata.imgui.render

import imgui.ImGui
import net.minecraft.nbt.Tag
import org.dimdev.dimcore.api.errata.schema.ReferenceNode
import org.dimdev.dimcore.client.errata.ClientSession
import org.dimdev.dimcore.client.errata.imgui.SchemaImGui.Editor

object ReferenceRenderer : SchemaRenderer<ReferenceNode> {
    override fun edit(editor: Editor, node: ReferenceNode, label: String, path: List<String>, value: Tag?): Tag? {
        val session = editor.session
        val options = session.options[node.source]
        val current = session.labels[path] ?: value?.asString ?: "-"

        if (options == null) {
            ImGui.text("$label: $current")
            return null
        }

        var picked: ClientSession.Option? = null

        if (ImGui.beginCombo("$label###value", current)) {
            options.forEach { if (ImGui.selectable(it.label, it.value == value)) picked = it }
            ImGui.endCombo()
        }

        return picked?.let {
            session.labels += path to it.label
            it.value.copy()
        }
    }
}
