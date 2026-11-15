package org.dimdev.dimdoors.rift.item

import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.item.ItemRift
import org.dimdev.dimdoors.rift.RiftRegistryVertex
import org.dimdev.dimdoors.rift.registry.SubSystem
import org.dimdev.dimdoors.rift.registry.SubsystemTypes
import java.util.*

object ItemRift : RiftRegistryVertex<Holder, ItemRiftRegistry, ItemRift>() {
    override fun getRift(self: UUID): ItemRift? = SubSystem.Companion.getInstance(riftRegistry)?.locationOf(self)?.findStack(self)?.item?.castOrNull<ItemRift>()

    override val riftRegistry: SubSystem.Type<ItemRiftRegistry> get() = SubsystemTypes.ITEM_RIFT
}
