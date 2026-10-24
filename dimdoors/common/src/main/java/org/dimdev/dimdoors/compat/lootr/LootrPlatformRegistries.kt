package org.dimdev.dimdoors.compat.lootr

import noobanidus.mods.lootr.common.api.LootrConstants
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors

object Blocks : PlatformRegistry.BlockItemPlatformRegistry(DimensionalDoors.getSided()) {
    val DRIFTWOOD_LOOTR_CHEST = create("driftwood_lootr_chest") {
        block(::LootrDriftwoodChestBlock)
        blockProperties = { LootrConstants.CHEST_PROPERTIES }
    }

    fun register() {}
}

object BlockEntities : PlatformRegistry.BlockEntityTypePlatformRegistry(DimensionalDoors.getSided()) {
    val DRIFTWOOD_LOOTR_CHEST = create("driftwood_lootr_chest", ::DriftwoodLootrChestBlockEntity) { Blocks.DRIFTWOOD_LOOTR_CHEST }
}