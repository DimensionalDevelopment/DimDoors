package org.dimdev.dimdoors.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.api.rift.target.Target
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.api.util.RGBA
import org.dimdev.dimdoors.rift.registry.LinkProperties
import org.dimdev.dimdoors.rift.registry.LinkPropertiesRegistry
import org.dimdev.dimdoors.rift.registry.RiftRegistry
import org.dimdev.dimdoors.rift.targets.MessageTarget
import org.dimdev.dimdoors.rift.targets.VirtualTarget
import org.dimdev.dimdoors.util.UUIDExtensions.rift
import java.util.function.Consumer

interface Rift : Target {

    var data: RiftData

    fun setDestination(destination: VirtualTarget<*>) {

        if (DimensionalDoors.LOGGER.isDebugEnabled) {
            DimensionalDoors.LOGGER.debug(
                "Setting destination {} for {}",
                destination,
                this.riftBlockPos.toShortString()
            )
        }

        val data = this.data

        val owner = location.riftOrPlaceholder().rift()

        if (data.destination != VirtualTarget.NoneTarget && this.isRegistered) {
            data.destination.unregister(owner)
        }

        data.destination = destination
        if (destination !== VirtualTarget.NoneTarget) {
            if (this.isRegistered) destination.register(owner)
        }
        this.setChanged()
        this.updateColor()
        this.isStateDirty = true
    }

    var isStateDirty: Boolean

    var properties: LinkProperties?
        get() = RiftRegistry.instance.idAt(location)?.let { LinkPropertiesRegistry.instance.getProperties(it) }
        set(properties) {
            val rift = RiftRegistry.instance.getRiftOrPlaceholder(location)

            LinkPropertiesRegistry.instance.setProperties(rift, properties)

            this.setChanged()
        }

    fun setChanged()

    fun markStateChanged() {
        this.isStateDirty = true
        this.setChanged()
    }

    val isAlwaysDelete: Boolean
        get() = this.data.alwaysDelete

    val isForcedColor: Boolean
        get() = this.data.forcedColor

    var color: RGBA
        get() = this.data.color
        set(color) {
            this.data.color = color
            this.setChanged()
        }

    val target: Target
        get() {
            val data = this.data

            return if (data.destination === VirtualTarget.NoneTarget) {
                MessageTarget("rifts.unlinked1")
            } else {
                data.destination
            }
        }

    val isRegistered: Boolean
        get() = RiftRegistry.instance.isRiftAt(location)

    fun register() {
        if (this.isRegistered) {
            return
        }

        val data = this.data

        RiftRegistry.instance.addRift(location)

        val owner = location.riftOrPlaceholder().rift()

        data.destination.takeIf { it !== VirtualTarget.NoneTarget }?.register(owner)

        this.updateColor()
    }

    fun unregister() {
        if (this.isDeleteRift && this.isRegistered) {
            RiftRegistry.instance.removeRift(location)
        }
    }

    fun updateType() {
        if (!this.isRegistered) return

        val rift = RiftRegistry.instance.getRift(location)
        LinkPropertiesRegistry.instance.setDetached(rift, this.isDetached)
    }

    fun handleSourceMoved(location: Location) {
        this.data.destination = location.asTarget()
        this.setChanged()
        this.updateColor()
    }

    fun handleTargetGone(location: Location) {
        val data = this.data

        if (data.destination.shouldInvalidate(location)) {
            data.destination = VirtualTarget.NoneTarget
            setChanged()
        }

        this.updateColor()
    }


    fun handleSourceGone(location: Location?) {
        this.updateColor()
    }

    fun updateColor() {
        val data = this.data

        if (data.forcedColor) return
        if (!this.isRegistered) {
            this.color = RGBA(0f, 0f, 0f, 1f)
        } else if (data.destination === VirtualTarget.NoneTarget) {
            data.color = RGBA(0.7f, 0.7f, 0.7f, 1f)
        } else {
            val newColor = data.destination.getColor(location.riftOrPlaceholder().rift())
            if (data.color != newColor) {
                data.color = newColor
                this.setChanged()
            }
        }
    }

    fun <T : Rift> copyFrom(rift: T) {
        this.data = rift.data.copy()
    }

    val isDetached: Boolean
        get() = false


    fun detach() {}

    val riftBlockPos: BlockPos

    val riftLevel: Level

    val riftBlockState: BlockState

    fun tick(level: Level, pos: BlockPos, blockState: BlockState) {
        if (level.isClientSide) return

        update(level, pos, blockState)
    }

    fun update(level: Level, pos: BlockPos, blockState: BlockState) {
    }

    var isDeleteRift: Boolean

    fun sync() {
        setChanged()

        val level = this.riftLevel

        try {
            level.sendBlockUpdated(this.riftBlockPos, this.riftBlockState, this.riftBlockState, 2)
        } catch (e: UnsupportedOperationException) {
            DimensionalDoors.LOGGER.warn("Failed to sync rift block entity: {}", e.message)
        }
    }

    fun gatherDebug(textConsumer: Consumer<Component>) {
        textConsumer.accept(Component.literal("Size: ${this.data.size}"))
    }

    val location: Location get() {
        return Location.ofWorld(
            this.riftLevel as ServerLevel,
            this.riftBlockPos
        )
    }
}