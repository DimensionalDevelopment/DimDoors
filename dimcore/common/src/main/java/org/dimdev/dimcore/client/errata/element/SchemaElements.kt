package org.dimdev.dimcore.client.errata.element

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimcore.api.errata.schema.SchemaNode


fun interface SchemaElement {
    fun edit(context: Context): Tag?

    class Context(
        val label: String,
        val path: List<String>,
        val value: Tag?,
        val config: CompoundTag,
        val node: SchemaNode,
        val fallback: () -> Tag?
    )
}

object SchemaElements {
    private val elements = mutableMapOf<ResourceLocation, SchemaElement>()

    fun register(id: ResourceLocation, element: SchemaElement) {
        elements[id] = element
    }

    operator fun get(id: ResourceLocation): SchemaElement? = elements[id]
}
