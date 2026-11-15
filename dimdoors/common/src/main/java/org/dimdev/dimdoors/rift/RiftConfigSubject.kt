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
import net.minecraft.world.level.block.entity.BlockEntity
import org.dimdev.dimcore.api.errata.schema.*
import org.dimdev.dimcore.api.errata.subject.ConfigSubject
import org.dimdev.dimcore.api.ext.at
import org.dimdev.dimcore.api.ext.putAt
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.block.entity.*
import org.dimdev.dimdoors.block.entity.CodecRecord
import org.dimdev.dimdoors.block.entity.Deserialize
import org.dimdev.dimdoors.block.entity.Serialize
import org.dimdev.dimdoors.command.literal
import org.dimdev.dimdoors.rift.targets.LocationProvider
import kotlin.jvm.optionals.getOrNull
import org.dimdev.dimdoors.block.entity.RiftData

class RiftConfigSubject<T : Rift<T, H, *>, H: LocationProvider>(val rift: T, val holder: H, private val hand: InteractionHand, private val item: Item) : ConfigSubject {
    override val title: Component get() = rift.name.literal() //TODO: Later turn this into a component.

    override fun schema(player: ServerPlayer): StructNode {
        val data = RiftData.CODEC.node as StructNode
        val fields = mutableListOf(
            FieldNode(
                DATA,
                data.copy(fields = data.fields.filterNot { it.name in HIDDEN_DATA }),
                Presence.Required
            )
        ).also(rift::gatherFieldNodes)

        return StructNode(
            (rift as? BlockEntity)?.type?.let(BuiltInRegistries.BLOCK_ENTITY_TYPE::getKey) ?: DimensionalDoors.id("rift"),
            fields
        )
    }

    override fun read(player: ServerPlayer): CompoundTag {
        val ops = ops(player)
        val tag = CompoundTag()

        RiftData.CODEC.encodeStart(ops, rift.data(holder)).result().getOrNull()?.let { data ->
            if (data is CompoundTag) HIDDEN_DATA.forEach(data::remove)
            tag.put(DATA, data)
        }

        rift.writeUniqueData(Serialize.nbt(null, tag, rift))

        return tag
    }

    override fun write(player: ServerPlayer, value: CompoundTag, paths: List<List<String>>): DataResult<Unit> {
        val ops = ops(player)
        val dataPaths = paths.filter { it.firstOrNull() == DATA }
        var data: RiftData? = null

        if (dataPaths.isNotEmpty()) {
            val merged = RiftData.CODEC.encodeStart(ops, rift.data(holder)).result().getOrNull() as? CompoundTag ?: return DataResult.error { "$DATA: current value did not encode" }
            dataPaths.forEach { merged.putAt(it.drop(1), value.at(it)) }

            val decoded = RiftData.CODEC.parse(ops, merged)
            data = decoded.result().getOrNull() ?: return DataResult.error { "$DATA: ${decoded.error().get().message()}" }
        }

        data?.let {
            val destination = it.destination
            it.destination = rift.data(holder).destination
            rift.setRiftData(holder, it)
            if (listOf(DATA, "destination") in dataPaths) rift.setDestination(holder, destination)
        }

        val unique = CompoundTag().also { rift.writeUniqueData(Serialize.nbt(player.server.registryAccess(), it, rift)) }
        paths.filter { it.firstOrNull() != DATA }.forEach { unique.putAt(it, value.at(it)) }
        rift.readUniqueData(Deserialize.nbt(player.server.registryAccess(), unique))

        rift.updateColor(holder)
        rift.sync(holder)
        return DataResult.success(Unit)
    }

    override fun isValid(player: ServerPlayer): Boolean {
        val location = holder.providedLocation!!
        return player.level() === location.world && player.canInteractWithBlock(location.blockPos, 0.0) && player.getItemInHand(hand).`is`(item)
    }

    private fun ops(player: ServerPlayer): DynamicOps<Tag> = player.server.registryAccess().createSerializationContext(NbtOps.INSTANCE)

    companion object {
        private const val DATA = "data"
        private val HIDDEN_DATA = setOf("properties", "forcedColor")

        val CodecRecord<*, *>.node: SchemaNode? get() = (codec as? SchemaCodec<*>)?.node
    }
}