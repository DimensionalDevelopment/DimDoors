package org.dimdev.dimdoors.mixin.accessor;

import java.util.List;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.gen.feature.ConfiguredFeature;

@Mixin(GenerationSettings.class)
public interface GenerationSettingsAccessor {
	@Mutable
	@Accessor
	void setFeatures(List<List<Supplier<ConfiguredFeature<?, ?>>>> features);
}
