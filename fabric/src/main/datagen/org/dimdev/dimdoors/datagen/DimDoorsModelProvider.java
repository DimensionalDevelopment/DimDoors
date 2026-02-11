package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.Direction;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;
import org.dimdev.dimdoors.item.ModItems;

import static net.minecraft.data.models.model.TextureMapping.getBlockTexture;
import static net.minecraft.data.models.model.TextureMapping.getItemTexture;

public class DimDoorsModelProvider extends FabricModelProvider {
	public DimDoorsModelProvider(FabricDataOutput dataGenerator) {
		super(dataGenerator);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators generator) {
		generator.createDoor(ModBlocks.GOLD_DOOR.get());
		generator.createDoor(ModBlocks.STONE_DOOR.get());
		generator.createDoor(ModBlocks.QUARTZ_DOOR.get());

//		BuiltInRegistries.BLOCK.stream().filter(a -> a instanceof DimensionalDoorBlockRegistrar.AutoGenDimensionalDoorBlock).map(a -> (DimensionalDoorBlockRegistrar.AutoGenDimensionalDoorBlock) a).forEach(a -> registerAutoGenDoor(generator, a));

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

        generateDecaySet(generator, Blocks.RED_SAND, ModBlocks.RED_SAND_SET);
        generateDecaySet(generator, Blocks.GRAVEL, ModBlocks.GRAVEL_SET);
        generateDecaySet(generator, ModBlocks.DARK_SAND.get(), ModBlocks.DARK_SAND_SET);
        generateDecaySet(generator, Blocks.CLAY, ModBlocks.CLAY_SET);
        generateDecaySet(generator, Blocks.TERRACOTTA, ModBlocks.TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.WHITE_TERRACOTTA, ModBlocks.WHITE_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.WHITE_GLAZED_TERRACOTTA, ModBlocks.WHITE_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.ORANGE_TERRACOTTA, ModBlocks.ORANGE_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.ORANGE_GLAZED_TERRACOTTA, ModBlocks.ORANGE_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.MAGENTA_TERRACOTTA, ModBlocks.MAGENTA_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.MAGENTA_GLAZED_TERRACOTTA, ModBlocks.MAGENTA_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.LIGHT_BLUE_TERRACOTTA, ModBlocks.LIGHT_BLUE_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, ModBlocks.LIGHT_BLUE_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.YELLOW_TERRACOTTA, ModBlocks.YELLOW_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.YELLOW_GLAZED_TERRACOTTA, ModBlocks.YELLOW_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.LIME_TERRACOTTA, ModBlocks.LIME_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.LIME_GLAZED_TERRACOTTA, ModBlocks.LIME_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.PINK_TERRACOTTA, ModBlocks.PINK_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.PINK_GLAZED_TERRACOTTA, ModBlocks.PINK_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.GRAY_TERRACOTTA, ModBlocks.GRAY_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.GRAY_GLAZED_TERRACOTTA, ModBlocks.GRAY_GLAZED_TERRACOTTASET);
        generateDecaySet(generator, Blocks.LIGHT_GRAY_TERRACOTTA, ModBlocks.LIGHT_GRAY_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, ModBlocks.LIGHT_GRAY_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.CYAN_TERRACOTTA, ModBlocks.CYAN_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.CYAN_GLAZED_TERRACOTTA, ModBlocks.CYAN_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.PURPLE_TERRACOTTA, ModBlocks.PURPLE_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.PURPLE_GLAZED_TERRACOTTA, ModBlocks.PURPLE_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.BLUE_TERRACOTTA, ModBlocks.BLUE_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.BLUE_GLAZED_TERRACOTTA, ModBlocks.BLUE_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.BROWN_TERRACOTTA, ModBlocks.BROWN_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.BROWN_GLAZED_TERRACOTTA, ModBlocks.BROWN_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.GREEN_TERRACOTTA, ModBlocks.GREEN_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.GREEN_GLAZED_TERRACOTTA, ModBlocks.GREEN_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.RED_TERRACOTTA, ModBlocks.RED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.RED_GLAZED_TERRACOTTA, ModBlocks.RED_GLAZED_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.BLACK_TERRACOTTA, ModBlocks.BLACK_TERRACOTTA_SET);
        generateDecaySet(generator, Blocks.BLACK_GLAZED_TERRACOTTA, ModBlocks.BLACK_GLAZED_TERRACOTTA_SET);


        generateDecaySet(generator, Blocks.MUD, ModBlocks.MUD_SET);
        generateDecaySet(generator, ModBlocks.UNRAVELLED_FABRIC.get(), ModBlocks.UNRAVELED_SET);
        generateDecaySet(generator, Blocks.DEEPSLATE, ModBlocks.DEEPSLATE_SET);
        generateDecaySet(generator, Blocks.SAND, ModBlocks.SAND_SET);
        generateDecaySet(generator, Blocks.END_STONE, ModBlocks.END_STONE_SET);
        generateDecaySet(generator, Blocks.NETHERRACK, ModBlocks.NETHERRACK_SET);

		generator.createTrivialCube(ModBlocks.DRIFTWOOD_LEAVES.get());
		generator.createCrossBlockWithDefaultItem(ModBlocks.DRIFTWOOD_SAPLING.get(), BlockModelGenerators.TintState.NOT_TINTED); //TODO: Decide if we need potted version
		generator.createTrivialCube(ModBlocks.GRITTY_STONE.get());
		generator.family(ModBlocks.REALITY_SPONGE.get());

		registerUnraveledSpike(generator);


        generator.createAirLikeBlock(ModBlocks.LIMBO_AIR.get(), Blocks.BARRIER.asItem());
	}
    private void generateDecaySet(BlockModelGenerators generator, Block block, ModBlocks.DecayGroupSet set) {
        generator.family(block)
                .button(set.button().get())
                .slab(set.slab().get())
                .stairs(set.stairs().get())
                .wall(set.wall().get())
                .fence(set.fence().get())
                .fenceGate(set.gate().get());
    }

    private void registerUnraveledSpike(BlockModelGenerators generator) {
		PropertyDispatch.C2<Direction, DripstoneThickness> doubleProperty = PropertyDispatch.properties(BlockStateProperties.VERTICAL_DIRECTION, BlockStateProperties.DRIPSTONE_THICKNESS);
		for (DripstoneThickness thickness : DripstoneThickness.values()) {
			doubleProperty.select(Direction.UP, thickness, createPointedUnraveledspikeVariant(generator, Direction.UP, thickness));
		}
		for (DripstoneThickness thickness : DripstoneThickness.values()) {
			doubleProperty.select(Direction.DOWN, thickness, createPointedUnraveledspikeVariant(generator, Direction.DOWN, thickness));
		}
		generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(ModBlocks.UNRAVELED_SPIKE.get()).with(doubleProperty));
	}

	public final Variant createPointedUnraveledspikeVariant(BlockModelGenerators generators, Direction direction, DripstoneThickness dripstoneThickness) {
		generators.skipAutoItemBlock(ModBlocks.UNRAVELED_SPIKE.get());
		String string = "_" + direction.getSerializedName() + "_" + dripstoneThickness.getSerializedName();
		TextureMapping textureMapping = TextureMapping.cross(TextureMapping.getBlockTexture(ModBlocks.UNRAVELED_SPIKE.get(), string));
		return Variant.variant().with(VariantProperties.MODEL, ModelTemplates.CROSS.createWithSuffix(ModBlocks.UNRAVELED_SPIKE.get(), string, textureMapping, generators.modelOutput));
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
		ModelTemplates.TWO_LAYERED_ITEM.create(ModelLocationUtils.getModelLocation(doorBlock), TextureMapping.layered(DimensionalDoors.id("item/dimdoor_back"), getItemTexture(textureSource.asItem())), generator.modelOutput);
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
