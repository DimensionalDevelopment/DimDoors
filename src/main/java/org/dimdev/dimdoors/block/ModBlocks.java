package org.dimdev.dimdoors.block;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import org.dimdev.dimdoors.Constants;
import org.dimdev.dimdoors.block.door.DimensionalTrapdoorBlock;
import org.dimdev.dimdoors.block.door.data.DoorData;

import static net.minecraft.world.level.block.Blocks.CLAY;
import static net.minecraft.world.level.block.Blocks.GRAVEL;
import static net.minecraft.world.level.block.Blocks.MUD;
import static net.minecraft.world.level.block.Blocks.OAK_LEAVES;
import static net.minecraft.world.level.block.Blocks.OAK_SAPLING;
import static net.minecraft.world.level.block.Blocks.STONE;
import static net.minecraft.world.level.block.Blocks.WATER;

public final class ModBlocks {
	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MODID);

	public static final Map<DyeColor, Block> FABRIC_BLOCKS = new HashMap<>();

	private static final Map<DyeColor, Block> ANCIENT_FABRIC_BLOCKS = new HashMap<>();

	public static final RegistryObject<Block> STONE_PLAYER = BLOCKS.register("stone_player", () -> register(new Block(BlockBehaviour.Properties.of(Material.STONE).strength(0.5F).noOcclusion())));

	public static final RegistryObject<Block> GOLD_DOOR = BLOCKS.register("gold_door", () -> register(new DoorBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.GOLD).strength(5.0F).requiresCorrectToolForDrops().noOcclusion(), SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN)));

	public static final RegistryObject<Block> STONE_DOOR = BLOCKS.register("stone_door", () -> register(new DoorBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WOOD).strength(5.0F).requiresCorrectToolForDrops().noOcclusion(), SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN)));

	public static final RegistryObject<Block> QUARTZ_DOOR = BLOCKS.register("quartz_door", () -> register(new DoorBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.QUARTZ).strength(5.0F).requiresCorrectToolForDrops().noOcclusion(), SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN)));

	public static final RegistryObject<Block> OAK_DIMENSIONAL_TRAPDOOR = BLOCKS.register("wood_dimensional_trapdoor", () -> register(new DimensionalTrapdoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_TRAPDOOR).lightLevel(state -> 10), SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.WOODEN_DOOR_OPEN)));

	public static final RegistryObject<Block> DIMENSIONAL_PORTAL = BLOCKS.register("dimensional_portal", () -> register(new DimensionalPortalBlock(BlockBehaviour.Properties.of(Material.AIR).noCollission().strength(-1.0F, 3600000.0F).noOcclusion().noLootTable().lightLevel(blockState -> 10))));

	public static final RegistryObject<Block> DETACHED_RIFT = BLOCKS.register("detached_rift", () -> register(new DetachedRiftBlock(BlockBehaviour.Properties.of(Material.AIR).strength(-1.0F, 3600000.0F).noCollission().noOcclusion())));

	public static final RegistryObject<Block> WHITE_FABRIC = BLOCKS.register("white_fabric", () -> registerFabric(DyeColor.WHITE));

	public static final RegistryObject<Block> ORANGE_FABRIC = BLOCKS.register("orange_fabric", () -> registerFabric(DyeColor.ORANGE));

	public static final RegistryObject<Block> MAGENTA_FABRIC = BLOCKS.register("magenta_fabric", () -> registerFabric(DyeColor.MAGENTA));

	public static final RegistryObject<Block> LIGHT_BLUE_FABRIC = BLOCKS.register("light_blue_fabric", () -> registerFabric(DyeColor.LIGHT_BLUE));

	public static final RegistryObject<Block> YELLOW_FABRIC = BLOCKS.register("yellow_fabric", () -> registerFabric(DyeColor.YELLOW));

	public static final RegistryObject<Block> LIME_FABRIC = BLOCKS.register("lime_fabric", () -> registerFabric(DyeColor.LIME));

	public static final RegistryObject<Block> PINK_FABRIC = BLOCKS.register("pink_fabric", () -> registerFabric(DyeColor.PINK));

	public static final RegistryObject<Block> GRAY_FABRIC = BLOCKS.register("gray_fabric", () -> registerFabric(DyeColor.GRAY));

	public static final RegistryObject<Block> LIGHT_GRAY_FABRIC = BLOCKS.register("light_gray_fabric", () -> registerFabric(DyeColor.LIGHT_GRAY));

	public static final RegistryObject<Block> CYAN_FABRIC = BLOCKS.register("cyan_fabric", () -> registerFabric(DyeColor.CYAN));

	public static final RegistryObject<Block> PURPLE_FABRIC = BLOCKS.register("purple_fabric", () -> registerFabric(DyeColor.PURPLE));

	public static final RegistryObject<Block> BLUE_FABRIC = BLOCKS.register("blue_fabric", () -> registerFabric(DyeColor.BLUE));

	public static final RegistryObject<Block> BROWN_FABRIC = BLOCKS.register("brown_fabric", () -> registerFabric(DyeColor.BROWN));

	public static final RegistryObject<Block> GREEN_FABRIC = BLOCKS.register("green_fabric", () -> registerFabric(DyeColor.GREEN));

	public static final RegistryObject<Block> RED_FABRIC = BLOCKS.register("red_fabric", () -> registerFabric(DyeColor.RED));

	public static final RegistryObject<Block> BLACK_FABRIC = BLOCKS.register("black_fabric", () -> registerFabric(DyeColor.BLACK));


	public static final RegistryObject<Block> WHITE_ANCIENT_FABRIC = BLOCKS.register("white_ancient_fabric", () -> registerAncientFabric(DyeColor.WHITE));

	public static final RegistryObject<Block> ORANGE_ANCIENT_FABRIC = BLOCKS.register("orange_ancient_fabric", () -> registerAncientFabric(DyeColor.ORANGE));

	public static final RegistryObject<Block> MAGENTA_ANCIENT_FABRIC = BLOCKS.register("magenta_ancient_fabric", () -> registerAncientFabric(DyeColor.MAGENTA));

	public static final RegistryObject<Block> LIGHT_BLUE_ANCIENT_FABRIC = BLOCKS.register("light_blue_ancient_fabric", () -> registerAncientFabric(DyeColor.LIGHT_BLUE));

	public static final RegistryObject<Block> YELLOW_ANCIENT_FABRIC = BLOCKS.register("yellow_ancient_fabric", () -> registerAncientFabric(DyeColor.YELLOW));

	public static final RegistryObject<Block> LIME_ANCIENT_FABRIC = BLOCKS.register("lime_ancient_fabric", () -> registerAncientFabric(DyeColor.LIME));

	public static final RegistryObject<Block> PINK_ANCIENT_FABRIC = BLOCKS.register("pink_ancient_fabric", () -> registerAncientFabric(DyeColor.PINK));

	public static final RegistryObject<Block> GRAY_ANCIENT_FABRIC = BLOCKS.register("gray_ancient_fabric", () -> registerAncientFabric(DyeColor.GRAY));

	public static final RegistryObject<Block> LIGHT_GRAY_ANCIENT_FABRIC = BLOCKS.register("light_gray_ancient_fabric", () -> registerAncientFabric(DyeColor.LIGHT_GRAY));

	public static final RegistryObject<Block> CYAN_ANCIENT_FABRIC = BLOCKS.register("cyan_ancient_fabric", () -> registerAncientFabric(DyeColor.CYAN));

	public static final RegistryObject<Block> PURPLE_ANCIENT_FABRIC = BLOCKS.register("purple_ancient_fabric", () -> registerAncientFabric(DyeColor.PURPLE));

	public static final RegistryObject<Block> BLUE_ANCIENT_FABRIC = BLOCKS.register("blue_ancient_fabric", () -> registerAncientFabric(DyeColor.BLUE));

	public static final RegistryObject<Block> BROWN_ANCIENT_FABRIC = BLOCKS.register("brown_ancient_fabric", () -> registerAncientFabric(DyeColor.BROWN));

	public static final RegistryObject<Block> GREEN_ANCIENT_FABRIC = BLOCKS.register("green_ancient_fabric", () -> registerAncientFabric(DyeColor.GREEN));

	public static final RegistryObject<Block> RED_ANCIENT_FABRIC = BLOCKS.register("red_ancient_fabric", () -> registerAncientFabric(DyeColor.RED));

	public static final RegistryObject<Block> BLACK_ANCIENT_FABRIC = BLOCKS.register("black_ancient_fabric", () -> registerAncientFabric(DyeColor.BLACK));
	private static final BlockBehaviour.Properties UNRAVELLED_FABRIC_BLOCK_SETTINGS = BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK).lightLevel(state -> 15).randomTicks().strength(0.3F, 0.3F);

	public static final RegistryObject<Block> ETERNAL_FLUID = BLOCKS.register("eternal_fluid", () -> register(new EternalFluidBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED).lightLevel(state -> 15))));

	public static final RegistryObject<Block> DECAYED_BLOCK = BLOCKS.register("decayed_block", () -> register(new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS)));

	public static final RegistryObject<Block> UNFOLDED_BLOCK = BLOCKS.register("unfolded_block", () -> register(new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS)));

	public static final RegistryObject<Block> UNWARPED_BLOCK = BLOCKS.register("unwarped_block", () -> register(new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS)));

	public static final RegistryObject<Block> UNRAVELLED_BLOCK = BLOCKS.register("unravelled_block", () -> register(new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS)));

	public static final RegistryObject<Block> UNRAVELLED_FABRIC = BLOCKS.register("unravelled_fabric", () -> register(new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS)));

	public static final RegistryObject<Block> MARKING_PLATE = BLOCKS.register("marking_plate", () -> register(new MarkingPlateBlock(BlockBehaviour.Properties.of(Material.METAL, DyeColor.BLACK).noOcclusion())));

	public static final RegistryObject<Block> SOLID_STATIC = BLOCKS.register("solid_static", () -> register(new UnravelledFabricBlock(BlockBehaviour.Properties.of(Material.STONE).strength(7, 25).randomTicks().requiresCorrectToolForDrops().sound(SoundType.SAND))));

	public static final RegistryObject<Block> TESSELATING_LOOM = BLOCKS.register("tesselating_loom", () -> register(new TesselatingLoomBlock(BlockBehaviour.Properties.copy(Blocks.LOOM))));

	public static final RegistryObject<Block> REALITY_SPONGE = BLOCKS.register("reality_sponge", () -> new RealitySpongeBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

	//Decay graph filler.
	public static final RegistryObject<Block> DRIFTWOOD_WOOD = BLOCKS.register("driftwood_wood", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_LIGHT_GRAY).strength(2.0F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> DRIFTWOOD_LOG = BLOCKS.register("driftwood_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_LIGHT_GRAY).strength(2.0F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> DRIFTWOOD_PLANKS = BLOCKS.register("driftwood_planks", () -> new Block(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_LIGHT_GRAY).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> DRIFTWOOD_LEAVES = BLOCKS.register("driftwood_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.copy(OAK_LEAVES)));
	public static final RegistryObject<Block> DRIFTWOOD_SAPLING = BLOCKS.register("driftwood_sapling", () -> new Block(BlockBehaviour.Properties.copy(OAK_SAPLING)));
	public static final RegistryObject<Block> DRIFTWOOD_FENCE = BLOCKS.register("driftwood_fence", () -> new FenceBlock(BlockBehaviour.Properties.of(Material.WOOD, DRIFTWOOD_PLANKS.get().defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> DRIFTWOOD_GATE = BLOCKS.register("driftwood_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.of(Material.WOOD, DRIFTWOOD_PLANKS.get().defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD), SoundEvents.FENCE_GATE_CLOSE, SoundEvents.FENCE_GATE_OPEN));
	public static final RegistryObject<Block> DRIFTWOOD_BUTTON = BLOCKS.register("driftwood_button", () -> new ButtonBlock(BlockBehaviour.Properties.of(Material.DECORATION, MaterialColor.COLOR_LIGHT_GRAY).noCollission().strength(0.5F), 20, true, SoundEvents.WOODEN_BUTTON_CLICK_OFF, SoundEvents.WOODEN_BUTTON_CLICK_ON));
	public static final RegistryObject<Block> DRIFTWOOD_SLAB = BLOCKS.register("driftwood_slab", () -> new SlabBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_LIGHT_GRAY)));
	public static final RegistryObject<Block> DRIFTWOOD_STAIRS = BLOCKS.register("driftwood_stairs", () -> new StairBlock(DRIFTWOOD_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.of(Material.WOOD,  MaterialColor.COLOR_LIGHT_GRAY)));
	public static final RegistryObject<Block> DRIFTWOOD_DOOR = BLOCKS.register("driftwood_door", () -> new DoorBlock(BlockBehaviour.Properties.of(Material.WOOD, DRIFTWOOD_PLANKS.get().defaultMaterialColor()).strength(3.0F).sound(SoundType.WOOD).noOcclusion(), SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.WOODEN_DOOR_OPEN));
	public static final RegistryObject<Block> DRIFTWOOD_TRAPDOOR = BLOCKS.register("driftwood_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.of(Material.WOOD, DRIFTWOOD_PLANKS.get().defaultMaterialColor()).strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn((state, world, pos, type) -> false), SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundEvents.WOODEN_TRAPDOOR_OPEN));

	public static final RegistryObject<Block> AMALGAM_BLOCK = BLOCKS.register("amalgam_block", () -> new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
	public static final RegistryObject<Block> AMALGAM_DOOR = BLOCKS.register("amalgam_door", () -> new DoorBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).noOcclusion(), SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN));
	public static final RegistryObject<Block> AMALGAM_TRAPDOOR = BLOCKS.register("amalgam_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).noOcclusion().isValidSpawn((state, world, pos, type) -> false), SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN));
	public static final RegistryObject<Block> RUST = BLOCKS.register("rust", () -> new Block(BlockBehaviour.Properties.of(Material.WOOD)));
	public static final RegistryObject<Block> AMALGAM_SLAB = BLOCKS.register("amalgam_slab", () -> createSlab(AMALGAM_BLOCK.get()));
	public static final RegistryObject<Block> AMALGAM_STAIRS = BLOCKS.register("amalgam_stairs", () -> createStairs(AMALGAM_BLOCK.get()));
	public static final RegistryObject<Block> AMALGAM_ORE = BLOCKS.register("amalgam_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));

	public static final RegistryObject<Block> CLOD_ORE = BLOCKS.register("clod_ore", () -> new Block(BlockBehaviour.Properties.of(Material.WOOD)));
	public static final RegistryObject<Block> CLOD_BLOCK = BLOCKS.register("clod_block", () -> new Block(BlockBehaviour.Properties.of(Material.WOOD)));

	public static final RegistryObject<Block> GRAVEL_FENCE = BLOCKS.register("gravel_fence", () -> createFence(GRAVEL));
	public static final RegistryObject<Block> GRAVEL_GATE = BLOCKS.register("gravel_gate", () -> createFenceGate(GRAVEL));
	public static final RegistryObject<Block> GRAVEL_BUTTON = BLOCKS.register("gravel_button", () -> createButton(GRAVEL));
	public static final RegistryObject<Block> GRAVEL_SLAB = BLOCKS.register("gravel_slab", () -> createSlab(GRAVEL));
	public static final RegistryObject<Block> GRAVEL_STAIRS = BLOCKS.register("gravel_stairs", () -> createStairs(GRAVEL));

	public static final RegistryObject<Block> DARK_SAND = BLOCKS.register("dark_sand", () -> new Block(BlockBehaviour.Properties.of(Material.SAND, MaterialColor.COLOR_BLACK).strength(0.5F).sound(SoundType.SAND)));
	public static final RegistryObject<Block> DARK_SAND_FENCE = BLOCKS.register("dark_sand_fence", () -> createFence(DARK_SAND.get()));
	public static final RegistryObject<Block> DARK_SAND_GATE = BLOCKS.register("dark_sand_gate", () -> createFenceGate(DARK_SAND.get()));
	public static final RegistryObject<Block> DARK_SAND_BUTTON = BLOCKS.register("dark_sand_button", () -> createButton(DARK_SAND.get()));
	public static final RegistryObject<Block> DARK_SAND_SLAB = BLOCKS.register("dark_sand_slab", () -> createSlab(DARK_SAND.get()));
	public static final RegistryObject<Block> DARK_SAND_STAIRS = BLOCKS.register("dark_sand_stairs", () -> createStairs(DARK_SAND.get()));

	public static final RegistryObject<Block> CLAY_FENCE = BLOCKS.register("clay_fence", () -> createFence(CLAY));
	public static final RegistryObject<Block> CLAY_GATE = BLOCKS.register("clay_gate", () -> createFenceGate(CLAY));
	public static final RegistryObject<Block> CLAY_BUTTON = BLOCKS.register("clay_button", () -> createButton(CLAY));
	public static final RegistryObject<Block> CLAY_SLAB = BLOCKS.register("clay_slab", () -> createSlab(CLAY));
	public static final RegistryObject<Block> CLAY_STAIRS = BLOCKS.register("clay_stairs", () -> createStairs(CLAY));

	public static final RegistryObject<Block> MUD_FENCE = BLOCKS.register("mud_fence", () -> createFence(MUD));
	public static final RegistryObject<Block> MUD_GATE = BLOCKS.register("mud_gate", () -> createFenceGate(MUD));
	public static final RegistryObject<Block> MUD_BUTTON = BLOCKS.register("mud_button", () -> createButton(MUD));
	public static final RegistryObject<Block> MUD_SLAB = BLOCKS.register("mud_slab", () -> createSlab(MUD));
	public static final RegistryObject<Block> MUD_STAIRS = BLOCKS.register("mud_stairs", () -> createStairs(MUD));

	public static final RegistryObject<Block> UNRAVELED_FENCE = BLOCKS.register("unraveled_fence", () -> createFence(UNRAVELLED_FABRIC.get()));
	public static final RegistryObject<Block> UNRAVELED_GATE = BLOCKS.register("unraveled_gate", () -> createFenceGate(UNRAVELLED_FABRIC.get()));
	public static final RegistryObject<Block> UNRAVELED_BUTTON = BLOCKS.register("unraveled_button", () -> createButton(UNRAVELLED_FABRIC.get()));
	public static final RegistryObject<Block> UNRAVELED_SLAB = BLOCKS.register("unraveled_slab", () -> createSlab(UNRAVELLED_FABRIC.get()));
	public static final RegistryObject<Block> UNRAVELED_STAIRS = BLOCKS.register("unraveled_stairs", () -> createStairs(UNRAVELLED_FABRIC.get()));

	public static final RegistryObject<Block> UNRAVELED_SPIKE = BLOCKS.register("unraveled_spike", () -> new Block(BlockBehaviour.Properties.copy(UNRAVELLED_FABRIC.get()).lightLevel(state -> 0)));
	public static final RegistryObject<Block> GRITTY_STONE = BLOCKS.register("gritty_stone", () -> new Block(BlockBehaviour.Properties.copy(STONE)));
	public static final RegistryObject<Block> LEAK = BLOCKS.register("leak", () -> new Block(BlockBehaviour.Properties.copy(WATER)));

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

	public static void init(IEventBus bus) {
		BLOCKS.register(bus);
//		DoorDataReader.read();
	}

	@Environment(Dist.CLIENT)
	public static void initClient() {
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), ModBlocks.QUARTZ_DOOR, ModBlocks.GOLD_DOOR);
		DoorData.DOORS.forEach(door -> BlockRenderLayerMap.INSTANCE.putBlock(door, RenderType.cutout()));
	}

	public static Block ancientFabricFromDye(DyeColor color) {
		return ANCIENT_FABRIC_BLOCKS.get(color);
	}

	public static Block fabricFromDye(DyeColor color) {
		return FABRIC_BLOCKS.get(color);
	}

	public static Block createFence(Block block) {
		return new FenceBlock(BlockBehaviour.Properties.copy(block));
	}

	public static Block createFenceGate(Block block) {
		return new FenceGateBlock(BlockBehaviour.Properties.copy(block), SoundEvents.FENCE_GATE_CLOSE, SoundEvents.FENCE_GATE_OPEN);
	}

	public static Block createButton(Block block) {
		return new ButtonBlock(BlockBehaviour.Properties.copy(block).noCollission().strength(0.5F), 20, false, SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON );
	}

	public static Block createSlab(Block block) {
		return new SlabBlock(BlockBehaviour.Properties.copy(block));
	}

	public static Block createStairs(Block block) {
		return new StairBlock(block.defaultBlockState(), BlockBehaviour.Properties.copy(block));
	}
}
