package org.dimdev.dimdoors.rift

import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.Item
import org.dimdev.dimcore.api.ext.at
import org.dimdev.dimcore.api.ext.putAt
import org.dimdev.dimcore.api.errata.schema.FieldNode
import org.dimdev.dimcore.api.errata.schema.Presence
import org.dimdev.dimcore.api.errata.schema.SchemaCodec
import org.dimdev.dimcore.api.errata.schema.SchemaNode
import org.dimdev.dimcore.api.errata.schema.StructNode
import org.dimdev.dimcore.api.errata.schema.UntypedNode
import org.dimdev.dimcore.api.errata.subject.BlockEntitySubject
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.block.entity.CodecRecord
import org.dimdev.dimdoors.block.entity.RiftBlockEntity
import org.dimdev.dimdoors.block.entity.RiftData
import kotlin.jvm.optionals.getOrNull

class RiftConfigSubject<T : RiftBlockEntity<T>>(rift: T, hand: InteractionHand, item: Item) : BlockEntitySubject<T>(rift, hand, item) {
    override val title: Component get() = blockEntity.blockState.block.name

    override fun schema(player: ServerPlayer): StructNode {
        val data = RiftData.CODEC.node as StructNode
        val fields = listOf(
            FieldNode(
                DATA,
                data.copy(fields = data.fields.filterNot { it.name in HIDDEN_DATA }),
                Presence.Required
            )
        ) + blockEntity.configRecords.map { FieldNode(it.name, it.node ?: UntypedNode, Presence.Required) }

        return StructNode(
            BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.type) ?: DimensionalDoors.id("rift"),
            fields
        )
    }

    override fun read(player: ServerPlayer): CompoundTag {
        val ops = ops(player)
        val tag = CompoundTag()

        RiftData.CODEC.encodeStart(ops, blockEntity.data).result().getOrNull()?.let { data ->
            if (data is CompoundTag) HIDDEN_DATA.forEach(data::remove)
            tag.put(DATA, data)
        }
        blockEntity.configRecords.forEach { record -> record.encode(blockEntity, ops).result().getOrNull()?.let { tag.put(record.name, it) } }

        return tag
    }

    override fun write(player: ServerPlayer, value: CompoundTag, paths: List<List<String>>): DataResult<Unit> {
        val ops = ops(player)
        val rift = blockEntity
        val dataPaths = paths.filter { it.firstOrNull() == DATA }
        var data: RiftData? = null

        if (dataPaths.isNotEmpty()) {
            val merged = RiftData.CODEC.encodeStart(ops, rift.data).result().getOrNull() as? CompoundTag ?: return DataResult.error { "$DATA: current value did not encode" }
            dataPaths.forEach { merged.putAt(it.drop(1), value.at(it)) }

            val decoded = RiftData.CODEC.parse(ops, merged)
            data = decoded.result().getOrNull() ?: return DataResult.error { "$DATA: ${decoded.error().get().message()}" }
        }

        val pending = mutableMapOf<String, () -> Unit>()
        for (record in rift.configRecords) {
            val tag = value.get(record.name) ?: continue
            val result = record.prepare(rift, tag, ops)
            pending[record.name] = result.result().getOrNull() ?: return DataResult.error { "${record.name}: ${result.error().get().message()}" }
        }

        data?.let {
            val destination = it.destination
            it.destination = rift.data.destination
            rift.data = it
            if (listOf(DATA, "destination") in dataPaths) rift.setDestination(destination)
        }

        for (path in paths) {
            if (path.firstOrNull() != DATA) path.firstOrNull()?.let(pending::get)?.invoke()
        }

        rift.updateColor()
        rift.sync()
        return DataResult.success(Unit)
    }

    private fun ops(player: ServerPlayer): DynamicOps<Tag> = player.server.registryAccess().createSerializationContext(NbtOps.INSTANCE)

    companion object {
        private const val DATA = "data"
        private val HIDDEN_DATA = setOf("properties", "forcedColor")

        private val CodecRecord<*, *>.node: SchemaNode? get() = (codec as? SchemaCodec<*>)?.node

        private fun <T, V> CodecRecord<T, V>.prepare(subject: T, tag: Tag, ops: DynamicOps<Tag>): DataResult<() -> Unit> = parse(ops, tag).map { value -> { setter(subject, value) } }
    }
}
