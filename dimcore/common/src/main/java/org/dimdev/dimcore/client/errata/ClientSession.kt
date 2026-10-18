package org.dimdev.dimcore.client.errata

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimcore.DimCore.platform
import org.dimdev.dimcore.api.errata.network.SchemaState
import org.dimdev.dimcore.api.errata.network.SessionApplyC2S
import org.dimdev.dimcore.api.errata.network.SessionResolveC2S
import org.dimdev.dimcore.api.errata.reference.ReferenceEntry
import org.dimdev.dimcore.api.errata.schema.ResolvedSchema
import org.dimdev.dimcore.api.errata.session.SchemaError
import org.dimdev.dimcore.api.ext.at
import org.dimdev.dimcore.api.ext.putAt

class ClientSession(val id: Int, val title: Component, val schema: ResolvedSchema, options: Map<ResourceLocation, List<ReferenceEntry>>, state: SchemaState) {
    class Option(val label: String, val value: Tag) {
        constructor(entry: ReferenceEntry) : this(entry.label?.string ?: entry.value.asString, entry.value)
    }

    val options: Map<ResourceLocation, List<Option>> = options.mapValues { (_, entries) -> entries.map(::Option) }
    var labels: Map<List<String>, String> = state.labels.mapValues { it.value.string }
    var inputs: Map<List<String>, Tag> = state.inputs
    var base: CompoundTag = state.value
    var working: CompoundTag = base.copy()
    var errors: List<SchemaError> = emptyList()

    val dirty get() = working != base

    fun get(path: List<String>): Tag? = working.at(path)

    fun set(path: List<String>, value: Tag?) = working.putAt(path, value)

    fun reload(path: List<String>) = set(path, base.at(path)?.copy())

    fun applied(state: SchemaState, errors: List<SchemaError>) {
        val kept = errors.mapNotNull { it.path }.associateWith { get(it)?.copy() }

        base = state.value
        working = state.value.copy()
        kept.forEach { (path, tag) -> set(path, tag) }
        labels = state.labels.mapValues { it.value.string }
        inputs = state.inputs
        this.errors = errors
    }

    fun resolved(path: List<String>, entry: ReferenceEntry?, input: Tag?, error: Component?) {
        entry?.let(::Option)?.let {
            set(path, it.value.copy())
            labels += path to it.label
        }
        input?.let { inputs += path to it }
        errors = listOfNotNull(error?.let { SchemaError(path, it) })
    }

    fun apply() = platform.sendPacket(SessionApplyC2S(id, working))

    fun resolve(path: List<String>, source: ResourceLocation, input: Tag) = platform.sendPacket(
        SessionResolveC2S(
            id,
            path,
            source,
            input
        )
    )
}
