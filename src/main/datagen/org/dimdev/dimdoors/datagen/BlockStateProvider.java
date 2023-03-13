package org.dimdev.dimdoors.datagen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
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
		createDoor(generator, BuiltInRegistries.BLOCK.get(DimensionalDoors.resource("iron_dimensional_door")), Blocks.IRON_DOOR);
		createDoor(generator, BuiltInRegistries.BLOCK.get(DimensionalDoors.resource("gold_dimensional_door")), ModBlocks.GOLD_DOOR);
		createDoor(generator, BuiltInRegistries.BLOCK.get(DimensionalDoors.resource("quartz_dimensional_door")), ModBlocks.QUARTZ_DOOR);
		createDoor(generator, BuiltInRegistries.BLOCK.get(DimensionalDoors.resource("oak_dimensional_door")), Blocks.OAK_DOOR);
//		createDoor(generator, Registry.BLOCK.get(Util.id("dungeon_door")), ModBlocks.STONE_DOOR);


		generator.woodProvider(ModBlocks.DRIFTWOOD_LOG).log(ModBlocks.DRIFTWOOD_LOG).wood(ModBlocks.DRIFTWOOD_WOOD);
		generator.family(ModBlocks.DRIFTWOOD_PLANKS)
				.fence(ModBlocks.DRIFTWOOD_FENCE)
				.fenceGate(ModBlocks.DRIFTWOOD_GATE)
				.button(ModBlocks.DRIFTWOOD_BUTTON)
				.slab(ModBlocks.DRIFTWOOD_SLAB)
				.stairs(ModBlocks.DRIFTWOOD_STAIRS);
		generator.createDoor(ModBlocks.DRIFTWOOD_DOOR);
		generator.createTrapdoor(ModBlocks.DRIFTWOOD_TRAPDOOR);

		generator.family(ModBlocks.AMALGAM_BLOCK)
				.slab(ModBlocks.AMALGAM_SLAB)
				.stairs(ModBlocks.AMALGAM_STAIRS);
		generator.createDoor(ModBlocks.AMALGAM_DOOR);
		generator.createTrapdoor(ModBlocks.AMALGAM_TRAPDOOR);
		generator.createTrivialCube(ModBlocks.AMALGAM_ORE);
		generator.createTrivialCube(ModBlocks.RUST);

		generator.createTrivialCube(ModBlocks.CLOD_ORE);
		generator.createTrivialCube(ModBlocks.CLOD_BLOCK);

		generator.family(Blocks.GRAVEL)
				.fence(ModBlocks.GRAVEL_FENCE)
				.button(ModBlocks.GRAVEL_BUTTON)
				.fenceGate(ModBlocks.GRAVEL_GATE)
				.slab(ModBlocks.GRAVEL_SLAB)
				.stairs(ModBlocks.GRAVEL_STAIRS);

		generator.family(ModBlocks.DARK_SAND)
				.fence(ModBlocks.DARK_SAND_FENCE)
				.button(ModBlocks.DARK_SAND_BUTTON)
				.fenceGate(ModBlocks.DARK_SAND_GATE)
				.slab(ModBlocks.DARK_SAND_SLAB)
				.stairs(ModBlocks.DARK_SAND_STAIRS);

		generator.family(Blocks.CLAY)
				.fence(ModBlocks.CLAY_FENCE)
				.fenceGate(ModBlocks.CLAY_GATE)
				.button(ModBlocks.CLAY_BUTTON)
				.slab(ModBlocks.CLAY_SLAB)
				.stairs(ModBlocks.CLAY_STAIRS);


	generator.family(ModBlocks.UNRAVELLED_FABRIC)
				.fence(ModBlocks.UNRAVELED_FENCE)
				.fenceGate(ModBlocks.UNRAVELED_GATE)
				.button(ModBlocks.UNRAVELED_BUTTON)
				.slab(ModBlocks.UNRAVELED_SLAB)
				.stairs(ModBlocks.UNRAVELED_STAIRS);

		generator.family(Blocks.MUD)
				.fence(ModBlocks.MUD_FENCE)
				.fenceGate(ModBlocks.MUD_GATE)
				.button(ModBlocks.MUD_BUTTON)
				.slab(ModBlocks.MUD_SLAB)
				.stairs(ModBlocks.MUD_STAIRS);


		generator.createTrivialCube(ModBlocks.GRITTY_STONE);


	}

	public void createDoor(BlockModelGenerators generator, Block doorBlock, Block textureSource) {
		TextureMapping textureMap = TextureMapping.door(textureSource);
		ResourceLocation identifier = ModelTemplates.DOOR_BOTTOM_LEFT.create(doorBlock, textureMap, generator.modelOutput);
		ResourceLocation identifier2 = ModelTemplates.DOOR_BOTTOM_LEFT_OPEN.create(doorBlock, textureMap, generator.modelOutput);
		ResourceLocation identifier3 = ModelTemplates.DOOR_BOTTOM_RIGHT.create(doorBlock, textureMap, generator.modelOutput);
		ResourceLocation identifier4 = ModelTemplates.DOOR_BOTTOM_RIGHT_OPEN.create(doorBlock, textureMap, generator.modelOutput);
		ResourceLocation identifier5 = ModelTemplates.DOOR_TOP_LEFT.create(doorBlock, textureMap, generator.modelOutput);
		ResourceLocation identifier6 = ModelTemplates.DOOR_TOP_LEFT_OPEN.create(doorBlock, textureMap, generator.modelOutput);
		ResourceLocation identifier7 = ModelTemplates.DOOR_TOP_RIGHT.create(doorBlock, textureMap, generator.modelOutput);
		ResourceLocation identifier8 = ModelTemplates.DOOR_TOP_RIGHT_OPEN.create(doorBlock, textureMap, generator.modelOutput);
		generator.createSimpleFlatItemModel(doorBlock.asItem());
		generator.blockStateOutput.accept(BlockModelGenerators.createDoor(doorBlock, identifier, identifier2, identifier3, identifier4, identifier5, identifier6, identifier7, identifier8));
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
