package org.dimdev.dimdoors.client.config

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.AlertScreen
import net.minecraft.client.gui.screens.Screen
import org.dimdev.dimcore.DimCore.platform
import org.dimdev.dimdoors.compat.clothconfig.ClothConfigCompat
import org.dimdev.dimdoors.util.ext.translate

object ConfigScreen {
    fun createScreen(previous: Screen?): Screen? =
        if (platform.isModLoaded("cloth_config") || platform.isModLoaded("cloth-config")) ClothConfigCompat.createScreen(previous)
        else AlertScreen({ Minecraft.getInstance().setScreen(previous) }, "config.dimdoors.title".translate(), "config.dimdoors.requires_cloth".translate())
}
