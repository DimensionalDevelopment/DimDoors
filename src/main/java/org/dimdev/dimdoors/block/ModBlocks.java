package org.dimdev.dimdoors.block;

import java.util.HashMap;
import java.util.Map;

import org.dimdev.matrix.Matrix;
import org.dimdev.matrix.Registrar;
import org.dimdev.matrix.RegistryEntry;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.registry.Registries;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import org.dimdev.dimdoors.block.door.DimensionalTrapdoorBlock;
import org.dimdev.dimdoors.block.door.data.DoorData;
import org.dimdev.dimdoors.block.door.data.DoorDataReader;

import static net.minecraft.block.Blocks.CLAY;
import static net.minecraft.block.Blocks.GRAVEL;
import static net.minecraft.block.Blocks.MUD;
import static net.minecraft.block.Blocks.OAK_LEAVES;
import static net.minecraft.block.Blocks.OAK_SAPLING;
import static net.minecraft.block.Blocks.STONE;
import static net.minecraft.block.Blocks.WATER;

@Registrar(element = Block.class, modid = "dimdoors")
public final class ModBlocks {
	public static final Map<DyeColor, Block> FABRIC_BLOCKS = new HashMap<>();

	private static final Map<DyeColor, Block> ANCIENT_FABRIC_BLOCKS = new HashMap<>();

	@RegistryEntry("stone_player") public static final Block STONE_PLAYER = register(new Block(FabricBlockSettings.of(Material.STONE).strength(0.5F).nonOpaque()));

	@RegistryEntry("gold_door") public static final Block GOLD_DOOR = register(new DoorBlock(FabricBlockSettings.of(Material.METAL, MapColor.GOLD).strength(5.0F).requiresTool().nonOpaque(), SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN));

	@RegistryEntry("stone_door") public static final Block STONE_DOOR = register(new DoorBlock(FabricBlockSettings.of(Material.METAL, MapColor.OAK_TAN).strength(5.0F).requiresTool().nonOpaque(), SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN));

	@RegistryEntry("quartz_door") public static final Block QUARTZ_DOOR = register(new DoorBlock(FabricBlockSettings.of(Material.STONE, MapColor.OFF_WHITE).strength(5.0F).requiresTool().nonOpaque(), SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN));

	@RegistryEntry("wood_dimensional_trapdoor") public static final Block OAK_DIMENSIONAL_TRAPDOOR = register(new DimensionalTrapdoorBlock(FabricBlockSettings.copyOf(Blocks.OAK_TRAPDOOR).luminance(state -> 10), SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundEvents.BLOCK_WOODEN_DOOR_OPEN));

	@RegistryEntry("dimensional_portal") public static final Block DIMENSIONAL_PORTAL = register(new DimensionalPortalBlock(FabricBlockSettings.of(Material.AIR).collidable(false).strength(-1.0F, 3600000.0F).nonOpaque().dropsNothing().luminance(blockState -> 10)));

	@RegistryEntry("detached_rift") public static final Block DETACHED_RIFT = register(new DetachedRiftBlock(FabricBlockSettings.of(Material.AIR).strength(-1.0F, 3600000.0F).noCollision().nonOpaque()));

	@RegistryEntry("white_fabric") public static final Block WHITE_FABRIC = registerFabric(DyeColor.WHITE);

	@RegistryEntry("orange_fabric") public static final Block ORANGE_FABRIC = registerFabric(DyeColor.ORANGE);

	@RegistryEntry("magenta_fabric") public static final Block MAGENTA_FABRIC = registerFabric(DyeColor.MAGENTA);

	@RegistryEntry("light_blue_fabric") public static final Block LIGHT_BLUE_FABRIC = registerFabric(DyeColor.LIGHT_BLUE);

	@RegistryEntry("yellow_fabric") public static final Block YELLOW_FABRIC = registerFabric(DyeColor.YELLOW);

	@RegistryEntry("lime_fabric") public static final Block LIME_FABRIC = registerFabric(DyeColor.LIME);

	@RegistryEntry("pink_fabric") public static final Block PINK_FABRIC = registerFabric(DyeColor.PINK);

	@RegistryEntry("gray_fabric") public static final Block GRAY_FABRIC = registerFabric(DyeColor.GRAY);

	@RegistryEntry("light_gray_fabric") public static final Block LIGHT_GRAY_FABRIC = registerFabric(DyeColor.LIGHT_GRAY);

	@RegistryEntry("cyan_fabric") public static final Block CYAN_FABRIC = registerFabric(DyeColor.CYAN);

	@RegistryEntry("purple_fabric") public static final Block PURPLE_FABRIC = registerFabric(DyeColor.PURPLE);

	@RegistryEntry("blue_fabric") public static final Block BLUE_FABRIC = registerFabric(DyeColor.BLUE);

	@RegistryEntry("brown_fabric") public static final Block BROWN_FABRIC = registerFabric(DyeColor.BROWN);

	@RegistryEntry("green_fabric") public static final Block GREEN_FABRIC = registerFabric(DyeColor.GREEN);

	@RegistryEntry("red_fabric") public static final Block RED_FABRIC = registerFabric(DyeColor.RED);

	@RegistryEntry("black_fabric") public static final Block BLACK_FABRIC = registerFabric(DyeColor.BLACK);


	@RegistryEntry("white_ancient_fabric") public static final Block WHITE_ANCIENT_FABRIC = registerAncientFabric(DyeColor.WHITE);

	@RegistryEntry("orange_ancient_fabric") public static final Block ORANGE_ANCIENT_FABRIC = registerAncientFabric(DyeColor.ORANGE);

	@RegistryEntry("magenta_ancient_fabric") public static final Block MAGENTA_ANCIENT_FABRIC = registerAncientFabric(DyeColor.MAGENTA);

	@RegistryEntry("light_blue_ancient_fabric") public static final Block LIGHT_BLUE_ANCIENT_FABRIC = registerAncientFabric(DyeColor.LIGHT_BLUE);

	@RegistryEntry("yellow_ancient_fabric") public static final Block YELLOW_ANCIENT_FABRIC = registerAncientFabric(DyeColor.YELLOW);

	@RegistryEntry("lime_ancient_fabric") public static final Block LIME_ANCIENT_FABRIC = registerAncientFabric(DyeColor.LIME);

	@RegistryEntry("pink_ancient_fabric") public static final Block PINK_ANCIENT_FABRIC = registerAncientFabric(DyeColor.PINK);

	@RegistryEntry("gray_ancient_fabric") public static final Block GRAY_ANCIENT_FABRIC = registerAncientFabric(DyeColor.GRAY);

	@RegistryEntry("light_gray_ancient_fabric") public static final Block LIGHT_GRAY_ANCIENT_FABRIC = registerAncientFabric(DyeColor.LIGHT_GRAY);

	@RegistryEntry("cyan_ancient_fabric") public static final Block CYAN_ANCIENT_FABRIC = registerAncientFabric(DyeColor.CYAN);

	@RegistryEntry("purple_ancient_fabric") public static final Block PURPLE_ANCIENT_FABRIC = registerAncientFabric(DyeColor.PURPLE);

	@RegistryEntry("blue_ancient_fabric") public static final Block BLUE_ANCIENT_FABRIC = registerAncientFabric(DyeColor.BLUE);

	@RegistryEntry("brown_ancient_fabric") public static final Block BROWN_ANCIENT_FABRIC = registerAncientFabric(DyeColor.BROWN);

	@RegistryEntry("green_ancient_fabric") public static final Block GREEN_ANCIENT_FABRIC = registerAncientFabric(DyeColor.GREEN);

	@RegistryEntry("red_ancient_fabric") public static final Block RED_ANCIENT_FABRIC = registerAncientFabric(DyeColor.RED);

	@RegistryEntry("black_ancient_fabric") public static final Block BLACK_ANCIENT_FABRIC = registerAncientFabric(DyeColor.BLACK);
	private static final FabricBlockSettings UNRAVELLED_FABRIC_BLOCK_SETTINGS = FabricBlockSettings.of(Material.STONE, MapColor.BLACK).ticksRandomly().luminance(15).strength(0.3F, 0.3F);

	@RegistryEntry("eternal_fluid") public static final Block ETERNAL_FLUID = register(new EternalFluidBlock(FabricBlockSettings.of(Material.STONE, MapColor.RED).luminance(15)));

	@RegistryEntry("decayed_block") public static final Block DECAYED_BLOCK = register(new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

	@RegistryEntry("unfolded_block") public static final Block UNFOLDED_BLOCK = register(new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

	@RegistryEntry("unwarped_block") public static final Block UNWARPED_BLOCK = register(new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

	@RegistryEntry("unravelled_block") public static final Block UNRAVELLED_BLOCK = register(new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

	@RegistryEntry("unravelled_fabric") public static final Block UNRAVELLED_FABRIC = register(new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

	@RegistryEntry("marking_plate") public static final Block MARKING_PLATE = register(new MarkingPlateBlock(FabricBlockSettings.of(Material.METAL, DyeColor.BLACK).nonOpaque()));

	@RegistryEntry("solid_static") public static final Block SOLID_STATIC = register(new UnravelledFabricBlock(FabricBlockSettings.of(Material.STONE).strength(7, 25).ticksRandomly().requiresTool().sounds(BlockSoundGroup.SAND)));

	@RegistryEntry("tesselating_loom") public static final Block TESSELATING_LOOM = register(new TesselatingLoomBlock(FabricBlockSettings.copy(Blocks.LOOM)));

	@RegistryEntry("reality_sponge")
	public static final Block REALITY_SPONGE = new RealitySpongeBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS);

	//Decay graph filler.
	@RegistryEntry("driftwood_wood") public static final Block DRIFTWOOD_WOOD = new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.LIGHT_GRAY).strength(2.0F).sounds(BlockSoundGroup.WOOD));
	@RegistryEntry("driftwood_log") public static final Block DRIFTWOOD_LOG = new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.LIGHT_GRAY).strength(2.0F).sounds(BlockSoundGroup.WOOD));
	@RegistryEntry("driftwood_planks") public static final Block DRIFTWOOD_PLANKS = new Block(AbstractBlock.Settings.of(Material.WOOD, MapColor.LIGHT_GRAY).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));
	@RegistryEntry("driftwood_leaves") public static final Block DRIFTWOOD_LEAVES = new LeavesBlock(AbstractBlock.Settings.copy(OAK_LEAVES));
	@RegistryEntry("driftwood_sapling") public static final Block DRIFTWOOD_SAPLING = new Block(AbstractBlock.Settings.copy(OAK_SAPLING));
	@RegistryEntry("driftwood_fence") public static final Block DRIFTWOOD_FENCE = new FenceBlock(AbstractBlock.Settings.of(Material.WOOD, DRIFTWOOD_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));
	@RegistryEntry("driftwood_gate") public static final Block DRIFTWOOD_GATE = new FenceGateBlock(AbstractBlock.Settings.of(Material.WOOD, DRIFTWOOD_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD), SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundEvents.BLOCK_FENCE_GATE_OPEN);
	@RegistryEntry("driftwood_button") public static final Block DRIFTWOOD_BUTTON = new ButtonBlock(AbstractBlock.Settings.of(Material.DECORATION, MapColor.LIGHT_GRAY).noCollision().strength(0.5F), 20, true, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON);
	@RegistryEntry("driftwood_slab") public static final Block DRIFTWOOD_SLAB = new SlabBlock(AbstractBlock.Settings.of(Material.WOOD, MapColor.LIGHT_GRAY));
	@RegistryEntry("driftwood_stairs") public static final Block DRIFTWOOD_STAIRS = new StairsBlock(DRIFTWOOD_PLANKS.getDefaultState(), AbstractBlock.Settings.of(Material.WOOD,  MapColor.LIGHT_GRAY));
	@RegistryEntry("driftwood_door") public static final Block DRIFTWOOD_DOOR = new DoorBlock(AbstractBlock.Settings.of(Material.WOOD, DRIFTWOOD_PLANKS.getDefaultMapColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque(), SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundEvents.BLOCK_WOODEN_DOOR_OPEN);
	@RegistryEntry("driftwood_trapdoor") public static final Block DRIFTWOOD_TRAPDOOR = new TrapdoorBlock(AbstractBlock.Settings.of(Material.WOOD, DRIFTWOOD_PLANKS.getDefaultMapColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning((state, world, pos, type) -> false), SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN);

	@RegistryEntry("amalgam_block") public static final Block AMALGAM_BLOCK = new Block(AbstractBlock.Settings.of(Material.METAL, MapColor.LIGHT_GRAY).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL));
	@RegistryEntry("amalgam_door") public static final Block AMALGAM_DOOR = new DoorBlock(AbstractBlock.Settings.of(Material.METAL, MapColor.LIGHT_GRAY).requiresTool().strength(5.0F).sounds(BlockSoundGroup.METAL).nonOpaque(), SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN);
	@RegistryEntry("amalgam_trapdoor") public static final Block AMALGAM_TRAPDOOR = new TrapdoorBlock(AbstractBlock.Settings.of(Material.METAL).requiresTool().strength(5.0F).sounds(BlockSoundGroup.METAL).nonOpaque().allowsSpawning((state, world, pos, type) -> false), SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN);
	@RegistryEntry("rust") public static final Block RUST = new Block(AbstractBlock.Settings.of(Material.WOOD));
	@RegistryEntry("amalgam_slab") public static final Block AMALGAM_SLAB = createSlab(AMALGAM_BLOCK);
	@RegistryEntry("amalgam_stairs") public static final Block AMALGAM_STAIRS = createStairs(AMALGAM_BLOCK);
	@RegistryEntry("amalgam_ore") public static final Block AMALGAM_ORE = new ExperienceDroppingBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(3.0F, 3.0F));

	@RegistryEntry("clod_ore") public static final Block CLOD_ORE = new Block(AbstractBlock.Settings.of(Material.WOOD));
	@RegistryEntry("clod_block") public static final Block CLOD_BLOCK = new Block(AbstractBlock.Settings.of(Material.WOOD));

	@RegistryEntry("gravel_fence") public static final Block GRAVEL_FENCE = createFence(GRAVEL);
	@RegistryEntry("gravel_gate") public static final Block GRAVEL_GATE = createFenceGate(GRAVEL);
	@RegistryEntry("gravel_button") public static final Block GRAVEL_BUTTON = createButton(GRAVEL);
	@RegistryEntry("gravel_slab") public static final Block GRAVEL_SLAB = createSlab(GRAVEL);
	@RegistryEntry("gravel_stairs") public static final Block GRAVEL_STAIRS = createStairs(GRAVEL);
	@RegistryEntry("gravel_wall") public static Block GRAVEL_WALL = createWall(GRAVEL);

	@RegistryEntry("dark_sand") public static final Block DARK_SAND = new Block(AbstractBlock.Settings.of(Material.AGGREGATE, MapColor.BLACK).strength(0.5F).sounds(BlockSoundGroup.SAND));
	@RegistryEntry("dark_sand_fence") public static final Block DARK_SAND_FENCE = createFence(DARK_SAND);
	@RegistryEntry("dark_sand_gate") public static final Block DARK_SAND_GATE = createFenceGate(DARK_SAND);
	@RegistryEntry("dark_sand_button") public static final Block DARK_SAND_BUTTON = createButton(DARK_SAND);
	@RegistryEntry("dark_sand_slab") public static final Block DARK_SAND_SLAB = createSlab(DARK_SAND);
	@RegistryEntry("dark_sand_stairs") public static final Block DARK_SAND_STAIRS = createStairs(DARK_SAND);
	@RegistryEntry("dark_sand_wall") public static Block DARK_SAND_WALL = createWall(DARK_SAND);

	@RegistryEntry("clay_fence") public static final Block CLAY_FENCE = createFence(CLAY);
	@RegistryEntry("clay_gate") public static final Block CLAY_GATE = createFenceGate(CLAY);
	@RegistryEntry("clay_button") public static final Block CLAY_BUTTON = createButton(CLAY);
	@RegistryEntry("clay_slab") public static final Block CLAY_SLAB = createSlab(CLAY);
	@RegistryEntry("clay_stairs") public static final Block CLAY_STAIRS = createStairs(CLAY);

	@RegistryEntry("mud_fence") public static final Block MUD_FENCE = createFence(MUD);
	@RegistryEntry("mud_gate") public static final Block MUD_GATE = createFenceGate(MUD);
	@RegistryEntry("mud_button") public static final Block MUD_BUTTON = createButton(MUD);
	@RegistryEntry("mud_slab") public static final Block MUD_SLAB = createSlab(MUD);
	@RegistryEntry("mud_stairs") public static final Block MUD_STAIRS = createStairs(MUD);

	@RegistryEntry("unraveled_fence") public static final Block UNRAVELED_FENCE = createFence(UNRAVELLED_FABRIC);
	@RegistryEntry("unraveled_gate") public static final Block UNRAVELED_GATE = createFenceGate(UNRAVELLED_FABRIC);
	@RegistryEntry("unraveled_button") public static final Block UNRAVELED_BUTTON = createButton(UNRAVELLED_FABRIC);
	@RegistryEntry("unraveled_slab") public static final Block UNRAVELED_SLAB = createSlab(UNRAVELLED_FABRIC);
	@RegistryEntry("unraveled_stairs") public static final Block UNRAVELED_STAIRS = createStairs(UNRAVELLED_FABRIC);

	@RegistryEntry("deepslate_slab") public static final Block DEEPSLATE_SLAB = createSlab(Blocks.DEEPSLATE);
	@RegistryEntry("deepslate_stairs") public static final Block DEEPSLATE_STAIRS = createStairs(Blocks.DEEPSLATE);
	@RegistryEntry("deepslate_wall") public static final Block DEEPSLATE_WALL = createWall(Blocks.DEEPSLATE);

	@RegistryEntry("red_sand_slab") public static final Block RED_SAND_SLAB = createSlab(Blocks.RED_SAND);
	@RegistryEntry("red_sand_stairs") public static final Block RED_SAND_STAIRS = createStairs(Blocks.RED_SAND);
	@RegistryEntry("red_sand_wall") public static final Block RED_SAND_WALL = createWall(Blocks.RED_SAND);

	@RegistryEntry("sand_slab") public static final Block SAND_SLAB = createSlab(Blocks.SAND);
	@RegistryEntry("sand_stairs") public static final Block SAND_STAIRS = createStairs(Blocks.SAND);
	@RegistryEntry("sand_wall") public static final Block SAND_WALL = createWall(Blocks.SAND);

	@RegistryEntry("end_stone_slab") public static final Block END_STONE_SLAB = createSlab(Blocks.END_STONE);
	@RegistryEntry("end_stone_stairs") public static final Block END_STONE_STAIRS = createStairs(Blocks.END_STONE);
	@RegistryEntry("end_stone_wall") public static final Block END_STONE_WALL = createWall(Blocks.END_STONE);

 	@RegistryEntry("netherrack_fence") public static final Block NETHERRACK_FENCE = createFence(Blocks.NETHERRACK);
 	@RegistryEntry("netherrack_slab") public static final Block NETHERRACK_SLAB = createSlab(Blocks.NETHERRACK);
 	@RegistryEntry("netherrack_stairs") public static final Block NETHERRACK_STAIRS = createStairs(Blocks.NETHERRACK);
 	@RegistryEntry("netherrack_wall") public static final Block NETHERRACK_WALL = createWall(Blocks.NETHERRACK);

	@RegistryEntry("unraveled_spike") public static final Block UNRAVELED_SPIKE = new Block(AbstractBlock.Settings.copy(UNRAVELLED_FABRIC).luminance(state -> 0));
	@RegistryEntry("gritty_stone") public static final Block GRITTY_STONE = new Block(AbstractBlock.Settings.copy(STONE));
	@RegistryEntry("leak") public static final Block LEAK = new Block(AbstractBlock.Settings.copy(WATER));

	private static Block register(Block block) {
		return block;
	}

	private static Block registerAncientFabric(DyeColor color) {
		Block block = new AncientFabricBlock(color);
		ANCIENT_FABRIC_BLOCKS.put(color, block);
		return register(block);
	}

	private static Block registerFabric(DyeColor color) {
		Block block = new FabricBlock(color);
		FABRIC_BLOCKS.put(color, block);
		return register(block);
	}

	public static void init() {
		Matrix.register(ModBlocks.class, Registries.BLOCK);
		DoorDataReader.read();
	}

	@Environment(EnvType.CLIENT)
	public static void initClient() {
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), ModBlocks.QUARTZ_DOOR, ModBlocks.GOLD_DOOR);
		DoorData.DOORS.forEach(door -> BlockRenderLayerMap.INSTANCE.putBlock(door, RenderLayer.getCutout()));
	}

	public static Block ancientFabricFromDye(DyeColor color) {
		return ANCIENT_FABRIC_BLOCKS.get(color);
	}

	public static Block fabricFromDye(DyeColor color) {
		return FABRIC_BLOCKS.get(color);
	}

	public static Block createFence(Block block) {
		return new FenceBlock(AbstractBlock.Settings.copy(block));
	}

	public static Block createFenceGate(Block block) {
		return new FenceGateBlock(AbstractBlock.Settings.copy(block), SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundEvents.BLOCK_FENCE_GATE_OPEN);
	}

	public static Block createButton(Block block) {
		return new ButtonBlock(AbstractBlock.Settings.copy(block).noCollision().strength(0.5F), 20, false, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON );
	}

	public static Block createSlab(Block block) {
		return new SlabBlock(AbstractBlock.Settings.copy(block));
	}

	public static Block createStairs(Block block) {
		return new StairsBlock(block.getDefaultState(), AbstractBlock.Settings.copy(block));
	}

	public static Block createWall(Block block) {
		return new WallBlock(AbstractBlock.Settings.copy(block));
	}
}
