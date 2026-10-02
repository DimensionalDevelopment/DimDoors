package org.dimdev.dimdoors.block.entity

import com.mojang.serialization.DynamicOps
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimcore.api.cast
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.api.util.Location

open class RiftBlockEntity<T : RiftBlockEntity<T>>(type: BlockEntityType<T>, pos: BlockPos, state: BlockState) : BlockEntity(type, pos, state), Rift {
    override var data: RiftData = RiftData()
    override var isStateDirty: Boolean = false
    override var isDeleteRift: Boolean = true

    override fun loadAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) {
        prepareTag(nbt)

        this.deserialize(Deserialize.create(provider.createSerializationContext(NbtOps.INSTANCE), nbt)
        )
    }

    protected open fun prepareTag(nbt: CompoundTag) {
        if (nbt.contains("size", Tag.TAG_FLOAT.toInt())) {
            val data = if (nbt.contains("data")) nbt.getCompound("data") else CompoundTag()
            data.putInt("size", Math.clamp(nbt.getFloat("size"), 0f, 200f).toInt())
            nbt.put("data", data)
        }
    }

    open fun deserialize(nbt: Deserialize<Tag>) {
        this.data = nbt.get(RIFT_DATA_BUILDER)
    }

    public override fun saveAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) {
        val serialize = Serialize.nbt(nbt, this.cast<T>())

        this.serialize(serialize)
    }

    open fun serialize(serialize: Serialize<Tag, T>) {
        serialize.put(RIFT_DATA_BUILDER)
    }

    @JvmRecord
    data class Deserialize<K>(val data: K, val ops: DynamicOps<K>) {
        fun <V, O> get(builder: CodecRecord<V, O>): O {
            val field = ops.get(data, builder.name).result().orElse(null) ?: return builder.defaultValue()

            return builder.codec.parse(ops, field)
                .ifError { DimensionalDoors.LOGGER.error("Failed to decode rift field '{}': {}", builder.name, it.message()) }
                .result().orElseGet(builder.defaultValue)
        }

        companion object {
            fun <V> create(ops: DynamicOps<V>, `object`: V): Deserialize<V> = Deserialize(`object`, ops)
        }
    }

    @JvmRecord
    data class Serialize<K, T>(
        val data: K,
        val ops: DynamicOps<K>,
        val subject: T,
        val consumer: (String, K, K) -> Unit
    ) {
        fun <O> put(builder: CodecRecord<in T, O>) {

            val value = builder.codec.encodeStart(ops, builder.function.invoke(subject)).getOrThrow()
            consumer.invoke(builder.name, data, value)
        }

        companion object {
            private fun nbtAccept(name: String, tag1: Tag, data: Tag) {
                (tag1 as CompoundTag).put(name, data)
            }

            fun <R> nbt(tag: CompoundTag, `object`: R): Serialize<Tag, R> {
                return Serialize<Tag, R>(
                    tag,
                    NbtOps.INSTANCE,
                    `object`,
                    ::nbtAccept
                )
            }
        }
    }


    override fun getUpdatePacket(): Packet<ClientGamePacketListener?>? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getUpdateTag(provider: HolderLookup.Provider): CompoundTag {
        val nbt = super.getUpdateTag(provider)

        saveAdditional(nbt, provider)
        return nbt
    }

    override fun setChanged() = super.setChanged()

    override fun handleSourceMoved(location: Location) {
        this.data.destination = location.asTarget()
        this.setChanged()
        this.updateColor()
    }


    override val riftBlockPos: BlockPos get() = blockPos

    override val riftBlockState: BlockState get() = blockState

    override val riftLevel get() = level!!

    class Impl(pos: BlockPos, state: BlockState) : EntranceRiftBlockEntity<Impl>(ModBlockEntityTypes.GENERIC_RIFT, pos, state)

    companion object {
        val RIFT_DATA_BUILDER = CodecRecord("data", RiftData.CODEC, ::RiftData, Rift::data)
    }
}
