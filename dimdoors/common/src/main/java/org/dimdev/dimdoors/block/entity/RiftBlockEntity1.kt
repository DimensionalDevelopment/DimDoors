package org.dimdev.dimdoors.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.TickTask
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimcore.api.errata.schema.FieldNode
import org.dimdev.dimcore.api.errata.schema.Presence
import org.dimdev.dimcore.api.errata.schema.UntypedNode
import org.dimdev.dimdoors.rift.RiftConfigSubject.Companion.node
import org.dimdev.dimdoors.rift.registry.LegacyDimensionalRegistryMigrator

open class RiftBlockEntity<V: RiftBlockEntity<V>>(type: BlockEntityType<out V>, pos: BlockPos, state: BlockState) : RecordCodecUsingBlockEntity<V>(type, pos, state), Rift<V> {
    override var data: RiftData = RiftData()
    override var isStateDirty: Boolean = false
    override var isDeleteRift: Boolean = true
    private var legacyDestination: CompoundTag? = null //TODO: purge this

    override fun loadAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) {
        prepareTag(nbt)
        super.loadAdditional(nbt, provider)
    }

    protected open fun prepareTag(nbt: CompoundTag) {
        if (nbt.contains("size", Tag.TAG_FLOAT.toInt())) {
            val data = if (nbt.contains("data")) nbt.getCompound("data") else CompoundTag()
            data.remove("forcedColor")
            legacyDestination = data.getCompound("destination")
            data.remove("destination")
            data.putInt("size", Math.clamp(nbt.getFloat("size"), 0f, 200f).toInt())
            nbt.put("data", data)
        }
    }

    override fun setLevel(level: Level) {
        super.setLevel(level)
        if (level !is ServerLevel) return

        legacyDestination?.let { data.destination = LegacyDimensionalRegistryMigrator.upgradeDestination(it, location) }
            ?.also {
                legacyDestination = null
                level.server.tell(TickTask(level.server.tickCount) {
                    if (!isRemoved) {
                        updateColor()
                        setChanged()
                    }
                })
            }
    }

    override fun <T> serialize(serialize: Serialize<T, V>) {
        serialize.put(RIFT_DATA_BUILDER)

        writeUniqueData(serialize)
    }

    override fun <T> deserialize(nbt: Deserialize<T>) {
        this.data = nbt.get(RIFT_DATA_BUILDER)

        readUniqueData(nbt)
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener?>? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getUpdateTag(provider: HolderLookup.Provider): CompoundTag {
        val nbt = super.getUpdateTag(provider)

        saveAdditional(nbt, provider)
        nbt.getCompound("data").run {
            remove("destination")
            remove("properties")
        }
        return nbt
    }

    override fun onlyOpCanSetNbt() = true

    override fun setChanged() = super.setChanged()

    override val riftBlockPos: BlockPos get() = blockPos

    override val riftBlockState: BlockState get() = blockState

    override val riftLevel get() = level!!

    class Impl(pos: BlockPos, state: BlockState) : EntranceRiftBlockEntity<Impl>(ModBlockEntityTypes.GENERIC_RIFT, pos, state)

    companion object {
        val CodecRecord<*, *>.fieldNode get() = FieldNode(name, node ?: UntypedNode, Presence.Required)

        val RIFT_DATA_BUILDER = CodecRecord("data", RiftData.CODEC, ::RiftData, Rift<*>::data) { rift, data -> rift.data = data }
    }
}
