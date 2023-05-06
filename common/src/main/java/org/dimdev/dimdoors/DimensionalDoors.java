package org.dimdev.dimdoors;

import dev.architectury.utils.GameInstance;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

public class DimensionalDoors {
	public static final String MOD_ID = "dimdoors";
	public static ResourceLocation id(String id) {
		return new ResourceLocation(MOD_ID, id);
	}

	public static MinecraftServer getServer() {
		return GameInstance.getServer();
	}

	public static Level getWorld(ResourceKey<Level> world) {
		return getServer().getLevel(world);
	}
}
