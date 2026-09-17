package org.dimdev.dimdoors.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.painting.ModPaintings;
import org.dimdev.dimdoors.tag.ModBlockTags;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.decay.DecayPatternHolder;
import org.dimdev.dimdoors.world.decay.conditions.DimensionDecayCondition;
import org.dimdev.dimdoors.world.decay.conditions.SimpleDecayCondition;
import org.dimdev.dimdoors.world.decay.pattern.CompoundDecayPattern;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class AbstractionDecayProvider extends LimboDecayProvider {
    public AbstractionDecayProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void generatePatterns(HolderLookup.Provider provider, Consumer<DecayPatternHolder> consumer) {
        addPattern(Blocks.AIR, ModBlockTags.DECAYS_TO_AIR).accept(consumer, provider);
        addPattern(ModBlocks.GRITTY_STONE, ModBlockTags.DECAYS_TO_GRITTY_STONE).accept(consumer, provider);
        DecayPatternHolder.builder(DimensionalDoors.id("solid_static"))
                .pattern(CompoundDecayPattern.builder()
                        .conditions(
                                DimensionDecayCondition.of(ModDimensions.LIMBO_TYPE_KEY),
                                SimpleDecayCondition.of(ModBlockTags.DECAYS_TO_SOLID_STATIC))
                        .result(getProcessor(ModBlocks.SOLID_STATIC)))
                .accept(consumer, provider);

        addPattern(ModBlocks.LINT_LAYER, ModBlockTags.DECAYS_TO_LINT_LAYER).accept(consumer, provider);
        addPattern(ModBlocks.DARK_SAND_LAYER, ModBlockTags.DECAYS_TO_DARK_SAND_LAYER).accept(consumer, provider);
        addPattern(ModBlocks.UNRAVELED_SPIKE, ModBlockTags.DECAYS_TO_UNRAVELED_SPIKE).accept(consumer, provider);
        addPattern(ModBlocks.UNRAVELLED_FABRIC, ModBlockTags.DECAYS_TO_UNRAVELED_FABRIC).accept(consumer, provider);
        addPattern(ModBlocks.UNRAVELED_SET.fence(), ModBlockTags.DECAYS_TO_UNRAVELED_FENCE).accept(consumer, provider);
        addPattern(ModBlocks.UNRAVELED_SET.gate(), ModBlockTags.DECAYS_TO_UNRAVELED_GATE).accept(consumer, provider);
        addPattern(ModBlocks.UNRAVELED_SET.button(), ModBlockTags.DECAYS_TO_UNRAVELED_BUTTON).accept(consumer, provider);
        addPattern(ModBlocks.UNRAVELED_SET.slab(), ModBlockTags.DECAYS_TO_UNRAVELED_SLAB).accept(consumer, provider);
        addPattern(ModBlocks.UNRAVELED_SET.stairs(), ModBlockTags.DECAYS_TO_UNRAVELED_STAIRS).accept(consumer, provider);
        addPattern(ModBlocks.UNRAVELED_SET.wall(), ModBlockTags.DECAYS_TO_UNRAVELED_WALL).accept(consumer, provider);
        addPattern(ModBlocks.DRIFTWOOD_LEAVES, ModBlockTags.DECAYS_TO_DRIFTWOOD_LEAVES).accept(consumer, provider);
        addPattern(ModBlocks.DRIFTWOOD_SAPLING, ModBlockTags.DECAYS_TO_DRIFTWOOD_SAPLING).accept(consumer, provider);
        addPattern(Blocks.WITHER_ROSE, ModBlockTags.DECAYS_TO_WITHER_ROSE).accept(consumer, provider);

        addPattern(Blocks.WHITE_WOOL, Blocks.TARGET).accept(consumer, provider);
        addDoublePattern(DimensionalDoors.id("white_wool_from_beds"), Blocks.WHITE_WOOL, BlockTags.BEDS).accept(consumer, provider);

        addPattern(ModBlocks.DARK_SAND, ModBlockTags.DECAYS_TO_DARK_SAND).accept(consumer, provider);
        addPattern(ModBlocks.DARK_SAND_SET.fence(), ModBlockTags.DECAYS_TO_DARK_SAND_FENCE).accept(consumer, provider);
        addPattern(ModBlocks.DARK_SAND_SET.gate(), ModBlockTags.DECAYS_TO_DARK_SAND_GATE).accept(consumer, provider);
        addPattern(ModBlocks.DARK_SAND_SET.button(), ModBlockTags.DECAYS_TO_DARK_SAND_BUTTON).accept(consumer, provider);
        addPattern(ModBlocks.DARK_SAND_SET.slab(), ModBlockTags.DECAYS_TO_DARK_SAND_SLAB).accept(consumer, provider);
        addPattern(ModBlocks.DARK_SAND_SET.stairs(), ModBlockTags.DECAYS_TO_DARK_SAND_STAIRS).accept(consumer, provider);
        addPattern(ModBlocks.DARK_SAND_SET.wall(), ModBlockTags.DECAYS_TO_DARK_SAND_WALL).accept(consumer, provider);

        addPattern(Blocks.GLASS, ModBlockTags.DECAYS_TO_GLASS).accept(consumer, provider);
        addPattern(Blocks.REDSTONE_LAMP, Blocks.BEACON).accept(consumer, provider);
        addPatterns(provider, consumer, Blocks.RED_SAND, Blocks.RED_SANDSTONE, Blocks.REDSTONE_BLOCK);
        addPattern(ModBlocks.RED_SAND_SET.slab(), ModBlockTags.DECAYS_TO_RED_SAND_SLAB).accept(consumer, provider);
        addPattern(ModBlocks.RED_SAND_SET.stairs(), ModBlockTags.DECAYS_TO_RED_SAND_STAIRS).accept(consumer, provider);
        addPattern(ModBlocks.RED_SAND_SET.wall(), ModBlockTags.DECAYS_TO_RED_SAND_WALL).accept(consumer, provider);
        addPatterns(provider, consumer, Blocks.SAND, Blocks.SANDSTONE, Blocks.TNT);
        addPattern(ModBlocks.SAND_SET.slab(), ModBlockTags.DECAYS_TO_SAND_SLAB).accept(consumer, provider);
        addPattern(ModBlocks.SAND_SET.stairs(), ModBlockTags.DECAYS_TO_SAND_STAIRS).accept(consumer, provider);
        addPattern(ModBlocks.SAND_SET.wall(), ModBlockTags.DECAYS_TO_SAND_WALL).accept(consumer, provider);
        addPattern(Blocks.SOUL_SAND, Blocks.SOUL_SOIL).accept(consumer, provider);
        addConcretePowderPatterns(provider, consumer);

        addPattern(Blocks.STONE, ModBlockTags.DECAYS_TO_STONE).accept(consumer, provider);
        addPattern(ModBlocks.STONE_SLAB, ModBlockTags.DECAYS_TO_STONE_SLAB).accept(consumer, provider);
        addPattern(ModBlocks.STONE_STAIRS, ModBlockTags.DECAYS_TO_STONE_STAIRS).accept(consumer, provider);
        addPattern(ModBlocks.STONE_WALL, ModBlockTags.DECAYS_TO_STONE_WALL).accept(consumer, provider);
        addPattern(ModBlocks.AMALGAM_BLOCK, ModBlockTags.DECAYS_TO_AMALGAM).accept(consumer, provider);
        addPattern(ModBlocks.AMALGAM_ORE, ModBlockTags.DECAYS_TO_AMALGAM_ORE).accept(consumer, provider);
        addPattern(Blocks.IRON_BLOCK, Blocks.ANVIL).accept(consumer, provider);
        addPattern(Blocks.ANCIENT_DEBRIS, Blocks.NETHERITE_BLOCK).accept(consumer, provider);
        createOxidizationChain(
                consumer, provider,
                Blocks.COPPER_BLOCK, Blocks.WAXED_COPPER_BLOCK,
                Blocks.EXPOSED_COPPER, Blocks.WAXED_EXPOSED_COPPER,
                Blocks.WEATHERED_COPPER, Blocks.WAXED_WEATHERED_COPPER,
                Blocks.OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_COPPER
        );
        createOxidizationChain(
                consumer, provider,
                Blocks.CUT_COPPER, Blocks.WAXED_CUT_COPPER,
                Blocks.EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER,
                Blocks.WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER,
                Blocks.OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER
        );

        addPattern(ModBlocks.CLOD_ORE, ModBlockTags.DECAYS_TO_CLOD_ORE).accept(consumer, provider);
        addPattern(ModBlocks.CLOD_BLOCK, ModBlockTags.DECAYS_TO_CLOD_BLOCK).accept(consumer, provider);
        addPattern(Blocks.COBBLESTONE, ModBlockTags.DECAYS_TO_COBBLESTONE).accept(consumer, provider);
        addPattern(Blocks.COBBLESTONE_SLAB, ModBlockTags.DECAYS_TO_COBBLESTONE_SLAB).accept(consumer, provider);
        addPattern(Blocks.FURNACE, ModBlockTags.DECAYS_TO_FURNACE).accept(consumer, provider);
        addPattern(Blocks.DROPPER, Blocks.DISPENSER).accept(consumer, provider);

        addPattern(Blocks.DEEPSLATE, ModBlockTags.DECAYS_TO_DEEPSLATE).accept(consumer, provider);
        addPattern(ModBlocks.DEEPSLATE_SET.slab(), ModBlockTags.DECAYS_TO_DEEPSLATE_SLAB).accept(consumer, provider);
        addPattern(ModBlocks.DEEPSLATE_SET.stairs(), ModBlockTags.DECAYS_TO_DEEPSLATE_STAIRS).accept(consumer, provider);
        addPattern(ModBlocks.DEEPSLATE_SET.wall(), ModBlockTags.DECAYS_TO_DEEPSLATE_WALL).accept(consumer, provider);

        addPattern(Blocks.END_STONE, ModBlockTags.DECAYS_TO_ENDSTONE).accept(consumer, provider);
        addPattern(ModBlocks.END_STONE_SET.slab(), ModBlockTags.DECAYS_TO_ENDSTONE_SLAB).accept(consumer, provider);
        addPattern(ModBlocks.END_STONE_SET.stairs(), ModBlockTags.DECAYS_TO_ENDSTONE_STAIRS).accept(consumer, provider);
        addPattern(ModBlocks.END_STONE_SET.wall(), ModBlockTags.DECAYS_TO_ENDSTONE_WALL).accept(consumer, provider);

        addPattern(Blocks.NETHERRACK, ModBlockTags.DECAYS_TO_NETHERRACK).accept(consumer, provider);
        addPattern(ModBlocks.NETHERRACK_SET.fence(), ModBlockTags.DECAYS_TO_NETHERRACK_FENCE).accept(consumer, provider);
        addPattern(ModBlocks.NETHERRACK_SET.slab(), ModBlockTags.DECAYS_TO_NETHERRACK_SLAB).accept(consumer, provider);
        addPattern(ModBlocks.NETHERRACK_SET.stairs(), ModBlockTags.DECAYS_TO_NETHERRACK_STAIRS).accept(consumer, provider);
        addPattern(ModBlocks.NETHERRACK_SET.wall(), ModBlockTags.DECAYS_TO_NETHERRACK_WALL).accept(consumer, provider);

        addPattern(Blocks.PRISMARINE, ModBlockTags.DECAYS_TO_PRISMARINE).accept(consumer, provider);
        addPattern(Blocks.PRISMARINE_SLAB, ModBlockTags.DECAYS_TO_PRISMARINE_SLAB).accept(consumer, provider);
        addPattern(Blocks.PRISMARINE_STAIRS, ModBlockTags.DECAYS_TO_PRISMARINE_STAIRS).accept(consumer, provider);

        addPattern(Blocks.OBSIDIAN, ModBlockTags.DECAYS_TO_OBSIDIAN).accept(consumer, provider);
        addPattern(Blocks.CRYING_OBSIDIAN, Blocks.RESPAWN_ANCHOR).accept(consumer, provider);
        addPattern(Blocks.MAGMA_BLOCK, Fluids.LAVA).accept(consumer, provider);

        addPattern(Blocks.STONE_BRICKS, ModBlockTags.DECAYS_TO_STONE_BRICKS).accept(consumer, provider);
        addPattern(Blocks.STONE_BRICK_SLAB, ModBlockTags.DECAYS_TO_STONE_BRICK_SLAB).accept(consumer, provider);
        addPattern(Blocks.STONE_BRICK_STAIRS, ModBlockTags.DECAYS_TO_STONE_BRICK_STAIRS).accept(consumer, provider);
        addPattern(Blocks.STONE_BRICK_WALL, ModBlockTags.DECAYS_TO_STONE_BRICK_WALL).accept(consumer, provider);

        addPattern(Blocks.BLACKSTONE, ModBlockTags.DECAYS_TO_BLACKSTONE).accept(consumer, provider);
        addPattern(Blocks.BLACKSTONE_SLAB, ModBlockTags.DECAYS_TO_BLACKSTONE_SLAB).accept(consumer, provider);
        addPattern(Blocks.BLACKSTONE_STAIRS, ModBlockTags.DECAYS_TO_BLACKSTONE_STAIRS).accept(consumer, provider);
        addPattern(Blocks.BLACKSTONE_WALL, ModBlockTags.DECAYS_TO_BLACKSTONE_WALL).accept(consumer, provider);

        addPattern(Blocks.DIORITE, ModBlockTags.DECAYS_TO_DIORITE).accept(consumer, provider);
        addPattern(Blocks.BASALT, ModBlockTags.DECAYS_TO_BASALT).accept(consumer, provider);
        addPattern(Blocks.BASALT, Fluids.LAVA).accept(consumer, provider);
        addPattern(ModBlocks.AMALGAM_TRAPDOOR, Blocks.IRON_TRAPDOOR).accept(consumer, provider);
        addPattern(ModBlocks.DRIFTWOOD_TRAPDOOR, ModBlockTags.DECAYS_TO_DRIFTWOOD_TRAPDOOR).accept(consumer, provider);
        addDoublePattern(DimensionalDoors.id("driftwood_trapdoor_from_driftwood_door"), ModBlocks.DRIFTWOOD_TRAPDOOR, ModBlocks.DRIFTWOOD_DOOR).accept(consumer, provider);
        addPattern(ModBlocks.PALE_SAND, ModBlockTags.DECAYS_TO_PALE_SAND).accept(consumer, provider);
        addPattern(ModBlocks.LEAK, Fluids.WATER).accept(consumer, provider);
        addPattern(Blocks.SNOW_BLOCK, ModFluids.LEAK).accept(consumer, provider);
        addPattern(Blocks.MOSS_CARPET, ModBlockTags.DECAYS_TO_MOSS_CARPET).accept(consumer, provider);
        addPattern(Blocks.RAIL, ModBlockTags.DECAYS_TO_RAIL).accept(consumer, provider);
        addPattern(Blocks.GLASS_PANE, ModBlockTags.DECAYS_TO_TO_GLASS_PANE).accept(consumer, provider);
        addPattern(Blocks.ICE, Blocks.PACKED_ICE).accept(consumer, provider);
        addPattern(Blocks.PACKED_ICE, Blocks.BLUE_ICE).accept(consumer, provider);

        addPattern(Blocks.CLAY, ModBlockTags.DECAYS_TO_CLAY).accept(consumer, provider);
        addPattern(ModBlocks.CLAY_SET.fence(), ModBlockTags.DECAYS_TO_CLAY_FENCE).accept(consumer, provider);
        addPattern(ModBlocks.CLAY_SET.gate(), ModBlockTags.DECAYS_TO_CLAY_GATE).accept(consumer, provider);
        addPattern(ModBlocks.CLAY_SET.button(), ModBlockTags.DECAYS_TO_CLAY_BUTTON).accept(consumer, provider);
        addPattern(ModBlocks.CLAY_SET.slab(), ModBlockTags.DECAYS_TO_CLAY_SLAB).accept(consumer, provider);
        addPattern(ModBlocks.CLAY_SET.stairs(), ModBlockTags.DECAYS_TO_CLAY_STAIRS).accept(consumer, provider);
        addPattern(ModBlocks.CLAY_SET.wall(), ModBlockTags.DECAYS_TO_CLAY_WALL).accept(consumer, provider);

        addTerracottaPatterns(provider, consumer);

        addPattern(Blocks.MUD, ModBlockTags.DECAYS_TO_MUD).accept(consumer, provider);

        addPatterns(provider, consumer, Blocks.BONE_BLOCK, Blocks.CONDUIT);
        addPattern(Blocks.CHEST, ModBlockTags.DECAYS_TO_CHEST).accept(consumer, provider);
        addPattern(Blocks.TRAPPED_CHEST, BlockTags.SHULKER_BOXES).accept(consumer, provider);
        addPattern(Blocks.COMPOSTER, Blocks.BARREL).accept(consumer, provider);
        addPatterns(provider, consumer, Blocks.BARREL, Blocks.BEEHIVE, Blocks.BEE_NEST);

        addPattern(Blocks.DIRT, ModBlockTags.DECAYS_TO_DIRT).accept(consumer, provider);
        addPattern(Blocks.GRASS_BLOCK, Blocks.DIRT_PATH).accept(consumer, provider);
        addPattern(Blocks.MYCELIUM, Blocks.CRIMSON_NYLIUM).accept(consumer, provider);
        addPattern(Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM).accept(consumer, provider);

        addPattern(ModBlocks.DRIFTWOOD_LOG, ModBlockTags.DECAYS_TO_DRIFTWOOD_LOG).accept(consumer, provider);
        addPattern(ModBlocks.DRIFTWOOD_PLANKS, ModBlockTags.DECAYS_TO_DRIFTWOOD_PLANK).accept(consumer, provider);

        addPattern(Blocks.HONEYCOMB_BLOCK, Blocks.SPONGE).accept(consumer, provider);
        addPattern(Blocks.SPONGE, Blocks.WET_SPONGE).accept(consumer, provider);
        addPattern(Blocks.LECTERN, Blocks.BOOKSHELF).accept(consumer, provider);
        addPattern(Blocks.MOSS_BLOCK, Blocks.SCULK).accept(consumer, provider);
        addPattern(Blocks.SCULK, Blocks.SCULK_CATALYST).accept(consumer, provider);
        addPattern(Blocks.NETHER_WART_BLOCK, ModBlockTags.DECAYS_TO_NETHERWART_BLOCK).accept(consumer, provider);
        addPattern(Blocks.PACKED_MUD, Blocks.MUD_BRICKS).accept(consumer, provider);
        addPattern(Blocks.PISTON, Blocks.STICKY_PISTON).accept(consumer, provider);
        addPattern(Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN).accept(consumer, provider);
        addPattern(Blocks.CARVED_PUMPKIN, Blocks.JACK_O_LANTERN).accept(consumer, provider);
        addPattern(Blocks.SKELETON_SKULL, ModBlockTags.DECAYS_TO_SKELETON_SKULL).accept(consumer, provider);
        addPattern(Blocks.SKELETON_WALL_SKULL, ModBlockTags.DECAYS_TO_SKELETON_WALL_SKULL).accept(consumer, provider);
        addPattern(Blocks.SLIME_BLOCK, Blocks.HONEY_BLOCK).accept(consumer, provider);

        addPattern(ModBlocks.DRIFTWOOD_FENCE, ModBlockTags.DECAYS_TO_DRIFTWOOD_FENCE).accept(consumer, provider);
        addPattern(ModBlocks.DRIFTWOOD_GATE, ModBlockTags.DECAYS_TO_DRIFTWOOD_GATE).accept(consumer, provider);
        addPattern(ModBlocks.DRIFTWOOD_BUTTON, ModBlockTags.DECAYS_TO_DRIFTWOOD_BUTTON).accept(consumer, provider);
        addPattern(ModBlocks.DRIFTWOOD_SLAB, ModBlockTags.DECAYS_TO_DRIFTWOOD_SLAB).accept(consumer, provider);
        addPattern(ModBlocks.DRIFTWOOD_STAIRS, ModBlockTags.DECAYS_TO_DRIFTWOOD_STAIRS).accept(consumer, provider);

        addPattern(ModBlocks.MUD_SET.fence(), ModBlockTags.DECAYS_TO_MUD_FENCE).accept(consumer, provider);
        addPattern(ModBlocks.MUD_SET.gate(), ModBlockTags.DECAYS_TO_MUD_GATE).accept(consumer, provider);
        addPattern(ModBlocks.MUD_SET.button(), ModBlockTags.DECAYS_TO_MUD_BUTTON).accept(consumer, provider);
        addPattern(ModBlocks.MUD_SET.slab(), ModBlockTags.DECAYS_TO_MUD_SLAB).accept(consumer, provider);
        addPattern(ModBlocks.MUD_SET.stairs(), ModBlockTags.DECAYS_TO_MUD_STAIRS).accept(consumer, provider);
        addPattern(ModBlocks.MUD_SET.wall(), ModBlockTags.DECAYS_TO_MUD_WALL).accept(consumer, provider);

        for (var key : ModPaintings.PAINTINGS_TO_DECAY_INTO) {
            var id = key.location().withPrefix("decays_into_");

            if (!id.getPath().contains("placeholder")) id = id.withSuffix("_painting");

            addPaintingPattern(key, TagKey.create(key.registryKey(), id)).accept(consumer, provider);
        }
    }

    private void addTerracottaPatterns(HolderLookup.Provider provider, Consumer<DecayPatternHolder> consumer) {
        addPatterns(provider, consumer, Blocks.TERRACOTTA,
                Blocks.WHITE_TERRACOTTA,
                Blocks.ORANGE_TERRACOTTA,
                Blocks.MAGENTA_TERRACOTTA,
                Blocks.LIGHT_BLUE_TERRACOTTA,
                Blocks.YELLOW_TERRACOTTA,
                Blocks.LIME_TERRACOTTA,
                Blocks.PINK_TERRACOTTA,
                Blocks.GRAY_TERRACOTTA,
                Blocks.LIGHT_GRAY_TERRACOTTA,
                Blocks.CYAN_TERRACOTTA,
                Blocks.PURPLE_TERRACOTTA,
                Blocks.BLUE_TERRACOTTA,
                Blocks.BROWN_TERRACOTTA,
                Blocks.GREEN_TERRACOTTA,
                Blocks.RED_TERRACOTTA,
                Blocks.BLACK_TERRACOTTA
        );
        addSetPatterns(provider, consumer, ModBlocks.TERRACOTTA_SET,
                ModBlocks.WHITE_TERRACOTTA_SET,
                ModBlocks.ORANGE_TERRACOTTA_SET,
                ModBlocks.MAGENTA_TERRACOTTA_SET,
                ModBlocks.LIGHT_BLUE_TERRACOTTA_SET,
                ModBlocks.YELLOW_TERRACOTTA_SET,
                ModBlocks.LIME_TERRACOTTA_SET,
                ModBlocks.PINK_TERRACOTTA_SET,
                ModBlocks.GRAY_TERRACOTTA_SET,
                ModBlocks.LIGHT_GRAY_TERRACOTTA_SET,
                ModBlocks.CYAN_TERRACOTTA_SET,
                ModBlocks.PURPLE_TERRACOTTA_SET,
                ModBlocks.BLUE_TERRACOTTA_SET,
                ModBlocks.BROWN_TERRACOTTA_SET,
                ModBlocks.GREEN_TERRACOTTA_SET,
                ModBlocks.RED_TERRACOTTA_SET,
                ModBlocks.BLACK_TERRACOTTA_SET
        );

        addTerracottaVariantPatterns(provider, consumer, Blocks.WHITE_TERRACOTTA, Blocks.WHITE_GLAZED_TERRACOTTA, ModBlocks.WHITE_TERRACOTTA_SET, ModBlocks.WHITE_GLAZED_TERRACOTTA_SET);
        addTerracottaVariantPatterns(provider, consumer, Blocks.ORANGE_TERRACOTTA, Blocks.ORANGE_GLAZED_TERRACOTTA, ModBlocks.ORANGE_TERRACOTTA_SET, ModBlocks.ORANGE_GLAZED_TERRACOTTA_SET);
        addTerracottaVariantPatterns(provider, consumer, Blocks.MAGENTA_TERRACOTTA, Blocks.MAGENTA_GLAZED_TERRACOTTA, ModBlocks.MAGENTA_TERRACOTTA_SET, ModBlocks.MAGENTA_GLAZED_TERRACOTTA_SET);
        addTerracottaVariantPatterns(provider, consumer, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, ModBlocks.LIGHT_BLUE_TERRACOTTA_SET, ModBlocks.LIGHT_BLUE_GLAZED_TERRACOTTA_SET);
        addTerracottaVariantPatterns(provider, consumer, Blocks.YELLOW_TERRACOTTA, Blocks.YELLOW_GLAZED_TERRACOTTA, ModBlocks.YELLOW_TERRACOTTA_SET, ModBlocks.YELLOW_GLAZED_TERRACOTTA_SET);
        addTerracottaVariantPatterns(provider, consumer, Blocks.LIME_TERRACOTTA, Blocks.LIME_GLAZED_TERRACOTTA, ModBlocks.LIME_TERRACOTTA_SET, ModBlocks.LIME_GLAZED_TERRACOTTA_SET);
        addTerracottaVariantPatterns(provider, consumer, Blocks.PINK_TERRACOTTA, Blocks.PINK_GLAZED_TERRACOTTA, ModBlocks.PINK_TERRACOTTA_SET, ModBlocks.PINK_GLAZED_TERRACOTTA_SET);
        addTerracottaVariantPatterns(provider, consumer, Blocks.GRAY_TERRACOTTA, Blocks.GRAY_GLAZED_TERRACOTTA, ModBlocks.GRAY_TERRACOTTA_SET, ModBlocks.GRAY_GLAZED_TERRACOTTA_SET);
        addTerracottaVariantPatterns(provider, consumer, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, ModBlocks.LIGHT_GRAY_TERRACOTTA_SET, ModBlocks.LIGHT_GRAY_GLAZED_TERRACOTTA_SET);
        addTerracottaVariantPatterns(provider, consumer, Blocks.CYAN_TERRACOTTA, Blocks.CYAN_GLAZED_TERRACOTTA, ModBlocks.CYAN_TERRACOTTA_SET, ModBlocks.CYAN_GLAZED_TERRACOTTA_SET);
        addTerracottaVariantPatterns(provider, consumer, Blocks.PURPLE_TERRACOTTA, Blocks.PURPLE_GLAZED_TERRACOTTA, ModBlocks.PURPLE_TERRACOTTA_SET, ModBlocks.PURPLE_GLAZED_TERRACOTTA_SET);
        addTerracottaVariantPatterns(provider, consumer, Blocks.BLUE_TERRACOTTA, Blocks.BLUE_GLAZED_TERRACOTTA, ModBlocks.BLUE_TERRACOTTA_SET, ModBlocks.BLUE_GLAZED_TERRACOTTA_SET);
        addTerracottaVariantPatterns(provider, consumer, Blocks.BROWN_TERRACOTTA, Blocks.BROWN_GLAZED_TERRACOTTA, ModBlocks.BROWN_TERRACOTTA_SET, ModBlocks.BROWN_GLAZED_TERRACOTTA_SET);
        addTerracottaVariantPatterns(provider, consumer, Blocks.GREEN_TERRACOTTA, Blocks.GREEN_GLAZED_TERRACOTTA, ModBlocks.GREEN_TERRACOTTA_SET, ModBlocks.GREEN_GLAZED_TERRACOTTA_SET);
        addTerracottaVariantPatterns(provider, consumer, Blocks.RED_TERRACOTTA, Blocks.RED_GLAZED_TERRACOTTA, ModBlocks.RED_TERRACOTTA_SET, ModBlocks.RED_GLAZED_TERRACOTTA_SET);
        addTerracottaVariantPatterns(provider, consumer, Blocks.BLACK_TERRACOTTA, Blocks.BLACK_GLAZED_TERRACOTTA, ModBlocks.BLACK_TERRACOTTA_SET, ModBlocks.BLACK_GLAZED_TERRACOTTA_SET);
    }

    private void addConcretePowderPatterns(HolderLookup.Provider provider, Consumer<DecayPatternHolder> consumer) {
        addPairedPatterns(provider, consumer,
                new Block[]{
                        Blocks.WHITE_CONCRETE_POWDER,
                        Blocks.ORANGE_CONCRETE_POWDER,
                        Blocks.MAGENTA_CONCRETE_POWDER,
                        Blocks.LIGHT_BLUE_CONCRETE_POWDER,
                        Blocks.YELLOW_CONCRETE_POWDER,
                        Blocks.LIME_CONCRETE_POWDER,
                        Blocks.PINK_CONCRETE_POWDER,
                        Blocks.GRAY_CONCRETE_POWDER,
                        Blocks.LIGHT_GRAY_CONCRETE_POWDER,
                        Blocks.CYAN_CONCRETE_POWDER,
                        Blocks.PURPLE_CONCRETE_POWDER,
                        Blocks.BLUE_CONCRETE_POWDER,
                        Blocks.BROWN_CONCRETE_POWDER,
                        Blocks.GREEN_CONCRETE_POWDER,
                        Blocks.RED_CONCRETE_POWDER,
                        Blocks.BLACK_CONCRETE_POWDER
                },
                new Block[]{
                        Blocks.WHITE_CONCRETE,
                        Blocks.ORANGE_CONCRETE,
                        Blocks.MAGENTA_CONCRETE,
                        Blocks.LIGHT_BLUE_CONCRETE,
                        Blocks.YELLOW_CONCRETE,
                        Blocks.LIME_CONCRETE,
                        Blocks.PINK_CONCRETE,
                        Blocks.GRAY_CONCRETE,
                        Blocks.LIGHT_GRAY_CONCRETE,
                        Blocks.CYAN_CONCRETE,
                        Blocks.PURPLE_CONCRETE,
                        Blocks.BLUE_CONCRETE,
                        Blocks.BROWN_CONCRETE,
                        Blocks.GREEN_CONCRETE,
                        Blocks.RED_CONCRETE,
                        Blocks.BLACK_CONCRETE
                }
        );
    }

    private void addTerracottaVariantPatterns(HolderLookup.Provider provider, Consumer<DecayPatternHolder> consumer, Block terracotta, Block glazedTerracotta, ModBlocks.DecayGroupSet terracottaSet, ModBlocks.DecayGroupSet glazedTerracottaSet) {
        addPatterns(provider, consumer, terracotta, glazedTerracotta);
        addSetPatterns(provider, consumer, terracottaSet, glazedTerracottaSet);
    }

    private void addSetPatterns(HolderLookup.Provider provider, Consumer<DecayPatternHolder> consumer, ModBlocks.DecayGroupSet to, ModBlocks.DecayGroupSet... from) {
        addPatterns(provider, consumer, to.fence(), selectSetEntries(from, ModBlocks.DecayGroupSet::fence));
        addPatterns(provider, consumer, to.gate(), selectSetEntries(from, ModBlocks.DecayGroupSet::gate));
        addPatterns(provider, consumer, to.button(), selectSetEntries(from, ModBlocks.DecayGroupSet::button));
        addPatterns(provider, consumer, to.slab(), selectSetEntries(from, ModBlocks.DecayGroupSet::slab));
        addPatterns(provider, consumer, to.stairs(), selectSetEntries(from, ModBlocks.DecayGroupSet::stairs));
        addPatterns(provider, consumer, to.wall(), selectSetEntries(from, ModBlocks.DecayGroupSet::wall));
    }

    private Object[] selectSetEntries(ModBlocks.DecayGroupSet[] sets, Function<ModBlocks.DecayGroupSet, ?> accessor) {
        return Arrays.stream(sets).map(accessor).toArray(Object[]::new);
    }

    private void addPairedPatterns(HolderLookup.Provider provider, Consumer<DecayPatternHolder> consumer, Block[] to, Block[] from) {
        for (int i = 0; i < Math.min(to.length, from.length); i++) {
            addPattern(DimensionalDoors.id(getId(to[i]) + "_from_" + getId(from[i])), to[i], from[i]).accept(consumer, provider);
        }
    }

    private void addPatterns(HolderLookup.Provider provider, Consumer<DecayPatternHolder> consumer, Object to, Object... from) {
        String targetId = getId(to);

        for (Object source : from) {
            String sourceId = getId(source);
            String id = from.length > 1 && sourceId != null ? targetId + "_from_" + sourceId : targetId;

            addPattern(DimensionalDoors.id(id), to, source).accept(consumer, provider);
        }
    }

    //    @Override
//    protected void generatePatterns(HolderLookup.Provider provider, Consumer<DecayPatternHolder> consumer) {
//        addPattern(Blocks.AIR, ModBlockTags.DECAYS_TO_AIR).accept(consumer, provider);
//
//        addPattern(Blocks.AIR, ModBlockTags.DECAYS_TO_AIR).accept(consumer, provider);
//        addPattern(ModBlocks.GRITTY_STONE, ModBlockTags.DECAYS_TO_GRITTY_STONE).accept(consumer, provider);
//        consumer.accept(new DecayPatternHolder(DimensionalDoors.id("black_ancient_fabric"), new CompoundDecayPattern(List.of(DimensionDecayCondition.of(ModDimensions.LIMBO_TYPE_KEY, true), SimpleDecayCondition.of(Blocks.BEDROCK)), new SingleBlockDecayResult(1, DEFAULT, ModBlocks.BLACK_ANCIENT_FABRIC.get()))));
//        DecayPatternHolder.builder(DimensionalDoors.id("powder_snow"))
//                        .pattern(CompoundDecayPattern.builder().result(BlockDecayResult.single(Blocks.POWDER_SNOW))
//                                        .condition(FluidDecayCondition.of(
//                                                Fluids.LAVA.builtInRegistryHolder().key(),
//                                                true,
//                                                FluidDecayCondition.Type.FLOWING)))
//                .accept(consumer, provider);
//
//        DecayPatternHolder.builder(DimensionalDoors.id("solid_static"))
//                .pattern(CompoundDecayPattern.builder()
//                        .conditions(
//                                DimensionDecayCondition.of(ModDimensions.LIMBO_TYPE_KEY),
//                                SimpleDecayCondition.of(ModBlockTags.DECAYS_TO_SOLID_STATIC))
//                        .result(BlockDecayResult.single(ModBlocks.SOLID_STATIC)))
//                .accept(consumer, provider);
//        addPattern(ModBlocks.UNRAVELLED_FABRIC, ModBlockTags.DECAYS_TO_UNRAVELED_FABRIC).accept(consumer, provider);
//        addPattern(ModBlocks.UNRAVELED_FENCE, ModBlockTags.DECAYS_TO_UNRAVELED_FENCE).accept(consumer, provider);
//        addPattern(ModBlocks.UNRAVELED_GATE, ModBlockTags.DECAYS_TO_UNRAVELED_GATE).accept(consumer, provider);
//        addPattern(ModBlocks.UNRAVELED_BUTTON, ModBlockTags.DECAYS_TO_UNRAVELED_BUTTON).accept(consumer, provider);
//        addPattern(ModBlocks.UNRAVELED_SLAB, ModBlockTags.DECAYS_TO_UNRAVELED_SLAB).accept(consumer, provider);
//        addPattern(ModBlocks.UNRAVELED_STAIRS, ModBlockTags.DECAYS_TO_UNRAVELED_STAIRS).accept(consumer, provider);
//        addPattern(ModBlocks.UNRAVELED_SPIKE, ModBlockTags.DECAYS_TO_UNRAVELED_SPIKE).accept(consumer, provider);
//
//        addPattern(ModBlocks.DRIFTWOOD_LEAVES, BlockTags.LEAVES).accept(consumer, provider);
//        addPattern(ModBlocks.DRIFTWOOD_SAPLING, BlockTags.SAPLINGS).accept(consumer, provider);
//        addPattern(Blocks.WITHER_ROSE, ModBlockTags.DECAYS_TO_WITHER_ROSE).accept(consumer, provider);
//        addPattern(ModBlocks.AMALGAM_TRAPDOOR, ModBlockTags.DECAYS_TO_AMALGAM_TRAPDOOR).accept(consumer, provider);
//        addPattern(ModBlocks.DARK_SAND_LAYER, ModBlockTags.DECAYS_TO_DARK_SAND_LAYER).accept(consumer, provider);
//        addPattern(ModBlocks.DRIFTWOOD_TRAPDOOR, BlockTags.WOODEN_TRAPDOORS).accept(consumer, provider);
//        DecayPatternHolder.builder(DimensionalDoors.id("driftwood_trapdoor_decay_door"))
//                        .pattern(CompoundDecayPattern.builder()
//                                .condition(SimpleDecayCondition.of(ModBlocks.DRIFTWOOD_DOOR))
//                                .result(BlockDecayResult.doubly(ModBlocks.DRIFTWOOD_TRAPDOOR)))
//                                .accept(consumer, provider);
//        addPattern(ModBlocks.DARK_SAND, ModBlockTags.DECAYS_TO_DARK_SAND).accept(consumer, provider);
//        addPattern(ModBlocks.DARK_SAND_FENCE, ModBlockTags.DECAYS_TO_DARK_SAND_FENCE).accept(consumer, provider);
//        addPattern(ModBlocks.DARK_SAND_BUTTON, ModBlockTags.DECAYS_TO_DARK_SAND_BUTTON).accept(consumer, provider);
//        addPattern(ModBlocks.DARK_SAND_SLAB, ModBlockTags.DECAYS_TO_DARK_SAND_SLAB).accept(consumer, provider);
//        addPattern(ModBlocks.DARK_SAND_STAIRS, ModBlockTags.DECAYS_TO_DARK_SAND_STAIRS).accept(consumer, provider);
//        addPattern(ModBlocks.DARK_SAND_WALL, ModBlockTags.DECAYS_TO_DARK_SAND_WALL).accept(consumer, provider);
//        addPattern(ModBlocks.CLAY_DOOR, ModBlockTags.DECAYS_TO_CLAY_DOOR).accept(consumer, provider);
//        addPattern(Blocks.WHITE_WOOL, Blocks.TARGET).accept(consumer, provider);
//        addDoublePattern(DimensionalDoors.id("wool_bed"), Blocks.WHITE_WOOL, BlockTags.BEDS).accept(consumer, provider);
//        addPattern(ModBlocks.AMALGAM_SPIKE, ModBlockTags.DECAYS_TO_AMALGAM_SPIKE);
//
//        addPattern(ModBlocks.LEAK, Fluids.WATER).accept(consumer, provider);
//
//        addPattern(Blocks.MOSS_CARPET, ModBlockTags.DECAYS_TO_MOSS_CARPET).accept(consumer, provider);
//        addPattern(Blocks.RAIL, ModBlockTags.DECAYS_TO_RAIL).accept(consumer, provider);
//        addPattern(Blocks.GLASS_PANE, ModBlockTags.DECAYS_TO_TO_GLASS_PANE).accept(consumer, provider);
//        addPattern(ModBlocks.DRIFTWOOD_TRAPDOOR, BlockTags.WOODEN_TRAPDOORS).accept(consumer, provider);
//        addDoublePattern(DimensionalDoors.id("driftwood_trapdoor_door"), ModBlocks.DRIFTWOOD_TRAPDOOR, ModBlocks.DRIFTWOOD_DOOR).accept(consumer, provider);
//        addPattern(Blocks.CLAY, ModBlockTags.DECAYS_TO_CLAY).accept(consumer, provider);
//        addPattern(ModBlocks.CLAY_FENCE, ModBlockTags.DECAYS_TO_CLAY_FENCE).accept(consumer, provider);
//        addPattern(ModBlocks.CLAY_GATE, ModBlockTags.DECAYS_TO_CLAY_GATE).accept(consumer, provider);
//        addPattern(ModBlocks.CLAY_WALL, ModBlockTags.DECAYS_TO_CLAY_WALL).accept(consumer, provider);
//        addPattern(ModBlocks.CLAY_BUTTON, ModBlockTags.DECAYS_TO_CLAY_BUTTON).accept(consumer, provider);
//        addPattern(ModBlocks.CLAY_SLAB, ModBlockTags.DECAYS_TO_CLAY_SLAB).accept(consumer, provider);
//        addPattern(ModBlocks.CLAY_STAIRS, ModBlockTags.DECAYS_TO_CLAY_STAIRS).accept(consumer, provider);
//        addPattern(ModBlocks.CLAY_DOOR, ModBlockTags.DECAYS_TO_CLAY_DOOR).accept(consumer, provider);
//        addPattern(ModBlocks.CLAY_TRAP_DOOR, ModBlockTags.DECAYS_TO_CLAY_TRAP_DOOR).accept(consumer, provider);
//
//
//        Stream.of(DyeColor.values()).map(DyeColor::getSerializedName).forEach(name -> {
//            addPattern(getBlock(ResourceLocation.tryParse(name + "_terracotta")), getBlock(ResourceLocation.tryParse(name + "_glazed_terracotta"))).accept(consumer, provider);
//            addPattern(getBlock(ResourceLocation.tryParse(name + "_concrete")), getBlock(ResourceLocation.tryParse(name + "_concrete_powder"))).accept(consumer, provider);
//        });
//        addPattern(Blocks.GLASS, ModBlockTags.DECAYS_TO_GLASS).accept(consumer, provider);
//
//        addPattern(Blocks.RED_SAND, Blocks.RED_SANDSTONE).accept(consumer, provider);
//        addPattern(ModBlocks.RED_SAND_SLAB, ModBlockTags.DECAYS_TO_RED_SAND_SLAB).accept(consumer, provider);
//        addPattern(ModBlocks.RED_SAND_STAIRS, ModBlockTags.DECAYS_TO_RED_SAND_STAIRS).accept(consumer, provider);
//        addPattern(ModBlocks.RED_SAND_WALL, ModBlockTags.DECAYS_TO_RED_SAND_WALL).accept(consumer, provider);
//        addPattern(Blocks.SAND, ModBlockTags.DECAYS_TO_SAND).accept(consumer, provider);
//        addPattern(ModBlocks.SAND_SLAB, ModBlockTags.DECAYS_TO_SAND_SLAB).accept(consumer, provider);
//        addPattern(ModBlocks.SAND_STAIRS, ModBlockTags.DECAYS_TO_SAND_STAIRS).accept(consumer, provider);
//        addPattern(ModBlocks.SAND_WALL, ModBlockTags.DECAYS_TO_SAND_WALL).accept(consumer, provider);
//        addPattern(Blocks.SOUL_SAND, Blocks.SOUL_SOIL).accept(consumer, provider);
//        addPattern(Blocks.STONE, ModBlockTags.DECAYS_TO_STONE).accept(consumer, provider);
//        addPattern(ModBlocks.STONE_SLAB, ModBlockTags.DECAYS_TO_STONE_SLAB).accept(consumer, provider);
//        addPattern(ModBlocks.STONE_STAIRS, ModBlockTags.DECAYS_TO_STONE_STAIRS).accept(consumer, provider);
//        addPattern(ModBlocks.STONE_WALL, ModBlockTags.DECAYS_TO_STONE_WALL).accept(consumer, provider);
//        addPattern(ModBlocks.STONE_DOOR, ModBlockTags.DECAYS_TO_STONE_DOOR).accept(consumer, provider);
//        addPattern(ModBlocks.STONE_TRAPDOOR, ModBlockTags.DECAYS_TO_STONE_TRAPDOOR).accept(consumer, provider);
//        addPattern(Blocks.SNOW_BLOCK, ModFluids.LEAK);
//        DecayPatternHolder.builder(DimensionalDoors.id("snow_block"))
//                .pattern(CompoundDecayPattern.builder().result(BlockDecayResult.single(Blocks.SNOW_BLOCK))
//                        .condition(FluidDecayCondition.of(
//                                Fluids.LAVA.builtInRegistryHolder().key(),
//                                true,
//                                FluidDecayCondition.Type.FLOWING)))
//                .accept(consumer, provider);
//        addPattern(Blocks.ICE, Blocks.PACKED_ICE).accept(consumer, provider);
//
//        addPattern(Blocks.MUD, ModBlockTags.DECAYS_TO_MUD).accept(consumer, provider);
//        addPattern(ModBlocks.MUD_FENCE, ModBlockTags.DECAYS_TO_MUD_FENCE).accept(consumer, provider);
//        addPattern(ModBlocks.MUD_GATE, ModBlockTags.DECAYS_TO_MUD_GATE).accept(consumer, provider);
//        addPattern(ModBlocks.MUD_BUTTON, ModBlockTags.DECAYS_TO_MUD_BUTTON).accept(consumer, provider);
//        addPattern(ModBlocks.MUD_SLAB, ModBlockTags.DECAYS_TO_MUD_SLAB).accept(consumer, provider);
//        addPattern(ModBlocks.MUD_STAIRS, ModBlockTags.DECAYS_TO_MUD_STAIRS).accept(consumer, provider);
//        addPattern(ModBlocks.MUD_DOOR, ModBlockTags.DECAYS_TO_MUD_DOOR).accept(consumer, provider);
//        addPattern(ModBlocks.MUD_TRAP_DOOR, ModBlockTags.DECAYS_TO_CLAY_MUD_DOOR).accept(consumer, provider);
//        addPattern(Blocks.REDSTONE_LAMP, Blocks.BEACON);
//        addPattern(Blocks.REDSTONE_LAMP, Blocks.BEACON).accept(consumer, provider);
//        addPattern(ModBlocks.AMALGAM_ORE, ModBlockTags.DECAYS_TO_AMALGAM_ORE).accept(consumer, provider);
//        addPattern(ModBlocks.CLOD_ORE, ModBlockTags.DECAYS_TO_CLOD_ORE).accept(consumer, provider);
//        addPattern(Blocks.COBBLESTONE, ModBlockTags.DECAYS_TO_COBBLESTONE).accept(consumer, provider);
//        addPattern(Blocks.COBBLESTONE_SLAB, ModBlockTags.DECAYS_TO_COBBLESTONE_SLAB).accept(consumer, provider);
//        addPattern(Blocks.COBBLESTONE_STAIRS, ModBlockTags.DECAYS_TO_COBBLESTONE_STAIRS).accept(consumer, provider);
//        addPattern(Blocks.COBBLESTONE_WALL, ModBlockTags.DECAYS_TO_COBBLESTONE_WALL).accept(consumer, provider);
//        addPattern(Blocks.NETHERRACK, ModBlockTags.DECAYS_TO_NETHERRACK).accept(consumer, provider);
//        addPattern(ModBlocks.NETHERRACK_FENCE, ModBlockTags.DECAYS_TO_NETHERRACK_FENCE).accept(consumer, provider);
//        addPattern(ModBlocks.NETHERRACK_SLAB, ModBlockTags.DECAYS_TO_NETHERRACK_SLAB).accept(consumer, provider);
//        addPattern(ModBlocks.NETHERRACK_STAIRS, ModBlockTags.DECAYS_TO_NETHERRACK_STAIRS).accept(consumer, provider);
//        addPattern(ModBlocks.NETHERRACK_WALL, ModBlockTags.DECAYS_TO_NETHERRACK_WALL).accept(consumer, provider);
//        addPattern(ModBlocks.NETHERRACK_FENCE Blocks.NETHERRACK, Blocks.NETHER_BRICKS).accept(consumer, provider);
//        addPattern(Blocks.OBSIDIAN, ModuleSourceProviderBase.DECAYS_TO_OBSIDIAN).accept(consumer, provider);
//        addPattern(Blocks.PRISMARINE, ModBlockTags.DECAYS_TO_PRISMARINE).accept(consumer, provider);
//        addPattern(Blocks.PRISMARINE_SLAB, ModBlockTags.DECAYS_TO_PRISMARINE_SLAB).accept(consumer, provider);
//        addPattern(Blocks.PRISMARINE_STAIRS, ModBlockTags.DECAYS_TO_PRISMARINE_STAIRS).accept(consumer, provider);
//        addPattern(Blocks.PRISMARINE_WALL, ModBlockTags.DECAYS_TO_PRISMARINE_WALL).accept(consumer, provider);
//        addPattern(Blocks.STONE_BRICKS, ModBlockTags.DECAYS_TO_STONE_BRICKS).accept(consumer, provider);
//        addPattern(Blocks.STONE_BRICK_SLAB, ModBlockTags.DECAYS_TO_STONE_BRICK_SLAB).accept(consumer, provider);
//        addPattern(Blocks.STONE_BRICK_STAIRS, ModBlockTags.DECAYS_TO_STONE_BRICK_STAIRS).accept(consumer, provider);
//        addPattern(Blocks.STONE_BRICK_WALL, ModBlockTags.DECAYS_TO_STONE_BRICK_WALL).accept(consumer, provider);
//
//        addPattern(Blocks.BONE_BLOCK, Blocks.CONDUIT).accept(consumer, provider);
//        addPattern(Blocks.CHEST, ModBlockTags.DECAYS_TO_CHEST).accept(consumer, provider);
//        addPattern(Blocks.COMPOSTER, Blocks.BARREL).accept(consumer, provider);
//        addPattern(Blocks.DIRT, ModBlockTags.DECAYS_TO_DIRT).accept(consumer, provider);
//
//
//
//
//        addPattern(ModBlocks.DRIFTWOOD_DOOR, ModBlockTags.DECAYS_TO_DRIFTWOOD_DOOR).accept(consumer, provider);
//
//        addPattern(Blocks.ICE, Blocks.PACKED_ICE).accept(consumer, provider);
//        addPattern(Blocks.IRON_BLOCK, Blocks.ANVIL).accept(consumer, provider);
//
//
//        createOxidizationChain(
//                consumer, provider,
//                Blocks.COPPER_BLOCK, Blocks.WAXED_COPPER_BLOCK,
//                Blocks.EXPOSED_COPPER, Blocks.WAXED_EXPOSED_COPPER,
//                Blocks.WEATHERED_COPPER, Blocks.WAXED_WEATHERED_COPPER,
//                Blocks.OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_COPPER
//        );
//
//        createOxidizationChain(
//                consumer, provider,
//                Blocks.CUT_COPPER, Blocks.WAXED_CUT_COPPER,
//                Blocks.EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER,
//                Blocks.WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER,
//                Blocks.OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER
//        );
//
//        createOxidizationChain(
//                consumer, provider,
//                Blocks.CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER_SLAB,
//                Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB,
//                Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB,
//                Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB
//        );
//
//        createOxidizationChain(
//                consumer, provider,
//                Blocks.CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER_STAIRS,
//                Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS,
//                Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS,
//                Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS
//        );
//
//        createOxidizationChain(
//                consumer, provider,
//                Blocks.COPPER_BULB, Blocks.WAXED_COPPER_BULB,
//                Blocks.EXPOSED_COPPER_BULB, Blocks.WAXED_EXPOSED_COPPER_BULB,
//                Blocks.WEATHERED_COPPER_BULB, Blocks.WAXED_WEATHERED_COPPER_BULB,
//                Blocks.OXIDIZED_COPPER_BULB, Blocks.WAXED_OXIDIZED_COPPER_BULB
//        );
//
//        createOxidizationChain(
//                consumer, provider,
//                Blocks.COPPER_DOOR, Blocks.WAXED_COPPER_DOOR,
//                Blocks.EXPOSED_COPPER_DOOR, Blocks.WAXED_EXPOSED_COPPER_DOOR,
//                Blocks.WEATHERED_COPPER_DOOR, Blocks.WAXED_WEATHERED_COPPER_DOOR,
//                Blocks.OXIDIZED_COPPER_DOOR, Blocks.WAXED_OXIDIZED_COPPER_DOOR
//        );
//
//        createOxidizationChain(
//                consumer, provider,
//                Blocks.COPPER_DOOR, Blocks.WAXED_COPPER_DOOR,
//                Blocks.EXPOSED_COPPER_DOOR, Blocks.WAXED_EXPOSED_COPPER_DOOR,
//                Blocks.WEATHERED_COPPER_DOOR, Blocks.WAXED_WEATHERED_COPPER_DOOR,
//                Blocks.OXIDIZED_COPPER_DOOR, Blocks.WAXED_OXIDIZED_COPPER_DOOR
//        );
//
//        for (var key : ModPaintings.PAINTINGS_TO_DECAY_INTO) {
//            var id = key.location().withPrefix("decays_into_");
//
//            if (!id.getPath().contains("placeholder")) id = id.withSuffix("_painting");
//
//            addPaintingPattern(key, TagKey.create(key.registryKey(), id)).accept(consumer, provider);
//        }
//    }

    @Override
    public String getName() {
        return "Abstraction Decay Patterns";
    }
}
