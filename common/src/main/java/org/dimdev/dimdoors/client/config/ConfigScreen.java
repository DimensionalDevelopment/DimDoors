package org.dimdev.dimdoors.client.config;

import net.minecraft.client.gui.screens.Screen;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.compat.clothconfig.ClothConfigCompat;

public class ConfigScreen {
    public static Screen createScreen(Screen previous) {
        if(DimensionalDoors.getSided().isModLoaded("cloth_config")) return ClothConfigCompat.createScreen(previous);
        else return null;
    }
}
