package org.dimdev.dimdoors.blockentity

import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.entity.BlockEntity
import org.dimdev.dimcore.api.ext.cast
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.rift.Rift
import org.dimdev.dimdoors.rift.RiftData
import org.dimdev.dimdoors.rift.block.LocationRiftRegistry
import org.dimdev.dimdoors.rift.link.LinkProperties
import org.dimdev.dimdoors.rift.targets.LocationProvider
import org.dimdev.dimdoors.rift.targets.VirtualTarget
import org.dimdev.dimdoors.subsystem.graph.RegistryVertex
import org.dimdev.dimdoors.subsystem.graph.RegistryVertices
import org.dimdev.dimdoors.subsystem.graph.Vertex
import org.dimdev.dimdoors.subsystem.registry.SubSystem
import org.dimdev.dimdoors.subsystem.registry.SubsystemTypes
import org.dimdev.dimdoors.util.Location
import java.util.function.Consumer

interface BlockEntityRift<R> : Rift<R, Location, LocationRiftRegistry>, LocationProvider where R : BlockEntity, R : BlockEntityRift<R> {
    fun self(): R = this.cast()

    override val providedLocation: Location
        get() {
            val be = self()
            val level = be.level ?: throw IllegalStateException("Rift has no level")
            return Location(level.dimension(), be.blockPos)
        }

    override val riftRegistry: LocationRiftRegistry
        get() {
            check(self().level is ServerLevel) { "Rift registry accessed on client" }
            return super.riftRegistry
        }

    override val riftRegistryType: SubSystem.Type<LocationRiftRegistry> get() = SubsystemTypes.LOCATION_RIFT
    override val vertexType: RegistryVertex get() = RegistryVertices.LOCATION_RIFT

    fun location(): Location = providedLocation
    fun data(): RiftData = data(providedLocation)
    fun setRiftData(data: RiftData) = setRiftData(providedLocation, data)
    fun vertex(): Vertex = vertex(providedLocation)
    fun isRegistered() = isRegistered(providedLocation)
    fun register() = register(providedLocation)
    fun unregister() = unregister(providedLocation)
    fun sync() = sync(providedLocation)
    fun updateColor() = updateColor(providedLocation)
    fun handleSourceGone() = handleSourceGone(providedLocation)
    fun handleTargetGone() = handleTargetGone(providedLocation)
    fun updateType() = updateType(providedLocation)
    fun configSubject(hand: InteractionHand, item: Item) = configSubject(providedLocation, hand, item)
    fun setDestination(destination: VirtualTarget<*>) = setDestination(providedLocation, destination)
    fun properties(): LinkProperties? = properties(providedLocation)
    fun setProperties(properties: LinkProperties?) = setProperties(providedLocation, properties)
    fun id() = id(providedLocation)
    fun color() = color(providedLocation)
    fun markStateChanged() = markStateChanged(providedLocation)
    fun gatherDebug(textConsumer: Consumer<Component>) = gatherDebug(providedLocation, textConsumer)
    fun detach() {}

    fun copyFrom(rift: BlockEntityRift<*>) {
        this.setRiftData(rift.data().copy())
    }



    override fun sync(holder: Location) {
        check(self().level is ServerLevel) { "Rift synced on client" }
        super.sync(holder)

        try {
            val state = holder.blockState
            self().setChanged()
            holder.world.sendBlockUpdated(holder.blockPos, state, state, 2)
        } catch (e: UnsupportedOperationException) {
            DimensionalDoors.LOGGER.warn("Failed to sync rift block entity: {}", e.message)
        }
    }
}