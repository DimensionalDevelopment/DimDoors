package org.dimdev.dimdoors.block;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.door.DimensionalTrapdoorBlock;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.matrix.Matrix;
import org.dimdev.matrix.Registrar;
import org.dimdev.matrix.RegistryEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static net.minecraft.block.Blocks.OAK_LEAVES;
import static net.minecraft.block.Blocks.OAK_SAPLING;
import static net.minecraft.block.Blocks.STONE;
import static net.minecraft.block.Blocks.WATER;
import static net.minecraft.world.level.block.Blocks.*;

@Registrar(element = Block.class, modid = "dimdoors")
public final class ModBlocks {
	public static final Map<DyeColor, RegistrySupplier<Block>> FABRIC_BLOCKS = new HashMap<DyeColor, RegistrySupplier<Block>>();

	private static final Map<DyeColor, RegistrySupplier<Block>> ANCIENT_FABRIC_BLOCKS = new HashMap<DyeColor, RegistrySupplier<Block>>();

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.BLOCK);

	public static final RegistrySupplier<Block> STONE_PLAYER = register("stone_player", () -> new Block(of(Material.STONE).strength(0.5F).noOcclusion()));

	public static final RegistrySupplier<Block> GOLD_DOOR = register("gold_door", () -> new DoorBlock(of(Material.METAL, MaterialColor.GOLD).strength(5.0F).requiresCorrectToolForDrops().noCollission(), BlockSetType.IRON));

	public static final RegistrySupplier<Block> STONE_DOOR = register("stone_door", () -> new DoorBlock(of(Material.METAL, MaterialColor.WOOD).strength(5.0F).requiresCorrectToolForDrops().noOcclusion(), BlockSetType.IRON));

	public static final RegistrySupplier<Block> QUARTZ_DOOR = register("quartz_door", () -> new DoorBlock(of(Material.STONE, MaterialColor.TERRACOTTA_WHITE).strength(5.0F).requiresCorrectToolForDrops().noOcclusion(), BlockSetType.IRON));

	public static final RegistrySupplier<Block> OAK_DIMENSIONAL_TRAPDOOR = register("wood_dimensional_trapdoor", () -> new DimensionalTrapdoorBlock(of(Blocks.OAK_TRAPDOOR).lightLevel(state -> 10), BlockSetType.OAK));

	public static final RegistrySupplier<Block> DIMENSIONAL_PORTAL = register("dimensional_portal", () -> new DimensionalPortalBlock(of(Material.AIR).noCollission().strength(-1.0F, 3600000.0F).noOcclusion().dropsLike(AIR).lightLevel(blockState -> 10)));

	public static final RegistrySupplier<Block> DETACHED_RIFT = register("detached_rift", () -> new DetachedRiftBlock(Setti.of(Material.AIR).strength(-1.0F, 3600000.0F).noCollision().nonOpaque()));

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
	private static final BlockBehaviour.Properties UNRAVELLED_FABRIC_BLOCK_SETTINGS = of(Material.STONE, MaterialColor.COLOR_BLACK).randomTicks().lightLevel(state -> 15).strength(0.3F, 0.3F);

	@RegistryEntry("eternal_fluid") public static final Block ETERNAL_FLUID = register(new EternalFluidBlock(of(Material.STONE, MaterialColor.COLOR_RED).lightLevel(state -> 15)));

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
	@RegistryEntry("driftwood_wood") public static final Block DRIFTWOOD_WOOD = new PillarBlock(of(Material.WOOD, MapColor.LIGHT_GRAY).strength(2.0F).sounds(BlockSoundGroup.WOOD));
	@RegistryEntry("driftwood_log") public static final Block DRIFTWOOD_LOG = new PillarBlock(of(Material.WOOD, MapColor.LIGHT_GRAY).strength(2.0F).sounds(BlockSoundGroup.WOOD));
	@RegistryEntry("driftwood_planks") public static final Block DRIFTWOOD_PLANKS = new Block(of(Material.WOOD, MapColor.LIGHT_GRAY).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));
	@RegistryEntry("driftwood_leaves") public static final Block DRIFTWOOD_LEAVES = new LeavesBlock(of(OAK_LEAVES));
	@RegistryEntry("driftwood_sapling") public static final Block DRIFTWOOD_SAPLING = new Block(of(OAK_SAPLING));
	@RegistryEntry("driftwood_fence") public static final Block DRIFTWOOD_FENCE = new FenceBlock(of(Material.WOOD, DRIFTWOOD_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));
	@RegistryEntry("driftwood_gate") public static final Block DRIFTWOOD_GATE = new FenceGateBlock(of(Material.WOOD, DRIFTWOOD_PLANKS.getDefaultMapColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD), WoodType.OAK); // TODO: add driftwood wood type
	@RegistryEntry("driftwood_button") public static final Block DRIFTWOOD_BUTTON = new ButtonBlock(of(Material.DECORATION, MapColor.LIGHT_GRAY).noCollision().strength(0.5F), BlockSetType.OAK, 20, true);
	@RegistryEntry("driftwood_slab") public static final Block DRIFTWOOD_SLAB = new SlabBlock(of(Material.WOOD, MapColor.LIGHT_GRAY));
	@RegistryEntry("driftwood_stairs") public static final Block DRIFTWOOD_STAIRS = new StairsBlock(DRIFTWOOD_PLANKS.getDefaultState(), of(Material.WOOD,  MapColor.LIGHT_GRAY));
	@RegistryEntry("driftwood_door") public static final Block DRIFTWOOD_DOOR = new DoorBlock(of(Material.WOOD, DRIFTWOOD_PLANKS.getDefaultMapColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque(), BlockSetType.OAK);
	@RegistryEntry("driftwood_trapdoor") public static final Block DRIFTWOOD_TRAPDOOR = new TrapdoorBlock(of(Material.WOOD, DRIFTWOOD_PLANKS.getDefaultMapColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning((state, world, pos, type) -> false), BlockSetType.OAK);

	@RegistryEntry("amalgam_block") public static final Block AMALGAM_BLOCK = new Block(of(Material.METAL, MapColor.LIGHT_GRAY).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL));
	@RegistryEntry("amalgam_door") public static final Block AMALGAM_DOOR = new DoorBlock(of(Material.METAL, MapColor.LIGHT_GRAY).requiresTool().strength(5.0F).sounds(BlockSoundGroup.METAL).nonOpaque(), BlockSetType.IRON);
	@RegistryEntry("amalgam_trapdoor") public static final Block AMALGAM_TRAPDOOR = new TrapdoorBlock(of(Material.METAL).requiresTool().strength(5.0F).sounds(BlockSoundGroup.METAL).nonOpaque().allowsSpawning((state, world, pos, type) -> false), BlockSetType.IRON);
	@RegistryEntry("rust") public static final Block RUST = new Block(of(Material.WOOD));
	@RegistryEntry("amalgam_slab") public static final Block AMALGAM_SLAB = createSlab(AMALGAM_BLOCK);
	@RegistryEntry("amalgam_stairs") public static final Block AMALGAM_STAIRS = createStairs(AMALGAM_BLOCK);
	@RegistryEntry("amalgam_ore") public static final Block AMALGAM_ORE = new DropExperienceBlock(of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F));

	@RegistryEntry("clod_ore") public static final Block CLOD_ORE = new Block(of(Material.WOOD));
	@RegistryEntry("clod_block") public static final Block CLOD_BLOCK = new Block(of(Material.WOOD));

	@RegistryEntry("gravel_fence") public static final Block GRAVEL_FENCE = createFence(GRAVEL);
	@RegistryEntry("gravel_gate") public static final Block GRAVEL_GATE = createFenceGate(GRAVEL);
	@RegistryEntry("gravel_button") public static final Block GRAVEL_BUTTON = createButton(GRAVEL);
	@RegistryEntry("gravel_slab") public static final Block GRAVEL_SLAB = createSlab(GRAVEL);
	@RegistryEntry("gravel_stairs") public static final Block GRAVEL_STAIRS = createStairs(GRAVEL);
	@RegistryEntry("gravel_wall") public static final Block GRAVEL_WALL = createWall(GRAVEL);

	@RegistryEntry("dark_sand") public static final Block DARK_SAND = new Block(of(Material.AGGREGATE, MapColor.BLACK).strength(0.5F).sounds(BlockSoundGroup.SAND));
	@RegistryEntry("dark_sand_fence") public static final Block DARK_SAND_FENCE = createFence(DARK_SAND);
	@RegistryEntry("dark_sand_gate") public static final Block DARK_SAND_GATE = createFenceGate(DARK_SAND);
	@RegistryEntry("dark_sand_button") public static final Block DARK_SAND_BUTTON = createButton(DARK_SAND);
	@RegistryEntry("dark_sand_slab") public static final Block DARK_SAND_SLAB = createSlab(DARK_SAND);
	@RegistryEntry("dark_sand_stairs") public static final Block DARK_SAND_STAIRS = createStairs(DARK_SAND);
	@RegistryEntry("dark_sand_wall") public static final Block DARK_SAND_WALL = createWall(DARK_SAND);

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

	@RegistryEntry("unraveled_spike") public static final Block UNRAVELED_SPIKE = new PointedDripstoneBlock(of(UNRAVELLED_FABRIC).luminance(state -> 0)); //TODO: make this proper class later
	@RegistryEntry("gritty_stone") public static final Block GRITTY_STONE = new Block(of(STONE));
	@RegistryEntry("leak") public static final Block LEAK = new Block(of(WATER));

	public static void init() {
		BLOCKS.register();
	}

	private static RegistrySupplier<Block> registerWithoutTab(String name, Supplier<Block> block) {
		return BLOCKS.register(name, block);
	}

	private static RegistrySupplier<Block> registerAncientFabric(DyeColor color) {
		RegistrySupplier<Block> block = register(color.getSerializedName() + "_ancient_fabric", () -> new AncientFabricBlock(color));
		ANCIENT_FABRIC_BLOCKS.put(color, block);
		return block;
	}

	private static RegistrySupplier<Block> registerFabric(DyeColor color) {
		RegistrySupplier<Block> block = register(color.getSerializedName() + "_fabric", () -> new AncientFabricBlock(color));
		FABRIC_BLOCKS.put(color, block);
		return block;
	}

//	@Environment(EnvType.CLIENT)
//	public static void initClient() {
//		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), ModBlocks.QUARTZ_DOOR, ModBlocks.GOLD_DOOR);
//		DoorData.DOORS.forEach(door -> BlockRenderLayerMap.INSTANCE.putBlock(door, RenderLayer.getCutout()));
//	}

	public static RegistrySupplier<Block> ancientFabricFromDye(DyeColor color) {
		return ANCIENT_FABRIC_BLOCKS.get(color);
	}

	public static RegistrySupplier<Block> fabricFromDye(DyeColor color) {
		return FABRIC_BLOCKS.get(color);
	}

	public static RegistrySupplier<Block> register(String name, Supplier<Block> block) {
		var supplier = BLOCKS.register(name, block);
		ModItems.register(name, properties -> new BlockItem(block.get(), properties));
		return supplier;
	}

	public static RegistrySupplier<Block> registerFence(String name, Block block) {
		return register(name, () -> new FenceBlock(of(block)));
	}

	public static RegistrySupplier<Block> registerFenceGate(String name, Block block) {
		return register(name, () -> new FenceGateBlock(of(block), WoodType.OAK)); // TODO: parameterize WoodType and BlockSetType
	}

	public static RegistrySupplier<Block> registerButton(String name, Block block) {
		return register(name, () -> new ButtonBlock(of(block).noCollission().strength(0.5F), BlockSetType.STONE, 20, false));
	}

	public static RegistrySupplier<Block> registerSlab(String name, Block block) {
		return register(name, () -> new SlabBlock(of(block)));
	}

	public static RegistrySupplier<Block> registerStairs(String name, Block block) {
		return register(name, () -> new StairBlock(block.defaultBlockState(), of(block)));
	}

	public static RegistrySupplier<Block> registerWall(String name, Block block) {
		return register(name, () -> new WallBlock(of(block)));
	}
	
	private static BlockBehaviour.Properties of(Material material, MaterialColor color) {
		return BlockBehaviour.Properties.of(material, color);
	}

	private static BlockBehaviour.Properties of(Material material) {
		return of(material);
	}

	private static BlockBehaviour.Properties of(Block block) {
		return BlockBehaviour.Properties.copy(block);
	}
}
