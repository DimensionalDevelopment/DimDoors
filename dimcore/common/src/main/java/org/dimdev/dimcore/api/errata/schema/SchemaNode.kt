package org.dimdev.dimcore.api.errata.schema

import io.netty.buffer.ByteBuf
import net.minecraft.nbt.*
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.codec.StreamCodec.recursive
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimcore.api.ext.nullable

sealed interface SchemaNode {
    val type: SchemaType<out SchemaNode>

    val children: List<SchemaNode> get() = emptyList()

    fun defaultTag(schema: ResolvedSchema): Tag?

    fun references(schema: ResolvedSchema, value: Tag, path: List<String>, visit: (List<String>, ReferenceNode, Tag) -> Unit) {}

    companion object {
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SchemaNode> = recursive {
            SchemaNodeTypes.STREAM_CODEC.cast<RegistryFriendlyByteBuf>().dispatch(SchemaNode::type) { type -> type.streamCodec }
        }
    }
}

object SchemaNodeTypes {
    val BOOL = SchemaType(BoolNode.STREAM_CODEC)
    val INT = SchemaType(IntNode.STREAM_CODEC)
    val FLOAT = SchemaType(FloatNode.STREAM_CODEC)
    val DOUBLE = SchemaType(DoubleNode.STREAM_CODEC)
    val STRING = SchemaType(StringNode.STREAM_CODEC)
    val LIST = SchemaType(ListNode.STREAM_CODEC)
    val STRUCT = SchemaType(StructNode.STREAM_CODEC)
    val DISPATCH = SchemaType(DispatchNode.STREAM_CODEC)
    val REFERENCE = SchemaType(ReferenceNode.STREAM_CODEC)
    val UNTYPED = SchemaType(UntypedNode.STREAM_CODEC)
    val ELEMENT = SchemaType(ElementNode.STREAM_CODEC)

    private val TYPES: List<SchemaType<out SchemaNode>> = listOf(BOOL, INT, FLOAT, DOUBLE, STRING, LIST, STRUCT, DISPATCH, REFERENCE, UNTYPED, ELEMENT)

    val STREAM_CODEC: StreamCodec<ByteBuf, SchemaType<out SchemaNode>> = ByteBufCodecs.idMapper(TYPES::get, TYPES::indexOf)
}

data class BoolNode(val default: Boolean = false) : SchemaNode {
    override val type get() = SchemaNodeTypes.BOOL

    override fun defaultTag(schema: ResolvedSchema): Tag = ByteTag.valueOf(default)

    companion object {
        val STREAM_CODEC: StreamCodec<ByteBuf, BoolNode> = ByteBufCodecs.BOOL.map(::BoolNode, BoolNode::default)
    }
}

data class StringNode(val default: String = "") : SchemaNode {
    override val type get() = SchemaNodeTypes.STRING

    override fun defaultTag(schema: ResolvedSchema): Tag = StringTag.valueOf(default)

    companion object {
        val STREAM_CODEC: StreamCodec<ByteBuf, StringNode> = ByteBufCodecs.STRING_UTF8.map(::StringNode, StringNode::default)
    }
}

data class ListNode(val element: SchemaNode, val unique: Boolean) : SchemaNode {
    override val type get() = SchemaNodeTypes.LIST

    override val children get() = listOf(element)

    override fun defaultTag(schema: ResolvedSchema): Tag = ListTag()

    override fun references(schema: ResolvedSchema, value: Tag, path: List<String>, visit: (List<String>, ReferenceNode, Tag) -> Unit) {
        if (value is ListTag) value.forEachIndexed { index, tag -> element.references(schema, tag, path + index.toString(), visit) }
    }

    companion object {
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ListNode> = StreamCodec.composite(
            SchemaNode.STREAM_CODEC, ListNode::element,
            ByteBufCodecs.BOOL, ListNode::unique,
            ::ListNode
        )
    }
}

data class StructNode(val id: ResourceLocation, val fields: List<FieldNode>) : SchemaNode {
    override val type get() = SchemaNodeTypes.STRUCT

    override val children get() = fields.map { it.node }

    val labelKey get() = "schema.${id.namespace}.${id.path.replace('/', '.')}"

    fun field(name: String): FieldNode? = fields.firstOrNull { it.name == name }

    fun fieldAt(path: List<String>): FieldNode? {
        val field = path.firstOrNull()?.let(::field) ?: return null
        return if (path.size == 1) field else (field.node as? StructNode)?.fieldAt(path.drop(1))
    }

    fun changed(client: CompoundTag, base: CompoundTag, prefix: List<String> = emptyList()): List<List<String>> = fields.filterNot { it.readOnly }.flatMap { field ->
        val path = prefix + field.name
        val clientValue = client.get(field.name)
        val baseValue = base.get(field.name)
        val node = field.node

        when {
            clientValue == baseValue -> emptyList()
            node is StructNode && clientValue is CompoundTag && baseValue is CompoundTag -> node.changed(clientValue, baseValue, path)
            else -> listOf(path)
        }
    }

    override fun defaultTag(schema: ResolvedSchema): CompoundTag = CompoundTag().also { tag -> fields.forEach { field -> field.presence.value(field.node, schema)?.let { tag.put(field.name, it) } } }

    override fun references(schema: ResolvedSchema, value: Tag, path: List<String>, visit: (List<String>, ReferenceNode, Tag) -> Unit) {
        if (value is CompoundTag) fields.forEach { field -> value.get(field.name)?.let { field.node.references(schema, it, path + field.name, visit) } }
    }

    companion object {
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, StructNode> = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, StructNode::id,
            FieldNode.STREAM_CODEC.apply(ByteBufCodecs.list()), StructNode::fields,
            ::StructNode
        )
    }
}

data class FieldNode(val name: String, val node: SchemaNode, val presence: Presence, val readOnly: Boolean = false, val label: String? = null, val overridable: Boolean = false) {
    fun labelKey(owner: StructNode): String = label ?: "${owner.labelKey}.$name"

    companion object {
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, FieldNode> = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, FieldNode::name,
            SchemaNode.STREAM_CODEC, FieldNode::node,
            Presence.STREAM_CODEC, FieldNode::presence,
            ByteBufCodecs.BOOL, FieldNode::readOnly,
            ByteBufCodecs.STRING_UTF8.nullable(), FieldNode::label,
            ByteBufCodecs.BOOL, FieldNode::overridable,
            ::FieldNode
        )
    }
}

data class DispatchNode(val id: ResourceLocation, val typeKey: String, val defaultVariant: ResourceLocation? = null) : SchemaNode {
    override val type get() = SchemaNodeTypes.DISPATCH

    fun variant(type: ResourceLocation, schema: ResolvedSchema): CompoundTag? = schema.variants(this)?.get(type)?.defaultTag(schema)?.apply { putString(typeKey, type.toString()) }

    override fun defaultTag(schema: ResolvedSchema): Tag? = (defaultVariant ?: schema.variants(this)?.keys?.firstOrNull())?.let { variant(it, schema) }

    override fun references(schema: ResolvedSchema, value: Tag, path: List<String>, visit: (List<String>, ReferenceNode, Tag) -> Unit) {
        if (value is CompoundTag) ResourceLocation.tryParse(value.getString(typeKey))?.let { schema.variants(this)?.get(it) }?.references(schema, value, path, visit)
    }

    companion object {
        val STREAM_CODEC: StreamCodec<ByteBuf, DispatchNode> = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, DispatchNode::id,
            ByteBufCodecs.STRING_UTF8, DispatchNode::typeKey,
            ResourceLocation.STREAM_CODEC.nullable(), DispatchNode::defaultVariant,
            ::DispatchNode
        )
    }
}

data class ReferenceNode(val source: ResourceLocation) : SchemaNode {
    override val type get() = SchemaNodeTypes.REFERENCE

    override fun defaultTag(schema: ResolvedSchema): Tag? = null

    override fun references(schema: ResolvedSchema, value: Tag, path: List<String>, visit: (List<String>, ReferenceNode, Tag) -> Unit) = visit(path, this, value)

    companion object {
        val STREAM_CODEC: StreamCodec<ByteBuf, ReferenceNode> = ResourceLocation.STREAM_CODEC.map(::ReferenceNode, ReferenceNode::source)
    }
}

data object UntypedNode : SchemaNode {
    override val type get() = SchemaNodeTypes.UNTYPED

    override fun defaultTag(schema: ResolvedSchema): Tag? = null

    val STREAM_CODEC: StreamCodec<ByteBuf, UntypedNode> = StreamCodec.unit(this)
}

data class ElementNode(val element: ResourceLocation, val node: SchemaNode, val config: CompoundTag = CompoundTag()) : SchemaNode {
    override val type get() = SchemaNodeTypes.ELEMENT

    override val children get() = listOf(node)

    override fun defaultTag(schema: ResolvedSchema): Tag? = node.defaultTag(schema)

    override fun references(schema: ResolvedSchema, value: Tag, path: List<String>, visit: (List<String>, ReferenceNode, Tag) -> Unit) = node.references(schema, value, path, visit)

    companion object {
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ElementNode> = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, ElementNode::element,
            SchemaNode.STREAM_CODEC, ElementNode::node,
            ByteBufCodecs.COMPOUND_TAG, ElementNode::config,
            ::ElementNode
        )
    }
}

sealed interface Presence {
    val type: SchemaType<out Presence>

    fun value(node: SchemaNode, schema: ResolvedSchema): Tag?

    data object Required : Presence {
        override val type get() = PresenceTypes.REQUIRED

        override fun value(node: SchemaNode, schema: ResolvedSchema): Tag? = node.defaultTag(schema)

        val STREAM_CODEC: StreamCodec<ByteBuf, Required> = StreamCodec.unit(this)
    }

    class Defaulted(supplier: () -> Tag?) : Presence {
        val default: Tag? by lazy(supplier)

        override val type get() = PresenceTypes.DEFAULTED

        override fun value(node: SchemaNode, schema: ResolvedSchema): Tag? = default ?: node.defaultTag(schema)

        companion object {
            val STREAM_CODEC: StreamCodec<ByteBuf, Defaulted> = ByteBufCodecs.TAG.nullable().map({ tag -> Defaulted { tag } }, Defaulted::default)
        }
    }

    data object Nullable : Presence {
        override val type get() = PresenceTypes.NULLABLE

        override fun value(node: SchemaNode, schema: ResolvedSchema): Tag? = null

        val STREAM_CODEC: StreamCodec<ByteBuf, Nullable> = StreamCodec.unit(this)
    }

    companion object {
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Presence> = PresenceTypes.STREAM_CODEC.cast<RegistryFriendlyByteBuf>().dispatch(Presence::type) { type -> type.streamCodec }
    }
}

object PresenceTypes {
    val REQUIRED = SchemaType(Presence.Required.STREAM_CODEC)
    val DEFAULTED = SchemaType(Presence.Defaulted.STREAM_CODEC)
    val NULLABLE = SchemaType(Presence.Nullable.STREAM_CODEC)

    private val TYPES: List<SchemaType<out Presence>> = listOf(REQUIRED, DEFAULTED, NULLABLE)

    val STREAM_CODEC: StreamCodec<ByteBuf, SchemaType<out Presence>> = ByteBufCodecs.idMapper(TYPES::get, TYPES::indexOf)
}
