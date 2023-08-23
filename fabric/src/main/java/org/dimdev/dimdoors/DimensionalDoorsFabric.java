package org.dimdev.dimdoors;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.GenerationStep;
import org.dimdev.dimdoors.api.util.RegisterRecipeBookCategoriesEvent;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.client.ModRecipeBookGroups;
import org.dimdev.dimdoors.client.ModRecipeBookTypes;
import org.dimdev.dimdoors.recipe.ModRecipeTypes;

import java.util.List;

import static org.dimdev.dimdoors.block.door.WaterLoggableDoorBlock.WATERLOGGED;
import static org.dimdev.dimdoors.world.feature.ModFeatures.Placed.*;

public class DimensionalDoorsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        DimensionalDoors.init();

		ModRecipeBookGroups.init();

		RegisterRecipeBookCategoriesEvent.EVENT.register(event -> {
			event.registerAggregateCategory(ModRecipeBookGroups.TESSELATING_SEARCH.get(), List.of(ModRecipeBookGroups.TESSELATING_GENERAL.get()));
			event.registerBookCategories(ModRecipeBookTypes.TESSELLATING, List.of(ModRecipeBookGroups.TESSELATING_GENERAL.get()));
			event.registerRecipeCategoryFinder(ModRecipeTypes.TESSELATING.get(), recipe -> ModRecipeBookGroups.TESSELATING_GENERAL.get());
		});

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

		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			if (player.isCreative() && !DimensionalDoors.getConfig().getDoorsConfig().placeRiftsInCreativeMode) {
				return;
			}
			if (blockEntity instanceof EntranceRiftBlockEntity && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
				world.setBlockAndUpdate(pos, ModBlocks.DETACHED_RIFT.get().defaultBlockState().setValue(WATERLOGGED, state.getValue(WATERLOGGED)));
				((DetachedRiftBlockEntity) world.getBlockEntity(pos)).setData(((EntranceRiftBlockEntity) blockEntity).getData());
			}
		});
    }
}
