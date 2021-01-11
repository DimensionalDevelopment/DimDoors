package org.dimdev.dimdoors.mixin.accessor;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltinBiomes;

@Mixin(BuiltinBiomes.class)
public interface BuiltinBiomesAccessor {
	@Accessor("BY_RAW_ID")
	static Int2ObjectMap<RegistryKey<Biome>> getIdMap() {
		throw new AssertionError();
	}
}
