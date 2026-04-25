package org.dimdev.dimdoors.block;

import dev.architectury.core.block.ArchitecturyLiquidBlock;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.fluid.ModFluids;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static net.minecraft.world.level.block.Blocks.CLAY;
import static net.minecraft.world.level.block.Blocks.SAND;
import static net.minecraft.world.level.block.Blocks.STONE;
import static net.minecraft.world.level.block.Blocks.WATER;
import static net.minecraft.world.level.block.Blocks.*;
import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy;
import static net.minecraft.world.level.material.MapColor.*;
import static org.dimdev.dimdoors.item.ModItems.DECAY;
import static org.dimdev.dimdoors.item.ModItems.DIMENSIONAL_DOORS;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.BLOCK);
    public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.ITEM);

    public static final Map<DyeColor, RegistrySupplier<Block>> FABRIC_BLOCKS = new HashMap<DyeColor, RegistrySupplier<Block>>();

    private static final Map<DyeColor, RegistrySupplier<Block>> ANCIENT_FABRIC_BLOCKS = new HashMap<DyeColor, RegistrySupplier<Block>>();

    public static final RegistrySupplier<Block> STONE_PLAYER = registerWithoutTabOrItem("stone_player", () -> new Block(ofFullCopy(STONE).strength(0.5F).noOcclusion()));

    public static final RegistrySupplier<Block> GOLD_DOOR = register("gold_door", () -> new DoorBlock(BlockSetType.GOLD, ofFullCopy(GOLD_BLOCK).strength(5.0F).requiresCorrectToolForDrops()));

    public static final RegistrySupplier<Block> STONE_DOOR = register("stone_door", () -> new DoorBlock( BlockSetType.IRON, ofFullCopy(STONE).mapColor(WOOD).strength(5.0F).requiresCorrectToolForDrops()));

    public static final RegistrySupplier<Block> QUARTZ_DOOR = register("quartz_door", () -> new DoorBlock(BlockSetType.IRON, ofFullCopy(QUARTZ_BLOCK).strength(5.0F).requiresCorrectToolForDrops()));

//    public static final RegistrySupplier<Block> OAK_DIMENSIONAL_TRAPDOOR = registerWithoutTabOrItem("wood_dimensional_trapdoor", () -> new DimensionalTrapdoorBlock(of(Blocks.OAK_TRAPDOOR).lightLevel(state -> 10), BlockSetType.OAK));

    public static final RegistrySupplier<Block> DIMENSIONAL_PORTAL = registerWithoutTab("dimensional_portal", () -> new DimensionalPortalBlock(BlockBehaviour.Properties.of().noCollission().noLootTable().strength(-1.0F, 3600000.0F).noOcclusion().dropsLike(AIR).lightLevel(blockState -> 10)));

    public static final RegistrySupplier<Block> DETACHED_RIFT = registerWithoutTabOrItem("detached_rift", () -> new DetachedRiftBlock(BlockBehaviour.Properties.of().noCollission().noLootTable().mapColor(COLOR_BLACK).strength(-1.0F, 3600000.0F).noCollission().noOcclusion()));

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
    private static final BlockBehaviour.Properties UNRAVELLED_FABRIC_BLOCK_SETTINGS = ofFullCopy(STONE).mapColor(COLOR_BLACK).randomTicks().lightLevel(state -> 15).strength(0.3F, 0.3F);

    public static final RegistrySupplier<LiquidBlock> ETERNAL_FLUID = registerWithoutTabOrItem("eternal_fluid", () -> new EternalFluidBlock(ofFullCopy(LAVA).mapColor(COLOR_RED).lightLevel(state -> 15)));

    public static final RegistrySupplier<LiquidBlock> LEAK = registerWithoutTabOrItem("leak", () -> new ArchitecturyLiquidBlock(ModFluids.LEAK, ofFullCopy(WATER)));

    public static final RegistrySupplier<Block> DECAYED_BLOCK = registerWithoutTabOrItem("decayed_block", () -> new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

    public static final RegistrySupplier<Block> UNFOLDED_BLOCK = registerWithoutTabOrItem("unfolded_block", () -> new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

    public static final RegistrySupplier<Block> UNWARPED_BLOCK = registerWithoutTabOrItem("unwarped_block", () -> new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

    public static final RegistrySupplier<Block> UNRAVELLED_BLOCK = registerWithoutTabOrItem("unravelled_block", () -> new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

    public static final RegistrySupplier<Block> UNRAVELLED_FABRIC = register("unravelled_fabric", () -> new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

    public static final RegistrySupplier<Block> MARKING_PLATE = registerWithoutTabOrItem("marking_plate", () -> new Block(ofFullCopy(IRON_BLOCK).mapColor(DyeColor.BLACK).noOcclusion()));

    public static final RegistrySupplier<Block> SOLID_STATIC = register("solid_static", () -> new UnravelledFabricBlock(ofFullCopy(STONE).strength(7, 25).randomTicks().requiresCorrectToolForDrops().sound(SoundType.SAND)));

    public static final RegistrySupplier<Block> TESSELATING_LOOM = register("tesselating_loom", () -> new TesselatingLoomBlock(of(LOOM)));

    public static final RegistrySupplier<Block> REALITY_SPONGE = register("reality_sponge", () -> new RealitySpongeBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));
    public static final RegistrySupplier<Block> LIMBO_AIR = registerWithoutTabOrItem("limbo_air", () -> new LimboAirBlock(BlockBehaviour.Properties.of().randomTicks().replaceable().noCollission().noLootTable().air()));

    //Decay graph filler.

    public static final RegistrySupplier<Block> CLOD_ORE = registerDecay("clod_ore", () -> new Block(ofFullCopy(Blocks.AMETHYST_BLOCK)));
    public static final RegistrySupplier<Block> CLOD_BLOCK = registerDecay("clod_block", () -> new Block(ofFullCopy(Blocks.AMETHYST_BLOCK)));

    public static final RegistrySupplier<Block> AMALGAM_BLOCK = registerDecay("amalgam_block", () -> new Block(ofFullCopy(IRON_BLOCK).mapColor(COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
    public static final RegistrySupplier<Block> AMALGAM_DOOR = registerDecay("amalgam_door", () -> new DoorBlock(BlockSetType.IRON, ofFullCopy(IRON_BLOCK).mapColor(COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).noOcclusion()));
    public static final RegistrySupplier<Block> AMALGAM_TRAPDOOR = registerDecay("amalgam_trapdoor", () -> new TrapDoorBlock(BlockSetType.IRON, ofFullCopy(IRON_BLOCK).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).isValidSpawn((state, world, pos, type) -> false)));
    public static final RegistrySupplier<Block> AMALGAM_SLAB = registerDecay("amalgam_slab", () -> new SlabBlock(of(AMALGAM_BLOCK.get())));
    public static final RegistrySupplier<Block> AMALGAM_STAIRS = registerDecay("amalgam_stairs", () -> new StairBlock(AMALGAM_BLOCK.get().defaultBlockState(), of(AMALGAM_BLOCK.get())));
    public static final RegistrySupplier<Block> AMALGAM_ORE = registerDecay("amalgam_ore", () -> new DropExperienceBlock(ConstantInt.of(1), ofFullCopy(STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));

    public static final RegistrySupplier<Block> RUST = registerDecay("rust", () -> new Block(ofFullCopy(OAK_WOOD)));

    public static final RegistrySupplier<Block> DRIFTWOOD_WOOD = registerDecay("driftwood_wood", () -> new RotatedPillarBlock(ofFullCopy(OAK_WOOD).mapColor(COLOR_LIGHT_GRAY).strength(2.0F).sound(SoundType.WOOD)));
    public static final RegistrySupplier<Block> DRIFTWOOD_LOG = registerDecay("driftwood_log", () -> new RotatedPillarBlock(ofFullCopy(OAK_WOOD).mapColor(COLOR_LIGHT_GRAY).strength(2.0F).sound(SoundType.WOOD)));
    public static final RegistrySupplier<Block> DRIFTWOOD_PLANKS = registerDecay("driftwood_planks", () -> new Block(ofFullCopy(OAK_WOOD).mapColor(COLOR_LIGHT_GRAY).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistrySupplier<Block> DRIFTWOOD_LEAVES = registerDecay("driftwood_leaves", () -> new LeavesBlock(of(OAK_LEAVES)));
    public static final RegistrySupplier<Block> DRIFTWOOD_SAPLING = registerDecay("driftwood_sapling", () -> new DriftwoodSaplingBlock(of(OAK_SAPLING)));
    public static final RegistrySupplier<Block> DRIFTWOOD_FENCE = registerDecay("driftwood_fence", () -> new FenceBlock(of(DRIFTWOOD_PLANKS.get())));
    public static final RegistrySupplier<Block> DRIFTWOOD_GATE = registerDecay("driftwood_gate", () -> new FenceGateBlock(WoodType.OAK, of(DRIFTWOOD_PLANKS.get())));
    public static final RegistrySupplier<Block> DRIFTWOOD_BUTTON = registerDecay("driftwood_button", () -> new ButtonBlock(BlockSetType.STONE, 20, of(DRIFTWOOD_PLANKS.get()).noCollission().strength(0.5F)));
    public static final RegistrySupplier<Block> DRIFTWOOD_SLAB = registerDecay("driftwood_slab", () -> new SlabBlock(of(DRIFTWOOD_PLANKS.get())));
    public static final RegistrySupplier<Block> DRIFTWOOD_STAIRS = registerDecay("driftwood_stairs", () -> new StairBlock(DRIFTWOOD_PLANKS.get().defaultBlockState(), of(DRIFTWOOD_PLANKS.get())));
    public static final RegistrySupplier<Block> DRIFTWOOD_DOOR = registerDecay("driftwood_door", () -> new DoorBlock(BlockSetType.OAK, ofFullCopy(OAK_WOOD).mapColor(COLOR_GRAY).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final RegistrySupplier<Block> DRIFTWOOD_TRAPDOOR = registerDecay("driftwood_trapdoor", () -> new TrapDoorBlock(BlockSetType.OAK, ofFullCopy(OAK_WOOD).mapColor(COLOR_GRAY).strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn((state, world, pos, type) -> false)));

    public static final RegistrySupplier<Block> DARK_SAND = registerDecay("dark_sand", () -> new Block(ofFullCopy(SAND).mapColor(COLOR_BLACK).strength(0.5F).sound(SoundType.SAND)));
    public static final RegistrySupplier<Block> PALE_SAND = registerDecay("pale_sand", () -> new ColoredFallingBlock(new ColorRGBA(0xFFF2E9D1), ofFullCopy(SAND).mapColor(COLOR_LIGHT_GRAY).strength(0.5F).sound(SoundType.SAND)));
    public static final RegistrySupplier<Block> DARK_SAND_LAYER = registerDecay("dark_sand_layer", () -> new CarpetBlock(ofFullCopy(MOSS_CARPET).mapColor(COLOR_BLACK).sound(SoundType.SAND)));
    public static final RegistrySupplier<Block> LINT_LAYER = registerDecay("lint_layer", () -> new CarpetBlock(ofFullCopy(MOSS_CARPET).mapColor(COLOR_LIGHT_GRAY)));
    public static final RegistrySupplier<Block> STONE_SLAB = registerDecay("stone_slab", () -> new SlabBlock(of(STONE)));
    public static final RegistrySupplier<Block> STONE_STAIRS = registerDecay("stone_stairs", () -> new StairBlock(STONE.defaultBlockState(), of(STONE)));
    public static final RegistrySupplier<Block> STONE_WALL = registerDecay("stone_wall", () -> new WallBlock(of(STONE)));

    public record DecayGroupSet(
            RegistrySupplier<Block> fence,
            RegistrySupplier<Block> gate,
            RegistrySupplier<Block> button,
            RegistrySupplier<Block> slab,
            RegistrySupplier<Block> stairs,
            RegistrySupplier<Block> wall
    ) {
        public static final List<DecayGroupSet> SETS = new ArrayList<>();

        public static DecayGroupSet create(String name, Supplier<Block> block, Supplier<BlockBehaviour.Properties> properites) {
            var set = new DecayGroupSet(
                    registerDecay(name + "_fence", () -> new FenceBlock(properites.get())),
                    registerDecay(name + "_gate", () -> new FenceGateBlock(WoodType.OAK, properites.get())),
                    registerDecay(name + "_button", () ->  new ButtonBlock(BlockSetType.STONE, 20, properites.get().noCollission().strength(0.5F))),
                    registerDecay(name + "_slab", () -> new SlabBlock(properites.get())),
                    registerDecay(name + "_stairs", () -> new StairBlock(block.get().defaultBlockState(), properites.get())),
                    registerDecay(name + "_wall", () -> new WallBlock(properites.get()))
            );

            SETS.add(set);

            return set;
        }

        public static DecayGroupSet create(String name, Supplier<Block> block) {
            return create(name, block, () -> of(block.get()));
        }

        public static DecayGroupSet create(String name, Block block) {

            return create(name, () -> block, () -> of(block));
        }
    }

    public static final DecayGroupSet GRAVEL_SET = DecayGroupSet.create("gravel", GRAVEL);

    public static final DecayGroupSet DARK_SAND_SET = DecayGroupSet.create("dark_sand", DARK_SAND);

    public static final DecayGroupSet CLAY_SET = DecayGroupSet.create("clay", CLAY);

    public static final DecayGroupSet TERRACOTTA_SET = DecayGroupSet.create("terracotta", TERRACOTTA);

    public static final DecayGroupSet WHITE_TERRACOTTA_SET = DecayGroupSet.create("white_terracotta", Blocks.WHITE_TERRACOTTA);
    public static final DecayGroupSet WHITE_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("white_glazed_terracotta", Blocks.WHITE_GLAZED_TERRACOTTA);
    public static final DecayGroupSet ORANGE_TERRACOTTA_SET = DecayGroupSet.create("orange_terracotta", Blocks.ORANGE_TERRACOTTA);
    public static final DecayGroupSet ORANGE_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("orange_glazed_terracotta", Blocks.ORANGE_GLAZED_TERRACOTTA);
    public static final DecayGroupSet MAGENTA_TERRACOTTA_SET = DecayGroupSet.create("magenta_terracotta", Blocks.MAGENTA_TERRACOTTA);
    public static final DecayGroupSet MAGENTA_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("magenta_glazed_terracotta", Blocks.MAGENTA_GLAZED_TERRACOTTA);
    public static final DecayGroupSet LIGHT_BLUE_TERRACOTTA_SET = DecayGroupSet.create("light_blue_terracotta", Blocks.LIGHT_BLUE_TERRACOTTA);
    public static final DecayGroupSet LIGHT_BLUE_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("light_blue_glazed_terracotta", Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA);
    public static final DecayGroupSet YELLOW_TERRACOTTA_SET = DecayGroupSet.create("yellow_terracotta", Blocks.YELLOW_TERRACOTTA);
    public static final DecayGroupSet YELLOW_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("yellow_glazed_terracotta", Blocks.YELLOW_GLAZED_TERRACOTTA);
    public static final DecayGroupSet LIME_TERRACOTTA_SET = DecayGroupSet.create("lime_terracotta", Blocks.LIME_TERRACOTTA);
    public static final DecayGroupSet LIME_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("lime_glazed_terracotta", Blocks.LIME_GLAZED_TERRACOTTA);
    public static final DecayGroupSet PINK_TERRACOTTA_SET = DecayGroupSet.create("pink_terracotta", Blocks.PINK_TERRACOTTA);
    public static final DecayGroupSet PINK_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("pink_glazed_terracotta", Blocks.PINK_GLAZED_TERRACOTTA);
    public static final DecayGroupSet GRAY_TERRACOTTA_SET = DecayGroupSet.create("gray_terracotta", Blocks.GRAY_TERRACOTTA);
    public static final DecayGroupSet GRAY_GLAZED_TERRACOTTASET = DecayGroupSet.create("gray_glazed_terracott", Blocks.GRAY_GLAZED_TERRACOTTA);
    public static final DecayGroupSet LIGHT_GRAY_TERRACOTTA_SET = DecayGroupSet.create("light_gray_terr", Blocks.LIGHT_GRAY_TERRACOTTA);
    public static final DecayGroupSet LIGHT_GRAY_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("light_gray_glazed_terracott", Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA);
    public static final DecayGroupSet CYAN_TERRACOTTA_SET = DecayGroupSet.create("cyan_terracotta", Blocks.CYAN_TERRACOTTA);
    public static final DecayGroupSet CYAN_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("cyan_glazed_terracotta", Blocks.CYAN_GLAZED_TERRACOTTA);
    public static final DecayGroupSet PURPLE_TERRACOTTA_SET = DecayGroupSet.create("purple_terracotta", Blocks.PURPLE_TERRACOTTA);
    public static final DecayGroupSet PURPLE_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("purple_glazed_terracotta", Blocks.PURPLE_GLAZED_TERRACOTTA);
    public static final DecayGroupSet BLUE_TERRACOTTA_SET = DecayGroupSet.create("blue_terracotta", Blocks.BLUE_TERRACOTTA);
    public static final DecayGroupSet BLUE_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("blue_glazed_terracotta", Blocks.BLUE_GLAZED_TERRACOTTA);
    public static final DecayGroupSet BROWN_TERRACOTTA_SET = DecayGroupSet.create("brown_terracotta", Blocks.BROWN_TERRACOTTA);
    public static final DecayGroupSet BROWN_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("brown_glazed_terracotta", Blocks.BROWN_GLAZED_TERRACOTTA);
    public static final DecayGroupSet GREEN_TERRACOTTA_SET = DecayGroupSet.create("green_terracotta", Blocks.GREEN_TERRACOTTA);
    public static final DecayGroupSet GREEN_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("green_glazed_terracotta", Blocks.GREEN_GLAZED_TERRACOTTA);
    public static final DecayGroupSet RED_TERRACOTTA_SET = DecayGroupSet.create("red_terracotta", Blocks.RED_TERRACOTTA);
    public static final DecayGroupSet RED_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("red_glazed_terracotta", Blocks.RED_GLAZED_TERRACOTTA);
    public static final DecayGroupSet BLACK_TERRACOTTA_SET = DecayGroupSet.create("black_terracotta", Blocks.BLACK_TERRACOTTA);
    public static final DecayGroupSet BLACK_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("black_glazed_terracotta", Blocks.BLACK_GLAZED_TERRACOTTA);

    public static final DecayGroupSet MUD_SET = DecayGroupSet.create("mud", () -> MUD, () -> of(MUD).isViewBlocking((blockState, blockGetter, blockPos) -> false).isSuffocating((blockState, blockGetter, blockPos) -> false));
    public static final DecayGroupSet UNRAVELED_SET = DecayGroupSet.create("unraveled", UNRAVELLED_FABRIC);
    public static final DecayGroupSet DEEPSLATE_SET = DecayGroupSet.create("deepslate", Blocks.DEEPSLATE);
    public static final DecayGroupSet RED_SAND_SET = DecayGroupSet.create("red_sand", Blocks.RED_SAND);
    public static final DecayGroupSet SAND_SET = DecayGroupSet.create("sand", Blocks.SAND);
    public static final DecayGroupSet END_STONE_SET = DecayGroupSet.create("end_stone", Blocks.END_STONE);
    public static final DecayGroupSet NETHERRACK_SET = DecayGroupSet.create("netherrack", Blocks.NETHERRACK);

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
    RenderTypeRegistry.register(RenderType.cutout(), ModBlocks.QUARTZ_DOOR.get(), ModBlocks.GOLD_DOOR.get(), ModBlocks.DRIFTWOOD_LEAVES.get(), ModBlocks.DRIFTWOOD_SAPLING.get(), ModBlocks.DRIFTWOOD_DOOR.get(), ModBlocks.DRIFTWOOD_TRAPDOOR.get(), ModBlocks.UNRAVELED_SPIKE.get(), ModBlocks.DRIFTWOOD_DOOR.get());
    }

    public static RegistrySupplier<Block> ancientFabricFromDye(DyeColor color) {
    return ANCIENT_FABRIC_BLOCKS.get(color);
    }

    public static RegistrySupplier<Block> fabricFromDye(DyeColor color) {
    return FABRIC_BLOCKS.get(color);
    }

    public static <T extends Block> RegistrySupplier<T> register(String name, Supplier<T> block) {
    var supplier = BLOCKS.register(name, block);
    BLOCK_ITEMS.register(name, () -> new BlockItem(supplier.get(), new Item.Properties().arch$tab(DIMENSIONAL_DOORS)));

    return supplier;
    }

    public static <T extends Block> RegistrySupplier<T> registerDecay(String name, Supplier<T> block) {
    var supplier = BLOCKS.register(name, block);
    BLOCK_ITEMS.register(name, () -> new BlockItem(supplier.get(), new Item.Properties().arch$tab(DECAY)));

    return supplier;
    }

    public static <T extends Block> RegistrySupplier<T> registerWithoutTab(String name, Supplier<T> block) {
    var supplier = BLOCKS.register(name, block);
    BLOCK_ITEMS.register(name, () -> new BlockItem(supplier.get(), new Item.Properties()));

    return supplier;
    }

    private static BlockBehaviour.Properties of(Block block) {
    return ofFullCopy(block);
    }
}
