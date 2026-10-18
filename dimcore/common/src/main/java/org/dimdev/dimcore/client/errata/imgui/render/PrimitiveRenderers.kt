package org.dimdev.dimcore.client.errata.imgui.render

import imgui.ImGui
import imgui.type.*
import net.minecraft.nbt.*
import org.dimdev.dimcore.api.errata.schema.BoolNode
import org.dimdev.dimcore.api.errata.schema.DoubleNode
import org.dimdev.dimcore.api.errata.schema.FloatNode
import org.dimdev.dimcore.api.errata.schema.IntNode
import org.dimdev.dimcore.api.errata.schema.StringNode
import org.dimdev.dimcore.client.errata.imgui.SchemaImGui.Editor

object BoolRenderer : SchemaRenderer<BoolNode> {
    override fun edit(editor: Editor, node: BoolNode, label: String, path: List<String>, value: Tag?): Tag? =
        ImBoolean((value as? NumericTag)?.asByte?.let { it.toInt() != 0 } ?: node.default).let { if (ImGui.checkbox("$label###value", it)) ByteTag.valueOf(it.get()) else null }
}

object IntRenderer : SchemaRenderer<IntNode> {
    override fun edit(editor: Editor, node: IntNode, label: String, path: List<String>, value: Tag?): Tag? {
        val id = "$label###value"

        return ImInt((value as? NumericTag)?.asInt ?: node.default).let {
            if (
                if(node.ranged)
                    ImGui.sliderInt(id, it.data, node.min, node.max)
                else ImGui.inputInt(id, it)
                )
                it.get().let(IntTag::valueOf)
            else
                null
        }
    }
}

object FloatRenderer : SchemaRenderer<FloatNode> {
    override fun edit(editor: Editor, node: FloatNode, label: String, path: List<String>, value: Tag?): Tag? {
        val id = "$label###value"

        return ImFloat((value as? NumericTag)?.asFloat ?: node.default).let {
            if (
                if(node.ranged)
                    ImGui.sliderFloat(id, it.data, node.min, node.max)
                else ImGui.inputFloat(id, it)
            ) it.get().let(FloatTag::valueOf)
            else null
        }
    }
}

object DoubleRenderer : SchemaRenderer<DoubleNode> {
    override fun edit(editor: Editor, node: DoubleNode, label: String, path: List<String>, value: Tag?): Tag? =
        ImDouble((value as? NumericTag)?.asDouble ?: node.default).let { if (ImGui.inputDouble("$label###value", it)) DoubleTag.valueOf(if (node.ranged) it.get().coerceIn(node.min, node.max) else it.get()) else null }
}

object StringRenderer : SchemaRenderer<StringNode> {
    override fun edit(editor: Editor, node: StringNode, label: String, path: List<String>, value: Tag?): Tag? =
        ImString((value as? StringTag)?.asString ?: node.default, 256).let { if (ImGui.inputText("$label###value", it)) StringTag.valueOf(it.get()) else null }
}
