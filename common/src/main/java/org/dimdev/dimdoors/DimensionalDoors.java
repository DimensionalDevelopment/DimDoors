package org.dimdev.dimdoors;

import dev.architectury.utils.GameInstance;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.api.DimensionalDoorsApi;

import java.util.List;

public class DimensionalDoors {
	public static final String MOD_ID = "dimdoors";
	public static List<DimensionalDoorsApi> apiSubscribers;

    public static ResourceLocation id(String id) {
		return new ResourceLocation(MOD_ID, id);
	}

	public static MinecraftServer getServer() {
		return GameInstance.getServer();
	}

	public static ServerLevel getWorld(ResourceKey<Level> world) {
		return getServer().getLevel(world);
	}
}
