package org.dimdev.dimdoors.rift.item.holder

import org.dimdev.dimcore.api.PlatformRegistry.MapCodecPlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided
import org.dimdev.dimdoors.ModRegistryKeys

object ItemHolders : MapCodecPlatformRegistry<Holder>(ModRegistryKeys.ITEM_HOLDER, getSided()) {
    val ENTITY = create("entity") { Holder.EntityHolder.CODEC }
    val BLOCK = create("block") { Holder.BlockHolder.CODEC }
    val NESTED = create("nested") { Holder.NestedHolder.CODEC }
    val ENDER_CHEST = create("ender_chest") { Holder.EnderChestHolder.CODEC }
    val ENTITY_INVENTORY = create("entity_inventory") { Holder.EntityInventoryHolder.CODEC }
}
