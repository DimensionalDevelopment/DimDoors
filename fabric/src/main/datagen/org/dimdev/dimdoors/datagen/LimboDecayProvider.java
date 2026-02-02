package org.dimdev.dimdoors.datagen;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.painting.ModPaintings;
import org.dimdev.dimdoors.tag.ModBlockTags;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.decay.conditions.DecayCondition;
import org.dimdev.dimdoors.world.decay.pattern.CompoundDecayPattern;
import org.dimdev.dimdoors.world.decay.pattern.DecayPattern;
import org.dimdev.dimdoors.world.decay.DecayPatternHolder;
import org.dimdev.dimdoors.world.decay.pattern.PaintingDecayPattern;
import org.dimdev.dimdoors.world.decay.results.DecayResult;
import org.dimdev.dimdoors.world.decay.conditions.DimensionDecayCondition;
import org.dimdev.dimdoors.world.decay.conditions.FluidDecayCondition;
import org.dimdev.dimdoors.world.decay.conditions.SimpleDecayCondition;
import org.dimdev.dimdoors.world.decay.results.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class LimboDecayProvider implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

	private final PackOutput.PathProvider decayPatternPathResolver;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public LimboDecayProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		this.decayPatternPathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, "decay_patterns");
        this.registries = registries;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return this.registries.thenCompose(provider -> {
            Set<ResourceLocation> generatedDecayPatterns = Sets.newHashSet();
            List<CompletableFuture<?>> list = new ArrayList<>();

            Consumer<DecayPatternHolder> consumer = (patternHolder) -> {
                JsonElement object = JsonOps.INSTANCE.withEncoder(DecayPattern.CODEC).apply(patternHolder.value()).getOrThrow();
                Path outputPath = decayPatternPathResolver.json(patternHolder.id());
                list.add(DataProvider.saveStable(cache, object, outputPath));
            };

            generatePatterns(provider, consumer);

            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

	protected void generatePatterns(HolderLookup.Provider provider, Consumer<DecayPatternHolder> consumer) {
        addPattern(Blocks.AIR, ModBlockTags.DECAYS_TO_AIR).accept(consumer, provider);

        addPattern(Blocks.AIR, ModBlockTags.DECAYS_TO_AIR).accept(consumer, provider);
        addPattern(ModBlocks.GRITTY_STONE, ModBlockTags.DECAYS_TO_GRITTY_STONE).accept(consumer, provider);
        addPattern(ModBlocks.LEAK, Fluids.WATER).accept(consumer, provider);
		consumer.accept(new DecayPatternHolder(DimensionalDoors.id("solid_static"), new CompoundDecayPattern(List.of(DimensionDecayCondition.of(ModDimensions.LIMBO_TYPE_KEY), SimpleDecayCondition.of(ModBlockTags.DECAYS_TO_SOLID_STATIC)), new BlockDecayImplResult(1, DEFAULT, ModBlocks.SOLID_STATIC.get()))));
		consumer.accept(new DecayPatternHolder(DimensionalDoors.id("black_ancient_fabric"), new CompoundDecayPattern(List.of(DimensionDecayCondition.of(ModDimensions.LIMBO_TYPE_KEY, true), SimpleDecayCondition.of(Blocks.BEDROCK)), new BlockDecayImplResult(1, DEFAULT, ModBlocks.BLACK_ANCIENT_FABRIC.get()))));
		addPattern(ModBlocks.UNRAVELLED_FABRIC, ModBlockTags.DECAYS_TO_UNRAVELED_FABRIC).accept(consumer, provider);
		addPattern(ModBlocks.UNRAVELED_FENCE, ModBlockTags.DECAYS_TO_UNRAVELED_FENCE).accept(consumer, provider);
		addPattern(ModBlocks.UNRAVELED_GATE, ModBlockTags.DECAYS_TO_UNRAVELED_GATE).accept(consumer, provider);
		addPattern(ModBlocks.UNRAVELED_BUTTON, ModBlockTags.DECAYS_TO_UNRAVELED_BUTTON).accept(consumer, provider);
		addPattern(ModBlocks.UNRAVELED_SLAB, ModBlockTags.DECAYS_TO_UNRAVELED_SLAB).accept(consumer, provider);
		addPattern(ModBlocks.UNRAVELED_STAIRS, ModBlockTags.DECAYS_TO_UNRAVELED_STAIRS).accept(consumer, provider);

		addPattern(Blocks.COBWEB, BlockTags.WOOL).accept(consumer, provider);
		addPattern(ModBlocks.DRIFTWOOD_LEAVES, BlockTags.LEAVES).accept(consumer, provider);
		addPattern(ModBlocks.DRIFTWOOD_SAPLING, BlockTags.SAPLINGS).accept(consumer, provider);
		addPattern(Blocks.GLASS_PANE, ModBlockTags.DECAYS_TO_TO_GLASS_PANE).accept(consumer, provider);
		addPattern(Blocks.MOSS_CARPET, ModBlockTags.DECAYS_TO_MOSS_CARPET).accept(consumer, provider);
		addPattern(ModBlocks.DRIFTWOOD_TRAPDOOR, BlockTags.WOODEN_TRAPDOORS).accept(consumer, provider);
		addDoublePattern(DimensionalDoors.id("driftwood_trapdoor_door"), ModBlocks.DRIFTWOOD_TRAPDOOR, ModBlocks.DRIFTWOOD_DOOR).accept(consumer, provider);
		addPattern(Blocks.RAIL, ModBlockTags.DECAYS_TO_RAIL).accept(consumer, provider);
		addPattern(ModBlocks.RUST, ModBlockTags.DECAYS_TO_RUST).accept(consumer, provider);
		addPattern(ModBlocks.UNRAVELED_SPIKE, ModBlockTags.DECAYS_TO_UNRAVELED_SPIKE).accept(consumer, provider);
		addPattern(Blocks.WITHER_ROSE, ModBlockTags.DECAYS_TO_WITHER_ROSE).accept(consumer, provider);
		addPattern(Fluids.WATER, Blocks.SNOW).accept(consumer, provider);
		addPattern(Blocks.CLAY, ModBlockTags.DECAYS_TO_CLAY).accept(consumer, provider);
		addPattern(ModBlocks.CLAY_FENCE, ModBlockTags.DECAYS_TO_CLAY_FENCE).accept(consumer, provider);
		addPattern(ModBlocks.CLAY_GATE, ModBlockTags.DECAYS_TO_CLAY_GATE).accept(consumer, provider);
		addPattern(ModBlocks.CLAY_WALL, ModBlockTags.DECAYS_TO_CLAY_WALL).accept(consumer, provider);
		addPattern(ModBlocks.CLAY_BUTTON, ModBlockTags.DECAYS_TO_CLAY_BUTTON).accept(consumer, provider);
		addPattern(ModBlocks.CLAY_SLAB, ModBlockTags.DECAYS_TO_CLAY_SLAB).accept(consumer, provider);
		addPattern(ModBlocks.CLAY_STAIRS, ModBlockTags.DECAYS_TO_CLAY_STAIRS).accept(consumer, provider);
		addPattern(ModBlocks.DARK_SAND, ModBlockTags.DECAYS_TO_DARK_SAND).accept(consumer, provider);
	 	addPattern(ModBlocks.DARK_SAND_FENCE, ModBlocks.GRAVEL_FENCE).accept(consumer, provider);
	 	addPattern(ModBlocks.DARK_SAND_BUTTON, ModBlockTags.DECAYS_TO_DARK_SAND_BUTTON).accept(consumer, provider);
	 	addPattern(ModBlocks.DARK_SAND_SLAB, ModBlockTags.DECAYS_TO_DARK_SAND_SLAB).accept(consumer, provider);
	 	addPattern(ModBlocks.DARK_SAND_STAIRS, ModBlockTags.DECAYS_TO_DARK_SAND_STAIRS).accept(consumer, provider);

		addPattern(Blocks.WHITE_WOOL, Blocks.TARGET).accept(consumer, provider);
		addDoublePattern(DimensionalDoors.id("wool_bed"), Blocks.WHITE_WOOL, BlockTags.BEDS).accept(consumer, provider);
		addPattern(ModBlocks.DRIFTWOOD_DOOR, ModBlockTags.DECAYS_TO_DRIFTWOOD_DOOR).accept(consumer, provider);
		addPattern(ModBlocks.AMALGAM_BLOCK, ModBlockTags.DECAYS_TO_AMALGAM).accept(consumer, provider);
		addPattern(ModBlocks.AMALGAM_SLAB, Blocks.CUT_COPPER_SLAB).accept(consumer, provider);
		addPattern(ModBlocks.AMALGAM_STAIRS, Blocks.CUT_COPPER_STAIRS).accept(consumer, provider);
		addPattern(Blocks.MUD, ModBlockTags.DECAYS_TO_MUD).accept(consumer, provider);
		addPattern(ModBlocks.MUD_FENCE, ModBlockTags.DECAYS_TO_MUD_FENCE).accept(consumer, provider);
		addPattern(ModBlocks.MUD_GATE, ModBlockTags.DECAYS_TO_MUD_GATE).accept(consumer, provider);
		addPattern(ModBlocks.MUD_BUTTON, ModBlockTags.DECAYS_TO_MUD_BUTTON).accept(consumer, provider);
		addPattern(ModBlocks.MUD_SLAB, ModBlockTags.DECAYS_TO_MUD_SLAB).accept(consumer, provider);
		addPattern(ModBlocks.MUD_STAIRS, ModBlockTags.DECAYS_TO_MUD_STAIRS).accept(consumer, provider);
		Stream.of(DyeColor.values()).map(DyeColor::getSerializedName).forEach(name -> {
			addPattern(getBlock(ResourceLocation.tryParse(name + "_terracotta")), getBlock(ResourceLocation.tryParse(name + "_glazed_terracotta"))).accept(consumer, provider);
			addPattern(getBlock(ResourceLocation.tryParse(name + "_concrete")), getBlock(ResourceLocation.tryParse(name + "_concrete_powder"))).accept(consumer, provider);
		});
		addPattern(Blocks.GLASS, ModBlockTags.DECAYS_TO_GLASS).accept(consumer, provider);
		addPattern(Blocks.GRAVEL, ModBlockTags.DECAYS_TO_GRAVEL).accept(consumer, provider);
		addPattern(ModBlocks.GRAVEL_FENCE, ModBlockTags.DECAYS_TO_GRAVEL_FENCE).accept(consumer, provider);
		addPattern(ModBlocks.GRAVEL_BUTTON, ModBlockTags.DECAYS_TO_GRAVEL_BUTTON).accept(consumer, provider);
		addPattern(ModBlocks.GRAVEL_SLAB, ModBlockTags.DECAYS_TO_GRAVEL_SLAB).accept(consumer, provider);
		addPattern(ModBlocks.GRAVEL_STAIRS, ModBlockTags.DECAYS_TO_GRAVEL_STAIRS).accept(consumer, provider);
		addPattern(ModBlocks.GRAVEL_WALL, ModBlockTags.DECAYS_TO_GRAVEL_WALL).accept(consumer, provider);
		addPattern(Blocks.RED_SAND, Blocks.RED_SANDSTONE).accept(consumer, provider);
		addPattern(ModBlocks.RED_SAND_SLAB, ModBlockTags.DECAYS_TO_RED_SAND_SLAB).accept(consumer, provider);
		addPattern(ModBlocks.RED_SAND_STAIRS, ModBlockTags.DECAYS_TO_RED_SAND_STAIRS).accept(consumer, provider);
		addPattern(ModBlocks.RED_SAND_WALL, ModBlockTags.DECAYS_TO_RED_SAND_WALL).accept(consumer, provider);
		addPattern(Blocks.SAND, ModBlockTags.DECAYS_TO_SAND).accept(consumer, provider);
		addPattern(ModBlocks.SAND_SLAB, ModBlockTags.DECAYS_TO_SAND_SLAB).accept(consumer, provider);
		addPattern(ModBlocks.SAND_STAIRS, ModBlockTags.DECAYS_TO_SAND_STAIRS).accept(consumer, provider);
		addPattern(ModBlocks.SAND_WALL, ModBlockTags.DECAYS_TO_SAND_WALL).accept(consumer, provider);
		addPattern(Blocks.SOUL_SAND, Blocks.SOUL_SOIL).accept(consumer, provider);

		addPattern(Blocks.ICE, Blocks.PACKED_ICE).accept(consumer, provider);
		addPattern(Blocks.IRON_BLOCK, Blocks.ANVIL).accept(consumer, provider);

        //TODO: chnage to a dedicated Oxidation pattern
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

		createOxidizationChain(
                consumer, provider,
                Blocks.CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER_SLAB,
                Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB,
                Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB,
                Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB
        );

        createOxidizationChain(
                consumer, provider,
                Blocks.CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER_STAIRS,
                Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS,
                Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS,
                Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS
        );

        createOxidizationChain(
                consumer, provider,
                Blocks.COPPER_BULB, Blocks.WAXED_COPPER_BULB,
                Blocks.EXPOSED_COPPER_BULB, Blocks.WAXED_EXPOSED_COPPER_BULB,
                Blocks.WEATHERED_COPPER_BULB, Blocks.WAXED_WEATHERED_COPPER_BULB,
                Blocks.OXIDIZED_COPPER_BULB, Blocks.WAXED_OXIDIZED_COPPER_BULB
        );

        createOxidizationChain(
                consumer, provider,
                Blocks.COPPER_DOOR, Blocks.WAXED_COPPER_DOOR,
                Blocks.EXPOSED_COPPER_DOOR, Blocks.WAXED_EXPOSED_COPPER_DOOR,
                Blocks.WEATHERED_COPPER_DOOR, Blocks.WAXED_WEATHERED_COPPER_DOOR,
                Blocks.OXIDIZED_COPPER_DOOR, Blocks.WAXED_OXIDIZED_COPPER_DOOR
        );

        createOxidizationChain(
                consumer, provider,
                Blocks.COPPER_DOOR, Blocks.WAXED_COPPER_DOOR,
                Blocks.EXPOSED_COPPER_DOOR, Blocks.WAXED_EXPOSED_COPPER_DOOR,
                Blocks.WEATHERED_COPPER_DOOR, Blocks.WAXED_WEATHERED_COPPER_DOOR,
                Blocks.OXIDIZED_COPPER_DOOR, Blocks.WAXED_OXIDIZED_COPPER_DOOR
        );

        for (var key : ModPaintings.PLACEHOLDERS) {
            addPaintingPattern(key, TagKey.create(key.registryKey(), key.location().withPrefix("decays_into_"))).accept(consumer, provider);
        }


        addPattern(Blocks.ANCIENT_DEBRIS, Blocks.NETHERITE_BLOCK).accept(consumer, provider);
		addPattern(Blocks.DIRT, ModBlockTags.DECAYS_TO_DIRT).accept(consumer, provider);
		addPattern(Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM).accept(consumer, provider);
		addPattern(ModBlocks.DRIFTWOOD_PLANKS, ModBlockTags.DECAYS_TO_DRIFTWOOD_PLANK).accept(consumer, provider);
		addPattern(ModBlocks.DRIFTWOOD_FENCE, ModBlockTags.DECAYS_TO_DRIFTWOOD_FENCE).accept(consumer, provider);
		addPattern(ModBlocks.DRIFTWOOD_GATE, ModBlockTags.DECAYS_TO_DRIFTWOOD_GATE).accept(consumer, provider);
		addPattern(ModBlocks.DRIFTWOOD_BUTTON, ModBlockTags.DECAYS_TO_DRIFTWOOD_BUTTON).accept(consumer, provider);
		addPattern(ModBlocks.DRIFTWOOD_SLAB, ModBlockTags.DECAYS_TO_DRIFTWOOD_SLAB).accept(consumer, provider);
		addPattern(ModBlocks.DRIFTWOOD_STAIRS, ModBlockTags.DECAYS_TO_DRIFTWOOD_STAIRS).accept(consumer, provider);
		addPattern(Blocks.COMPOSTER, Blocks.BARREL).accept(consumer, provider);
		addPattern(Blocks.CHEST, ModBlockTags.DECAYS_TO_CHEST).accept(consumer, provider);
		addPattern(Blocks.BONE_BLOCK, Blocks.CONDUIT).accept(consumer, provider);
		addPattern(Blocks.SKELETON_SKULL, ModBlockTags.DECAYS_TO_SKELETON_SKULL).accept(consumer, provider);
		addPattern(Blocks.SKELETON_WALL_SKULL, ModBlockTags.DECAYS_TO_SKELETON_WALL_SKULL).accept(consumer, provider);
		addPattern(Blocks.BAMBOO, Blocks.SCAFFOLDING).accept(consumer, provider);
		addPattern(Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN).accept(consumer, provider);
		addPattern(Blocks.SLIME_BLOCK, Blocks.HONEY_BLOCK).accept(consumer, provider);
		addPattern(Blocks.HONEYCOMB_BLOCK, Blocks.SPONGE).accept(consumer, provider);
		addPattern(Blocks.LECTERN, Blocks.BOOKSHELF).accept(consumer, provider);
		addPattern(Blocks.PISTON, Blocks.STICKY_PISTON).accept(consumer, provider);
		addPattern(Blocks.NETHER_WART_BLOCK, ModBlockTags.DECAYS_TO_NETHERWART_BLOCK).accept(consumer, provider);
		addPattern(Blocks.REDSTONE_LAMP, Blocks.BEACON).accept(consumer, provider);
		addPattern(ModBlocks.AMALGAM_ORE, ModBlockTags.DECAYS_TO_AMALGAM_ORE).accept(consumer, provider);
		addPattern(ModBlocks.CLOD_ORE, ModBlockTags.DECAYS_TO_CLOD_ORE).accept(consumer, provider);
		addPattern(Blocks.COBBLESTONE, ModBlockTags.DECAYS_TO_COBBLESTONE).accept(consumer, provider);
		addPattern(Blocks.COBBLESTONE_SLAB, ModBlockTags.DECAYS_TO_COBBLESTONE_SLAB).accept(consumer, provider);
		addPattern(Blocks.COBBLESTONE_STAIRS, ModBlockTags.DECAYS_TO_COBBLESTONE_STAIRS).accept(consumer, provider);
		addPattern(Blocks.COBBLESTONE_WALL, ModBlockTags.DECAYS_TO_COBBLESTONE_WALL).accept(consumer, provider);
		addPattern(Blocks.RED_SANDSTONE, ModBlockTags.DECAYS_TO_RED_SANDSTONE).accept(consumer, provider);
		addPattern(Blocks.SANDSTONE, ModBlockTags.DECAYS_TO_SANDSTONE).accept(consumer, provider);

		addPattern(Blocks.PACKED_ICE, Blocks.BLUE_ICE).accept(consumer, provider);
		addPattern(ModBlocks.DRIFTWOOD_WOOD, ModBlockTags.DECAYS_TO_DRIFTWOOD_WOOD).accept(consumer, provider);
		addPattern(ModBlocks.DRIFTWOOD_LOG, ModBlockTags.DECAYS_TO_DRIFTWOOD_LOG).accept(consumer, provider);
		addPattern(Blocks.BARREL, Blocks.BEEHIVE).accept(consumer, provider);
		addPattern(Blocks.CARVED_PUMPKIN, Blocks.JACK_O_LANTERN).accept(consumer, provider);
		addPattern(Blocks.SPONGE, Blocks.WET_SPONGE).accept(consumer, provider);
		addPattern(Blocks.COAL_ORE, Blocks.DIAMOND_ORE).accept(consumer, provider);
		addPattern(Blocks.ANDESITE, Blocks.POLISHED_ANDESITE).accept(consumer, provider);
		addPattern(Blocks.ANDESITE_SLAB, Blocks.POLISHED_ANDESITE_SLAB).accept(consumer, provider);
		addPattern(Blocks.ANDESITE_STAIRS, Blocks.POLISHED_ANDESITE_STAIRS).accept(consumer, provider);
		addPattern(Blocks.BASALT, ModBlockTags.DECAYS_TO_BASALT).accept(consumer, provider);
		addPattern(DimensionalDoors.id("basalt_lava"), Blocks.BASALT, Fluids.LAVA).accept(consumer, provider);
		addPattern(Blocks.BLACKSTONE, ModBlockTags.DECAYS_TO_BLACKSTONE).accept(consumer, provider);
		addPattern(Blocks.BLACKSTONE_SLAB, ModBlockTags.DECAYS_TO_BLACKSTONE_SLAB).accept(consumer, provider);
		addPattern(Blocks.BLACKSTONE_STAIRS, ModBlockTags.DECAYS_TO_BLACKSTONE_STAIRS).accept(consumer, provider);
		addPattern(Blocks.BLACKSTONE_WALL, ModBlockTags.DECAYS_TO_BLACKSTONE_WALL).accept(consumer, provider);
		addPattern(Blocks.DEEPSLATE, ModBlockTags.DECAYS_TO_DEEPSLATE).accept(consumer, provider);
		addPattern(ModBlocks.DEEPSLATE_SLAB, ModBlockTags.DECAYS_TO_DEEPSLATE_SLAB).accept(consumer, provider);
		addPattern(ModBlocks.DEEPSLATE_STAIRS, ModBlockTags.DECAYS_TO_DEEPSLATE_STAIRS).accept(consumer, provider);
		addPattern(ModBlocks.DEEPSLATE_WALL, ModBlockTags.DECAYS_TO_DEEPSLATE_WALL).accept(consumer, provider);
		addPattern(Blocks.DIORITE, ModBlockTags.DECAYS_TO_DIORITE).accept(consumer, provider);
		addPattern(Blocks.DIORITE_SLAB, ModBlockTags.DECAYS_TO_DIORITE_SLAB).accept(consumer, provider);
		addPattern(Blocks.DIORITE_STAIRS, ModBlockTags.DECAYS_TO_DIORITE_STAIRS).accept(consumer, provider);
		addPattern(Blocks.DIORITE_WALL, ModBlockTags.DECAYS_TO_DIORITE_WALL).accept(consumer, provider);
		addPattern(Blocks.END_STONE, ModBlockTags.DECAYS_TO_ENDSTONE).accept(consumer, provider);
		addPattern(ModBlocks.END_STONE_SLAB, ModBlockTags.DECAYS_TO_ENDSTONE_SLAB).accept(consumer, provider);
		addPattern(ModBlocks.END_STONE_STAIRS, ModBlockTags.DECAYS_TO_ENDSTONE_STAIRS).accept(consumer, provider);
		addPattern(ModBlocks.END_STONE_WALL, ModBlockTags.DECAYS_TO_ENDSTONE_WALL).accept(consumer, provider);
		addPattern(Blocks.FURNACE, ModBlockTags.DECAYS_TO_FURNACE).accept(consumer, provider);
		addPattern(Blocks.GRANITE, ModBlockTags.DECAYS_TO_GRANITE).accept(consumer, provider);
		addPattern(Blocks.GRANITE_SLAB, ModBlockTags.DECAYS_TO_GRANITE_SLAB).accept(consumer, provider);
		addPattern(Blocks.GRANITE_STAIRS, ModBlockTags.DECAYS_TO_GRANITE_STAIRS).accept(consumer, provider);
		addPattern(Blocks.GRANITE, ModBlockTags.DECAYS_TO_GRANITE).accept(consumer, provider);
		addPattern(Blocks.GRANITE_SLAB, ModBlockTags.DECAYS_TO_GRANITE_SLAB).accept(consumer, provider);
		addPattern(Blocks.GRANITE_STAIRS, ModBlockTags.DECAYS_TO_GRANITE_STAIRS).accept(consumer, provider);
		addPattern(Blocks.NETHERRACK, ModBlockTags.DECAYS_TO_NETHERRACK).accept(consumer, provider);
		addPattern(ModBlocks.NETHERRACK_FENCE, ModBlockTags.DECAYS_TO_NETHERRACK_FENCE).accept(consumer, provider);
		addPattern(ModBlocks.NETHERRACK_SLAB, ModBlockTags.DECAYS_TO_NETHERRACK_SLAB).accept(consumer, provider);
		addPattern(ModBlocks.NETHERRACK_STAIRS, ModBlockTags.DECAYS_TO_NETHERRACK_STAIRS).accept(consumer, provider);
		addPattern(ModBlocks.NETHERRACK_WALL, ModBlockTags.DECAYS_TO_NETHERRACK_WALL).accept(consumer, provider);
		addPattern(Blocks.PRISMARINE, ModBlockTags.DECAYS_TO_PRISMARINE).accept(consumer, provider);
		addPattern(Blocks.PRISMARINE_SLAB, ModBlockTags.DECAYS_TO_PRISMARINE_SLAB).accept(consumer, provider);
		addPattern(Blocks.PRISMARINE_STAIRS, ModBlockTags.DECAYS_TO_PRISMARINE_STAIRS).accept(consumer, provider);
		addPattern(Blocks.PRISMARINE_WALL, ModBlockTags.DECAYS_TO_PRISMARINE_WALL).accept(consumer, provider);
		addPattern(Blocks.STONE, ModBlockTags.DECAYS_TO_STONE).accept(consumer, provider);

		addPattern(Fluids.LAVA, Blocks.MAGMA_BLOCK).accept(consumer, provider);
		addPattern(Blocks.DROPPER, Blocks.DISPENSER).accept(consumer, provider);
		addPattern(Blocks.DARK_PRISMARINE, ModBlockTags.DECAYS_TO_DARK_PRISMARINE).accept(consumer, provider);
		addPattern(Blocks.DARK_PRISMARINE_SLAB, ModBlockTags.DECAYS_TO_DARK_PRISMARINE_SLAB).accept(consumer, provider);
		addPattern(Blocks.DARK_PRISMARINE_STAIRS, ModBlockTags.DECAYS_TO_DARK_PRISMARINE_STAIRS).accept(consumer, provider);
		addPattern(ModBlocks.CLOD_BLOCK, ModBlockTags.DECAYS_TO_CLOD_BLOCK).accept(consumer, provider);
		addPattern(Blocks.OBSIDIAN, ModBlockTags.DECAYS_TO_OBSIDIAN).accept(consumer, provider);
		addPattern(Blocks.STONE_BRICKS, ModBlockTags.DECAYS_TO_STONE_BRICKS).accept(consumer, provider);
		addPattern(Blocks.STONE_BRICK_SLAB, ModBlockTags.DECAYS_TO_STONE_BRICK_SLAB).accept(consumer, provider);
		addPattern(Blocks.STONE_BRICK_STAIRS, ModBlockTags.DECAYS_TO_STONE_BRICK_STAIRS).accept(consumer, provider);
		addPattern(Blocks.STONE_BRICK_WALL, ModBlockTags.DECAYS_TO_STONE_BRICK_WALL).accept(consumer, provider);
		addPattern(Blocks.CRYING_OBSIDIAN, Blocks.RESPAWN_ANCHOR).accept(consumer, provider);
	}

    private DecayPatternHolder.Builder addPaintingPattern(ResourceKey<PaintingVariant> key, TagKey<PaintingVariant> decaysInto) {
        return DecayPatternHolder.builder(key.location()).pattern(PaintingDecayPattern.builder().from(decaysInto).to(key));
    }

    private DecayPatternHolder.Builder addPattern(Object to, Object from) {
        var id = getId(to);

        if(id == null) {
            return null;
        }

        return addPattern(DimensionalDoors.id(getId(to)), to, from);
    }

    private DecayPatternHolder.Builder addPattern(ResourceLocation id, Object to, Object from) {
        return createPatterData(id, from, to);
    }

	private DecayCondition getPredicate(Object object) {
		if (object instanceof TagKey<?> tag) {
			if (tag.isFor(Registries.BLOCK)) return SimpleDecayCondition.of((TagKey<Block>) tag);
			else if (tag.isFor(Registries.FLUID)) return FluidDecayCondition.of((TagKey<Fluid>) tag);
			else if (tag.isFor(Registries.DIMENSION_TYPE)) return DimensionDecayCondition.of((TagKey<DimensionType>) tag);

		} else if(object instanceof ResourceKey<?> key) {
			if (key.isFor(Registries.BLOCK)) return SimpleDecayCondition.of((ResourceKey<Block>) key);
			else if (key.isFor(Registries.FLUID)) return FluidDecayCondition.of((ResourceKey<Fluid>) key);
			else if (key.isFor(Registries.DIMENSION_TYPE)) return DimensionDecayCondition.of((ResourceKey<DimensionType>) key);
		} else if (object instanceof Block block) {
            return SimpleDecayCondition.of(block.builtInRegistryHolder().key());
        } else if (object instanceof Fluid fluid) {
            return FluidDecayCondition.of(fluid.builtInRegistryHolder().key());
        }

        return DecayCondition.NONE;
	}

    private String getId(Object object) {
        if(object instanceof ResourceKey<?> key) {
            return key.registryKey().location().getPath();
        } else if (object instanceof Block block) {
            return block.builtInRegistryHolder().key().location().getPath();
        } else if (object instanceof Fluid fluid) {
            return fluid.builtInRegistryHolder().key().location().getPath();
        } else if (object instanceof RegistrySupplier<?> registrySupplier) {
            return registrySupplier.getId().getPath();
        }

        return null;
    }

	private DecayResult getProcessor(Object object) {
		return getProcessor(object, 1);
	}

	private float DEFAULT = 0.01f;

	private DecayResult getProcessor(Object object, int entropy) {
		if(object instanceof Block block) return new BlockDecayImplResult(entropy, DEFAULT, block);
		else if(object instanceof Fluid fluid) return new FluidDecayResult(entropy, DEFAULT, fluid);
		else return NoneDecayResult.instance();
	}

	private void createOxidizationChain(Consumer<DecayPatternHolder> consumer, HolderLookup.Provider provider, Block... blocks) {
        for (int i = 0; i < blocks.length - 2; i += 2) {
            var from = blocks[i];
            var fromWaxed = blocks[i+1];
            var to = blocks[i+2];
            var toWaxed = blocks[i+3];

            addPattern(to, from).accept(consumer, provider);
            addPattern(DimensionalDoors.id("dewaxed_" + getId(from)), from, fromWaxed).accept(consumer, provider);
            addPattern(DimensionalDoors.id("dewaxed_" + getId(to)), to, toWaxed).accept(consumer, provider);
        }
	}

	private Block getBlock(ResourceLocation id) {
		return BuiltInRegistries.BLOCK.get(id);
	}

	private ResourceLocation getBlockId(Block block) {
		return BuiltInRegistries.BLOCK.getKey(block);
	}

	private DecayPatternHolder turnIntoSelf(ResourceLocation ResourceLocation, Object before) {
		return new DecayPatternHolder(ResourceLocation, new CompoundDecayPattern(List.of(getPredicate(before)), SelfDecayResult.instance()));
	}

    @Override
    public String getName() {
        return "Limbo Decay";
    }

    private static Path getOutput(Path rootOutput, ResourceLocation lootTableId) {
        return rootOutput.resolve("data/" + lootTableId.getNamespace() + "/decay_patterns/" + lootTableId.getPath() + ".json");
    }

    public DecayPatternHolder.Builder createPatterData(ResourceLocation id, Object before, Object after) {
        return DecayPatternHolder.builder(id).pattern(CompoundDecayPattern.builder().condition(getPredicate(before)).result(getProcessor(after)));
    }

    public void addDoublePattern(Object before, Block after) {
        addDoublePattern(DimensionalDoors.id(getId(after)), after, before);
    }

    public DecayPatternHolder.Builder addDoublePattern(ResourceLocation id, Object after, Object before) {
        Block block = after instanceof RegistrySupplier<?> supplier ? (Block) supplier.get() : (Block) after;

        return DecayPatternHolder.builder(id).pattern(CompoundDecayPattern.builder().condition(getPredicate(before)).result(new DoubleBlockDecayResult(1, DEFAULT, block)));
    }

	public DecayPatternHolder createDoublePattern(ResourceLocation id, Object before, Block after) {
		return new DecayPatternHolder(id, new CompoundDecayPattern(List.of(getPredicate(before)), new DoubleBlockDecayResult(1, DEFAULT, after)));
	}
}
