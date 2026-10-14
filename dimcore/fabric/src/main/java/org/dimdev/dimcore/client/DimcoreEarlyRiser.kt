package org.dimdev.dimcore.client

import net.fabricmc.loader.api.FabricLoader
import org.dimdev.dimcore.EnumExtender

class DimcoreEarlyRiser : Runnable {
    override fun run() {
        val remapper = FabricLoader.getInstance().mappingResolver
        val mc = { name: String -> remapper.mapClassName("intermediary", "net.minecraft.$name") }
        FabricLoader.getInstance()
            .getEntrypoints("dimcore:enum_extensions", EnumExtender::class.java)
            .forEach { it.extend(mc) }
    }
}
