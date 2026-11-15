package org.dimdev.dimdoors.rift

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimcore.api.errata.schema.FieldNode
import org.dimdev.dimcore.api.errata.subject.ConfigSubject
import org.dimdev.dimcore.api.ext.cast
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.api.rift.target.Target
import org.dimdev.dimdoors.block.entity.Deserialize
import org.dimdev.dimdoors.block.entity.Serialize
import org.dimdev.dimdoors.rift.registry.LinkProperties
import org.dimdev.dimdoors.rift.registry.LinkPropertiesRegistry
import org.dimdev.dimdoors.rift.targets.LocationProvider
import org.dimdev.dimdoors.rift.targets.MessageTarget
import org.dimdev.dimdoors.rift.targets.VirtualTarget
import org.dimdev.dimdoors.rift.registry.RegistryVertex
import org.dimdev.dimdoors.rift.registry.Vertex
import org.dimdev.dimdoors.rift.registry.SubSystem
import org.dimdev.dimdoors.api.util.RGBA
import java.util.*
import java.util.function.Consumer
import org.dimdev.dimdoors.block.entity.RiftData

interface Rift<R: Rift<R, H, V>, H: LocationProvider, V: RiftRegistry<H, V>> : Target {
    fun data(holder: H): RiftData
    fun setRiftData(holder: H, data: RiftData)

    val name: String

    val riftRegistryType: SubSystem.Type<V>
    val vertexType: RegistryVertex

    val riftRegistry: V get() = SubSystem.getInstance(riftRegistryType)!!

    fun gatherFieldNodes(list: MutableList<FieldNode>) {}

    fun <T> writeUniqueData(serialize: Serialize<T, out R>) {}
    fun <T> readUniqueData(deserialize: Deserialize<T>) {}

    fun configSubject(holder: H, hand: InteractionHand, item: Item): ConfigSubject = RiftConfigSubject<R, H>(this.cast(), holder, hand, item)

    fun setDestination(holder: H, destination: VirtualTarget<*>) {

        if (DimensionalDoors.LOGGER.isDebugEnabled) {
            DimensionalDoors.LOGGER.debug(
                "Setting destination {} for {}",
                destination,
                holder
            )
        }

        val data = this.data(holder)

        val owner = vertex(holder)

        if (data.destination != VirtualTarget.NoneTarget && this.isRegistered(holder)) {
            data.destination.unregister(owner)
        }

        data.destination = destination
        setRiftData(holder, data)
        if (destination !== VirtualTarget.NoneTarget) {
            if (this.isRegistered(holder)) destination.register(owner)
        }
        this.sync(holder)
        this.updateColor(holder)
        this.isStateDirty = true
    }

    var isStateDirty: Boolean

    fun id(holder: H): UUID? = riftRegistry.idAt(holder)

    fun properties(holder: H): LinkProperties? = id(holder)?.let { LinkPropertiesRegistry.instance.getProperties(it) }

    fun setProperties(holder: H, properties: LinkProperties?) {
        LinkPropertiesRegistry.instance.setProperties(vertex(holder).id, properties)
        sync(holder)
    }

    fun markStateChanged(holder: H) {
        this.isStateDirty = true
        this.sync(holder)
    }

    fun isAlwaysDelete(holder: H): Boolean = data(holder).alwaysDelete

    fun isColorOverridden(holder: H): Boolean = data(holder).colorOverride != null

    fun color(holder: H): RGBA {
        return data(holder).color
    }

    fun setColor(holder: H, color: RGBA) {
        setRiftData(holder, data(holder).also { it.color = color })
        this.sync(holder)
    }

    //Color used when showing text related to a rift with the Tailor's EyePiece
    val textColor: Int get() = 0x20FFFFFF

    fun target(holder: H): Target {
        val data = this.data(holder)

        return if (data.destination === VirtualTarget.NoneTarget) {
            MessageTarget("rifts.unlinked1")
        } else {
            data.destination
        }
    }

    fun isRegistered(holder: H): Boolean = riftRegistry.isRiftAt(holder)

    fun register(holder: H) {
        if (this.isRegistered(holder)) {
            return
        }

        val owner = vertex(holder)

        val data = this.data(holder)

        riftRegistry.addRift(holder)

        data.destination.takeIf { it !== VirtualTarget.NoneTarget }?.register(owner)

        this.updateColor(holder)
    }

    fun unregister(holder: H) {
        if (this.isDeleteRift && this.isRegistered(holder)) {
            riftRegistry.removeRift(holder)
        }
    }

    fun updateType(holder: H) {
        if (!this.isRegistered(holder)) return

        LinkPropertiesRegistry.instance.setDetached(vertex(holder).id, this.isDetached)
    }

    fun handleTargetGone(holder: H) {
        val data = this.data(holder)

        if (data.destination.shouldInvalidate()) {
            data.destination = VirtualTarget.NoneTarget
            setRiftData(holder, data)
            sync(holder)
        }

        this.updateColor(holder)
    }


    fun handleSourceGone(holder: H) {
        this.updateColor(holder)
    }

    fun updateColor(holder: H) {
        val data = this.data(holder)

        data.colorOverride?.let {
            if (data.color != it) this.setColor(holder, it)
            return
        }
        if (!this.isRegistered(holder)) {
            this.setColor(holder, RGBA(0f, 0f, 0f, 1f))
        } else if (data.destination === VirtualTarget.NoneTarget) {
            data.color = RGBA(0.7f, 0.7f, 0.7f, 1f)
            setRiftData(holder, data)
        } else {
            val newColor = data.destination.getColor(vertex(holder))
            if (data.color != newColor) {
                data.color = newColor
                setRiftData(holder, data)
                this.sync(holder)
            }
        }
    }

    fun <S : LocationProvider> copyFrom(holder: H, rift: Rift<*, S, *>, other: S) = setRiftData(holder, rift.data(other).copy())

    val isDetached: Boolean
        get() = false

    fun tick(level: Level, pos: BlockPos, blockState: BlockState) {
        if (level.isClientSide) return

        update(level, pos, blockState)
    }

    fun update(level: Level, pos: BlockPos, blockState: BlockState) {
    }

    var isDeleteRift: Boolean

    fun sync(holder: H) {}

    fun gatherDebug(holder: H, textConsumer: Consumer<Component>) {
        textConsumer.accept(Component.literal("Size: ${this.data(holder).size}"))
    }

    fun vertex(holder: H): Vertex = Vertex(riftRegistry.getRiftOrPlaceholder(holder), vertexType)
}