package org.dimdev.dimdoors.client.config;

import net.minecraft.client.gui.screens.Screen;

public class ConfigScreenProvider {
    public static Screen getConfigScreen(Screen previous) {
        return new DimDoorsConfigScreen(previous);
    }
}
