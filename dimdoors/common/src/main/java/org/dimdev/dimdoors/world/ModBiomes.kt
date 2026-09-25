package org.dimdev.dimdoors.world

import net.minecraft.core.registries.Registries
import org.dimdev.dimdoors.api.util.key

object ModBiomes {
    val PERSONAL_WHITE_VOID_KEY = Registries.BIOME.key("white_void")
    val PUBLIC_BLACK_VOID_KEY = Registries.BIOME.key("black_void")
    val DUNGEON_DANGEROUS_BLACK_VOID_KEY = Registries.BIOME.key("dangerous_black_void")
    val LIMBO_KEY = Registries.BIOME.key("limbo")

    fun register() {
    }
}
