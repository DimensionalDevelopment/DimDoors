package org.dimdev.dimdoors.block;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.door.DialingDoor;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.client.DimensionalDoorsClient;
import org.dimdev.dimdoors.fluid.ModFluids;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import static net.minecraft.world.level.block.Blocks.*;
import static net.minecraft.world.level.block.Blocks.CLAY;
import static net.minecraft.world.level.block.Blocks.SAND;
import static net.minecraft.world.level.block.Blocks.STONE;
import static net.minecraft.world.level.block.Blocks.WATER;
import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy;
import static net.minecraft.world.level.material.MapColor.*;
import static org.dimdev.dimdoors.item.ModItems.DECAY;
import static org.dimdev.dimdoors.item.ModItems.DIMENSIONAL_DOORS;

public final class ModBlocks {
    public static final Map<DyeColor, Block> FABRIC_BLOCKS = new HashMap<>();

    private static final Map<DyeColor, Block> ANCIENT_FABRIC_BLOCKS = new HashMap<DyeColor, Block>();

    public static final Block STONE_PLAYER = registerWithoutTabOrItem("stone_player", new Block(ofFullCopy(STONE).strength(0.5F).noOcclusion()));

    public static final Block GOLD_DOOR = register("gold_door", new ModDoorBlock(BlockSetType.GOLD, ofFullCopy(GOLD_BLOCK).strength(5.0F).requiresCorrectToolForDrops()));

    public static final Block STONE_DOOR = register("stone_door", new ModDoorBlock(BlockSetType.IRON, ofFullCopy(STONE).mapColor(WOOD).strength(5.0F).requiresCorrectToolForDrops()));

    public static final Block QUARTZ_DOOR = register("quartz_door", new ModDoorBlock(BlockSetType.IRON, ofFullCopy(QUARTZ_BLOCK).strength(5.0F).requiresCorrectToolForDrops()));

    public static final Block DIMENSIONAL_PORTAL = registerWithoutTab("dimensional_portal", new DimensionalPortalBlock(BlockBehaviour.Properties.of().noLootTable().strength(-1.0F, 3600000.0F).noOcclusion().dropsLike(AIR).lightLevel(blockState -> 10)));

    public static final Block DETACHED_RIFT = registerWithoutTabOrItem("detached_rift", new DetachedRiftBlock(BlockBehaviour.Properties.of().noCollission().noLootTable().mapColor(COLOR_BLACK).strength(-1.0F, 3600000.0F)));

    public static final Block DIALING_DOOR = register("dialing_door", new DialingDoor(ofFullCopy(GOLD_BLOCK).requiresCorrectToolForDrops(), BlockSetType.IRON));

    public static final Block WHITE_FABRIC = registerFabric(DyeColor.WHITE);

    public static final Block ORANGE_FABRIC = registerFabric(DyeColor.ORANGE);

    public static final Block MAGENTA_FABRIC = registerFabric(DyeColor.MAGENTA);

    public static final Block LIGHT_BLUE_FABRIC = registerFabric(DyeColor.LIGHT_BLUE);

    public static final Block YELLOW_FABRIC = registerFabric(DyeColor.YELLOW);

    public static final Block LIME_FABRIC = registerFabric(DyeColor.LIME);

    public static final Block PINK_FABRIC = registerFabric(DyeColor.PINK);

    public static final Block GRAY_FABRIC = registerFabric(DyeColor.GRAY);

    public static final Block LIGHT_GRAY_FABRIC = registerFabric(DyeColor.LIGHT_GRAY);

    public static final Block CYAN_FABRIC = registerFabric(DyeColor.CYAN);

    public static final Block PURPLE_FABRIC = registerFabric(DyeColor.PURPLE);

    public static final Block BLUE_FABRIC = registerFabric(DyeColor.BLUE);

    public static final Block BROWN_FABRIC = registerFabric(DyeColor.BROWN);

    public static final Block GREEN_FABRIC = registerFabric(DyeColor.GREEN);

    public static final Block RED_FABRIC = registerFabric(DyeColor.RED);

    public static final Block BLACK_FABRIC = registerFabric(DyeColor.BLACK);


    public static final Block WHITE_ANCIENT_FABRIC = registerAncientFabric(DyeColor.WHITE);

    public static final Block ORANGE_ANCIENT_FABRIC = registerAncientFabric(DyeColor.ORANGE);

    public static final Block MAGENTA_ANCIENT_FABRIC = registerAncientFabric(DyeColor.MAGENTA);

    public static final Block LIGHT_BLUE_ANCIENT_FABRIC = registerAncientFabric(DyeColor.LIGHT_BLUE);

    public static final Block YELLOW_ANCIENT_FABRIC = registerAncientFabric(DyeColor.YELLOW);

    public static final Block LIME_ANCIENT_FABRIC = registerAncientFabric(DyeColor.LIME);

    public static final Block PINK_ANCIENT_FABRIC = registerAncientFabric(DyeColor.PINK);

    public static final Block GRAY_ANCIENT_FABRIC = registerAncientFabric(DyeColor.GRAY);

    public static final Block LIGHT_GRAY_ANCIENT_FABRIC = registerAncientFabric(DyeColor.LIGHT_GRAY);

    public static final Block CYAN_ANCIENT_FABRIC = registerAncientFabric(DyeColor.CYAN);

    public static final Block PURPLE_ANCIENT_FABRIC = registerAncientFabric(DyeColor.PURPLE);

    public static final Block BLUE_ANCIENT_FABRIC = registerAncientFabric(DyeColor.BLUE);

    public static final Block BROWN_ANCIENT_FABRIC = registerAncientFabric(DyeColor.BROWN);

    public static final Block GREEN_ANCIENT_FABRIC = registerAncientFabric(DyeColor.GREEN);

    public static final Block RED_ANCIENT_FABRIC = registerAncientFabric(DyeColor.RED);

    public static final Block BLACK_ANCIENT_FABRIC = registerAncientFabric(DyeColor.BLACK);
    private static final BlockBehaviour.Properties UNRAVELLED_FABRIC_BLOCK_SETTINGS = ofFullCopy(STONE).mapColor(COLOR_BLACK).randomTicks().lightLevel(state -> 15).strength(0.3F, 0.3F);

    public static final LiquidBlock ETERNAL_FLUID = registerWithoutTabOrItem("eternal_fluid", new EternalFluidBlock(ofFullCopy(LAVA).mapColor(COLOR_RED).lightLevel(state -> 15)));

    public static final LiquidBlock LEAK = registerWithoutTabOrItem("leak", new LeakLiquidBlock(ModFluids.LEAK, ofFullCopy(WATER)));

    public static final Block DECAYED_BLOCK = registerWithoutTabOrItem("decayed_block", new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

    public static final Block UNFOLDED_BLOCK = registerWithoutTabOrItem("unfolded_block", new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

    public static final Block UNWARPED_BLOCK = registerWithoutTabOrItem("unwarped_block", new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

    public static final Block UNRAVELLED_BLOCK = registerWithoutTabOrItem("unravelled_block", new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

    public static final Block UNRAVELLED_FABRIC = register("unravelled_fabric", new UnravelledFabricBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));

    public static final Block MARKING_PLATE = registerWithoutTabOrItem("marking_plate", new Block(ofFullCopy(IRON_BLOCK).mapColor(DyeColor.BLACK).noOcclusion()));

    public static final Block SOLID_STATIC = register("solid_static", new UnravelledFabricBlock(ofFullCopy(STONE).strength(7, 25).randomTicks().requiresCorrectToolForDrops().sound(SoundType.SAND)));

    public static final Block TESSELATING_LOOM = register("tesselating_loom", new TesselatingLoomBlock(of(LOOM)));

    public static final Block REALITY_SPONGE = register("reality_sponge", new RealitySpongeBlock(UNRAVELLED_FABRIC_BLOCK_SETTINGS));
    public static final Block LIMBO_AIR = registerWithoutTabOrItem("limbo_air", new LimboAirBlock(BlockBehaviour.Properties.of().randomTicks().replaceable().noCollission().noLootTable().air()));

    //Decay graph filler.

    public static final Block CLOD_ORE = registerDecay("clod_ore", new Block(ofFullCopy(Blocks.AMETHYST_BLOCK)));
    public static final Block CLOD_BLOCK = registerDecay("clod_block", new Block(ofFullCopy(Blocks.AMETHYST_BLOCK)));

    public static final Block AMALGAM_BLOCK = registerDecay("amalgam_block", new Block(ofFullCopy(IRON_BLOCK).mapColor(COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
    public static final Block AMALGAM_DOOR = registerDecay("amalgam_door", new ModDoorBlock(BlockSetType.IRON, ofFullCopy(IRON_BLOCK).mapColor(COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).noOcclusion()));
    public static final Block AMALGAM_TRAPDOOR = registerDecay("amalgam_trapdoor", new ModTrapDoorBlock(BlockSetType.IRON, ofFullCopy(IRON_BLOCK).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).isValidSpawn((state, world, pos, type) -> false)));
    public static final Block AMALGAM_SLAB = registerDecay("amalgam_slab", new SlabBlock(of(AMALGAM_BLOCK)));
    public static final Block AMALGAM_STAIRS = registerDecay("amalgam_stairs", new ModStairBlock(AMALGAM_BLOCK.defaultBlockState(), of(AMALGAM_BLOCK)));
    public static final Block AMALGAM_ORE = registerDecay("amalgam_ore", new DropExperienceBlock(ConstantInt.of(1), ofFullCopy(STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));

    public static final Block RUST = registerDecay("rust", new Block(ofFullCopy(OAK_WOOD)));

    public static final Block DRIFTWOOD_WOOD = registerDecay("driftwood_wood", new RotatedPillarBlock(ofFullCopy(OAK_WOOD).mapColor(COLOR_LIGHT_GRAY).strength(2.0F).sound(SoundType.WOOD)));
    public static final Block DRIFTWOOD_LOG = registerDecay("driftwood_log", new RotatedPillarBlock(ofFullCopy(OAK_WOOD).mapColor(COLOR_LIGHT_GRAY).strength(2.0F).sound(SoundType.WOOD)));
    public static final Block DRIFTWOOD_PLANKS = registerDecay("driftwood_planks", new Block(ofFullCopy(OAK_WOOD).mapColor(COLOR_LIGHT_GRAY).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final Block DRIFTWOOD_LEAVES = registerDecay("driftwood_leaves", new LeavesBlock(of(OAK_LEAVES)));
    public static final Block DRIFTWOOD_SAPLING = registerDecay("driftwood_sapling", new DriftwoodSaplingBlock(of(OAK_SAPLING)));
    public static final Block DRIFTWOOD_FENCE = registerDecay("driftwood_fence", new FenceBlock(of(DRIFTWOOD_PLANKS)));
    public static final Block DRIFTWOOD_GATE = registerDecay("driftwood_gate", new FenceGateBlock(WoodType.OAK, of(DRIFTWOOD_PLANKS)));
    public static final Block DRIFTWOOD_BUTTON = registerDecay("driftwood_button", new ModButtonBlock(BlockSetType.STONE, 20, of(DRIFTWOOD_PLANKS).noCollission().strength(0.5F)));
    public static final Block DRIFTWOOD_SLAB = registerDecay("driftwood_slab", new SlabBlock(of(DRIFTWOOD_PLANKS)));
    public static final Block DRIFTWOOD_STAIRS = registerDecay("driftwood_stairs", new ModStairBlock(DRIFTWOOD_PLANKS.defaultBlockState(), of(DRIFTWOOD_PLANKS)));
    public static final Block DRIFTWOOD_DOOR = registerDecay("driftwood_door", new ModDoorBlock(BlockSetType.OAK, ofFullCopy(OAK_WOOD).mapColor(COLOR_GRAY).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final Block DRIFTWOOD_TRAPDOOR = registerDecay("driftwood_trapdoor", new ModTrapDoorBlock(BlockSetType.OAK, ofFullCopy(OAK_WOOD).mapColor(COLOR_GRAY).strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn((state, world, pos, type) -> false)));

    public static final Block DARK_SAND = registerDecay("dark_sand", new Block(ofFullCopy(SAND).mapColor(COLOR_BLACK).strength(0.5F).sound(SoundType.SAND)));
    public static final Block PALE_SAND = registerDecay("pale_sand", new ColoredFallingBlock(new ColorRGBA(0xFFF2E9D1), ofFullCopy(SAND).mapColor(COLOR_LIGHT_GRAY).strength(0.5F).sound(SoundType.SAND)));
    public static final Block DARK_SAND_LAYER = registerDecay("dark_sand_layer", new CarpetBlock(ofFullCopy(MOSS_CARPET).mapColor(COLOR_BLACK).sound(SoundType.SAND)));
    public static final Block LINT_LAYER = registerDecay("lint_layer", new CarpetBlock(ofFullCopy(MOSS_CARPET).mapColor(COLOR_LIGHT_GRAY)));
    public static final Block STONE_SLAB = registerDecay("stone_slab", new SlabBlock(of(STONE)));
    public static final Block STONE_STAIRS = registerDecay("stone_stairs", new ModStairBlock(STONE.defaultBlockState(), of(STONE)));
    public static final Block STONE_WALL = registerDecay("stone_wall", new WallBlock(of(STONE)));

    public record DecayGroupSet(
            Block fence,
            Block gate,
            Block button,
            Block slab,
            Block stairs,
            Block wall
    ) {
        public static final List<DecayGroupSet> SETS = new ArrayList<>();

        public static DecayGroupSet create(String name, Block block, UnaryOperator<BlockBehaviour.Properties> propertiesModifier) {
            var set = new DecayGroupSet(
                    registerDecay(name + "_fence", new FenceBlock(propertiesModifier.apply(of(block)))),
                    registerDecay(name + "_gate", new FenceGateBlock(WoodType.OAK, propertiesModifier.apply(of(block)))),
                    registerDecay(name + "_button", new ModButtonBlock(BlockSetType.STONE, 20, propertiesModifier.apply(of(block)).noCollission().strength(0.5F))),
                    registerDecay(name + "_slab", new SlabBlock(propertiesModifier.apply(of(block)))),
                    registerDecay(name + "_stairs", new ModStairBlock(block.defaultBlockState(), propertiesModifier.apply(of(block)))),
                    registerDecay(name + "_wall", new WallBlock(propertiesModifier.apply(of(block))))
            );

            SETS.add(set);

            return set;
        }

        public static DecayGroupSet create(String name, Block block) {
            return create(name, block, UnaryOperator.identity());
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
    public static final DecayGroupSet GRAY_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("gray_glazed_terracotta", Blocks.GRAY_GLAZED_TERRACOTTA);
    public static final DecayGroupSet LIGHT_GRAY_TERRACOTTA_SET = DecayGroupSet.create("light_gray_terracotta", Blocks.LIGHT_GRAY_TERRACOTTA);
    public static final DecayGroupSet LIGHT_GRAY_GLAZED_TERRACOTTA_SET = DecayGroupSet.create("light_gray_glazed_terracotta", Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA);
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

    public static final DecayGroupSet MUD_SET = DecayGroupSet.create("mud", MUD, properties -> properties.isViewBlocking((blockState, blockGetter, blockPos) -> false).isSuffocating((blockState, blockGetter, blockPos) -> false));
    public static final DecayGroupSet UNRAVELED_SET = DecayGroupSet.create("unraveled", UNRAVELLED_FABRIC);
    public static final DecayGroupSet DEEPSLATE_SET = DecayGroupSet.create("deepslate", Blocks.DEEPSLATE);
    public static final DecayGroupSet RED_SAND_SET = DecayGroupSet.create("red_sand", Blocks.RED_SAND);
    public static final DecayGroupSet SAND_SET = DecayGroupSet.create("sand", Blocks.SAND);
    public static final DecayGroupSet END_STONE_SET = DecayGroupSet.create("end_stone", Blocks.END_STONE);
    public static final DecayGroupSet NETHERRACK_SET = DecayGroupSet.create("netherrack", Blocks.NETHERRACK);

    public static final Block UNRAVELED_SPIKE = registerDecay("unraveled_spike", new PointedDripstoneBlock(of(UNRAVELLED_FABRIC).lightLevel(state -> 0))); //TODO: make this proper class later
    public static final Block GRITTY_STONE = registerDecay("gritty_stone", new Block(of(STONE)));


    public static void init() {
        ModBlockEntityTypes.DETACHED_RIFT.addBlock(DETACHED_RIFT);
        ModBlockEntityTypes.TESSELATING_LOOM.addBlock(TESSELATING_LOOM);
        ModBlockEntityTypes.ENTRANCE_RIFT.addBlock(ModBlocks.DIMENSIONAL_PORTAL);
        ModBlockEntityTypes.DIALING_DOOR.addBlock(ModBlocks.DIALING_DOOR);
    }

    private static <T extends Block> T registerWithoutTabOrItem(String name, T block) {
        return DimensionalDoors.getSided().register(Registries.BLOCK, name, block);
    }

    private static Block registerAncientFabric(DyeColor color) {
        Block block = register(color.getSerializedName() + "_ancient_fabric", new AncientFabricBlock(color));
        ANCIENT_FABRIC_BLOCKS.put(color, block);
        return block;
    }

    private static Block registerFabric(DyeColor color) {
        Block block = register(color.getSerializedName() + "_fabric", new FabricBlock(color));
        FABRIC_BLOCKS.put(color, block);
        return block;
    }

    public static Block ancientFabricFromDye(DyeColor color) {
        return ANCIENT_FABRIC_BLOCKS.get(color);
    }

    public static Block fabricFromDye(DyeColor color) {
        return FABRIC_BLOCKS.get(color);
    }

    public static <T extends Block> T register(String name, T block) {
        var sided = DimensionalDoors.getSided();
        var supplier = sided.register(Registries.BLOCK, name, block);
        var item = sided.register(Registries.ITEM, name, new BlockItem(supplier, new Item.Properties()));
        sided.appendStack(DIMENSIONAL_DOORS, item.getDefaultInstance());

        return supplier;
    }

    public static <T extends Block> T registerDecay(String name, T block) {
        var sided = DimensionalDoors.getSided();
        var supplier = sided.register(Registries.BLOCK, name, block);
        var item = sided.register(Registries.ITEM, name, new BlockItem(supplier, new Item.Properties()));
        sided.appendStack(DECAY, item.getDefaultInstance());
        return supplier;
    }

    public static <T extends Block> T registerWithoutTab(String name, T block) {
        var sided = DimensionalDoors.getSided();
        var supplier = sided.register(Registries.BLOCK, name, block);
        sided.register(Registries.ITEM, name, new BlockItem(supplier, new Item.Properties()));

        return supplier;
    }

    private static BlockBehaviour.Properties of(Block block) {
        return ofFullCopy(block);
    }
}
