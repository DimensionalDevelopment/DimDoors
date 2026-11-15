package org.dimdev.dimdoors.item

import net.minecraft.world.item.ItemStack
import org.dimdev.dimdoors.rift.Rift
import org.dimdev.dimdoors.block.entity.RiftData
import org.dimdev.dimdoors.rift.item.Holder
import org.dimdev.dimdoors.rift.item.ItemRiftRegistry
import org.dimdev.dimdoors.rift.registry.RegistryVertex
import org.dimdev.dimdoors.rift.registry.RegistryVertices
import org.dimdev.dimdoors.rift.registry.SubSystem
import org.dimdev.dimdoors.rift.registry.SubsystemTypes

interface ItemRift : Rift<ItemRift, Holder, ItemRiftRegistry> {
    private fun stack(holder: Holder): ItemStack? = riftRegistry.idAt(holder)?.let { holder.findStack(it) }

    override fun data(holder: Holder): RiftData {
        return stack(holder)?.get(ModDataComponentTypes.RIFT_DATA)?.copy() ?: RiftData()
    }

    override fun setRiftData(holder: Holder, data: RiftData) {
        stack(holder)?.set(ModDataComponentTypes.RIFT_DATA, data.copy())
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