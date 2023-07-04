package org.dimdev.dimdoors.datagen;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.tag.ModBlockTags;
import org.dimdev.dimdoors.world.decay.DecayPredicate;
import org.dimdev.dimdoors.world.decay.DecayProcessor;
import org.dimdev.dimdoors.world.decay.predicates.FluidDecayPredicate;
import org.dimdev.dimdoors.world.decay.predicates.SimpleDecayPredicate;
import org.dimdev.dimdoors.world.decay.processors.BlockDecayProcessor;
import org.dimdev.dimdoors.world.decay.processors.DoubleDecayProcessor;
import org.dimdev.dimdoors.world.decay.processors.FluidDecayProcessor;
import org.dimdev.dimdoors.world.decay.processors.SelfDecayProcessor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class LimboDecayProvider implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

	private final PackOutput.PathProvider decayPatternPathResolver;

	public LimboDecayProvider(PackOutput output) {
		this.decayPatternPathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, "decay_patterns");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
		Set<ResourceLocation> generatedDecayPatterns = Sets.newHashSet();
		List<CompletableFuture<?>> list = new ArrayList<>();


        BiConsumer<ResourceLocation, JsonObject> consumer = (resourceLocation, json)  -> {
            Path outputPath = decayPatternPathResolver.json(resourceLocation);
			list.add(DataProvider.saveStable(cache, json, outputPath));
		};

		generatePatterns(consumer);

		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

	protected void generatePatterns(BiConsumer<ResourceLocation, JsonObject> consumer) {
		createPatterData(DimensionalDoors.id("air"), ModBlockTags.DECAY_TO_AIR, Blocks.AIR).run(consumer);
		createPatterData(DimensionalDoors.id("gritty_stone"), ModBlockTags.DECAY_TO_GRITTY_STONE, ModBlocks.GRITTY_STONE.get()).run(consumer);
		createPatterData(DimensionalDoors.id("leak"), Fluids.WATER, ModBlocks.LEAK.get()).run(consumer);
		createPatterData(DimensionalDoors.id("solid_static"), ModBlockTags.DECAY_TO_SOLID_STATIC, ModBlocks.SOLID_STATIC.get()).run(consumer);
		createPatterData(DimensionalDoors.id("unraveled_fabric"), Blocks.CLAY, ModBlocks.UNRAVELLED_BLOCK.get()).run(consumer);
		createPatterData(DimensionalDoors.id("unraveled_fence"), ModBlockTags.DECAY_UNRAVELED_FENCE, ModBlocks.UNRAVELED_FENCE.get()).run(consumer);
		createPatterData(DimensionalDoors.id("unraveled_gate"), ModBlockTags.DECAY_UNRAVELED_GATE, ModBlocks.UNRAVELED_GATE.get()).run(consumer);
		createPatterData(DimensionalDoors.id("unraveled_button"), ModBlockTags.DECAY_UNRAVELED_BUTTON, ModBlocks.UNRAVELED_BUTTON.get()).run(consumer);
		createPatterData(DimensionalDoors.id("unraveled_slab"), ModBlockTags.DECAY_UNRAVELED_SLAB, ModBlocks.UNRAVELED_SLAB.get()).run(consumer);
		createPatterData(DimensionalDoors.id("unraveled_stairs"), ModBlockTags.DECAY_UNRAVELED_STAIRS, ModBlocks.UNRAVELED_STAIRS.get()).run(consumer);

		createPatterData(DimensionalDoors.id("cobweb"), BlockTags.WOOL, Blocks.COBWEB).run(consumer);
		createPatterData(DimensionalDoors.id("driftwood_leaves"), BlockTags.LEAVES, ModBlocks.DRIFTWOOD_LEAVES.get()).run(consumer);
		createPatterData(DimensionalDoors.id("driftwood_sapling"), BlockTags.SAPLINGS, ModBlocks.DRIFTWOOD_SAPLING.get()).run(consumer);
		createPatterData(DimensionalDoors.id("glass_pane"), ModBlockTags.DECAY_TO_GLASS_PANE, Blocks.GLASS_PANE).run(consumer);
		createPatterData(DimensionalDoors.id("moss_carpet"), BlockTags.WOOL_CARPETS, Blocks.MOSS_CARPET).run(consumer);
		createPatterData(DimensionalDoors.id("driftwood_trapdoor"), BlockTags.WOODEN_TRAPDOORS, ModBlocks.DRIFTWOOD_TRAPDOOR.get()).run(consumer);
		createDoublePattern(DimensionalDoors.id("driftwood_trapdoor_door"), ModBlocks.DRIFTWOOD_DOOR.get(), ModBlocks.DRIFTWOOD_TRAPDOOR.get()).run(consumer);
		createPatterData(DimensionalDoors.id("rail"), ModBlockTags.DECAY_TO_RAIL, Blocks.RAIL).run(consumer);
		createPatterData(DimensionalDoors.id("rust"), ModBlockTags.DECAY_TO_RUST, ModBlocks.RUST.get()).run(consumer);
		createPatterData(DimensionalDoors.id("unraveled_spike"), ModBlockTags.DECAY_TO_UNRAVELED_SPIKE, ModBlocks.UNRAVELED_SPIKE.get()).run(consumer);
		createPatterData(DimensionalDoors.id("wither_rose"), ModBlockTags.DECAY_TO_WITHER_ROSE, Blocks.WITHER_ROSE).run(consumer);
		createPatterData(DimensionalDoors.id("water"), Blocks.SNOW, Fluids.WATER).run(consumer);
		createPatterData(DimensionalDoors.id("clay"), ModBlockTags.DECAY_TO_CLAY, Blocks.CLAY).run(consumer);
		createPatterData(DimensionalDoors.id("clay_fence"), ModBlockTags.DECAY_CLAY_FENCE, ModBlocks.CLAY_FENCE.get()).run(consumer);
		createPatterData(DimensionalDoors.id("clay_gate"), ModBlockTags.DECAY_CLAY_GATE, ModBlocks.CLAY_GATE.get()).run(consumer);
		createPatterData(DimensionalDoors.id("clay_button"), ModBlockTags.DECAY_CLAY_BUTTON, ModBlocks.CLAY_BUTTON.get()).run(consumer);
		createPatterData(DimensionalDoors.id("clay_slab"), ModBlockTags.DECAY_CLAY_SLAB, ModBlocks.CLAY_SLAB.get()).run(consumer);
		createPatterData(DimensionalDoors.id("clay_stairs"), ModBlockTags.DECAY_CLAY_STAIRS, ModBlocks.CLAY_STAIRS.get()).run(consumer);
		createPatterData(DimensionalDoors.id("dark_sand"), ModBlockTags.DECAY_TO_DARK_SAND, ModBlocks.DARK_SAND.get()).run(consumer);
	 	createPatterData(DimensionalDoors.id("dark_sand_fence"), ModBlockTags.DECAY_DARK_SAND_FENCE, ModBlocks.DARK_SAND_FENCE.get()).run(consumer);
	 	createPatterData(DimensionalDoors.id("dark_sand_gate"), ModBlockTags.DECAY_DARK_SAND_GATE, ModBlocks.DARK_SAND_GATE.get()).run(consumer);
	 	createPatterData(DimensionalDoors.id("dark_sand_button"), ModBlockTags.DECAY_DARK_SAND_BUTTON, ModBlocks.DARK_SAND_BUTTON.get()).run(consumer);
	 	createPatterData(DimensionalDoors.id("dark_sand_slab"), ModBlockTags.DECAY_DARK_SAND_SLAB, ModBlocks.DARK_SAND_SLAB.get()).run(consumer);
	 	createPatterData(DimensionalDoors.id("dark_sand_stairs"), ModBlockTags.DECAY_DARK_SAND_STAIRS, ModBlocks.DARK_SAND_STAIRS.get()).run(consumer);

		createPatterData(DimensionalDoors.id("wool"), Blocks.TARGET, Blocks.WHITE_WOOL).run(consumer);
		createDoublePattern(DimensionalDoors.id("wool_bed"), BlockTags.BEDS, Blocks.WHITE_WOOL).run(consumer);
		createPatterData(DimensionalDoors.id("driftwood_door"), ModBlockTags.DECAY_TO_DRIFTWOOD_DOOR, ModBlocks.DRIFTWOOD_DOOR.get()).run(consumer);
		createPatterData(DimensionalDoors.id("amalgam"), ModBlockTags.DECAY_TO_AMALGAM, ModBlocks.AMALGAM_BLOCK.get()).run(consumer);
		createPatterData(DimensionalDoors.id("amalgam_slab"), Blocks.CUT_COPPER_SLAB, ModBlocks.AMALGAM_SLAB.get()).run(consumer);
		createPatterData(DimensionalDoors.id("amalgam_stairs"), Blocks.CUT_COPPER_STAIRS, ModBlocks.AMALGAM_STAIRS.get()).run(consumer);
		createPatterData(DimensionalDoors.id("mud"), ModBlockTags.DECAY_TO_MUD, Blocks.MUD).run(consumer);
		createPatterData(DimensionalDoors.id("mud_fence"), ModBlockTags.DECAY_TO_MUD_FENCE, ModBlocks.MUD_FENCE.get()).run(consumer);
		createPatterData(DimensionalDoors.id("mud_gate"), ModBlockTags.DECAY_TO_MUD_GATE, ModBlocks.MUD_GATE.get()).run(consumer);
		createPatterData(DimensionalDoors.id("mud_button"), ModBlockTags.DECAY_TO_MUD_BUTTON, ModBlocks.MUD_BUTTON.get()).run(consumer);
		createPatterData(DimensionalDoors.id("mud_slab"), ModBlockTags.DECAY_TO_MUD_SLAB, ModBlocks.MUD_SLAB.get()).run(consumer);
		createPatterData(DimensionalDoors.id("mud_stairs"), ModBlockTags.DECAY_TO_MUD_STAIRS, ModBlocks.MUD_STAIRS.get()).run(consumer);
		Stream.of(DyeColor.values()).map(DyeColor::getSerializedName).forEach(name -> {
			createPatterData(DimensionalDoors.id(name + "_terracotta"), getBlock(ResourceLocation.tryParse(name + "_glazed_terracotta")), getBlock(ResourceLocation.tryParse(name + "_terracotta"))).run(consumer);
			createPatterData(DimensionalDoors.id(name + "_concrete_powder"), getBlock(ResourceLocation.tryParse(name + "_powered_concrete")), getBlock(ResourceLocation.tryParse(name + "_concrete"))).run(consumer);
		});
		createPatterData(DimensionalDoors.id("glass"), ModBlockTags.DECAY_TO_GLASS, Blocks.GLASS).run(consumer);
		createPatterData(DimensionalDoors.id("gravel"), ModBlockTags.DECAY_TO_GRAVEL, Blocks.GRAVEL).run(consumer);
		createPatterData(DimensionalDoors.id("gravel_fence"), ModBlockTags.DECAY_TO_GRAVEL_FENCE, ModBlocks.GRAVEL_FENCE.get()).run(consumer);
		createPatterData(DimensionalDoors.id("gravel_gate"), ModBlockTags.DECAY_TO_GRAVEL_GATE, ModBlocks.GRAVEL_GATE.get()).run(consumer);
		createPatterData(DimensionalDoors.id("gravel_button"), ModBlockTags.DECAY_TO_GRAVEL_BUTTON, ModBlocks.GRAVEL_BUTTON.get()).run(consumer);
		createPatterData(DimensionalDoors.id("gravel_slab"), ModBlockTags.DECAY_TO_GRAVEL_SLAB, ModBlocks.GRAVEL_SLAB.get()).run(consumer);
		createPatterData(DimensionalDoors.id("gravel_stairs"), ModBlockTags.DECAY_TO_GRAVEL_STAIRS, ModBlocks.GRAVEL_STAIRS.get()).run(consumer);
		createPatterData(DimensionalDoors.id("gravel_wall"), ModBlockTags.DECAY_TO_GRAVEL_WALL, ModBlocks.GRAVEL_WALL.get()).run(consumer);
		createPatterData(DimensionalDoors.id("red_sand"), Blocks.RED_SANDSTONE, Blocks.RED_SAND).run(consumer);
		createPatterData(DimensionalDoors.id("red_sand_slab"), ModBlockTags.DECAY_TO_RED_SAND_SLAB, ModBlocks.RED_SAND_SLAB.get()).run(consumer);
		createPatterData(DimensionalDoors.id("red_sand_stairs"), ModBlockTags.DECAY_TO_RED_SAND_STAIRS, ModBlocks.RED_SAND_STAIRS.get()).run(consumer);
		createPatterData(DimensionalDoors.id("red_sand_wall"), ModBlockTags.DECAY_TO_RED_SAND_WALL, ModBlocks.RED_SAND_WALL.get()).run(consumer);
		createPatterData(DimensionalDoors.id("sand"), ModBlockTags.DECAY_TO_SAND, Blocks.SAND).run(consumer);
		createPatterData(DimensionalDoors.id("sand_slab"), ModBlockTags.DECAY_TO_SAND_SLAB, ModBlocks.SAND_SLAB.get()).run(consumer);
		createPatterData(DimensionalDoors.id("sand_stairs"), ModBlockTags.DECAY_TO_SAND_STAIRS, ModBlocks.SAND_STAIRS.get()).run(consumer);
		createPatterData(DimensionalDoors.id("sand_wall"), ModBlockTags.DECAY_TO_SAND_WALL, ModBlocks.SAND_WALL.get()).run(consumer);
		createPatterData(DimensionalDoors.id("soul_sand"), Blocks.SOUL_SOIL, Blocks.SOUL_SAND).run(consumer);

		createSimplePattern(DimensionalDoors.id("ice"), Blocks.PACKED_ICE, Blocks.ICE).run(consumer);
		createSimplePattern(DimensionalDoors.id("iron_block"), Blocks.ANVIL, Blocks.IRON_BLOCK).run(consumer);
		createOxidizationChain(Blocks.COPPER_BLOCK, Blocks.EXPOSED_COPPER, Blocks.WEATHERED_COPPER, Blocks.OXIDIZED_COPPER, consumer);
		createOxidizationChain(Blocks.CUT_COPPER, Blocks.EXPOSED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER, consumer);
		createOxidizationChain(Blocks.CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER_SLAB, consumer);
		createOxidizationChain(Blocks.CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER_STAIRS, consumer);
		createPatterData(DimensionalDoors.id("ancient_debris"), Blocks.NETHERITE_BLOCK, Blocks.ANCIENT_DEBRIS).run(consumer);
		createPatterData(DimensionalDoors.id("dirt"), ModBlockTags.DECAY_TO_DIRT, Blocks.DIRT).run(consumer);
		createPatterData(DimensionalDoors.id("crimson_nylium"), Blocks.WARPED_NYLIUM, Blocks.CRIMSON_NYLIUM).run(consumer);
		createPatterData(DimensionalDoors.id("driftwood_plank"), ModBlockTags.DECAY_TO_DRIFTWOOD_PLANK, ModBlocks.DRIFTWOOD_PLANKS.get()).run(consumer);
		createPatterData(DimensionalDoors.id("driftwood_fence"), ModBlockTags.DECAY_TO_DRIFTWOOD_FENCE, ModBlocks.DRIFTWOOD_FENCE.get()).run(consumer);
		createPatterData(DimensionalDoors.id("driftwood_gate"), ModBlockTags.DECAY_TO_DRIFTWOOD_GATE, ModBlocks.DRIFTWOOD_GATE.get()).run(consumer);
		createPatterData(DimensionalDoors.id("driftwood_button"), ModBlockTags.DECAY_TO_DRIFTWOOD_BUTTON, ModBlocks.DRIFTWOOD_BUTTON.get()).run(consumer);
		createPatterData(DimensionalDoors.id("driftwood_slab"), ModBlockTags.DECAY_TO_DRIFTWOOD_SLAB, ModBlocks.DRIFTWOOD_SLAB.get()).run(consumer);
		createPatterData(DimensionalDoors.id("driftwood_stairs"), ModBlockTags.DECAY_TO_DRIFTWOOD_STAIRS, ModBlocks.DRIFTWOOD_STAIRS.get()).run(consumer);
		createPatterData(DimensionalDoors.id("composter"), Blocks.BARREL, Blocks.COMPOSTER).run(consumer);
		createPatterData(DimensionalDoors.id("chest"), ModBlockTags.DECAY_TO_CHEST, Blocks.CHEST).run(consumer);
		createPatterData(DimensionalDoors.id("bone_block"), Blocks.CONDUIT, Blocks.BONE_BLOCK).run(consumer);
		createPatterData(DimensionalDoors.id("skeleton_skull"), ModBlockTags.DECAY_TO_SKELETON_SKULL, Blocks.SKELETON_SKULL).run(consumer);
		createPatterData(DimensionalDoors.id("skeleton_wall_skull"), ModBlockTags.DECAY_TO_SKELETON_WALL_SKULL, Blocks.SKELETON_WALL_SKULL).run(consumer);
		createPatterData(DimensionalDoors.id("bamboo"), Blocks.SCAFFOLDING, Blocks.BAMBOO).run(consumer);
		createPatterData(DimensionalDoors.id("pumpkin"), Blocks.CARVED_PUMPKIN, Blocks.PUMPKIN).run(consumer);
		createPatterData(DimensionalDoors.id("slime_block"), Blocks.HONEY_BLOCK, Blocks.SLIME_BLOCK).run(consumer);
		createPatterData(DimensionalDoors.id("honeycomb_block"), Blocks.SPONGE, Blocks.HONEYCOMB_BLOCK).run(consumer);
		createPatterData(DimensionalDoors.id("lectern"), Blocks.BOOKSHELF, Blocks.LECTERN).run(consumer);
		createPatterData(DimensionalDoors.id("piston"), Blocks.STICKY_PISTON, Blocks.PISTON).run(consumer);
		createPatterData(DimensionalDoors.id("netherwart_block"), ModBlockTags.DECAY_TO_NETHERWART_BLOCK, Blocks.NETHER_WART_BLOCK).run(consumer);
		createPatterData(DimensionalDoors.id("redstone_lamp"), Blocks.BEACON, Blocks.REDSTONE_LAMP).run(consumer);
		createPatterData(DimensionalDoors.id("amalgam_ore"), ModBlockTags.DECAY_TO_AMALGAM_ORE, ModBlocks.AMALGAM_ORE.get()).run(consumer);
		createPatterData(DimensionalDoors.id("clod_ore"), ModBlockTags.DECAY_TO_CLOD_ORE, ModBlocks.CLOD_ORE.get()).run(consumer);
		createPatterData(DimensionalDoors.id("cobblestone"), ModBlockTags.DECAY_TO_COBBLESTONE, Blocks.COBBLESTONE).run(consumer);
		createPatterData(DimensionalDoors.id("cobblestone_slab"), ModBlockTags.DECAY_TO_COBBLESTONE_SLAB, Blocks.COBBLESTONE_SLAB).run(consumer);
		createPatterData(DimensionalDoors.id("cobblestone_stairs"), ModBlockTags.DECAY_TO_COBBLESTONE_STAIRS, Blocks.COBBLESTONE_STAIRS).run(consumer);
		createPatterData(DimensionalDoors.id("cobblestone_wall"), ModBlockTags.DECAY_TO_COBBLESTONE_WALL, Blocks.COBBLESTONE_WALL).run(consumer);
		createPatterData(DimensionalDoors.id("red_sandstone"), ModBlockTags.DECAY_TO_RED_SANDSTONE, Blocks.RED_SANDSTONE).run(consumer);
		createPatterData(DimensionalDoors.id("red_sandstone"), ModBlockTags.DECAY_TO_SANDSTONE, Blocks.SANDSTONE).run(consumer);

		createPatterData(DimensionalDoors.id("packed_ice"), Blocks.BLUE_ICE, Blocks.PACKED_ICE).run(consumer);
		createPatterData(DimensionalDoors.id("driftwood_wood"), ModBlockTags.DECAY_TO_DRIFTWOOD_WOOD, ModBlocks.DRIFTWOOD_WOOD.get()).run(consumer);
		createPatterData(DimensionalDoors.id("driftwood_log"), ModBlockTags.DECAY_TO_DRIFTWOOD_LOG, ModBlocks.DRIFTWOOD_LOG.get()).run(consumer);
		createPatterData(DimensionalDoors.id("barrel"), Blocks.BEEHIVE, Blocks.BARREL).run(consumer);
		createPatterData(DimensionalDoors.id("carved_pumpkin"), Blocks.JACK_O_LANTERN, Blocks.CARVED_PUMPKIN).run(consumer);
		createPatterData(DimensionalDoors.id("sponge"), Blocks.WET_SPONGE, Blocks.SPONGE).run(consumer);
		createPatterData(DimensionalDoors.id("coal_ore"), Blocks.DIAMOND_ORE, Blocks.COAL_ORE).run(consumer);
		createPatterData(DimensionalDoors.id("andesite"), Blocks.POLISHED_ANDESITE, Blocks.ANDESITE).run(consumer);
		createPatterData(DimensionalDoors.id("andesite_slab"), Blocks.POLISHED_ANDESITE_SLAB, Blocks.ANDESITE_SLAB).run(consumer);
		createPatterData(DimensionalDoors.id("andesite_stairs"), Blocks.POLISHED_ANDESITE_STAIRS, Blocks.ANDESITE_STAIRS).run(consumer);
		createPatterData(DimensionalDoors.id("basalt"), ModBlockTags.DECAY_TO_BASALT, Blocks.BASALT).run(consumer);
		createPatterData(DimensionalDoors.id("basalt_lava"), Fluids.LAVA, Blocks.BASALT).run(consumer);
		createPatterData(DimensionalDoors.id("blackstone"), ModBlockTags.DECAY_TO_BLACKSTONE, Blocks.BLACKSTONE).run(consumer);
		createPatterData(DimensionalDoors.id("blackstone_slab"), ModBlockTags.DECAY_TO_BLACKSTONE_SLAB, Blocks.BLACKSTONE_SLAB).run(consumer);
		createPatterData(DimensionalDoors.id("blackstone_stairs"), ModBlockTags.DECAY_TO_BLACKSTONE_STAIRS, Blocks.BLACKSTONE_STAIRS).run(consumer);
		createPatterData(DimensionalDoors.id("blackstone_wall"), ModBlockTags.DECAY_TO_BLACKSTONE_WALL, Blocks.BLACKSTONE_WALL).run(consumer);
		createPatterData(DimensionalDoors.id("deepslate"), ModBlockTags.DECAY_TO_DEEPSLATE, Blocks.DEEPSLATE).run(consumer);
		createPatterData(DimensionalDoors.id("deepslate_slab"), ModBlockTags.DECAY_TO_DEEPSLATE_SLAB, ModBlocks.DEEPSLATE_SLAB.get()).run(consumer);
		createPatterData(DimensionalDoors.id("deepslate_stairs"), ModBlockTags.DECAY_TO_DEEPSLATE_STAIRS, ModBlocks.DEEPSLATE_STAIRS.get()).run(consumer);
		createPatterData(DimensionalDoors.id("deepslate_wall"), ModBlockTags.DECAY_TO_DEEPSLATE_WALL, ModBlocks.DEEPSLATE_WALL.get()).run(consumer);
		createPatterData(DimensionalDoors.id("diorite"), ModBlockTags.DECAY_TO_DIORITE, Blocks.DIORITE).run(consumer);
		createPatterData(DimensionalDoors.id("diorite_slab"), ModBlockTags.DECAY_TO_DIORITE_SLAB, Blocks.DIORITE_SLAB).run(consumer);
		createPatterData(DimensionalDoors.id("diorite_stairs"), ModBlockTags.DECAY_TO_DIORITE_STAIRS, Blocks.DIORITE_STAIRS).run(consumer);
		createPatterData(DimensionalDoors.id("diorite_wall"), ModBlockTags.DECAY_TO_DIORITE_WALL, Blocks.DIORITE_WALL).run(consumer);
		createPatterData(DimensionalDoors.id("endstone"), ModBlockTags.DECAY_TO_ENDSTONE, Blocks.END_STONE).run(consumer);
		createPatterData(DimensionalDoors.id("endstone_slab"), ModBlockTags.DECAY_TO_ENDSTONE_SLAB, ModBlocks.END_STONE_SLAB.get()).run(consumer);
		createPatterData(DimensionalDoors.id("endstone_stairs"), ModBlockTags.DECAY_TO_ENDSTONE_STAIRS, ModBlocks.END_STONE_STAIRS.get()).run(consumer);
		createPatterData(DimensionalDoors.id("endstone_wall"), ModBlockTags.DECAY_TO_ENDSTONE_WALL, ModBlocks.END_STONE_WALL.get()).run(consumer);
		createPatterData(DimensionalDoors.id("furnace"), ModBlockTags.DECAY_TO_FURNACE, Blocks.FURNACE).run(consumer);
		createPatterData(DimensionalDoors.id("granite"), ModBlockTags.DECAY_TO_GRANITE, Blocks.GRANITE).run(consumer);
		createPatterData(DimensionalDoors.id("granite_slab"), ModBlockTags.DECAY_TO_GRANITE_SLAB, Blocks.GRANITE_SLAB).run(consumer);
		createPatterData(DimensionalDoors.id("granite_stairs"), ModBlockTags.DECAY_TO_GRANITE_STAIRS, Blocks.GRANITE_STAIRS).run(consumer);
		createPatterData(DimensionalDoors.id("granite"), ModBlockTags.DECAY_TO_GRANITE, Blocks.GRANITE).run(consumer);
		createPatterData(DimensionalDoors.id("granite_slab"), ModBlockTags.DECAY_TO_GRANITE_SLAB, Blocks.GRANITE_SLAB).run(consumer);
		createPatterData(DimensionalDoors.id("granite_stairs"), ModBlockTags.DECAY_TO_GRANITE_STAIRS, Blocks.GRANITE_STAIRS).run(consumer);
		createPatterData(DimensionalDoors.id("netherrack"), ModBlockTags.NETHERRACK, Blocks.NETHERRACK).run(consumer);
		createPatterData(DimensionalDoors.id("netherrack_fence"), ModBlockTags.NETHERRACK_FENCE, ModBlocks.NETHERRACK_FENCE.get()).run(consumer);
		createPatterData(DimensionalDoors.id("netherrack_slab"), ModBlockTags.NETHERRACK_SLAB, ModBlocks.NETHERRACK_SLAB.get()).run(consumer);
		createPatterData(DimensionalDoors.id("netherrack_stairs"), ModBlockTags.NETHERRACK_STAIRS, ModBlocks.NETHERRACK_STAIRS.get()).run(consumer);
		createPatterData(DimensionalDoors.id("netherrack_wall"), ModBlockTags.NETHERRACK_WALL, ModBlocks.NETHERRACK_WALL.get()).run(consumer);
		createPatterData(DimensionalDoors.id("prismarine"), ModBlockTags.DECAY_TO_PRISMARINE, Blocks.PRISMARINE).run(consumer);
		createPatterData(DimensionalDoors.id("prismarine_slab"), ModBlockTags.DECAY_TO_PRISMARINE_SLAB, Blocks.PRISMARINE_SLAB).run(consumer);
		createPatterData(DimensionalDoors.id("prismarine_stairs"), ModBlockTags.DECAY_TO_PRISMARINE_STAIRS, Blocks.PRISMARINE_STAIRS).run(consumer);
		createPatterData(DimensionalDoors.id("prismarine_wall"), ModBlockTags.DECAY_TO_PRISMARINE_WALL, Blocks.PRISMARINE_WALL).run(consumer);
		createPatterData(DimensionalDoors.id("stone"), ModBlockTags.DECAY_TO_STONE, Blocks.STONE).run(consumer);

		createPatterData(DimensionalDoors.id("lava"), Blocks.MAGMA_BLOCK, Fluids.LAVA);
		createPatterData(DimensionalDoors.id("dropper"), Blocks.DISPENSER, Blocks.DROPPER);
		createPatterData(DimensionalDoors.id("dark_prismarine"), ModBlockTags.DECAY_TO_DARK_PRISMARINE, Blocks.DARK_PRISMARINE).run(consumer);
		createPatterData(DimensionalDoors.id("dark_prismarine_slab"), ModBlockTags.DECAY_TO_DARK_PRISMARINE_SLAB, Blocks.DARK_PRISMARINE_SLAB).run(consumer);
		createPatterData(DimensionalDoors.id("dark_prismarine_stairs"), ModBlockTags.DECAY_TO_DARK_PRISMARINE_STAIRS, Blocks.DARK_PRISMARINE_STAIRS).run(consumer);
		createPatterData(DimensionalDoors.id("clod_block"), ModBlockTags.DECAY_TO_CLOD_BLOCK, ModBlocks.CLOD_BLOCK.get()).run(consumer);
		createPatterData(DimensionalDoors.id("cracked_stone_brick"), Blocks.CRACKED_STONE_BRICKS, Blocks.STONE_BRICKS).run(consumer);
		createPatterData(DimensionalDoors.id("obsidian"), ModBlockTags.DECAY_TO_OBSIDIAN, Blocks.OBSIDIAN).run(consumer);
		createPatterData(DimensionalDoors.id("stone_brick"), ModBlockTags.DECAY_TO_STONE_BRICKS, Blocks.STONE_BRICKS).run(consumer);
		createPatterData(DimensionalDoors.id("stone_brick_slab"), ModBlockTags.DECAY_TOSTONE_BRICK_SLAB, Blocks.STONE_BRICK_SLAB).run(consumer);
		createPatterData(DimensionalDoors.id("stone_brick_stairs"), ModBlockTags.DECAY_TOSTONE_BRICK_STAIRS, Blocks.STONE_BRICK_STAIRS).run(consumer);
		createPatterData(DimensionalDoors.id("stone_brick_wall"), ModBlockTags.DECAY_TOSTONE_BRICK_WALL, Blocks.STONE_BRICK_WALL).run(consumer);
		createPatterData(DimensionalDoors.id("crying_obsidian"), Blocks.RESPAWN_ANCHOR, Blocks.CRYING_OBSIDIAN).run(consumer);
	}

	public DecayPatternData createPatterData(ResourceLocation id, Object before, Object after) {
		return new DecayPatternData(id, getPredicate(before), getProcessor(after));
	}

	private DecayPredicate getPredicate(Object object) {
		if (!(object instanceof TagKey<?> tag)) {
			if(object instanceof Block block) return SimpleDecayPredicate.builder().block(block).create();
			else if(object instanceof Fluid fluid) return FluidDecayPredicate.builder().fluid(fluid).create();
		} else {
			if (tag.isFor(Registries.BLOCK)) return SimpleDecayPredicate.builder().tag((TagKey<Block>) tag).create();
			else if (tag.isFor(Registries.FLUID)) return FluidDecayPredicate.builder().tag((TagKey<Fluid>) tag).create();
		}

		return DecayPredicate.NONE;
	}

	private DecayProcessor getProcessor(Object object) {
		if(object instanceof Block block) return BlockDecayProcessor.builder().block(block).create();
		else if(object instanceof Fluid fluid) return FluidDecayProcessor.builder().fluid(fluid).create();
		else return DecayProcessor.NONE;
	}

	private void createOxidizationChain(Block regular, Block exposed, Block weathered, Block oxidized, BiConsumer<ResourceLocation, JsonObject> consumer) {
		Function<Block, Block> waxed = block -> {
			ResourceLocation id = getId(block);

			return getBlock(new ResourceLocation(id.getNamespace(), "waxed_" + id.getPath()));
		};
		Function<Block, ResourceLocation> id = block -> new ResourceLocation("dimdoors:" + getId(block).getPath());

		Block regularWaxed = waxed.apply(regular);
		Block exposedWaxed = waxed.apply(exposed);
		Block weatheredWaxed = waxed.apply(weathered);
		Block oxidizedWaxed = waxed.apply(oxidized);

		createPatterData(id.apply(weathered), oxidized, weathered).run(consumer);
		createPatterData(id.apply(exposed), weathered, exposed).run(consumer);
		createPatterData(id.apply(regular), exposed, regular).run(consumer);

		createPatterData(id.apply(regularWaxed), regularWaxed, regular);
		createPatterData(id.apply(exposedWaxed), exposedWaxed, exposed);
		createPatterData(id.apply(weathered), weatheredWaxed, weathered);
		createPatterData(id.apply(oxidizedWaxed), oxidizedWaxed, oxidized);
	}

	private Block getBlock(ResourceLocation id) {
		return BuiltInRegistries.BLOCK.get(id);
	}

	private ResourceLocation getId(Block block) {
		return BuiltInRegistries.BLOCK.getKey(block);
	}

	private DecayPatternData turnIntoSelf(ResourceLocation ResourceLocation, Object before) {
		return new DecayPatternData(ResourceLocation, getPredicate(before), SelfDecayProcessor.instance());
	}

    @Override
    public String getName() {
        return "Limbo Decay";
    }

    private static Path getOutput(Path rootOutput, ResourceLocation lootTableId) {
        return rootOutput.resolve("data/" + lootTableId.getNamespace() + "/decay_patterns/" + lootTableId.getPath() + ".json");
    }

    public DecayPatternData createSimplePattern(ResourceLocation id, Block before, Block after) {
        return new DecayPatternData(id, SimpleDecayPredicate.builder().block(before).create(), BlockDecayProcessor.builder().block(after).entropy(1).create());
    }

	public DecayPatternData createSimplePattern(ResourceLocation id, TagKey<Block> before, Block after) {
		return new DecayPatternData(id, SimpleDecayPredicate.builder().tag(before).create(), BlockDecayProcessor.builder().block(after).entropy(1).create());
	}

	public DecayPatternData createDoublePattern(ResourceLocation id, Object before, Block after) {
		return new DecayPatternData(id, getPredicate(before), DoubleDecayProcessor.builder().block(after).entropy(1).create());
	}

    public static class DecayPatternData {
        private ResourceLocation id;
        private DecayPredicate predicate;
        private DecayProcessor processor;

        public DecayPatternData(ResourceLocation id, DecayPredicate predicate, DecayProcessor processor) {
            this.id = id;
            this.predicate = predicate;
            this.processor = processor;
        }

        public void run(BiConsumer<ResourceLocation, JsonObject> consumer) {
            JsonObject object = new JsonObject();
            object.add("predicate", ResourceUtil.NBT_TO_JSON.apply(predicate.toNbt(new CompoundTag())));
            object.add("processor", ResourceUtil.NBT_TO_JSON.apply(processor.toNbt(new CompoundTag())));

            consumer.accept(id, object);
        }
    }
}
