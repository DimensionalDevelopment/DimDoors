package org.dimdev.dimdoors.entity

import net.minecraft.world.entity.MobCategory
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors

object ModEntityTypes : PlatformRegistry.EntityTypePlatformRegistry(DimensionalDoors.getSided()) {
    @JvmField
    val MONOLITH = create("monolith", ::MonolithEntity, MobCategory.MONSTER) {
        sized(2f, 2.7f)
        fireImmune()
    }

    val MASK = create("mask", ::MaskEntity, MobCategory.MONSTER) {
        sized(0.9375f, 0.9375f)
        fireImmune()
    }

    @JvmField
    var FARSHOT_ENDER_PEARL = create("farshot_ender_pearl", ::FarShotEnderPearlEntity, MobCategory.MISC) {
        sized(0.25f, 0.25f)
        clientTrackingRange(8)
        updateInterval(1)
    }
}
