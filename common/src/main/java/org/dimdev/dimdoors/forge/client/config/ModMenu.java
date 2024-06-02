package org.dimdev.dimdoors.forge.client.config;

import me.shedaniel.autoconfig.AutoConfig;

import net.minecraft.client.gui.screens.Screen;
import org.dimdev.dimdoors.ModConfig;

public class ModMenu {
	public static Screen getConfigScreen(Screen previous) {
		return AutoConfig.getConfigScreen(ModConfig.class, previous).get();
	}
}
