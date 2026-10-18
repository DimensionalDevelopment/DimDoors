package org.dimdev.dimcore.client.errata.imgui.render

import net.minecraft.nbt.Tag
import org.dimdev.dimcore.api.errata.schema.ElementNode
import org.dimdev.dimcore.client.errata.element.SchemaElement
import org.dimdev.dimcore.client.errata.element.SchemaElements
import org.dimdev.dimcore.client.errata.imgui.SchemaImGui.Editor

object ElementRenderer : SchemaRenderer<ElementNode> {
    override fun edit(editor: Editor, node: ElementNode, label: String, path: List<String>, value: Tag?): Tag? {
        val element = SchemaElements[node.element] ?: return editor.edit(node.node, label, path, value)
        return element.edit(SchemaElement.Context(label, path, value, node.config, node.node) { editor.edit(node.node, label, path, value) })
    }
}
