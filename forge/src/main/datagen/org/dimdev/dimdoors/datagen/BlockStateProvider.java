package org.dimdev.dimdoors.datagen;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.Thickness;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.BlockStateVariantMap;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureMap;
import net.minecraft.data.client.VariantsBlockStateSupplier;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

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
	public void generateBlockStateModels(BlockStateModelGenerator generator) {
		generator.registerDoor(ModBlocks.GOLD_DOOR);
		generator.registerDoor(ModBlocks.STONE_DOOR);
		generator.registerDoor(ModBlocks.QUARTZ_DOOR);
//		registerDoor(generator, Registries.BLOCK.get(DimensionalDoors.id("block_ag_dim_minecraft_iron_door")), Blocks.IRON_DOOR);
//		registerDoor(generator, Registries.BLOCK.get(DimensionalDoors.id("block_ag_dim_dimdoors_gold_door")), ModBlocks.GOLD_DOOR);
//		registerDoor(generator, Registries.BLOCK.get(DimensionalDoors.id("quartz_dimensional_door")), ModBlocks.QUARTZ_DOOR);
//		registerDoor(generator, Registries.BLOCK.get(DimensionalDoors.id("block_ag_dim_minecraft_oak_door")), Blocks.OAK_DOOR);
//		registerDoor(generator, Registry.BLOCK.get(Util.id("dungeon_door")), ModBlocks.STONE_DOOR);


		generator.registerLog(ModBlocks.DRIFTWOOD_LOG).log(ModBlocks.DRIFTWOOD_LOG).wood(ModBlocks.DRIFTWOOD_WOOD);
		generator.registerCubeAllModelTexturePool(ModBlocks.DRIFTWOOD_PLANKS)
				.fence(ModBlocks.DRIFTWOOD_FENCE)
				.fenceGate(ModBlocks.DRIFTWOOD_GATE)
				.button(ModBlocks.DRIFTWOOD_BUTTON)
				.slab(ModBlocks.DRIFTWOOD_SLAB)
				.stairs(ModBlocks.DRIFTWOOD_STAIRS);
		generator.registerDoor(ModBlocks.DRIFTWOOD_DOOR);
		generator.registerTrapdoor(ModBlocks.DRIFTWOOD_TRAPDOOR);

		generator.registerCubeAllModelTexturePool(ModBlocks.AMALGAM_BLOCK)
				.slab(ModBlocks.AMALGAM_SLAB)
				.stairs(ModBlocks.AMALGAM_STAIRS);
		generator.registerDoor(ModBlocks.AMALGAM_DOOR);
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
				.stairs(ModBlocks.GRAVEL_STAIRS)
				.wall(ModBlocks.GRAVEL_WALL);

		generator.registerCubeAllModelTexturePool(ModBlocks.DARK_SAND)
				.fence(ModBlocks.DARK_SAND_FENCE)
				.button(ModBlocks.DARK_SAND_BUTTON)
				.fenceGate(ModBlocks.DARK_SAND_GATE)
				.slab(ModBlocks.DARK_SAND_SLAB)
				.stairs(ModBlocks.DARK_SAND_STAIRS)
				.wall(ModBlocks.DARK_SAND_WALL);

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

		generator.registerCubeAllModelTexturePool(Blocks.DEEPSLATE)
				.slab(ModBlocks.DEEPSLATE_SLAB)
				.stairs(ModBlocks.DEEPSLATE_STAIRS)
				.wall(ModBlocks.DEEPSLATE_WALL);

		generator.registerCubeAllModelTexturePool(Blocks.RED_SAND)
				.slab(ModBlocks.RED_SAND_SLAB)
				.stairs(ModBlocks.RED_SAND_STAIRS)
				.wall(ModBlocks.RED_SAND_WALL);

		generator.registerCubeAllModelTexturePool(Blocks.SAND)
				.slab(ModBlocks.SAND_SLAB)
				.stairs(ModBlocks.SAND_STAIRS)
				.wall(ModBlocks.SAND_WALL);

		generator.registerCubeAllModelTexturePool(Blocks.END_STONE)
				.slab(ModBlocks.END_STONE_SLAB)
				.stairs(ModBlocks.END_STONE_STAIRS)
				.wall(ModBlocks.END_STONE_WALL);

		generator.registerCubeAllModelTexturePool(Blocks.NETHERRACK)
				.fence(ModBlocks.NETHERRACK_FENCE)
				.slab(ModBlocks.NETHERRACK_SLAB)
				.stairs(ModBlocks.NETHERRACK_STAIRS)
				.wall(ModBlocks.NETHERRACK_WALL);

		generator.registerSimpleCubeAll(ModBlocks.DRIFTWOOD_LEAVES);
		generator.registerTintableCross(ModBlocks.DRIFTWOOD_SAPLING, BlockStateModelGenerator.TintType.NOT_TINTED); //TODO: Decide if we need potted version
		generator.registerSimpleCubeAll(ModBlocks.GRITTY_STONE);
		generator.registerCubeAllModelTexturePool(ModBlocks.REALITY_SPONGE);

		registerPointedDripstone(generator);

	}

	private void registerPointedDripstone(BlockStateModelGenerator generator) {
		generator.excludeFromSimpleItemModelGeneration(ModBlocks.UNRAVELED_SPIKE);
		BlockStateVariantMap.DoubleProperty<Direction, Thickness> doubleProperty = BlockStateVariantMap.create(Properties.VERTICAL_DIRECTION, Properties.THICKNESS);
		for (Thickness thickness : Thickness.values()) {
			doubleProperty.register(Direction.UP, thickness, generator.getDripstoneVariant(Direction.UP, thickness));
		}
		for (Thickness thickness : Thickness.values()) {
			doubleProperty.register(Direction.DOWN, thickness, generator.getDripstoneVariant(Direction.DOWN, thickness));
		}
		generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(ModBlocks.UNRAVELED_SPIKE).coordinate(doubleProperty));
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
		itemModelGenerator.register(ModItems.FUZZY_FIREBALL, Models.GENERATED);
		itemModelGenerator.register(ModItems.FABRIC_OF_FINALITY, Models.GENERATED);
		itemModelGenerator.register(ModItems.LIMINAL_LINT, Models.GENERATED);
		itemModelGenerator.register(ModItems.ENDURING_FIBERS, Models.GENERATED);
		itemModelGenerator.register(ModItems.RIFT_PEARL, Models.GENERATED);
		itemModelGenerator.register(ModItems.AMALGAM_LUMP, Models.GENERATED);
		itemModelGenerator.register(ModItems.CLOD, Models.GENERATED);

		itemModelGenerator.register(ModItems.GARMENT_OF_REALITY_BOOTS, Models.GENERATED);
		itemModelGenerator.register(ModItems.GARMENT_OF_REALITY_CHESTPLATE, Models.GENERATED);
		itemModelGenerator.register(ModItems.GARMENT_OF_REALITY_HELMET, Models.GENERATED);
		itemModelGenerator.register(ModItems.GARMENT_OF_REALITY_LEGGINGS, Models.GENERATED);
	}
}
