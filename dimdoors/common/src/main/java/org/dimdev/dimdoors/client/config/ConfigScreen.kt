package org.dimdev.dimdoors.client.config

import net.minecraft.client.gui.screens.Screen
import org.dimdev.dimcore.DimCore.platform
import org.dimdev.dimdoors.compat.clothconfig.ClothConfigCompat

object ConfigScreen {
    fun createScreen(previous: Screen?) = if (platform.isModLoaded("cloth_config")) ClothConfigCompat.createScreen(previous) else null
}
