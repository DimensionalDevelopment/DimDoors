package org.dimdev.dimdoors.datagen;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.tag.ModBlockTags;
import org.dimdev.dimdoors.world.decay.DecayPredicate;
import org.dimdev.dimdoors.world.decay.DecayProcessor;
import org.dimdev.dimdoors.world.decay.predicates.SimpleDecayPredicate;
import org.dimdev.dimdoors.world.decay.predicates.SimpleTagDecayPredicate;
import org.dimdev.dimdoors.world.decay.processors.DoubleDecayProcessor;
import org.dimdev.dimdoors.world.decay.processors.SelfDecayProcessor;
import org.dimdev.dimdoors.world.decay.processors.SimpleDecayProcesor;

import static net.minecraft.block.Blocks.ANCIENT_DEBRIS;
import static net.minecraft.block.Blocks.ANVIL;
import static net.minecraft.block.Blocks.BAMBOO;
import static net.minecraft.block.Blocks.BARREL;
import static net.minecraft.block.Blocks.BEACON;
import static net.minecraft.block.Blocks.BONE_BLOCK;
import static net.minecraft.block.Blocks.BOOKSHELF;
import static net.minecraft.block.Blocks.CARVED_PUMPKIN;
import static net.minecraft.block.Blocks.CHEST;
import static net.minecraft.block.Blocks.CLAY;
import static net.minecraft.block.Blocks.COBBLESTONE;
import static net.minecraft.block.Blocks.COBWEB;
import static net.minecraft.block.Blocks.COMPOSTER;
import static net.minecraft.block.Blocks.CONDUIT;
import static net.minecraft.block.Blocks.CRIMSON_NYLIUM;
import static net.minecraft.block.Blocks.DIRT;
import static net.minecraft.block.Blocks.GLASS_PANE;
import static net.minecraft.block.Blocks.HONEYCOMB_BLOCK;
import static net.minecraft.block.Blocks.HONEY_BLOCK;
import static net.minecraft.block.Blocks.ICE;
import static net.minecraft.block.Blocks.IRON_BLOCK;
import static net.minecraft.block.Blocks.LECTERN;
import static net.minecraft.block.Blocks.MOSS_CARPET;
import static net.minecraft.block.Blocks.NETHERITE_BLOCK;
import static net.minecraft.block.Blocks.NETHER_WART_BLOCK;
import static net.minecraft.block.Blocks.PACKED_ICE;
import static net.minecraft.block.Blocks.PISTON;
import static net.minecraft.block.Blocks.PUMPKIN;
import static net.minecraft.block.Blocks.RAIL;
import static net.minecraft.block.Blocks.REDSTONE_LAMP;
import static net.minecraft.block.Blocks.SCAFFOLDING;
import static net.minecraft.block.Blocks.SKELETON_SKULL;
import static net.minecraft.block.Blocks.SKELETON_WALL_SKULL;
import static net.minecraft.block.Blocks.SLIME_BLOCK;
import static net.minecraft.block.Blocks.SNOW;
import static net.minecraft.block.Blocks.SPONGE;
import static net.minecraft.block.Blocks.STICKY_PISTON;
import static net.minecraft.block.Blocks.WARPED_NYLIUM;
import static net.minecraft.block.Blocks.WATER;
import static net.minecraft.block.Blocks.WITHER_ROSE;

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
		createSimplePattern(DimensionalDoors.id("air"), ModBlockTags.DECAY_TO_AIR, Blocks.AIR).run(consumer);
		createSimplePattern(DimensionalDoors.id("gritty_stone"), ModBlockTags.DECAY_TO_GRITTY_STONE, ModBlocks.GRITTY_STONE).run(consumer);
		createSimplePattern(DimensionalDoors.id("leak"), Blocks.WATER, ModBlocks.LEAK).run(consumer);
		createSimplePattern(DimensionalDoors.id("solid_static"), ModBlockTags.DECAY_TO_SOLID_STATIC, ModBlocks.SOLID_STATIC).run(consumer);
		createSimplePattern(DimensionalDoors.id("unraveled_fabric"), Blocks.CLAY, ModBlocks.UNRAVELLED_BLOCK).run(consumer);
		createSimplePattern(DimensionalDoors.id("unraveled_fence"), ModBlockTags.DECAY_UNRAVELED_FENCE, ModBlocks.UNRAVELED_FENCE).run(consumer);
		createSimplePattern(DimensionalDoors.id("unraveled_gate"), ModBlockTags.DECAY_UNRAVELED_GATE, ModBlocks.UNRAVELED_GATE).run(consumer);
		createSimplePattern(DimensionalDoors.id("unraveled_button"), ModBlockTags.DECAY_UNRAVELED_BUTTON, ModBlocks.UNRAVELED_BUTTON).run(consumer);
		createSimplePattern(DimensionalDoors.id("unraveled_slab"), ModBlockTags.DECAY_UNRAVELED_SLAB, ModBlocks.UNRAVELED_SLAB).run(consumer);
		createSimplePattern(DimensionalDoors.id("unraveled_stairs"), ModBlockTags.DECAY_UNRAVELED_STAIRS, ModBlocks.UNRAVELED_STAIRS).run(consumer);

		createSimplePattern(new Identifier("dimdoors:cobweb"), BlockTags.WOOL, COBWEB).run(consumer);
		createSimplePattern(new Identifier("dimdoors:driftwood_leaves"), BlockTags.LEAVES, ModBlocks.DRIFTWOOD_LEAVES).run(consumer);
		createSimplePattern(new Identifier("dimdoors:driftwood_sapling"), BlockTags.SAPLINGS, ModBlocks.DRIFTWOOD_SAPLING).run(consumer);
		createSimplePattern(new Identifier("dimdoors:glass_pane"), ModBlockTags.DECAY_TO_GLASS_PANE, GLASS_PANE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:moss_carpet"), BlockTags.WOOL_CARPETS, MOSS_CARPET).run(consumer);
		createSimplePattern(new Identifier("dimdoors:driftwood_trapdoor"), BlockTags.WOODEN_TRAPDOORS, ModBlocks.DRIFTWOOD_TRAPDOOR).run(consumer);
		createDoorPattern(new Identifier("dimdoors:driftwood_trapdoor_door"), ModBlocks.DRIFTWOOD_DOOR, ModBlocks.DRIFTWOOD_TRAPDOOR).run(consumer);
		createSimplePattern(new Identifier("dimdoors:rail"), ModBlockTags.DECAY_TO_RAIL, RAIL).run(consumer);
		createSimplePattern(new Identifier("dimdoors:rust"), ModBlockTags.DECAY_TO_RUST, ModBlocks.RUST).run(consumer);
		createSimplePattern(new Identifier("dimdoors:unraveled_spike"), ModBlockTags.DECAY_TO_UNRAVELED_SPIKE, ModBlocks.UNRAVELED_SPIKE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:wither_rose"), ModBlockTags.DECAY_TO_WITHER_ROSE, WITHER_ROSE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:water"), SNOW, WATER).run(consumer);
		createSimplePattern(new Identifier("dimdoors:clay"), ModBlockTags.DECAY_TO_CLAY, CLAY).run(consumer);
		createSimplePattern(new Identifier("dimdoors:clay_fence"), ModBlockTags.DECAY_CLAY_FENCE, ModBlocks.CLAY_FENCE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:clay_gate"), ModBlockTags.DECAY_CLAY_GATE, ModBlocks.CLAY_GATE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:clay_button"), ModBlockTags.DECAY_CLAY_BUTTON, ModBlocks.CLAY_BUTTON).run(consumer);
		createSimplePattern(new Identifier("dimdoors:clay_slab"), ModBlockTags.DECAY_CLAY_SLAB, ModBlocks.CLAY_SLAB).run(consumer);
		createSimplePattern(new Identifier("dimdoors:clay_stairs"), ModBlockTags.DECAY_CLAY_STAIRS, ModBlocks.CLAY_STAIRS).run(consumer);
		createSimplePattern(new Identifier("dimdoors:dark_sand"), ModBlockTags.DECAY_TO_DARK_SAND, ModBlocks.DARK_SAND).run(consumer);
	 	createSimplePattern(new Identifier("dimdoors:dark_sand_fence"), ModBlockTags.DECAY_DARK_SAND_FENCE, ModBlocks.DARK_SAND_FENCE).run(consumer);
	 	createSimplePattern(new Identifier("dimdoors:dark_sand_gate"), ModBlockTags.DECAY_DARK_SAND_GATE, ModBlocks.DARK_SAND_GATE).run(consumer);
	 	createSimplePattern(new Identifier("dimdoors:dark_sand_button"), ModBlockTags.DECAY_DARK_SAND_BUTTON, ModBlocks.DARK_SAND_BUTTON).run(consumer);
	 	createSimplePattern(new Identifier("dimdoors:dark_sand_slab"), ModBlockTags.DECAY_DARK_SAND_SLAB, ModBlocks.DARK_SAND_SLAB).run(consumer);
	 	createSimplePattern(new Identifier("dimdoors:dark_sand_stairs"), ModBlockTags.DECAY_DARK_SAND_STAIRS, ModBlocks.DARK_SAND_STAIRS).run(consumer);
		createSimplePattern(new Identifier("dimdoors:amalgam"), ModBlockTags.DECAY_TO_AMALGAM, ModBlocks.AMALGAM_BLOCK).run(consumer);

		createSimplePattern(new Identifier("dimdoors:ice"), PACKED_ICE, ICE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:iron_block"), ANVIL, IRON_BLOCK).run(consumer);
		createSimplePattern(new Identifier("dimdoors:ancient_debris"), NETHERITE_BLOCK, ANCIENT_DEBRIS).run(consumer);
		createSimplePattern(new Identifier("dimdoors:dirt"), ModBlockTags.DECAY_TO_DIRT, DIRT).run(consumer);
		createSimplePattern(new Identifier("dimdoors:crimson_nylium"), WARPED_NYLIUM, CRIMSON_NYLIUM).run(consumer);
		createSimplePattern(new Identifier("dimdoors:driftwood_plank"), ModBlockTags.DECAY_TO_DRIFTWOOD_PLANK, ModBlocks.DRIFTWOOD_PLANKS).run(consumer);
		createSimplePattern(new Identifier("dimdoors:driftwood_fence"), ModBlockTags.DECAY_TO_DRIFTWOOD_FENCE, ModBlocks.DRIFTWOOD_FENCE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:driftwood_gate"), ModBlockTags.DECAY_TO_DRIFTWOOD_GATE, ModBlocks.DRIFTWOOD_GATE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:driftwood_button"), ModBlockTags.DECAY_TO_DRIFTWOOD_BUTTON, ModBlocks.DRIFTWOOD_BUTTON).run(consumer);
		createSimplePattern(new Identifier("dimdoors:driftwood_slab"), ModBlockTags.DECAY_TO_DRIFTWOOD_SLAB, ModBlocks.DRIFTWOOD_SLAB).run(consumer);
		createSimplePattern(new Identifier("dimdoors:driftwood_stairs"), ModBlockTags.DECAY_TO_DRIFTWOOD_STAIRS, ModBlocks.DRIFTWOOD_STAIRS).run(consumer);
		createSimplePattern(new Identifier("dimdoors:composter"), BARREL, COMPOSTER).run(consumer);
		createSimplePattern(new Identifier("dimdoors:chest"), ModBlockTags.DECAY_TO_CHEST, CHEST).run(consumer);
		createSimplePattern(new Identifier("dimdoors:bone_block"), CONDUIT, BONE_BLOCK).run(consumer);
		createSimplePattern(new Identifier("dimdoors:skeleton_skull"), ModBlockTags.DECAY_TO_SKELETON_SKULL, SKELETON_SKULL).run(consumer);
		createSimplePattern(new Identifier("dimdoors:skeleton_wall_skull"), ModBlockTags.DECAY_TO_SKELETON_WALL_SKULL, SKELETON_WALL_SKULL).run(consumer);
		createSimplePattern(new Identifier("dimdoors:bamboo"), SCAFFOLDING, BAMBOO).run(consumer);
		createSimplePattern(new Identifier("dimdoors:pumpkin"), CARVED_PUMPKIN, PUMPKIN).run(consumer);
		createSimplePattern(new Identifier("dimdoors:slime_block"), HONEY_BLOCK, SLIME_BLOCK).run(consumer);
		createSimplePattern(new Identifier("dimdoors:honeycomb_block"), SPONGE, HONEYCOMB_BLOCK).run(consumer);
		createSimplePattern(new Identifier("dimdoors:lectern"), BOOKSHELF, LECTERN).run(consumer);
		createSimplePattern(new Identifier("dimdoors:piston"), STICKY_PISTON, PISTON).run(consumer);
		createSimplePattern(new Identifier("dimdoors:netherwart_block"), ModBlockTags.DECAY_TO_NETHERWART_BLOCK, NETHER_WART_BLOCK).run(consumer);
		createSimplePattern(new Identifier("dimdoors:redstone_lamp"), BEACON, REDSTONE_LAMP).run(consumer);
		createSimplePattern(new Identifier("dimdoors:amalgam_ore"), ModBlockTags.DECAY_TO_AMALGAM_ORE, ModBlocks.AMALGAM_ORE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:clod_ore"), ModBlockTags.DECAY_TO_CLOD_ORE, ModBlocks.CLOD_ORE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:cobblestone"), ModBlockTags.DECAY_TO_COBBLESTONE, COBBLESTONE).run(consumer);

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

		createSimplePattern(id.apply(weathered), oxidized, weathered).run(consumer);
		createSimplePattern(id.apply(exposed), weathered, exposed).run(consumer);
		createSimplePattern(id.apply(regular), exposed, regular).run(consumer);

		createSimplePattern(id.apply(regularWaxed), regularWaxed, regular);
		createSimplePattern(id.apply(exposedWaxed), exposedWaxed, exposed);
		createSimplePattern(id.apply(weathered), weatheredWaxed, weathered);
		createSimplePattern(id.apply(oxidizedWaxed), oxidizedWaxed, oxidized);
	}

	private Block getBlock(Identifier id) {
		return Registries.BLOCK.get(id);
	}

	private Identifier getId(Block block) {
		return Registries.BLOCK.getId(block);
	}

	private DecayPatternData turnIntoSelf(Identifier identifier, Block before) {
        return new DecayPatternData(identifier, SimpleDecayPredicate.builder().block(before).create(), SelfDecayProcessor.instance());
    }

	private DecayPatternData turnIntoSelf(Identifier identifier, TagKey<Block> tag) {
		return new DecayPatternData(identifier, SimpleTagDecayPredicate.builder().tag(tag).create(), SelfDecayProcessor.instance());
	}


    @Override
    public String getName() {
        return "Limbo Decay";
    }

    private static Path getOutput(Path rootOutput, Identifier lootTableId) {
        return rootOutput.resolve("data/" + lootTableId.getNamespace() + "/decay_patterns/" + lootTableId.getPath() + ".json");
    }

    public DecayPatternData createSimplePattern(Identifier id, Block before, Block after) {
        return new DecayPatternData(id, SimpleDecayPredicate.builder().block(before).create(), SimpleDecayProcesor.builder().block(after).entropy(1).create());
    }

	public DecayPatternData createSimplePattern(Identifier id, TagKey<Block> before, Block after) {
		return new DecayPatternData(id, SimpleDecayPredicate.builder().tag(before).create(), SimpleDecayProcesor.builder().block(after).entropy(1).create());
	}

	public DecayPatternData createDoorPattern(Identifier id, Block before, Block after) {
		return new DecayPatternData(id, SimpleDecayPredicate.builder().block(before).create(), DoubleDecayProcessor.builder().block(after).entropy(1).create());
	}

	public DecayPatternData createDoorPattern(Identifier id, TagKey<Block> before, Block after) {
		return new DecayPatternData(id, SimpleDecayPredicate.builder().tag(before).create(), DoubleDecayProcessor.builder().block(after).entropy(1).create());
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
