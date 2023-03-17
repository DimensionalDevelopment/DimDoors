package org.dimdev.dimdoors.client.config;

import me.shedaniel.autoconfig.AutoConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import org.dimdev.dimdoors.ModConfig;

public class ModMenu {
	public static Screen getConfigScreen(Minecraft minecraft, Screen previous) {
		return AutoConfig.getConfigScreen(ModConfig.class, previous).get();
	}
}
