package org.dimdev.dimdoors.entity.stat;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModStats {
	public static final DeferredRegister<ResourceLocation> STATS = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.CUSTOM_STAT);

	public static final RegistrySupplier<ResourceLocation> DEATHS_IN_POCKETS = register("deaths_in_pocket", StatFormatter.DEFAULT);
	public static final RegistrySupplier<ResourceLocation> TIMES_SENT_TO_LIMBO = register("times_sent_to_limbo", StatFormatter.DEFAULT);
	public static final RegistrySupplier<ResourceLocation> TIMES_TELEPORTED_BY_MONOLITH = register("times_teleported_by_monolith", StatFormatter.DEFAULT);
	public static final RegistrySupplier<ResourceLocation> TIMES_BEEN_TO_DUNGEON = register("times_been_to_dungeon", StatFormatter.DEFAULT);

	private static RegistrySupplier<ResourceLocation> register(String string, StatFormatter statFormatter) {
		ResourceLocation resourceLocation = DimensionalDoors.id(string);
		return STATS.register(string, () -> {
			Stats.CUSTOM.get(resourceLocation, statFormatter);
			return resourceLocation;
		});
	}

	public static void init() {
		// just loads the class
	}
}
