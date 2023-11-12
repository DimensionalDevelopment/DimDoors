package org.dimdev.dimdoors;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;

import static org.dimdev.dimdoors.world.feature.ModFeatures.Placed.*;

public class DimensionalDoorsFabric implements ModInitializer {

	@Override
    public void onInitialize() {
        DimensionalDoors.init();

		BiomeModifications.addFeature(ctx -> ctx.hasTag(ConventionalBiomeTags.IN_OVERWORLD) &&
						!ctx.hasTag(ConventionalBiomeTags.DESERT) &&
						!ctx.hasTag(ConventionalBiomeTags.OCEAN),
				GenerationStep.Decoration.SURFACE_STRUCTURES,
				TWO_PILLARS
		);
		BiomeModifications.addFeature(
				ctx -> ctx.hasTag(ConventionalBiomeTags.DESERT),
				GenerationStep.Decoration.SURFACE_STRUCTURES,
				SANDSTONE_PILLARS
		);

		BiomeModifications.addFeature(
				ctx -> !ctx.getBiomeKey().equals(Biomes.THE_END) && ctx.hasTag(ConventionalBiomeTags.IN_THE_END),
				GenerationStep.Decoration.SURFACE_STRUCTURES,
				END_GATEWAY
		);

		PlayerBlockBreakEvents.AFTER.register(DimensionalDoors::afterBlockBreak);
    }
}
