package org.dimdev.dimcore.api.errata.session

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import org.dimdev.dimcore.DimCore.platform
import org.dimdev.dimcore.api.errata.network.SchemaState
import org.dimdev.dimcore.api.errata.network.SessionAppliedS2C
import org.dimdev.dimcore.api.errata.network.SessionResolvedS2C
import org.dimdev.dimcore.api.errata.reference.ReferenceEntry
import org.dimdev.dimcore.api.errata.reference.ReferenceSources
import org.dimdev.dimcore.api.errata.schema.ResolvedSchema
import org.dimdev.dimcore.api.errata.subject.ConfigSubject
import org.dimdev.dimcore.api.ext.at

class ErrataSession(val id: Int, val player: ServerPlayer, val subject: ConfigSubject, val schema: ResolvedSchema, var base: CompoundTag) {
    fun apply(value: CompoundTag) {
        val current = subject.read(player)
        val errors = mutableListOf<SchemaError>()

        val paths = schema.root.changed(value, base).filter { path ->
            if (!ErrataPermissions.canWrite(player, subject, path)) {
                errors += SchemaError(path, SchemaMessages.NO_PERMISSION)
                false
            } else if (current.at(path) != base.at(path) && !canOverride(path)) {
                errors += SchemaError(path, SchemaMessages.CONFLICT)
                false
            } else {
                validReferences(value, path, errors)
            }
        }

        if (paths.isNotEmpty()) subject.write(player, value, paths).ifError { errors += SchemaError(null, Component.literal(it.message())) }

        base = subject.read(player)
        platform.sendPacket(player, SessionAppliedS2C(id, state(), errors))
    }

    fun resolve(path: List<String>, source: ResourceLocation, input: Tag) {
        val referenceSource = ReferenceSources[source]?.takeIf { it.id in schema.sources } ?: return
        val entry = referenceSource.resolve(player, input)

        platform.sendPacket(player, when {
            entry == null -> SessionResolvedS2C(id, path, null, null, SchemaMessages.UNRESOLVED)
            !referenceSource.validate(player, entry.value) -> SessionResolvedS2C(id, path, null, null, SchemaMessages.INVALID_REFERENCE)
            else -> SessionResolvedS2C(id, path, entry, referenceSource.input(player, entry.value), null)
        })
    }

    fun options(): Map<ResourceLocation, List<ReferenceEntry>> = schema.sources
        .mapNotNull { ReferenceSources[it] }
        .associate { it.id to it.options(player) }
        .filterValues { it.isNotEmpty() }

    fun state(): SchemaState {
        val labels = linkedMapOf<List<String>, Component>()
        val inputs = linkedMapOf<List<String>, Tag>()

        schema.root.references(schema, base, emptyList()) { path, node, tag ->
            val source = ReferenceSources[node.source] ?: return@references
            source.describe(player, tag)?.let { labels[path] = it }
            source.input(player, tag)?.let { inputs[path] = it }
        }

        return SchemaState(base, labels, inputs)
    }

    fun closeReason(): Component? = when {
        player.isRemoved -> SchemaMessages.CLOSED_INVALID
        !ErrataPermissions.canOpen(player, subject) -> SchemaMessages.CLOSED_PERMISSION
        !subject.isValid(player) -> SchemaMessages.CLOSED_INVALID
        else -> null
    }

    private fun canOverride(path: List<String>) =
        (1..path.size).any { schema.root.fieldAt(path.take(it))?.overridable == true } && ErrataPermissions.canOverride(player, subject, path)

    private fun validReferences(value: CompoundTag, path: List<String>, errors: MutableList<SchemaError>): Boolean {
        val field = schema.root.fieldAt(path) ?: return false
        var valid = true

        value.at(path)?.let {
            field.node.references(schema, it, path) { referencePath, node, tag ->
                if (ReferenceSources[node.source]?.validate(player, tag) != true) {
                    valid = false
                    errors += SchemaError(referencePath, SchemaMessages.INVALID_REFERENCE)
                }
            }
        }

        return valid
    }
}
