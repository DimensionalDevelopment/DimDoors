package org.dimdev.dimdoors

import net.minecraft.world.level.GameRules
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided

object ModGameRules {
    val RIFT_SIGNATURE_WORKS_IN_PRIVATE_POCKETS: GameRules.Key<GameRules.BooleanValue> = getSided().registerGameRule("pocketsRiftSignaturesWorkInPrivatePockets", GameRules.Category.MISC, true)

    fun register() {}
}
