package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;
import org.dimdev.dimdoors.item.ModItems;

import static net.minecraft.data.models.model.TextureMapping.getBlockTexture;
import static net.minecraft.data.models.model.TextureMapping.getItemTexture;

public class BlockStateProvider extends FabricModelProvider {
	public BlockStateProvider(FabricDataOutput dataGenerator) {
		super(dataGenerator);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators generator) {
		generator.createDoor(ModBlocks.GOLD_DOOR.get());
		generator.createDoor(ModBlocks.STONE_DOOR.get());
		generator.createDoor(ModBlocks.QUARTZ_DOOR.get());

		BuiltInRegistries.BLOCK.stream().filter(a -> a instanceof DimensionalDoorBlockRegistrar.AutoGenDimensionalDoorBlock).map(a -> (DimensionalDoorBlockRegistrar.AutoGenDimensionalDoorBlock) a).forEach(a -> registerAutoGenDoor(generator, a));

//		registerAutoGenDoor(generator, BuiltInRegistries.BLOCK.get(DimensionalDoors.id("block_ag_dim_minecraft_iron_door")), Blocks.IRON_DOOR);
//		registerAutoGenDoor(generator, BuiltInRegistries.BLOCK.get(DimensionalDoors.id("block_ag_dim_dimdoors_gold_door")), ModBlocks.GOLD_DOOR.get());
//		registerAutoGenDoor(generator, BuiltInRegistries.BLOCK.get(DimensionalDoors.id("block_ag_dim_dimdoors_quartz_door")), ModBlocks.QUARTZ_DOOR.get());
//		registerAutoGenDoor(generator, BuiltInRegistries.BLOCK.get(DimensionalDoors.id("block_ag_dim_minecraft_oak_door")), Blocks.OAK_DOOR);
//		registerAutoGenDoor(generator, BuiltInRegistries.BLOCK.get(DimensionalDoors.id("block_ag_dim_dimdoors_stone_door")), ModBlocks.STONE_DOOR.get());


		generator.woodProvider(ModBlocks.DRIFTWOOD_LOG.get()).log(ModBlocks.DRIFTWOOD_LOG.get()).wood(ModBlocks.DRIFTWOOD_WOOD.get());
		generator.family(ModBlocks.DRIFTWOOD_PLANKS.get())
				.fence(ModBlocks.DRIFTWOOD_FENCE.get())
				.fenceGate(ModBlocks.DRIFTWOOD_GATE.get())
				.button(ModBlocks.DRIFTWOOD_BUTTON.get())
				.slab(ModBlocks.DRIFTWOOD_SLAB.get())
				.stairs(ModBlocks.DRIFTWOOD_STAIRS.get());
		generator.createDoor(ModBlocks.DRIFTWOOD_DOOR.get());
		generator.createTrapdoor(ModBlocks.DRIFTWOOD_TRAPDOOR.get());

		generator.family(ModBlocks.AMALGAM_BLOCK.get())
				.slab(ModBlocks.AMALGAM_SLAB.get())
				.stairs(ModBlocks.AMALGAM_STAIRS.get());
		generator.createDoor(ModBlocks.AMALGAM_DOOR.get());
		generator.createTrapdoor(ModBlocks.AMALGAM_TRAPDOOR.get());
		generator.createTrivialCube(ModBlocks.AMALGAM_ORE.get());
		generator.createTrivialCube(ModBlocks.RUST.get());

		generator.createTrivialCube(ModBlocks.CLOD_ORE.get());
		generator.createTrivialCube(ModBlocks.CLOD_BLOCK.get());

		generator.family(Blocks.GRAVEL)
				.fence(ModBlocks.GRAVEL_FENCE.get())
				.button(ModBlocks.GRAVEL_BUTTON.get())
				.slab(ModBlocks.GRAVEL_SLAB.get())
				.stairs(ModBlocks.GRAVEL_STAIRS.get())
				.wall(ModBlocks.GRAVEL_WALL.get());

		generator.family(ModBlocks.DARK_SAND.get())
				.fence(ModBlocks.DARK_SAND_FENCE.get())
				.button(ModBlocks.DARK_SAND_BUTTON.get())
				.slab(ModBlocks.DARK_SAND_SLAB.get())
				.stairs(ModBlocks.DARK_SAND_STAIRS.get())
				.wall(ModBlocks.DARK_SAND_WALL.get());

		generator.family(Blocks.CLAY)
				.fence(ModBlocks.CLAY_FENCE.get())
				.fenceGate(ModBlocks.CLAY_GATE.get())
				.button(ModBlocks.CLAY_BUTTON.get())
				.slab(ModBlocks.CLAY_SLAB.get())
				.stairs(ModBlocks.CLAY_STAIRS.get());


	generator.family(ModBlocks.UNRAVELLED_FABRIC.get())
				.fence(ModBlocks.UNRAVELED_FENCE.get())
				.fenceGate(ModBlocks.UNRAVELED_GATE.get())
				.button(ModBlocks.UNRAVELED_BUTTON.get())
				.slab(ModBlocks.UNRAVELED_SLAB.get())
				.stairs(ModBlocks.UNRAVELED_STAIRS.get());

		generator.family(Blocks.MUD)
				.fence(ModBlocks.MUD_FENCE.get())
				.fenceGate(ModBlocks.MUD_GATE.get())
				.button(ModBlocks.MUD_BUTTON.get())
				.slab(ModBlocks.MUD_SLAB.get())
				.stairs(ModBlocks.MUD_STAIRS.get());

		generator.family(Blocks.DEEPSLATE)
				.slab(ModBlocks.DEEPSLATE_SLAB.get())
				.stairs(ModBlocks.DEEPSLATE_STAIRS.get())
				.wall(ModBlocks.DEEPSLATE_WALL.get());

		generator.family(Blocks.RED_SAND)
				.slab(ModBlocks.RED_SAND_SLAB.get())
				.stairs(ModBlocks.RED_SAND_STAIRS.get())
				.wall(ModBlocks.RED_SAND_WALL.get());

		generator.family(Blocks.SAND)
				.slab(ModBlocks.SAND_SLAB.get())
				.stairs(ModBlocks.SAND_STAIRS.get())
				.wall(ModBlocks.SAND_WALL.get());

		generator.family(Blocks.END_STONE)
				.slab(ModBlocks.END_STONE_SLAB.get())
				.stairs(ModBlocks.END_STONE_STAIRS.get())
				.wall(ModBlocks.END_STONE_WALL.get());

		generator.family(Blocks.NETHERRACK)
				.fence(ModBlocks.NETHERRACK_FENCE.get())
				.slab(ModBlocks.NETHERRACK_SLAB.get())
				.stairs(ModBlocks.NETHERRACK_STAIRS.get())
				.wall(ModBlocks.NETHERRACK_WALL.get());

		generator.createTrivialCube(ModBlocks.DRIFTWOOD_LEAVES.get());
		generator.createCrossBlockWithDefaultItem(ModBlocks.DRIFTWOOD_SAPLING.get(), BlockModelGenerators.TintState.NOT_TINTED); //TODO: Decide if we need potted version
		generator.createTrivialCube(ModBlocks.GRITTY_STONE.get());
		generator.family(ModBlocks.REALITY_SPONGE.get());

		registerPointedDripstone(generator);

	}

	private void registerPointedDripstone(BlockModelGenerators generator) {
		generator.skipAutoItemBlock(ModBlocks.UNRAVELED_SPIKE.get());
		PropertyDispatch.C2<Direction, DripstoneThickness> doubleProperty = PropertyDispatch.properties(BlockStateProperties.VERTICAL_DIRECTION, BlockStateProperties.DRIPSTONE_THICKNESS);
		for (DripstoneThickness thickness : DripstoneThickness.values()) {
			doubleProperty.select(Direction.UP, thickness, generator.createPointedDripstoneVariant(Direction.UP, thickness));
		}
		for (DripstoneThickness thickness : DripstoneThickness.values()) {
			doubleProperty.select(Direction.DOWN, thickness, generator.createPointedDripstoneVariant(Direction.DOWN, thickness));
		}
		generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModBlocks.UNRAVELED_SPIKE.get()).with(doubleProperty));
	}


	public void registerDoor(BlockModelGenerators generator, Block doorBlock, Block textureSource) {
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

	public void registerAutoGenDoor(BlockModelGenerators generator, DimensionalDoorBlockRegistrar.AutoGenDimensionalDoorBlock doorBlock) {
		Block textureSource = doorBlock.getOriginalBlock();

		ResourceLocation identifier = getBlockTexture(textureSource, "_bottom_left");
		ResourceLocation identifier2 = getBlockTexture(textureSource, "_bottom_left_open");
		ResourceLocation identifier3 = getBlockTexture(textureSource, "_bottom_right");
		ResourceLocation identifier4 = getBlockTexture(textureSource, "_bottom_right_open");
		ResourceLocation identifier5 = getBlockTexture(textureSource, "_top_left");
		ResourceLocation identifier6 = getBlockTexture(textureSource, "_top_left_open");
		ResourceLocation identifier7 = getBlockTexture(textureSource, "_top_right");
		ResourceLocation identifier8 = getBlockTexture(textureSource, "_top_right_open");
		ModelTemplates.TWO_LAYERED_ITEM.create(ModelLocationUtils.getModelLocation(doorBlock), TextureMapping.layered(getItemTexture(Items.ENDER_PEARL), getItemTexture(textureSource.asItem())), generator.modelOutput);
		generator.blockStateOutput.accept(BlockModelGenerators.createDoor(doorBlock, identifier, identifier2, identifier3, identifier4, identifier5, identifier6, identifier7, identifier8));
	}

	@Override
	public void generateItemModels(ItemModelGenerators itemModelGenerator) {
		itemModelGenerator.generateFlatItem(ModItems.FUZZY_FIREBALL.get(), ModelTemplates.FLAT_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.FABRIC_OF_FINALITY.get(), ModelTemplates.FLAT_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.LIMINAL_LINT.get(), ModelTemplates.FLAT_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.ENDURING_FIBERS.get(), ModelTemplates.FLAT_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.RIFT_PEARL.get(), ModelTemplates.FLAT_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.AMALGAM_LUMP.get(), ModelTemplates.FLAT_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.CLOD.get(), ModelTemplates.FLAT_ITEM);

		itemModelGenerator.generateFlatItem(ModItems.GARMENT_OF_REALITY_BOOTS.get(), ModelTemplates.FLAT_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.GARMENT_OF_REALITY_CHESTPLATE.get(), ModelTemplates.FLAT_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.GARMENT_OF_REALITY_HELMET.get(), ModelTemplates.FLAT_ITEM);
		itemModelGenerator.generateFlatItem(ModItems.GARMENT_OF_REALITY_LEGGINGS.get(), ModelTemplates.FLAT_ITEM);
	}
}
