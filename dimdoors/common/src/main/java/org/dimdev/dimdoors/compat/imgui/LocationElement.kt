package org.dimdev.dimdoors.compat.imgui

import imgui.ImGui
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import org.dimdev.dimcore.api.errata.schema.ReferenceNode
import org.dimdev.dimcore.client.errata.ErrataClient
import org.dimdev.dimcore.client.errata.element.SchemaElement
import org.dimdev.dimdoors.api.util.Location
import kotlin.jvm.optionals.getOrNull

object LocationElement : SchemaElement {
    private class Draft(val session: Int, val from: Tag?, var world: ResourceKey<Level>, val pos: IntArray)

    private val drafts = mutableMapOf<List<String>, Draft>()

    override fun edit(context: SchemaElement.Context): Tag? {
        val session = ErrataClient.session ?: return null
        val source = (context.node as? ReferenceNode)?.source ?: return context.fallback()
        val input = session.inputs[context.path]
        val draft = drafts[context.path]?.takeIf { it.session == session.id && it.from == input } ?: draft(session.id, input).also { drafts[context.path] = it }

        ImGui.text("${context.label}: ${session.labels[context.path] ?: "-"}")

        var changed = false

        if (ImGui.beginCombo("World", draft.world.location().toString())) {
            for (level in Minecraft.getInstance().connection?.levels().orEmpty()) {
                if (ImGui.selectable(level.location().toString(), level == draft.world) && level != draft.world) {
                    draft.world = level
                    changed = true
                }
            }
            ImGui.endCombo()
        }
        ImGui.inputInt3("Position", draft.pos)
        if (ImGui.isItemDeactivatedAfterEdit()) changed = true

        if (ImGui.button("Here")) Minecraft.getInstance().player?.let { player ->
            draft.world = player.level().dimension()
            player.blockPosition().let { draft.pos[0] = it.x; draft.pos[1] = it.y; draft.pos[2] = it.z }
            changed = true
        }

        if (changed) encode(draft)?.let { session.resolve(context.path, source, it) }

        return null
    }

    private fun draft(session: Int, input: Tag?): Draft {
        val location = input?.let { Location.CODEC.parse(NbtOps.INSTANCE, it).result().getOrNull() }
        val player = Minecraft.getInstance().player
        val world = location?.worldId ?: player?.level()?.dimension() ?: Level.OVERWORLD
        val pos = location?.blockPos ?: player?.blockPosition() ?: BlockPos.ZERO

        return Draft(session, input, world, intArrayOf(pos.x, pos.y, pos.z))
    }

    private fun encode(draft: Draft): Tag? = Location.CODEC.encodeStart(NbtOps.INSTANCE, Location(draft.world, BlockPos(draft.pos[0], draft.pos[1], draft.pos[2]))).result().getOrNull()
}
