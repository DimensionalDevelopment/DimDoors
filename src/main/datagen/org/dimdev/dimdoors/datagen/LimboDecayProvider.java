package org.dimdev.dimdoors.datagen;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.tag.ModBlockTags;
import org.dimdev.dimdoors.world.decay.DecayPredicate;
import org.dimdev.dimdoors.world.decay.DecayProcessor;
import org.dimdev.dimdoors.world.decay.predicates.SimpleDecayPredicate;
import org.dimdev.dimdoors.world.decay.processors.DoubleDecayProcessor;
import org.dimdev.dimdoors.world.decay.processors.SelfDecayProcessor;
import org.dimdev.dimdoors.world.decay.processors.BlockDecayProcessor;

import static net.minecraft.block.Blocks.ANCIENT_DEBRIS;
import static net.minecraft.block.Blocks.ANDESITE;
import static net.minecraft.block.Blocks.ANDESITE_SLAB;
import static net.minecraft.block.Blocks.ANDESITE_STAIRS;
import static net.minecraft.block.Blocks.ANVIL;
import static net.minecraft.block.Blocks.BAMBOO;
import static net.minecraft.block.Blocks.BARREL;
import static net.minecraft.block.Blocks.BASALT;
import static net.minecraft.block.Blocks.BEACON;
import static net.minecraft.block.Blocks.BEEHIVE;
import static net.minecraft.block.Blocks.BLACKSTONE;
import static net.minecraft.block.Blocks.BLACKSTONE_SLAB;
import static net.minecraft.block.Blocks.BLACKSTONE_STAIRS;
import static net.minecraft.block.Blocks.BLACKSTONE_WALL;
import static net.minecraft.block.Blocks.BLUE_ICE;
import static net.minecraft.block.Blocks.BONE_BLOCK;
import static net.minecraft.block.Blocks.BOOKSHELF;
import static net.minecraft.block.Blocks.CARVED_PUMPKIN;
import static net.minecraft.block.Blocks.CHEST;
import static net.minecraft.block.Blocks.CLAY;
import static net.minecraft.block.Blocks.COAL_ORE;
import static net.minecraft.block.Blocks.COBBLESTONE;
import static net.minecraft.block.Blocks.COBBLESTONE_SLAB;
import static net.minecraft.block.Blocks.COBBLESTONE_STAIRS;
import static net.minecraft.block.Blocks.COBBLESTONE_WALL;
import static net.minecraft.block.Blocks.COBWEB;
import static net.minecraft.block.Blocks.COMPOSTER;
import static net.minecraft.block.Blocks.CONDUIT;
import static net.minecraft.block.Blocks.COPPER_BLOCK;
import static net.minecraft.block.Blocks.CRIMSON_NYLIUM;
import static net.minecraft.block.Blocks.CRYING_OBSIDIAN;
import static net.minecraft.block.Blocks.CUT_COPPER;
import static net.minecraft.block.Blocks.CUT_COPPER_SLAB;
import static net.minecraft.block.Blocks.CUT_COPPER_STAIRS;
import static net.minecraft.block.Blocks.DIAMOND_ORE;
import static net.minecraft.block.Blocks.DIRT;
import static net.minecraft.block.Blocks.DISPENSER;
import static net.minecraft.block.Blocks.DROPPER;
import static net.minecraft.block.Blocks.EXPOSED_COPPER;
import static net.minecraft.block.Blocks.EXPOSED_CUT_COPPER;
import static net.minecraft.block.Blocks.EXPOSED_CUT_COPPER_SLAB;
import static net.minecraft.block.Blocks.EXPOSED_CUT_COPPER_STAIRS;
import static net.minecraft.block.Blocks.FURNACE;
import static net.minecraft.block.Blocks.GLASS_PANE;
import static net.minecraft.block.Blocks.GRANITE;
import static net.minecraft.block.Blocks.GRANITE_SLAB;
import static net.minecraft.block.Blocks.GRANITE_STAIRS;
import static net.minecraft.block.Blocks.GRANITE_WALL;
import static net.minecraft.block.Blocks.HONEYCOMB_BLOCK;
import static net.minecraft.block.Blocks.HONEY_BLOCK;
import static net.minecraft.block.Blocks.ICE;
import static net.minecraft.block.Blocks.IRON_BLOCK;
import static net.minecraft.block.Blocks.JACK_O_LANTERN;
import static net.minecraft.block.Blocks.LECTERN;
import static net.minecraft.block.Blocks.MAGMA_BLOCK;
import static net.minecraft.block.Blocks.MOSS_CARPET;
import static net.minecraft.block.Blocks.NETHERITE_BLOCK;
import static net.minecraft.block.Blocks.NETHER_BRICKS;
import static net.minecraft.block.Blocks.NETHER_BRICK_FENCE;
import static net.minecraft.block.Blocks.NETHER_BRICK_SLAB;
import static net.minecraft.block.Blocks.NETHER_BRICK_STAIRS;
import static net.minecraft.block.Blocks.NETHER_BRICK_WALL;
import static net.minecraft.block.Blocks.NETHER_WART_BLOCK;
import static net.minecraft.block.Blocks.OXIDIZED_COPPER;
import static net.minecraft.block.Blocks.OXIDIZED_CUT_COPPER;
import static net.minecraft.block.Blocks.OXIDIZED_CUT_COPPER_SLAB;
import static net.minecraft.block.Blocks.OXIDIZED_CUT_COPPER_STAIRS;
import static net.minecraft.block.Blocks.PACKED_ICE;
import static net.minecraft.block.Blocks.PISTON;
import static net.minecraft.block.Blocks.POLISHED_ANDESITE;
import static net.minecraft.block.Blocks.POLISHED_ANDESITE_SLAB;
import static net.minecraft.block.Blocks.POLISHED_ANDESITE_STAIRS;
import static net.minecraft.block.Blocks.POLISHED_GRANITE;
import static net.minecraft.block.Blocks.POLISHED_GRANITE_SLAB;
import static net.minecraft.block.Blocks.POLISHED_GRANITE_STAIRS;
import static net.minecraft.block.Blocks.PUMPKIN;
import static net.minecraft.block.Blocks.RAIL;
import static net.minecraft.block.Blocks.REDSTONE_LAMP;
import static net.minecraft.block.Blocks.RED_SAND;
import static net.minecraft.block.Blocks.RED_SANDSTONE;
import static net.minecraft.block.Blocks.RESPAWN_ANCHOR;
import static net.minecraft.block.Blocks.SAND;
import static net.minecraft.block.Blocks.SANDSTONE;
import static net.minecraft.block.Blocks.SCAFFOLDING;
import static net.minecraft.block.Blocks.SKELETON_SKULL;
import static net.minecraft.block.Blocks.SKELETON_WALL_SKULL;
import static net.minecraft.block.Blocks.SLIME_BLOCK;
import static net.minecraft.block.Blocks.SNOW;
import static net.minecraft.block.Blocks.SPONGE;
import static net.minecraft.block.Blocks.STICKY_PISTON;
import static net.minecraft.block.Blocks.STONE;
import static net.minecraft.block.Blocks.STONE_BRICKS;
import static net.minecraft.block.Blocks.TARGET;
import static net.minecraft.block.Blocks.WARPED_NYLIUM;
import static net.minecraft.block.Blocks.WATER;
import static net.minecraft.block.Blocks.WEATHERED_COPPER;
import static net.minecraft.block.Blocks.WEATHERED_CUT_COPPER;
import static net.minecraft.block.Blocks.WEATHERED_CUT_COPPER_SLAB;
import static net.minecraft.block.Blocks.WEATHERED_CUT_COPPER_STAIRS;
import static net.minecraft.block.Blocks.WET_SPONGE;
import static net.minecraft.block.Blocks.WHITE_WOOL;
import static net.minecraft.block.Blocks.WITHER_ROSE;
import static net.minecraft.data.family.BlockFamilies.DEEPSLATE;
import static org.dimdev.dimdoors.DimensionalDoors.id;

public class LimboDecayProvider implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

	private final DataOutput.PathResolver decayPatternPathResolver;

	public LimboDecayProvider(FabricDataOutput output) {
		this.decayPatternPathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "decay_patterns");
    }

    @Override
    public CompletableFuture<?> run(DataWriter cache) {
		Set<Identifier> generatedDecayPatterns = Sets.newHashSet();
		List<CompletableFuture<?>> list = new ArrayList<>();


        BiConsumer<Identifier, JsonObject> consumer = (identifier, json)  -> {
            Path outputPath = decayPatternPathResolver.resolveJson(identifier);
			list.add(DataProvider.writeToPath(cache, json, outputPath));
		};

		generatePatterns(consumer);

		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

	protected void generatePatterns(BiConsumer<Identifier, JsonObject> consumer) {
		createPatterData(id("air"), ModBlockTags.DECAY_TO_AIR, Blocks.AIR).run(consumer);
		createPatterData(id("gritty_stone"), ModBlockTags.DECAY_TO_GRITTY_STONE, ModBlocks.GRITTY_STONE).run(consumer);
		createPatterData(id("leak"), Fluids.WATER, ModBlocks.LEAK).run(consumer);
		createPatterData(id("solid_static"), ModBlockTags.DECAY_TO_SOLID_STATIC, ModBlocks.SOLID_STATIC).run(consumer);
		createPatterData(id("unraveled_fabric"), Blocks.CLAY, ModBlocks.UNRAVELLED_BLOCK).run(consumer);
		createPatterData(id("unraveled_fence"), ModBlockTags.DECAY_UNRAVELED_FENCE, ModBlocks.UNRAVELED_FENCE).run(consumer);
		createPatterData(id("unraveled_gate"), ModBlockTags.DECAY_UNRAVELED_GATE, ModBlocks.UNRAVELED_GATE).run(consumer);
		createPatterData(id("unraveled_button"), ModBlockTags.DECAY_UNRAVELED_BUTTON, ModBlocks.UNRAVELED_BUTTON).run(consumer);
		createPatterData(id("unraveled_slab"), ModBlockTags.DECAY_UNRAVELED_SLAB, ModBlocks.UNRAVELED_SLAB).run(consumer);
		createPatterData(id("unraveled_stairs"), ModBlockTags.DECAY_UNRAVELED_STAIRS, ModBlocks.UNRAVELED_STAIRS).run(consumer);

		createPatterData(id("cobweb"), BlockTags.WOOL, COBWEB).run(consumer);
		createPatterData(id("driftwood_leaves"), BlockTags.LEAVES, ModBlocks.DRIFTWOOD_LEAVES).run(consumer);
		createPatterData(id("driftwood_sapling"), BlockTags.SAPLINGS, ModBlocks.DRIFTWOOD_SAPLING).run(consumer);
		createPatterData(id("glass_pane"), ModBlockTags.DECAY_TO_GLASS_PANE, GLASS_PANE).run(consumer);
		createPatterData(id("moss_carpet"), BlockTags.WOOL_CARPETS, MOSS_CARPET).run(consumer);
		createPatterData(id("driftwood_trapdoor"), BlockTags.WOODEN_TRAPDOORS, ModBlocks.DRIFTWOOD_TRAPDOOR).run(consumer);
		createDoublePattern(id("driftwood_trapdoor_door"), ModBlocks.DRIFTWOOD_DOOR, ModBlocks.DRIFTWOOD_TRAPDOOR).run(consumer);
		createPatterData(id("rail"), ModBlockTags.DECAY_TO_RAIL, RAIL).run(consumer);
		createPatterData(id("rust"), ModBlockTags.DECAY_TO_RUST, ModBlocks.RUST).run(consumer);
		createPatterData(id("unraveled_spike"), ModBlockTags.DECAY_TO_UNRAVELED_SPIKE, ModBlocks.UNRAVELED_SPIKE).run(consumer);
		createPatterData(id("wither_rose"), ModBlockTags.DECAY_TO_WITHER_ROSE, WITHER_ROSE).run(consumer);
		createPatterData(id("water"), SNOW, Fluids.WATER).run(consumer);
		createPatterData(id("clay"), ModBlockTags.DECAY_TO_CLAY, CLAY).run(consumer);
		createPatterData(id("clay_fence"), ModBlockTags.DECAY_CLAY_FENCE, ModBlocks.CLAY_FENCE).run(consumer);
		createPatterData(id("clay_gate"), ModBlockTags.DECAY_CLAY_GATE, ModBlocks.CLAY_GATE).run(consumer);
		createPatterData(id("clay_button"), ModBlockTags.DECAY_CLAY_BUTTON, ModBlocks.CLAY_BUTTON).run(consumer);
		createPatterData(id("clay_slab"), ModBlockTags.DECAY_CLAY_SLAB, ModBlocks.CLAY_SLAB).run(consumer);
		createPatterData(id("clay_stairs"), ModBlockTags.DECAY_CLAY_STAIRS, ModBlocks.CLAY_STAIRS).run(consumer);
		createPatterData(id("dark_sand"), ModBlockTags.DECAY_TO_DARK_SAND, ModBlocks.DARK_SAND).run(consumer);
	 	createPatterData(id("dark_sand_fence"), ModBlockTags.DECAY_DARK_SAND_FENCE, ModBlocks.DARK_SAND_FENCE).run(consumer);
	 	createPatterData(id("dark_sand_gate"), ModBlockTags.DECAY_DARK_SAND_GATE, ModBlocks.DARK_SAND_GATE).run(consumer);
	 	createPatterData(id("dark_sand_button"), ModBlockTags.DECAY_DARK_SAND_BUTTON, ModBlocks.DARK_SAND_BUTTON).run(consumer);
	 	createPatterData(id("dark_sand_slab"), ModBlockTags.DECAY_DARK_SAND_SLAB, ModBlocks.DARK_SAND_SLAB).run(consumer);
	 	createPatterData(id("dark_sand_stairs"), ModBlockTags.DECAY_DARK_SAND_STAIRS, ModBlocks.DARK_SAND_STAIRS).run(consumer);

		createPatterData(id("wool"), TARGET, WHITE_WOOL).run(consumer);
		createDoublePattern(id("wool_bed"), BlockTags.BEDS, WHITE_WOOL).run(consumer);
		createPatterData(id("driftwood_door"), ModBlockTags.DECAY_TO_DRIFTWOOD_DOOR, ModBlocks.DRIFTWOOD_DOOR).run(consumer);
		createPatterData(id("amalgam"), ModBlockTags.DECAY_TO_AMALGAM, ModBlocks.AMALGAM_BLOCK).run(consumer);
		createPatterData(id("amalgam_slab"), Blocks.CUT_COPPER_SLAB, ModBlocks.AMALGAM_SLAB).run(consumer);
		createPatterData(id("amalgam_stairs"), Blocks.CUT_COPPER_STAIRS, ModBlocks.AMALGAM_STAIRS).run(consumer);
		createPatterData(id("mud"), ModBlockTags.DECAY_TO_MUD, Blocks.MUD).run(consumer);
		createPatterData(id("mud_fence"), ModBlockTags.DECAY_TO_MUD_FENCE, ModBlocks.MUD_FENCE).run(consumer);
		createPatterData(id("mud_gate"), ModBlockTags.DECAY_TO_MUD_GATE, ModBlocks.MUD_GATE).run(consumer);
		createPatterData(id("mud_button"), ModBlockTags.DECAY_TO_MUD_BUTTON, ModBlocks.MUD_BUTTON).run(consumer);
		createPatterData(id("mud_slab"), ModBlockTags.DECAY_TO_MUD_SLAB, ModBlocks.MUD_SLAB).run(consumer);
		createPatterData(id("mud_stairs"), ModBlockTags.DECAY_TO_MUD_STAIRS, ModBlocks.MUD_STAIRS).run(consumer);
		Stream.of(DyeColor.values()).map(DyeColor::asString).forEach(name -> {
			createPatterData(id(name + "_terracotta"), getBlock(Identifier.tryParse(name + "_glazed_terracotta")), getBlock(Identifier.tryParse(name + "_terracotta"))).run(consumer);
			createPatterData(id(name + "_concrete_powder"), getBlock(Identifier.tryParse(name + "_powered_concrete")), getBlock(Identifier.tryParse(name + "_concrete"))).run(consumer);
		});
		createPatterData(id("glass"), ModBlockTags.DECAY_TO_GLASS, Blocks.GLASS).run(consumer);
		createPatterData(id("gravel"), ModBlockTags.DECAY_TO_GRAVEL, Blocks.GRAVEL).run(consumer);
		createPatterData(id("gravel_fence", ModBlockTags.DECAY_TO_GRAVEL_FENCE, ModBlocks.GRAVEL_FENCE)).run(consumer);
		createPatterData(id("gravel_gate", ModBlockTags.DECAY_TO_GRAVEL_GATE, ModBlocks.GRAVEL_GATE)).run(consumer);
		createPatterData(id("gravel_button", ModBlockTags.DECAY_TO_GRAVEL_BUTTON, ModBlocks.GRAVEL_BUTTON)).run(consumer);
		createPatterData(id("gravel_slab", ModBlockTags.DECAY_TO_GRAVEL_SLAB, ModBlocks.GRAVEL_SLAB)).run(consumer);
		createPatterData(id("gravel_stairs", ModBlockTags.DECAY_TO_GRAVEL_STAIRS, ModBlocks.GRAVEL_STAIRS)).run(consumer);
		createPatterData(id("gravel_wall", ModBlockTags.DECAY_TO_GRAVEL_WALL, ModBlocks.GRAVEL_WALL)).run(consumer);
		createPatterData(id("red_sand"), Blocks.RED_SANDSTONE, RED_SAND).run(consumer);
		createPatterData(id("red_sand_slab", ModBlockTags.DECAY_TO_RED_SAND_SLAB, ModBlocks.RED_SAND_SLAB)).run(consumer);
		createPatterData(id("red_sand_stairs", ModBlockTags.DECAY_TO_RED_SAND_STAIRS, ModBlocks.RED_SAND_STAIRS)).run(consumer);
		createPatterData(id("red_sand_wall", ModBlockTags.DECAY_TO_RED_SAND_WALL, ModBlocks.RED_SAND_WALL)).run(consumer);
		createPatterData(id("sand"), ModBlockTags.DECAY_TO_SAND, SAND).run(consumer);
		createPatterData(id("sand_slab", ModBlockTags.DECAY_TO_SAND_SLAB, ModBlocks.SAND_SLAB)).run(consumer);
		createPatterData(id("sand_stairs", ModBlockTags.DECAY_TO_SAND_STAIRS, ModBlocks.SAND_STAIRS)).run(consumer);
		createPatterData(id("sand_wall", ModBlockTags.DECAY_TO_SAND_WALL, ModBlocks.SAND_WALL)).run(consumer);
		createPatterData(id("soul_sand"), Blocks.SOUL_SOIL, Blocks.SOUL_SAND).run(consumer);

		createSimplePattern(id("ice"), PACKED_ICE, ICE).run(consumer);
		createSimplePattern(id("iron_block"), ANVIL, IRON_BLOCK).run(consumer);
		createOxidizationChain(COPPER_BLOCK, EXPOSED_COPPER, WEATHERED_COPPER, OXIDIZED_COPPER, consumer);
		createOxidizationChain(CUT_COPPER, EXPOSED_CUT_COPPER, WEATHERED_CUT_COPPER, OXIDIZED_CUT_COPPER, consumer);
		createOxidizationChain(CUT_COPPER_SLAB, EXPOSED_CUT_COPPER_SLAB, WEATHERED_CUT_COPPER_SLAB, OXIDIZED_CUT_COPPER_SLAB, consumer);
		createOxidizationChain(CUT_COPPER_STAIRS, EXPOSED_CUT_COPPER_STAIRS, WEATHERED_CUT_COPPER_STAIRS, OXIDIZED_CUT_COPPER_STAIRS, consumer);
		createPatterData(id("ancient_debris"), NETHERITE_BLOCK, ANCIENT_DEBRIS).run(consumer);
		createPatterData(id("dirt"), ModBlockTags.DECAY_TO_DIRT, DIRT).run(consumer);
		createPatterData(id("crimson_nylium"), WARPED_NYLIUM, CRIMSON_NYLIUM).run(consumer);
		createPatterData(id("driftwood_plank"), ModBlockTags.DECAY_TO_DRIFTWOOD_PLANK, ModBlocks.DRIFTWOOD_PLANKS).run(consumer);
		createPatterData(id("driftwood_fence"), ModBlockTags.DECAY_TO_DRIFTWOOD_FENCE, ModBlocks.DRIFTWOOD_FENCE).run(consumer);
		createPatterData(id("driftwood_gate"), ModBlockTags.DECAY_TO_DRIFTWOOD_GATE, ModBlocks.DRIFTWOOD_GATE).run(consumer);
		createPatterData(id("driftwood_button"), ModBlockTags.DECAY_TO_DRIFTWOOD_BUTTON, ModBlocks.DRIFTWOOD_BUTTON).run(consumer);
		createPatterData(id("driftwood_slab"), ModBlockTags.DECAY_TO_DRIFTWOOD_SLAB, ModBlocks.DRIFTWOOD_SLAB).run(consumer);
		createPatterData(id("driftwood_stairs"), ModBlockTags.DECAY_TO_DRIFTWOOD_STAIRS, ModBlocks.DRIFTWOOD_STAIRS).run(consumer);
		createPatterData(id("composter"), BARREL, COMPOSTER).run(consumer);
		createPatterData(id("chest"), ModBlockTags.DECAY_TO_CHEST, CHEST).run(consumer);
		createPatterData(id("bone_block"), CONDUIT, BONE_BLOCK).run(consumer);
		createPatterData(id("skeleton_skull"), ModBlockTags.DECAY_TO_SKELETON_SKULL, SKELETON_SKULL).run(consumer);
		createPatterData(id("skeleton_wall_skull"), ModBlockTags.DECAY_TO_SKELETON_WALL_SKULL, SKELETON_WALL_SKULL).run(consumer);
		createPatterData(id("bamboo"), SCAFFOLDING, BAMBOO).run(consumer);
		createPatterData(id("pumpkin"), CARVED_PUMPKIN, PUMPKIN).run(consumer);
		createPatterData(id("slime_block"), HONEY_BLOCK, SLIME_BLOCK).run(consumer);
		createPatterData(id("honeycomb_block"), SPONGE, HONEYCOMB_BLOCK).run(consumer);
		createPatterData(id("lectern"), BOOKSHELF, LECTERN).run(consumer);
		createPatterData(id("piston"), STICKY_PISTON, PISTON).run(consumer);
		createPatterData(id("netherwart_block"), ModBlockTags.DECAY_TO_NETHERWART_BLOCK, NETHER_WART_BLOCK).run(consumer);
		createPatterData(id("redstone_lamp"), BEACON, REDSTONE_LAMP).run(consumer);
		createPatterData(id("amalgam_ore"), ModBlockTags.DECAY_TO_AMALGAM_ORE, ModBlocks.AMALGAM_ORE).run(consumer);
		createPatterData(id("clod_ore"), ModBlockTags.DECAY_TO_CLOD_ORE, ModBlocks.CLOD_ORE).run(consumer);
		createPatterData(id("cobblestone"), ModBlockTags.DECAY_TO_COBBLESTONE, COBBLESTONE).run(consumer);
		createPatterData(id("cobblestone_slab"), ModBlockTags.DECAY_TO_COBBLESTONE_SLAB, COBBLESTONE_SLAB).run(consumer);
		createPatterData(id("cobblestone_stairs"), ModBlockTags.DECAY_TO_COBBLESTONE_STAIRS, COBBLESTONE_STAIRS).run(consumer);
		createPatterData(id("cobblestone_wall"), ModBlockTags.DECAY_TO_COBBLESTONE_WALL, COBBLESTONE_WALL).run(consumer);
		createPatterData(id("red_sandstone"), ModBlockTags.DECAY_TO_RED_SANDSTONE, RED_SANDSTONE);
		createPatterData(id("red_sandstone"), ModBlockTags.DECAY_TO_SANDSTONE, SANDSTONE);

		createPatterData(id("packed_ice"), BLUE_ICE, PACKED_ICE).run(consumer);
		createPatterData(id("driftwood_wood"), ModBlockTags.DECAY_TO_DRIFTWOOD_WOOD, ModBlocks.DRIFTWOOD_WOOD).run(consumer);
		createPatterData(id("driftwood_log"), ModBlockTags.DECAY_TO_DRIFTWOOD_LOG, ModBlocks.DRIFTWOOD_LOG).run(consumer);
		createPatterData(id("barrel"), BEEHIVE, BARREL).run(consumer);
		createPatterData(id("carved_pumpkin"), JACK_O_LANTERN, CARVED_PUMPKIN).run(consumer);
		createPatterData(id("sponge"), WET_SPONGE, SPONGE);
		createPatterData(id("coal_ore"), DIAMOND_ORE, COAL_ORE);
		createPatterData(id("andesite"), POLISHED_ANDESITE, ANDESITE).run(consumer);
		createPatterData(id("andesite_slab"), POLISHED_ANDESITE_SLAB, ANDESITE_SLAB).run(consumer);
		createPatterData(id("andesite_stairs"), POLISHED_ANDESITE_STAIRS, ANDESITE_STAIRS).run(consumer);
		createPatterData(id("basalt"), ModBlockTags.DECAY_TO_BASALT, BASALT);
		createPatterData(id("basalt_lava"), Fluids.LAVA, BASALT);
		createPatterData(id("blackstone"), ModBlockTags.DECAY_TO_BLACKSTONE, BLACKSTONE).run(consumer);
		createPatterData(id("blackstone_slab"), ModBlockTags.DECAY_TO_BLACKSTONE_SLAB, BLACKSTONE_SLAB).run(consumer);
		createPatterData(id("blackstone_stairs"), ModBlockTags.DECAY_TO_BLACKSTONE_STAIRS, BLACKSTONE_STAIRS).run(consumer);
		createPatterData(id("blackstone_wall"), ModBlockTags.DECAY_TO_BLACKSTONE_WALL, BLACKSTONE_WALL).run(consumer);
		createPatterData(id("deepslate"), ModBlockTags.DECAY_TO_DEEPSLATE, DEEPSLATE).run(consumer);
		createPatterData(id("deepslate_slab"), ModBlockTags.DECAY_TO_DEEPSLATE_SLAB, ModBlocks.DEEPSLATE_SLAB).run(consumer);
		createPatterData(id("deepslate_stairs"), ModBlockTags.DECAY_TO_DEEPSLATE_STAIRS, ModBlocks.DEEPSLATE_STAIRS).run(consumer);
		createPatterData(id("deepslate_wall"), ModBlockTags.DECAY_TO_DEEPSLATE_WALL, ModBlocks.DEEPSLATE_WALL).run(consumer);
		createPatterData(id("diorite"), ModBlockTags.DECAY_TO_DIORITE, Blocks.DIORITE).run(consumer);
		createPatterData(id("diorite_slab"), ModBlockTags.DECAY_TO_DIORITE_SLAB, Blocks.DIORITE_SLAB).run(consumer);
		createPatterData(id("diorite_stairs"), ModBlockTags.DECAY_TO_DIORITE_STAIRS, Blocks.DIORITE_STAIRS).run(consumer);
		createPatterData(id("diorite_wall"), ModBlockTags.DECAY_TO_DIORITE_WALL, Blocks.DIORITE_WALL).run(consumer);
		createPatterData(id("endstone"), ModBlockTags.DECAY_TO_ENDSTONE, Blocks.END_STONE).run(consumer);
		createPatterData(id("endstone_slab"), ModBlockTags.DECAY_TO_ENDSTONE_SLAB, ModBlocks.END_STONE_SLAB).run(consumer);
		createPatterData(id("endstone_stairs"), ModBlockTags.DECAY_TO_ENDSTONE_STAIRS, ModBlocks.END_STONE_STAIRS).run(consumer);
		createPatterData(id("endstone_wall"), ModBlockTags.DECAY_TO_ENDSTONE_WALL, ModBlocks.END_STONE_WALL).run(consumer);
		createPatterData(id("furnace"), ModBlockTags.DECAY_TO_FURNACE, FURNACE);
		createPatterData(id("granite"), ModBlockTags.DECAY_TO_GRANITE, GRANITE).run(consumer);
		createPatterData(id("granite_slab"), ModBlockTags.DECAY_TO_GRANITE_SLAB, GRANITE_SLAB).run(consumer);
		createPatterData(id("granite_stairs"), ModBlockTags.DECAY_TO_GRANITE_STAIRS, GRANITE_STAIRS).run(consumer);
		createPatterData(id("granite"), ModBlockTags.DECAY_TO_GRANITE, GRANITE).run(consumer);
		createPatterData(id("granite_slab"), ModBlockTags.DECAY_TO_GRANITE_SLAB, GRANITE_SLAB).run(consumer);
		createPatterData(id("granite_stairs"), ModBlockTags.DECAY_TO_GRANITE_STAIRS, GRANITE_STAIRS).run(consumer);
		createPatterData(id("netherrack"), ModBlockTags.NETHERRACK, Blocks.NETHERRACK);
		createPatterData(id("netherrack_fence"), ModBlockTags.NETHERRACK_FENCE, ModBlocks.NETHERRACK_FENCE);
		createPatterData(id("netherrack_slab"), ModBlockTags.NETHERRACK_SLAB, ModBlocks.NETHERRACK_SLAB);
		createPatterData(id("netherrack_stairs"), ModBlockTags.NETHERRACK_STAIRS, ModBlocks.NETHERRACK_STAIRS);
		createPatterData(id("netherrack_wall"), ModBlockTags.NETHERRACK_WALL, ModBlocks.NETHERRACK_WALL);
		createPatterData(id("prismarine"), ModBlockTags.DECAY_TO_PRISMARINE, Blocks.PRISMARINE).run(consumer);
		createPatterData(id("prismarine_slab"), ModBlockTags.DECAY_TO_PRISMARINE_SLAB, Blocks.PRISMARINE_SLAB).run(consumer);
		createPatterData(id("prismarine_stairs"), ModBlockTags.DECAY_TO_PRISMARINE_STAIRS, Blocks.PRISMARINE_STAIRS).run(consumer);
		createPatterData(id("prismarine_wall"), ModBlockTags.DECAY_TO_PRISMARINE_WALL, Blocks.PRISMARINE_WALL).run(consumer);
		createPatterData(id("stone"), ModBlockTags.DECAY_TO_STONE, STONE).run(consumer);

		createPatterData(id("lava"), MAGMA_BLOCK, Fluids.LAVA);
		createPatterData(id("dropper"), DISPENSER, DROPPER);
		createPatterData(id("dark_prismarine"), ModBlockTags.DECAY_TO_DARK_PRISMARINE, Blocks.DARK_PRISMARINE).run(consumer);
		createPatterData(id("dark_prismarine_slab"), ModBlockTags.DECAY_TO_DARK_PRISMARINE_SLAB, Blocks.DARK_PRISMARINE_SLAB).run(consumer);
		createPatterData(id("dark_prismarine_stairs"), ModBlockTags.DECAY_TO_DARK_PRISMARINE_STAIRS, Blocks.DARK_PRISMARINE_STAIRS).run(consumer);
		createPatterData(id("clod_block"), ModBlockTags.DECAY_TO_CLOD_BLOCK, ModBlocks.CLOD_BLOCK);
		createPatterData(id("cracked_stone_brick"), Blocks.CRACKED_STONE_BRICKS, STONE_BRICKS);
		createPatterData(id("obsidian"), ModBlockTags.DECAY_TO_OBSIDIAN).run(consumer);
		createPatterData(id("stone_brick"), ModBlockTags.DECAY_TO_STONE_BRICKS, Blocks.STONE_BRICKS).run(consumer);
		createPatterData(id("stone_brick_slab"), ModBlockTags.DECAY_TOSTONE_BRICK_SLAB, Blocks.STONE_BRICK_SLAB).run(consumer);
		createPatterData(id("stone_brick_stairs"), ModBlockTags.DECAY_TOSTONE_BRICK_STAIRS, Blocks.STONE_BRICK_STAIRS).run(consumer);
		createPatterData(id("stone_brick_wall"), ModBlockTags.DECAY_TOSTONE_BRICK_WALL, Blocks.STONE_BRICK_WALL).run(consumer);
		createPatterData(id("crying_obsidian"), RESPAWN_ANCHOR, CRYING_OBSIDIAN);
	}

	public DecayPatternData createPatterData(Identifier id, Object before, Object after) {
		return new DecayPatternData(id, getPredicate(before), getProcessor(after));
	}

	private DecayPredicate getPredicate(Object object) {
		if (!(object instanceof TagKey<?> tag)) {
			if(object instanceof Block block) return SimpleDecayPredicate.builder().block(block).create();
			else if(object instanceof Fluid fluid) return FluidDecayPredicate.builder().fluid(fluid).create();
		} else {
			if (tag.isOf(RegistryKeys.BLOCK)) return SimpleDecayPredicate.builder().tag((TagKey<Block>) tag).create();
			else if (tag.isOf(RegistryKeys.FLUID)) return FluidDecayPredicate.builder().tag((TagKey<Fluid>) tag).create();
		}

		return DecayPredicate.NONE;
	}

	private DecayProcessor getProcessor(Object object) {
		if(object instanceof Block block) return BlockDecayProcessor.builder().block(block).create();
		else if(object instanceof Fluid fluid) return FluidDecayProcessor.builder().fluid(fluid).create();
		else return DecayProcessor.NONE;
	}

	private void createOxidizationChain(Block regular, Block exposed, Block weathered, Block oxidized, BiConsumer<Identifier, JsonObject> consumer) {
		Function<Block, Block> waxed = block -> {
			Identifier id = getId(block);

			return getBlock(new Identifier(id.getNamespace(), "waxed_" + id.getPath()));
		};
		Function<Block, Identifier> id = block -> new Identifier("dimdoors:" + getId(block).getPath());

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

	private Block getBlock(Identifier id) {
		return Registries.BLOCK.get(id);
	}

	private Identifier getId(Block block) {
		return Registries.BLOCK.getId(block);
	}

	private DecayPatternData turnIntoSelf(Identifier identifier, Object before) {
		return new DecayPatternData(identifier, getPredicate(before), SelfDecayProcessor.instance());
	}

    @Override
    public String getName() {
        return "Limbo Decay";
    }

    private static Path getOutput(Path rootOutput, Identifier lootTableId) {
        return rootOutput.resolve("data/" + lootTableId.getNamespace() + "/decay_patterns/" + lootTableId.getPath() + ".json");
    }

    public DecayPatternData createSimplePattern(Identifier id, Block before, Block after) {
        return new DecayPatternData(id, SimpleDecayPredicate.builder().block(before).create(), BlockDecayProcessor.builder().block(after).entropy(1).create());
    }

	public DecayPatternData createSimplePattern(Identifier id, TagKey<Block> before, Block after) {
		return new DecayPatternData(id, SimpleDecayPredicate.builder().tag(before).create(), BlockDecayProcessor.builder().block(after).entropy(1).create());
	}

	public DecayPatternData createDoublePattern(Identifier id, Object before, Block after) {
		return new DecayPatternData(id, getPredicate(before), DoubleDecayProcessor.builder().block(after).entropy(1).create());
	}

    public static class DecayPatternData {
        private Identifier id;
        private DecayPredicate predicate;
        private DecayProcessor processor;

        public DecayPatternData(Identifier id, DecayPredicate predicate, DecayProcessor processor) {
            this.id = id;
            this.predicate = predicate;
            this.processor = processor;
        }

        public void run(BiConsumer<Identifier, JsonObject> consumer) {
            JsonObject object = new JsonObject();
            object.add("predicate", ResourceUtil.NBT_TO_JSON.apply(predicate.toNbt(new NbtCompound())));
            object.add("processor", ResourceUtil.NBT_TO_JSON.apply(processor.toNbt(new NbtCompound())));

            consumer.accept(id, object);
        }
    }
}
