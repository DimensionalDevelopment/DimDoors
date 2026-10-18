package org.dimdev.dimcore.api.errata.schema

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimcore.api.errata.network.SchemaStreamCodecs

class ResolvedSchema(val root: StructNode, val tables: DispatchTables) {
    val sources: Set<ResourceLocation> by lazy {
        val sources = mutableSetOf<ResourceLocation>()

        fun visit(node: SchemaNode) {
            if (node is ReferenceNode) sources += node.source
            node.children.forEach(::visit)
        }

        visit(root)
        tables.values.forEach { it.values.forEach(::visit) }
        sources
    }

    fun variants(node: DispatchNode): Map<ResourceLocation, StructNode>? = tables[node.id]

    companion object {
        @JvmField val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ResolvedSchema> = StreamCodec.composite(
            StructNode.STREAM_CODEC, ResolvedSchema::root,
            SchemaStreamCodecs.linkedMap(ResourceLocation.STREAM_CODEC, SchemaStreamCodecs.linkedMap(ResourceLocation.STREAM_CODEC, StructNode.STREAM_CODEC)), ResolvedSchema::tables,
            ::ResolvedSchema
        )

        fun of(root: StructNode): ResolvedSchema {
            val tables = linkedMapOf<ResourceLocation, Map<ResourceLocation, StructNode>>()

            fun visit(node: SchemaNode) {
                if (node is DispatchNode && node.id !in tables) DispatchSchemas.variants(node.id)?.let { table ->
                    tables[node.id] = table
                    table.values.forEach(::visit)
                }
                node.children.forEach(::visit)
            }

            visit(root)
            return ResolvedSchema(root, tables)
        }
    }
}
