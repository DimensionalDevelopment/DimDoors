package org.dimdev.dimdoors.mixin.accessor;

import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(BiomeGenerationSettings.class)
public interface GenerationSettingsAccessor {
	@Mutable
	@Accessor
	void setFeatures(List<HolderSet<PlacedFeature>> features);
}
