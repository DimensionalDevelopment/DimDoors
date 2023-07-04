package org.dimdev.dimdoors.entity.stat;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import org.dimdev.dimdoors.DimensionalDoors;

import static net.minecraft.stats.Stats.CUSTOM;

public class ModStats {
	public static final DeferredRegister<ResourceLocation> STATS = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.CUSTOM_STAT);

	public static final ResourceLocation DEATHS_IN_POCKETS = register("deaths_in_pocket", StatFormatter.DEFAULT);
	public static final ResourceLocation TIMES_SENT_TO_LIMBO = register("times_sent_to_limbo", StatFormatter.DEFAULT);
	public static final ResourceLocation TIMES_TELEPORTED_BY_MONOLITH = register("times_teleported_by_monolith", StatFormatter.DEFAULT);
	public static final ResourceLocation TIMES_BEEN_TO_DUNGEON = register("times_been_to_dungeon", StatFormatter.DEFAULT);

	private static ResourceLocation register(String string, StatFormatter statFormatter) {
		ResourceLocation resourceLocation = DimensionalDoors.id(string);
		Registry.register(BuiltInRegistries.CUSTOM_STAT, string, resourceLocation);
		CUSTOM.get(resourceLocation, statFormatter);
		return resourceLocation;
	}

	public static void init() {
		STATS.register();
		// just loads the class
	}
}
