package org.dimdev.dimdoors.rift.item

import net.minecraft.world.item.ItemStack
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.item.ItemRift
import org.dimdev.dimdoors.rift.RiftRegistryVertex
import org.dimdev.dimdoors.rift.item.holder.Holder
import org.dimdev.dimdoors.subsystem.registry.SubSystem
import org.dimdev.dimdoors.subsystem.registry.SubsystemTypes
import java.util.*

object ItemRift : RiftRegistryVertex<Holder, ItemStack, ItemRiftRegistry, ItemRift>() {
    override fun getRift(self: UUID): ItemRift? = SubSystem.getInstance(riftRegistry)?.locationOf(self)?.findStack()?.item?.castOrNull<ItemRift>()

    override val riftRegistry: SubSystem.Type<ItemRiftRegistry> get() = SubsystemTypes.ITEM_RIFT
}
