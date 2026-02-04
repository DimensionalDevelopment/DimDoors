package org.dimdev.dimdoors.datagen;

import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.painting.ModPaintings;
import org.dimdev.dimdoors.tag.ModBlockTags;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.decay.DecayPatternHolder;
import org.dimdev.dimdoors.world.decay.conditions.DimensionDecayCondition;
import org.dimdev.dimdoors.world.decay.conditions.FluidDecayCondition;
import org.dimdev.dimdoors.world.decay.conditions.SimpleDecayCondition;
import org.dimdev.dimdoors.world.decay.pattern.CompoundDecayPattern;
import org.dimdev.dimdoors.world.decay.pattern.DecayPattern;
import org.dimdev.dimdoors.world.decay.results.BlockDecayResult;
import org.dimdev.dimdoors.world.decay.results.SingleBlockDecayResult;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProviderBase;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class AbstractionDecayProvider extends LimboDecayProvider {
    public AbstractionDecayProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void generatePatterns(HolderLookup.Provider provider, Consumer<DecayPatternHolder> consumer) {
//        addPattern(Blocks.AIR, ModBlockTags.DECAYS_TO_AIR).accept(consumer, provider);
//        addPattern(ModBlocks.GRITTY_STONE, ModBlockTags.DECAYS_TO_GRITTY_STONE).accept(consumer, provider);
//        DecayPatternHolder.builder(DimensionalDoors.id("powder_snow"))
//                        .pattern(CompoundDecayPattern.builder().result(BlockDecayResult.single(Blocks.POWDER_SNOW))
//                                        .condition(
//                                                FluidDecayCondition.of(Fluids.LAVA, true, FluidDecayCondition.Type.FLOWING)
//                                        )
//                        ).accept(consumer, provider);
//
//        consumer.accept(new DecayPatternHolder(DimensionalDoors.id("solid_static"), new CompoundDecayPattern(List.of(DimensionDecayCondition.of(ModDimensions.LIMBO_TYPE_KEY, true), SimpleDecayCondition.of(Blocks.BEDROCK)), new SingleBlockDecayResult(1, DEFAULT, ModBlocks.BLACK_ANCIENT_FABRIC.get()))));
//        DecayPatternHolder.builder(DimensionalDoors.id("solid_static"))
//                .pattern(CompoundDecayPattern.builder()
//                        .conditions(
//                                DimensionDecayCondition.of(ModDimensions.LIMBO_TYPE_KEY),
//                                SimpleDecayCondition.of(ModBlockTags.DECAYS_TO_SOLID_STATIC))
//                        .result(BlockDecayResult.single(ModBlocks.SOLID_STATIC)))
//                .accept(consumer, provider);
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
