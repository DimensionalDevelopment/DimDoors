package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureMap;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;

public class BlockStateProvider extends FabricModelProvider {
	public BlockStateProvider(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	public void generateBlockStateModels(BlockStateModelGenerator generator) {
		generator.registerDoor(ModBlocks.GOLD_DOOR);
		generator.registerDoor(ModBlocks.STONE_DOOR);
		generator.registerDoor(ModBlocks.QUARTZ_DOOR);
		registerDoor(generator, Registry.BLOCK.get(DimensionalDoors.id("iron_dimensional_door")), Blocks.IRON_DOOR);
		registerDoor(generator, Registry.BLOCK.get(DimensionalDoors.id("gold_dimensional_door")), ModBlocks.GOLD_DOOR);
		registerDoor(generator, Registry.BLOCK.get(DimensionalDoors.id("quartz_dimensional_door")), ModBlocks.QUARTZ_DOOR);
		registerDoor(generator, Registry.BLOCK.get(DimensionalDoors.id("oak_dimensional_door")), Blocks.OAK_DOOR);
//		registerDoor(generator, Registry.BLOCK.get(Util.id("dungeon_door")), ModBlocks.STONE_DOOR);
	}

	public void registerDoor(BlockStateModelGenerator generator, Block doorBlock, Block textureSource) {
		TextureMap textureMap = TextureMap.topBottom(textureSource);
		Identifier identifier = Models.DOOR_BOTTOM_LEFT.upload(doorBlock, textureMap, generator.modelCollector);
		Identifier identifier2 = Models.DOOR_BOTTOM_LEFT_OPEN.upload(doorBlock, textureMap, generator.modelCollector);
		Identifier identifier3 = Models.DOOR_BOTTOM_RIGHT.upload(doorBlock, textureMap, generator.modelCollector);
		Identifier identifier4 = Models.DOOR_BOTTOM_RIGHT_OPEN.upload(doorBlock, textureMap, generator.modelCollector);
		Identifier identifier5 = Models.DOOR_TOP_LEFT.upload(doorBlock, textureMap, generator.modelCollector);
		Identifier identifier6 = Models.DOOR_TOP_LEFT_OPEN.upload(doorBlock, textureMap, generator.modelCollector);
		Identifier identifier7 = Models.DOOR_TOP_RIGHT.upload(doorBlock, textureMap, generator.modelCollector);
		Identifier identifier8 = Models.DOOR_TOP_RIGHT_OPEN.upload(doorBlock, textureMap, generator.modelCollector);
		generator.registerItemModel(doorBlock.asItem());
		generator.blockStateCollector.accept(BlockStateModelGenerator.createDoorBlockState(doorBlock, identifier, identifier2, identifier3, identifier4, identifier5, identifier6, identifier7, identifier8));
	}

	@Override
	public void generateItemModels(ItemModelGenerator itemModelGenerator) {

	}
}
