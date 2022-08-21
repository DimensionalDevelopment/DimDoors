package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockStateDefinitionProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureMap;
import net.minecraft.util.Identifier;

import static net.minecraft.data.client.BlockStateModelGenerator.createDoorBlockState;

public class DatagenInitializer implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
		dataGenerator.addProvider(new FabricBlockStateDefinitionProvider(dataGenerator) {

			@Override
			public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
//				registerDoor(blockStateModelGenerator, ModBlocks.STONE_DOOR, Blocks.STONE);
			}

			@Override
			public void generateItemModels(ItemModelGenerator itemModelGenerator) {
			}

			public void registerDoor(BlockStateModelGenerator generator, Block doorBlock, Block base) {
				TextureMap textureMap = TextureMap.all(base);
				Identifier identifier = Models.DOOR_BOTTOM.upload(doorBlock, textureMap, generator.modelCollector);
				Identifier identifier2 = Models.DOOR_BOTTOM_RH.upload(doorBlock, textureMap, generator.modelCollector);
				Identifier identifier3 = Models.DOOR_TOP.upload(doorBlock, textureMap, generator.modelCollector);
				Identifier identifier4 = Models.DOOR_TOP_RH.upload(doorBlock, textureMap, generator.modelCollector);
				generator.registerItemModel(doorBlock.asItem());
				generator.blockStateCollector.accept(createDoorBlockState(doorBlock, identifier, identifier2, identifier3, identifier4));
			}

		});

		dataGenerator.addProvider(new FabricRecipeProvider(dataGenerator));
		dataGenerator.addProvider(new AdvancementProvider(dataGenerator));
		dataGenerator.addProvider(new LootTableProvider(dataGenerator));
		dataGenerator.addProvider(new LimboDecayProvider(dataGenerator));
		dataGenerator.addProvider(ThemeProvider::new);
	}
}
