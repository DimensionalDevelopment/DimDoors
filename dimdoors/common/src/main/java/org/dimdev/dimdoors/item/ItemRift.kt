package org.dimdev.dimdoors.item

import net.minecraft.world.item.ItemStack
import org.dimdev.dimdoors.rift.Rift
import org.dimdev.dimdoors.rift.RiftData
import org.dimdev.dimdoors.rift.item.holder.Holder
import org.dimdev.dimdoors.rift.item.ItemRiftRegistry
import org.dimdev.dimdoors.subsystem.graph.RegistryVertex
import org.dimdev.dimdoors.subsystem.graph.RegistryVertices
import org.dimdev.dimdoors.subsystem.registry.SubSystem
import org.dimdev.dimdoors.subsystem.registry.SubsystemTypes

interface ItemRift : Rift<ItemRift, Holder, ItemStack, ItemRiftRegistry> {

    override fun data(holder: Holder): RiftData {
        return riftRegistry.storedAt(holder)?.get(ModDataComponentTypes.RIFT_DATA)?.copy() ?: RiftData()
    }

    override fun setRiftData(holder: Holder, data: RiftData) {
        riftRegistry.storedAt(holder)?.set(ModDataComponentTypes.RIFT_DATA, data.copy())
    }

    override val riftRegistryType: SubSystem.Type<ItemRiftRegistry> get() = SubsystemTypes.ITEM_RIFT
    override val vertexType: RegistryVertex get() = RegistryVertices.ITEM_RIFT

    override var isStateDirty: Boolean
        get() = false
        set(value) {}
    override var isDeleteRift: Boolean
        get() = true
        set(value) {}


}