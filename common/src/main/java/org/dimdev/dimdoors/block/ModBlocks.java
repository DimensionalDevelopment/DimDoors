package org.dimdev.dimdoors.block;

import dev.architectury.core.block.ArchitecturyLiquidBlock;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.door.DimensionalTrapdoorBlock;
import org.dimdev.dimdoors.fluid.ModFluids;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static net.minecraft.world.level.block.Blocks.CLAY;
import static net.minecraft.world.level.block.Blocks.SAND;
import static net.minecraft.world.level.block.Blocks.STONE;
import static net.minecraft.world.level.block.Blocks.WATER;
import static net.minecraft.world.level.block.Blocks.*;
import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy;
import static net.minecraft.world.level.material.MaterialColor.*;
import static org.dimdev.dimdoors.forge.item.ModItems.DECAY;
import static org.dimdev.dimdoors.forge.item.ModItems.DIMENSIONAL_DOORS;

public final class ModBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(DimensionalDoors.MOD_ID, Registry.BLOCK_REGISTRY);
	public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(DimensionalDoors.MOD_ID, Registry.ITEM_REGISTRY);

	public static final Map<DyeColor, RegistrySupplier<Block>> FABRIC_BLOCKS = new HashMap<DyeColor, RegistrySupplier<Block>>();

	private static final Map<DyeColor, RegistrySupplier<Block>> ANCIENT_FABRIC_BLOCKS = new HashMap<DyeColor, RegistrySupplier<Block>>();

	public static final RegistrySupplier<Block> STONE_PLAYER = registerWithoutTabOrItem("stone_player", () -> new Block(copy(STONE).strength(0.5F).noOcclusion()));

	public static final RegistrySupplier<Block> GOLD_DOOR = register("gold_door", () -> new DoorBlock(copy(IRON_BLOCK).color(GOLD).strength(5.0F).requiresCorrectToolForDrops().noCollission()));

	public static final RegistrySupplier<Block> STONE_DOOR = register("stone_door", () -> new DoorBlock(copy(STONE).color(WOOD).strength(5.0F).requiresCorrectToolForDrops().noOcclusion()));

	public static final RegistrySupplier<Block> QUARTZ_DOOR = register("quartz_door", () -> new DoorBlock(copy(STONE).color(TERRACOTTA_WHITE).strength(5.0F).requiresCorrectToolForDrops().noOcclusion()));

	public static final RegistrySupplier<Block> OAK_DIMENSIONAL_TRAPDOOR = registerWithoutTabOrItem("wood_dimensional_trapdoor", () -> new DimensionalTrapdoorBlock(of(Blocks.OAK_TRAPDOOR).lightLevel(state -> 10)));

	public static final RegistrySupplier<Block> DIMENSIONAL_PORTAL = registerWithoutTab("dimensional_portal", () -> new DimensionalPortalBlock(BlockBehaviour.Properties.of(Material.AIR).noCollission().noLootTable().strength(-1.0F, 3600000.0F).noOcclusion().dropsLike(AIR).lightLevel(blockState -> 10)));

	public static final RegistrySupplier<Block> DETACHED_RIFT = registerWithoutTabOrItem("detached_rift", () -> new DetachedRiftBlock(BlockBehaviour.Properties.of(Material.AIR).noCollission().noLootTable().color(COLOR_BLACK).strength(-1.0F, 3600000.0F).noCollission().noOcclusion()));

	public static final RegistrySupplier<Block> WHITE_FABRIC = registerFabric(DyeColor.WHITE);

	public static final RegistrySupplier<Block> ORANGE_FABRIC = registerFabric(DyeColor.ORANGE);

	public static final RegistrySupplier<Block> MAGENTA_FABRIC = registerFabric(DyeColor.MAGENTA);

	public static final RegistrySupplier<Block> LIGHT_BLUE_FABRIC = registerFabric(DyeColor.LIGHT_BLUE);

	public static final RegistrySupplier<Block> YELLOW_FABRIC = registerFabric(DyeColor.YELLOW);

	public static final RegistrySupplier<Block> LIME_FABRIC = registerFabric(DyeColor.LIME);

	public static final RegistrySupplier<Block> PINK_FABRIC = registerFabric(DyeColor.PINK);

	public static final RegistrySupplier<Block> GRAY_FABRIC = registerFabric(DyeColor.GRAY);

	public static final RegistrySupplier<Block> LIGHT_GRAY_FABRIC = registerFabric(DyeColor.LIGHT_GRAY);

	public static final RegistrySupplier<Block> CYAN_FABRIC = registerFabric(DyeColor.CYAN);

	public static final RegistrySupplier<Block> PURPLE_FABRIC = registerFabric(DyeColor.PURPLE);

	public static final RegistrySupplier<Block> BLUE_FABRIC = registerFabric(DyeColor.BLUE);

	public static final RegistrySupplier<Block> BROWN_FABRIC = registerFabric(DyeColor.BROWN);

	public static final RegistrySupplier<Block> GREEN_FABRIC = registerFabric(DyeColor.GREEN);

	public static final RegistrySupplier<Block> RED_FABRIC = registerFabric(DyeColor.RED);

	public static final RegistrySupplier<Block> BLACK_FABRIC = registerFabric(DyeColor.BLACK);


	public static final RegistrySupplier<Block> WHITE_ANCIENT_FABRIC = registerAncientFabric(DyeColor.WHITE);

	public static final RegistrySupplier<Block> ORANGE_ANCIENT_FABRIC = registerAncientFabric(DyeColor.ORANGE);

	public static final RegistrySupplier<Block> MAGENTA_ANCIENT_FABRIC = registerAncientFabric(DyeColor.MAGENTA);

	public static final RegistrySupplier<Block> LIGHT_BLUE_ANCIENT_FABRIC = registerAncientFabric(DyeColor.LIGHT_BLUE);

	public static final RegistrySupplier<Block> YELLOW_ANCIENT_FABRIC = registerAncientFabric(DyeColor.YELLOW);

	public static final RegistrySupplier<Block> LIME_ANCIENT_FABRIC = registerAncientFabric(DyeColor.LIME);

	public static final RegistrySupplier<Block> PINK_ANCIENT_FABRIC = registerAncientFabric(DyeColor.PINK);

	public static final RegistrySupplier<Block> GRAY_ANCIENT_FABRIC = registerAncientFabric(DyeColor.GRAY);

	public static final RegistrySupplier<Block> LIGHT_GRAY_ANCIENT_FABRIC = registerAncientFabric(DyeColor.LIGHT_GRAY);

	public static final RegistrySupplier<Block> CYAN_ANCIENT_FABRIC = registerAncientFabric(DyeColor.CYAN);

	public static final RegistrySupplier<Block> PURPLE_ANCIENT_FABRIC = registerAncientFabric(DyeColor.PURPLE);

	public static final RegistrySupplier<Block> BLUE_ANCIENT_FABRIC = registerAncientFabric(DyeColor.BLUE);

	public static final RegistrySupplier<Block> BROWN_ANCIENT_FABRIC = registerAncientFabric(DyeColor.BROWN);

	public static final RegistrySupplier<Block> GREEN_ANCIENT_FABRIC = registerAncientFabric(DyeColor.GREEN);

	public static final RegistrySupplier<Block> RED_ANCIENT_FABRIC = registerAncientFabric(DyeColor.RED);

	public static final RegistrySupplier<Block> BLACK_ANCIENT_FABRIC = registerAncientFabric(DyeColor.BLACK);
	private static final BlockBehaviour.Properties UNRAVELLED_FABRIC_BLOCK_SETTINGS = copy(STONE).color(COLOR_BLACK).randomTicks().lightLevel(state -> 15).strength(0.3F, 0.3F);

	public static final RegistrySupplier<LiquidBlock> ETERNAL_FLUID = registerWithoutTabOrItem("eternal_fluid", () -> new EternalFluidBlock(copy(LAVA).color(COLOR_RED).lightLevel(state -> 15)));

	public static final RegistrySupplier<LiquidBlock> LEAK = registerWithoutTabOrItem("leak", () -> new ArchitecturyLiquidBlock(ModFluids.LEAK, copy(WATER)));

	public static final RegistrySupplier<Block> DECAYED_BLOCK = registerWithoutTabOrItem("decayed_block", () -> new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

	public static final RegistrySupplier<Block> UNFOLDED_BLOCK = registerWithoutTabOrItem("unfolded_block", () -> new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

	public static final RegistrySupplier<Block> UNWARPED_BLOCK = registerWithoutTabOrItem("unwarped_block", () -> new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

	public static final RegistrySupplier<Block> UNRAVELLED_BLOCK = registerWithoutTabOrItem("unravelled_block", () -> new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

	public static final RegistrySupplier<Block> UNRAVELLED_FABRIC = register("unravelled_fabric", () -> new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

	public static final RegistrySupplier<Block> SOLID_STATIC = register("solid_static", () -> new UnravelledFabricBlock(copy(STONE).strength(7, 25).randomTicks().requiresCorrectToolForDrops().sound(SoundType.SAND)));

	public static final RegistrySupplier<Block> TESSELATING_LOOM = register("tesselating_loom", () -> new TesselatingLoomBlock(of(LOOM)));

	public static final RegistrySupplier<Block> REALITY_SPONGE = register("reality_sponge", () -> new RealitySpongeBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));
	public static final RegistrySupplier<Block> LIMBO_AIR = registerWithoutTabOrItem("limbo_air", () -> new LimboAirBlock(BlockBehaviour.Properties.of(Material.AIR).randomTicks().noCollission().noLootTable().air()));

	//Decay graph filler.
	public static final RegistrySupplier<Block> DRIFTWOOD_WOOD = registerDecay("driftwood_wood", () -> new RotatedPillarBlock(copy(OAK_WOOD).color(COLOR_LIGHT_GRAY).strength(2.0F).sound(SoundType.WOOD)));
	public static final RegistrySupplier<Block> DRIFTWOOD_LOG = registerDecay("driftwood_log", () -> new RotatedPillarBlock(copy(OAK_WOOD).color(COLOR_LIGHT_GRAY).strength(2.0F).sound(SoundType.WOOD)));
	public static final RegistrySupplier<Block> DRIFTWOOD_PLANKS = registerDecay("driftwood_planks", () -> new Block(copy(OAK_WOOD).color(COLOR_LIGHT_GRAY).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	public static final RegistrySupplier<Block> DRIFTWOOD_LEAVES = registerDecay("driftwood_leaves", () -> new LeavesBlock(of(OAK_LEAVES)));
	public static final RegistrySupplier<Block> DRIFTWOOD_SAPLING = registerDecay("driftwood_sapling", () -> new Block(of(OAK_SAPLING)));
	public static final RegistrySupplier<Block> DRIFTWOOD_FENCE = registerFence("driftwood_fence", DRIFTWOOD_PLANKS);
	public static final RegistrySupplier<Block> DRIFTWOOD_GATE = registerFenceGate("driftwood_gate", DRIFTWOOD_PLANKS); // TODO: add driftwood wood type
	public static final RegistrySupplier<Block> DRIFTWOOD_BUTTON = registerButton("driftwood_button", DRIFTWOOD_PLANKS);
	public static final RegistrySupplier<Block> DRIFTWOOD_SLAB = registerSlab("driftwood_slab", DRIFTWOOD_PLANKS);
	public static final RegistrySupplier<Block> DRIFTWOOD_STAIRS = registerStairs("driftwood_stairs", DRIFTWOOD_PLANKS);
	public static final RegistrySupplier<Block> DRIFTWOOD_DOOR = registerDecay("driftwood_door", () -> new DoorBlock(copy(OAK_WOOD).color(COLOR_GRAY).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
	public static final RegistrySupplier<Block> DRIFTWOOD_TRAPDOOR = registerDecay("driftwood_trapdoor", () -> new TrapDoorBlock(copy(OAK_WOOD).color(COLOR_GRAY).strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn((state, world, pos, type) -> false)));

	public static final RegistrySupplier<Block> AMALGAM_BLOCK = registerDecay("amalgam_block", () -> new Block(copy(IRON_BLOCK).color(COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
	public static final RegistrySupplier<Block> AMALGAM_DOOR = registerDecay("amalgam_door", () -> new DoorBlock(copy(IRON_BLOCK).color(COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).noOcclusion()));
	public static final RegistrySupplier<Block> AMALGAM_TRAPDOOR = registerDecay("amalgam_trapdoor", () -> new TrapDoorBlock(copy(IRON_BLOCK).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).isValidSpawn((state, world, pos, type) -> false)));
	public static final RegistrySupplier<Block> RUST = registerDecay("rust", () -> new Block(copy(OAK_WOOD)));
	public static final RegistrySupplier<Block> AMALGAM_SLAB = registerSlab("amalgam_slab", AMALGAM_BLOCK);
	public static final RegistrySupplier<Block> AMALGAM_STAIRS = registerStairs("amalgam_stairs", AMALGAM_BLOCK);
	public static final RegistrySupplier<Block> AMALGAM_ORE = registerDecay("amalgam_ore", () -> new DropExperienceBlock(copy(STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));

	public static final RegistrySupplier<Block> CLOD_ORE = registerDecay("clod_ore", () -> new Block(copy(Blocks.AMETHYST_BLOCK)));
	public static final RegistrySupplier<Block> CLOD_BLOCK = registerDecay("clod_block", () -> new Block(copy(Blocks.AMETHYST_BLOCK)));

	public static final RegistrySupplier<Block> GRAVEL_FENCE = registerFence("gravel_fence", GRAVEL);
	public static final RegistrySupplier<Block> GRAVEL_BUTTON = registerButton("gravel_button", GRAVEL);
	public static final RegistrySupplier<Block> GRAVEL_SLAB = registerSlab("gravel_slab", GRAVEL);
	public static final RegistrySupplier<Block> GRAVEL_STAIRS = registerStairs("gravel_stairs", GRAVEL);
	public static final RegistrySupplier<Block> GRAVEL_WALL = registerWall("gravel_wall", GRAVEL);

	public static final RegistrySupplier<Block> DARK_SAND = register("dark_sand", () -> new Block(copy(SAND).color(COLOR_BLACK).strength(0.5F).sound(SoundType.SAND)));
	public static final RegistrySupplier<Block> DARK_SAND_FENCE = registerFence("dark_sand_fence", DARK_SAND);
	public static final RegistrySupplier<Block> DARK_SAND_BUTTON = registerButton("dark_sand_button", DARK_SAND);
	public static final RegistrySupplier<Block> DARK_SAND_SLAB = registerSlab("dark_sand_slab", DARK_SAND);
	public static final RegistrySupplier<Block> DARK_SAND_STAIRS = registerStairs("dark_sand_stairs", DARK_SAND);
	public static final RegistrySupplier<Block> DARK_SAND_WALL = registerWall("dark_sand_wall", DARK_SAND);

	public static final RegistrySupplier<Block> CLAY_FENCE = registerFence("clay_fence", CLAY);
	public static final RegistrySupplier<Block> CLAY_GATE = registerFenceGate("clay_gate", CLAY);
	public static final RegistrySupplier<Block> CLAY_BUTTON = registerButton("clay_button", CLAY);
	public static final RegistrySupplier<Block> CLAY_SLAB = registerSlab("clay_slab", CLAY);
	public static final RegistrySupplier<Block> CLAY_STAIRS = registerStairs("clay_stairs", CLAY);

	public static final RegistrySupplier<Block> CLAY_WALL = registerWall("clay_wall", CLAY);

	public static final RegistrySupplier<Block> MUD_FENCE = registerFence("mud_fence", MUD);
	public static final RegistrySupplier<Block> MUD_GATE = registerFenceGate("mud_gate", MUD);
	public static final RegistrySupplier<Block> MUD_BUTTON = registerButton("mud_button", MUD);
	public static final RegistrySupplier<Block> MUD_SLAB = registerSlab("mud_slab", MUD);
	public static final RegistrySupplier<Block> MUD_STAIRS = registerStairs("mud_stairs", MUD);

	public static final RegistrySupplier<Block> UNRAVELED_FENCE = registerFence("unraveled_fence", UNRAVELLED_FABRIC);
	public static final RegistrySupplier<Block> UNRAVELED_GATE = registerFenceGate("unraveled_gate", UNRAVELLED_FABRIC);
	public static final RegistrySupplier<Block> UNRAVELED_BUTTON = registerButton("unraveled_button", UNRAVELLED_FABRIC);
	public static final RegistrySupplier<Block> UNRAVELED_SLAB = registerSlab("unraveled_slab", UNRAVELLED_FABRIC);
	public static final RegistrySupplier<Block> UNRAVELED_STAIRS = registerStairs("unraveled_stairs", UNRAVELLED_FABRIC);

	public static final RegistrySupplier<Block> DEEPSLATE_SLAB = registerSlab("deepslate_slab", Blocks.DEEPSLATE);
	public static final RegistrySupplier<Block> DEEPSLATE_STAIRS = registerStairs("deepslate_stairs", Blocks.DEEPSLATE);
	public static final RegistrySupplier<Block> DEEPSLATE_WALL = registerWall("deepslate_wall", Blocks.DEEPSLATE);

	public static final RegistrySupplier<Block> RED_SAND_SLAB = registerSlab("red_sand_slab", Blocks.RED_SAND);
	public static final RegistrySupplier<Block> RED_SAND_STAIRS = registerStairs("red_sand_stairs", Blocks.RED_SAND);
	public static final RegistrySupplier<Block> RED_SAND_WALL = registerWall("red_sand_wall", Blocks.RED_SAND);

	public static final RegistrySupplier<Block> SAND_SLAB = registerSlab("sand_slab", SAND);
	public static final RegistrySupplier<Block> SAND_STAIRS = registerStairs("sand_stairs", SAND);
	public static final RegistrySupplier<Block> SAND_WALL = registerWall("sand_wall", SAND);

	public static final RegistrySupplier<Block> END_STONE_SLAB = registerSlab("end_stone_slab", Blocks.END_STONE);
	public static final RegistrySupplier<Block> END_STONE_STAIRS = registerStairs("end_stone_stairs", Blocks.END_STONE);
	public static final RegistrySupplier<Block> END_STONE_WALL = registerWall("end_stone_wall", Blocks.END_STONE);

 	public static final RegistrySupplier<Block> NETHERRACK_FENCE = registerFence("netherrack_fence", Blocks.NETHERRACK);
 	public static final RegistrySupplier<Block> NETHERRACK_SLAB = registerSlab("netherrack_slab", Blocks.NETHERRACK);
 	public static final RegistrySupplier<Block> NETHERRACK_STAIRS = registerStairs("netherrack_stairs", Blocks.NETHERRACK);
 	public static final RegistrySupplier<Block> NETHERRACK_WALL = registerWall("netherrack_wall", Blocks.NETHERRACK);

	public static final RegistrySupplier<Block> UNRAVELED_SPIKE = registerDecay("unraveled_spike", () -> new PointedDripstoneBlock(of(UNRAVELLED_FABRIC.get()).lightLevel(state -> 0))); //TODO: make this proper class later
	public static final RegistrySupplier<Block> GRITTY_STONE = registerDecay("gritty_stone", () -> new Block(of(STONE)));

	public static void init() {
		BLOCKS.register();
		BLOCK_ITEMS.register();
	}

	private static <T extends Block> RegistrySupplier<T> registerWithoutTabOrItem(String name, Supplier<T> block) {
		return BLOCKS.register(name, block);
	}

	private static RegistrySupplier<Block> registerAncientFabric(DyeColor color) {
		RegistrySupplier<Block> block = register(color.getSerializedName() + "_ancient_fabric", () -> new AncientFabricBlock(color));
		ANCIENT_FABRIC_BLOCKS.put(color, block);
		return block;
	}

	private static RegistrySupplier<Block> registerFabric(DyeColor color) {
		RegistrySupplier<Block> block = register(color.getSerializedName() + "_fabric", () -> new FabricBlock(color));
		FABRIC_BLOCKS.put(color, block);
		return block;
	}

	@Environment(EnvType.CLIENT)
	public static void initClient() {
		RenderTypeRegistry.register(RenderType.cutout(), ModBlocks.QUARTZ_DOOR.get(), ModBlocks.GOLD_DOOR.get(), ModBlocks.DRIFTWOOD_LEAVES.get(), ModBlocks.UNRAVELED_SPIKE.get(), ModBlocks.DRIFTWOOD_DOOR.get());
	}

	public static RegistrySupplier<Block> ancientFabricFromDye(DyeColor color) {
		return ANCIENT_FABRIC_BLOCKS.get(color);
	}

	public static RegistrySupplier<Block> fabricFromDye(DyeColor color) {
		return FABRIC_BLOCKS.get(color);
	}

	public static <T extends Block> RegistrySupplier<T> register(String name, Supplier<T> block) {
		var supplier = BLOCKS.register(name, block);
		BLOCK_ITEMS.register(name, () -> new BlockItem(supplier.get(), new Item.Properties().tab(DIMENSIONAL_DOORS)));

		return supplier;
	}

	public static <T extends Block> RegistrySupplier<T> registerDecay(String name, Supplier<T> block) {
		var supplier = BLOCKS.register(name, block);
		BLOCK_ITEMS.register(name, () -> new BlockItem(supplier.get(), new Item.Properties().tab(DECAY)));

		return supplier;
	}

	public static <T extends Block> RegistrySupplier<T> registerWithoutTab(String name, Supplier<T> block) {
		var supplier = BLOCKS.register(name, block);
		BLOCK_ITEMS.register(name, () -> new BlockItem(supplier.get(), new Item.Properties()));

		return supplier;
	}

	public static RegistrySupplier<Block> registerFence(String name, Block block) {
		return registerDecay(name, () -> new FenceBlock(of(block)));
	}

	public static RegistrySupplier<Block> registerFence(String name, RegistrySupplier<Block> block) {
		return registerDecay(name, () -> new FenceBlock(of(block.get())));
	}

	public static RegistrySupplier<Block> registerFenceGate(String name, Block block) {
		return registerDecay(name, () -> new FenceGateBlock(of(block))); // TODO: parameterize WoodType and BlockSetType
	}

	public static RegistrySupplier<Block> registerFenceGate(String name, RegistrySupplier<Block> block) {
		return registerDecay(name, () -> new FenceGateBlock(of(block.get()))); // TODO: parameterize WoodType and BlockSetType
	}

	public static RegistrySupplier<Block> registerButton(String name, Block block) {
		return registerButton(name, () -> block);
	}

	public static RegistrySupplier<Block> registerButton(String name, Supplier<Block> block) {
		return registerDecay(name, () -> new ButtonBlock(false, of(block.get()).noCollission().strength(0.5F)) {
			@Override
			protected SoundEvent getSound(boolean isOn) {
				return isOn ? SoundEvents.STONE_BUTTON_CLICK_ON : SoundEvents.STONE_BUTTON_CLICK_OFF;
			}
		});
	}

	public static RegistrySupplier<Block> registerSlab(String name, Block block) {
		return registerDecay(name, () -> new SlabBlock(of(block)));
	}

	public static RegistrySupplier<Block> registerSlab(String name, RegistrySupplier<Block> block) {
		return registerDecay(name, () -> new SlabBlock(of(block.get())));
	}

	public static RegistrySupplier<Block> registerStairs(String name, Block block) {
		return registerDecay(name, () -> new StairBlock(block.defaultBlockState(), of(block)));
	}

	public static RegistrySupplier<Block> registerStairs(String name, RegistrySupplier<Block> block) {
		return registerDecay(name, () -> {
			var b = block.get();
			return new StairBlock(b.defaultBlockState(), of(b));
		});
	}

	public static RegistrySupplier<Block> registerWall(String name, Block block) {
		return registerDecay(name, () -> new WallBlock(of(block)));
	}

	public static RegistrySupplier<Block> registerWall(String name, RegistrySupplier<Block> block) {
		return registerDecay(name, () -> new WallBlock(of(block.get())));
	}
	
//	private static BlockBehaviour.Properties of(Material material, MaterialColor color) {
//		return BlockBehaviour.Properties.of(material, color);
//	}
//
//	private static BlockBehaviour.Properties of(Material material) {
//		return BlockBehaviour.Properties.of(material);
//	}
//
//	private static BlockBehaviour.Properties of(Material material, DyeColor dyeColor) {
//		return BlockBehaviour.Properties.of(material, dyeColor);
//	}

	private static BlockBehaviour.Properties of(Block block) {
		return copy(block);
	}
}
