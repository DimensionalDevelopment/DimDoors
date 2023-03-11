package org.dimdev.dimdoors.datagen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;

import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;

public class BlockStateProvider extends FabricModelProvider {
	public BlockStateProvider(FabricDataOutput dataGenerator) {
		super(dataGenerator);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators generator) {
		generator.createDoor(ModBlocks.GOLD_DOOR);
		generator.createDoor(ModBlocks.STONE_DOOR);
		generator.createDoor(ModBlocks.QUARTZ_DOOR);
		createDoor(generator, BuiltInRegistries.BLOCK.get(DimensionalDoors.id("iron_dimensional_door")), Blocks.IRON_DOOR);
		createDoor(generator, BuiltInRegistries.BLOCK.get(DimensionalDoors.id("gold_dimensional_door")), ModBlocks.GOLD_DOOR);
		createDoor(generator, BuiltInRegistries.BLOCK.get(DimensionalDoors.id("quartz_dimensional_door")), ModBlocks.QUARTZ_DOOR);
		createDoor(generator, BuiltInRegistries.BLOCK.get(DimensionalDoors.id("oak_dimensional_door")), Blocks.OAK_DOOR);
//		createDoor(generator, Registry.BLOCK.get(Util.id("dungeon_door")), ModBlocks.STONE_DOOR);


		generator.woodProvider(ModBlocks.DRIFTWOOD_LOG).log(ModBlocks.DRIFTWOOD_LOG).wood(ModBlocks.DRIFTWOOD_WOOD);
		generator.createTrivialCube(ModBlocks.DRIFTWOOD_PLANKS)
				.fence(ModBlocks.DRIFTWOOD_FENCE)
				.fenceGate(ModBlocks.DRIFTWOOD_GATE)
				.button(ModBlocks.DRIFTWOOD_BUTTON)
				.slab(ModBlocks.DRIFTWOOD_SLAB)
				.stairs(ModBlocks.DRIFTWOOD_STAIRS);
		generator.createDoor(ModBlocks.DRIFTWOOD_DOOR);
		generator.registerTrapdoor(ModBlocks.DRIFTWOOD_TRAPDOOR);

		generator.registerCubeAllModelTexturePool(ModBlocks.AMALGAM_BLOCK)
				.slab(ModBlocks.AMALGAM_SLAB)
				.stairs(ModBlocks.AMALGAM_STAIRS);
		generator.createDoor(ModBlocks.AMALGAM_DOOR);
		generator.registerTrapdoor(ModBlocks.AMALGAM_TRAPDOOR);
		generator.registerSimpleCubeAll(ModBlocks.AMALGAM_ORE);
		generator.registerSimpleCubeAll(ModBlocks.RUST);

		generator.registerSimpleCubeAll(ModBlocks.CLOD_ORE);
		generator.registerSimpleCubeAll(ModBlocks.CLOD_BLOCK);

		generator.registerCubeAllModelTexturePool(Blocks.GRAVEL)
				.fence(ModBlocks.GRAVEL_FENCE)
				.button(ModBlocks.GRAVEL_BUTTON)
				.fenceGate(ModBlocks.GRAVEL_GATE)
				.slab(ModBlocks.GRAVEL_SLAB)
				.stairs(ModBlocks.GRAVEL_STAIRS);

		generator.registerCubeAllModelTexturePool(ModBlocks.DARK_SAND)
				.fence(ModBlocks.DARK_SAND_FENCE)
				.button(ModBlocks.DARK_SAND_BUTTON)
				.fenceGate(ModBlocks.DARK_SAND_GATE)
				.slab(ModBlocks.DARK_SAND_SLAB)
				.stairs(ModBlocks.DARK_SAND_STAIRS);

		generator.registerCubeAllModelTexturePool(Blocks.CLAY)
				.fence(ModBlocks.CLAY_FENCE)
				.fenceGate(ModBlocks.CLAY_GATE)
				.button(ModBlocks.CLAY_BUTTON)
				.slab(ModBlocks.CLAY_SLAB)
				.stairs(ModBlocks.CLAY_STAIRS);


	generator.registerCubeAllModelTexturePool(ModBlocks.UNRAVELLED_FABRIC)
				.fence(ModBlocks.UNRAVELED_FENCE)
				.fenceGate(ModBlocks.UNRAVELED_GATE)
				.button(ModBlocks.UNRAVELED_BUTTON)
				.slab(ModBlocks.UNRAVELED_SLAB)
				.stairs(ModBlocks.UNRAVELED_STAIRS);

		generator.registerCubeAllModelTexturePool(Blocks.MUD)
				.fence(ModBlocks.MUD_FENCE)
				.fenceGate(ModBlocks.MUD_GATE)
				.button(ModBlocks.MUD_BUTTON)
				.slab(ModBlocks.MUD_SLAB)
				.stairs(ModBlocks.MUD_STAIRS);


		generator.registerSimpleCubeAll(ModBlocks.GRITTY_STONE);


	}

	public void createDoor(BlockModelGenerators generator, Block doorBlock, Block textureSource) {
		TextureMap textureMap = TextureMap.topBottom(textureSource);
		ResourceLocation identifier = Models.DOOR_BOTTOM_LEFT.upload(doorBlock, textureMap, generator.modelCollector);
		ResourceLocation identifier2 = Models.DOOR_BOTTOM_LEFT_OPEN.upload(doorBlock, textureMap, generator.modelCollector);
		ResourceLocation identifier3 = Models.DOOR_BOTTOM_RIGHT.upload(doorBlock, textureMap, generator.modelCollector);
		ResourceLocation identifier4 = Models.DOOR_BOTTOM_RIGHT_OPEN.upload(doorBlock, textureMap, generator.modelCollector);
		ResourceLocation identifier5 = Models.DOOR_TOP_LEFT.upload(doorBlock, textureMap, generator.modelCollector);
		ResourceLocation identifier6 = Models.DOOR_TOP_LEFT_OPEN.upload(doorBlock, textureMap, generator.modelCollector);
		ResourceLocation identifier7 = Models.DOOR_TOP_RIGHT.upload(doorBlock, textureMap, generator.modelCollector);
		ResourceLocation identifier8 = Models.DOOR_TOP_RIGHT_OPEN.upload(doorBlock, textureMap, generator.modelCollector);
		generator.registerItemModel(doorBlock.asItem());
		generator.blockStateCollector.accept(BlockStateModelGenerator.createDoorBlockState(doorBlock, identifier, identifier2, identifier3, identifier4, identifier5, identifier6, identifier7, identifier8));
	}

	@Override
	public void generateItemModels(ItemModelGenerators itemModelGenerator) {
		itemModelGenerator.generateFlatItem(ModItems.FUZZY_FIREBALL, ModelTemplates.FLAT_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.FABRIC_OF_FINALITY, ModelTemplates.FLAT_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.GARMENT_OF_REALITY, ModelTemplates.FLAT_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.LIMINAL_LINT, ModelTemplates.FLAT_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.ENDURING_FIBERS, ModelTemplates.FLAT_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.RIFT_PEARL, ModelTemplates.FLAT_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.AMALGAM_LUMP, ModelTemplates.FLAT_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.CLOD, ModelTemplates.FLAT_ITEM);

	}
}
