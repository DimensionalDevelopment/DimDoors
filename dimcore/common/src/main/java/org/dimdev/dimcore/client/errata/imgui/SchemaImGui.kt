package org.dimdev.dimcore.client.errata.imgui

import foundry.imgui.api.ImGuiMCEvents
import imgui.ImGui
import imgui.flag.ImGuiWindowFlags
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.locale.Language
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import org.dimdev.dimcore.api.errata.schema.ElementIds
import org.dimdev.dimcore.api.errata.schema.FieldNode
import org.dimdev.dimcore.api.errata.schema.Presence
import org.dimdev.dimcore.api.errata.schema.SchemaNode
import org.dimdev.dimcore.api.errata.schema.SchemaNodeTypes
import org.dimdev.dimcore.api.errata.schema.SchemaType
import org.dimdev.dimcore.api.errata.schema.StructNode
import org.dimdev.dimcore.client.errata.ClientSession
import org.dimdev.dimcore.client.errata.ErrataClient
import org.dimdev.dimcore.client.errata.element.ColorElement
import org.dimdev.dimcore.client.errata.element.SchemaElements
import org.dimdev.dimcore.client.errata.imgui.render.*

object SchemaImGui : ErrataClient.View {
    private const val DISCARD = "Discard changes?"

    private var confirmClose = false

    private val renderers = mutableMapOf<SchemaType<out SchemaNode>, SchemaRenderer<out SchemaNode>>()

    fun init() {
        ErrataClient.view = this
        register(SchemaNodeTypes.BOOL, BoolRenderer)
        register(SchemaNodeTypes.INT, IntRenderer)
        register(SchemaNodeTypes.FLOAT, FloatRenderer)
        register(SchemaNodeTypes.DOUBLE, DoubleRenderer)
        register(SchemaNodeTypes.STRING, StringRenderer)
        register(SchemaNodeTypes.LIST, ListRenderer)
        register(SchemaNodeTypes.STRUCT, StructRenderer)
        register(SchemaNodeTypes.DISPATCH, DispatchRenderer)
        register(SchemaNodeTypes.REFERENCE, ReferenceRenderer)
        register(SchemaNodeTypes.UNTYPED, UntypedRenderer)
        register(SchemaNodeTypes.ELEMENT, ElementRenderer)
        SchemaElements.register(ElementIds.COLOR, ColorElement)
        ImGuiMCEvents.INSTANCE.preRenderImGuiEvent { render() }
    }

    private fun <T : SchemaNode> register(type: SchemaType<T>, renderer: SchemaRenderer<T>) {
        renderers[type] = renderer
    }

    private fun <T : SchemaNode> renderer(node: T): SchemaRenderer<T> = renderers.getValue(node.type) as SchemaRenderer<T>

    override fun open() = Minecraft.getInstance().setScreen(EditorScreen())

    override fun hide() {
        if (Minecraft.getInstance().screen is EditorScreen) Minecraft.getInstance().setScreen(null)
    }

    private fun close() {
        confirmClose = false
        ErrataClient.close()
        Minecraft.getInstance().setScreen(null)
    }

    private fun render() {
        if (Minecraft.getInstance().screen !is EditorScreen) return
        val session = ErrataClient.session ?: return
        var discard = false

        ImGui.begin("${session.title.string}###dimcore_schema", ImGuiWindowFlags.AlwaysAutoResize)

        for (error in session.errors) if (error.path == null) ImGui.textColored(1f, 0.4f, 0.4f, 1f, error.message.string)

        Editor(session).editFields(session.schema.root, emptyList(), session.working)?.let { session.working = it }

        ImGui.separator()
        if (ImGui.button("Apply")) session.apply()
        ImGui.sameLine()
        if (ImGui.button("Close")) Minecraft.getInstance().screen?.onClose()

        if (confirmClose) ImGui.openPopup(DISCARD)
        if (ImGui.beginPopupModal(DISCARD)) {
            ImGui.text("Unapplied changes will be lost.")
            if (ImGui.button("Discard")) discard = true
            ImGui.sameLine()
            if (ImGui.button("Cancel")) {
                confirmClose = false
                ImGui.closeCurrentPopup()
            }
            ImGui.endPopup()
        }

        ImGui.end()

        if (discard) close()
    }

    private class EditorScreen : Screen(Component.empty()) {
        override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {}

        override fun isPauseScreen() = false

        override fun onClose() {
            if (ErrataClient.session?.dirty == true) confirmClose = true else close()
        }
    }

    class Editor(val session: ClientSession) {
        private class Edit(val tag: Tag?)

        fun edit(node: SchemaNode, label: String, path: List<String>, value: Tag?): Tag? = renderer(node).edit(this, node, label, path, value)

        fun editFields(struct: StructNode, path: List<String>, value: CompoundTag): CompoundTag? {
            var result: CompoundTag? = null

            for (field in struct.fields) {
                ImGui.pushID(field.name)
                val edit = editField(struct, field, path + field.name, value.get(field.name))
                ImGui.popID()
                if (edit == null) continue

                val target = result ?: value.copy().also { result = it }

                if (edit.tag == null) target.remove(field.name) else target.put(field.name, edit.tag)
            }

            return result
        }

        private fun editField(owner: StructNode, field: FieldNode, path: List<String>, value: Tag?): Edit? {
            val label = label(field.labelKey(owner), field.name)
            var result: Edit? = null

            if (field.readOnly) ImGui.beginDisabled()

            if (field.presence == Presence.Nullable) {
                if (ImGui.checkbox("##set", value != null)) result = Edit(if (value == null) field.node.defaultTag(session.schema) else null)
                ImGui.sameLine()
            }

            if (value == null && field.presence == Presence.Nullable) ImGui.textDisabled(label)
            else edit(field.node, label, path, value ?: field.presence.value(field.node, session.schema))?.let { result = Edit(it) }

            if (field.readOnly) ImGui.endDisabled()

            errors(path)
            return result
        }

        private fun errors(path: List<String>) {
            for (error in session.errors) {
                if (error.path != path) continue

                ImGui.textColored(1f, 0.4f, 0.4f, 1f, error.message.string)
                ImGui.sameLine()
                if (ImGui.button("Reload")) session.reload(path)
            }
        }

        fun label(key: String, fallback: String): String = Language.getInstance().getOrDefault(key, fallback)
    }
}
