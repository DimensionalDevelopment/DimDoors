package org.dimdev.dimcore.client.errata.imgui.render

import imgui.ImGui
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimcore.api.errata.schema.DispatchNode
import org.dimdev.dimcore.api.errata.schema.StructNode
import org.dimdev.dimcore.client.errata.imgui.SchemaImGui.Editor

object DispatchRenderer : SchemaRenderer<DispatchNode> {
    override fun edit(editor: Editor, node: DispatchNode, label: String, path: List<String>, value: Tag?): Tag? {
        val compound = value as? CompoundTag ?: CompoundTag()
        val variants = editor.session.schema.variants(node) ?: return null
        val type = ResourceLocation.tryParse(compound.getString(node.typeKey))
        var switched: CompoundTag? = null

        if (ImGui.beginCombo("$label###value", type?.let(variants::get)?.let { variantLabel(editor, it) } ?: type?.toString() ?: "")) {
            for ((variant, struct) in variants) {
                if (ImGui.selectable(variantLabel(editor, struct), variant == type) && variant != type) switched = node.variant(variant, editor.session.schema)
            }
            ImGui.endCombo()
        }
        if (switched != null) return switched

        val struct = type?.let(variants::get) ?: return null
        ImGui.indent()
        val edited = editor.editFields(struct, path, compound)
        ImGui.unindent()
        return edited
    }

    private fun variantLabel(editor: Editor, struct: StructNode) = editor.label(struct.labelKey, struct.id.toString())
}
