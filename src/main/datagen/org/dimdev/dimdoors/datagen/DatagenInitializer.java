package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DatagenInitializer implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
//		dataGenerator.addProvider(new FabricBlockStateDefinitionProvider(dataGenerator) {
//
//			@Override
//			public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
//				registerDoor(blockStateModelGenerator, ModBlocks.STONE_DOOR, Blocks.STONE);
//			}
//
//			@Override
//			public void generateItemModels(ItemModelGenerator itemModelGenerator) {
//			}
//
//			public void registerDoor(BlockStateModelGenerator generator, Block doorBlock, Block base) {
//				TextureMap textureMap = TextureMap.all(base);
//				Identifier identifier = Models.DOOR_BOTTOM_LEFT.DOOR_BOTTOM.upload(doorBlock, textureMap, generator.modelCollector);
//				Identifier identifier2 = Models.DOOR_BOTTOM_RH.upload(doorBlock, textureMap, generator.modelCollector);
//				Identifier identifier3 = Models.DOOR_TOP.upload(doorBlock, textureMap, generator.modelCollector);
//				Identifier identifier4 = Models.DOOR_TOP_RH.upload(doorBlock, textureMap, generator.modelCollector);
//				generator.registerItemModel(doorBlock.asItem());
//				generator.blockStateCollector.accept(createDoorBlockState(doorBlock, identifier, identifier2, identifier3, identifier4));
//			}
//
//		});

		dataGenerator.addProvider(new FabricRecipeProvider(dataGenerator));
		dataGenerator.addProvider(new AdvancementProvider(dataGenerator));
		dataGenerator.addProvider(new LootTableProvider(dataGenerator));
		dataGenerator.addProvider(new LimboDecayProvider(dataGenerator));
	}
}
