package org.dimdev.dimdoors.datagen;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

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
import org.dimdev.dimdoors.world.decay.predicates.SimpleBlockDecayPredicate;
import org.dimdev.dimdoors.world.decay.predicates.SimpleTagDecayPredicate;
import org.dimdev.dimdoors.world.decay.processors.DoorDecayProccessor;
import org.dimdev.dimdoors.world.decay.processors.SelfDecayProcessor;
import org.dimdev.dimdoors.world.decay.processors.SimpleDecayProcesor;

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
		createSimplePattern(DimensionalDoors.id("gritty_stone"), ModBlockTags.DECAY_TO_GRITTY_STONE, ModBlocks.GRITTY_STONE);
		createSimplePattern(DimensionalDoors.id("leak"), Blocks.WATER, ModBlocks.LEAK);
		createSimplePattern(DimensionalDoors.id("solid_static"), ModBlockTags.DECAY_TO_SOLID_STATIC, ModBlocks.SOLID_STATIC);
		createSimplePattern(DimensionalDoors.id("unraveled_fabric"), Blocks.CLAY, ModBlocks.UNRAVELLED_BLOCK);
		createSimplePattern(DimensionalDoors.id("unraveled_fence"), ModBlockTags.DECAY_UNRAVELED_FENCE, ModBlocks.UNRAVELED_FENCE);
		createSimplePattern(DimensionalDoors.id("unraveled_gate"), ModBlockTags.DECAY_UNRAVELED_GATE, ModBlocks.UNRAVELED_GATE);
		createSimplePattern(DimensionalDoors.id("unraveled_button"), ModBlockTags.DECAY_UNRAVELED_BUTTON, ModBlocks.UNRAVELED_BUTTON);
		createSimplePattern(DimensionalDoors.id("unraveled_slab"), ModBlockTags.DECAY_UNRAVELED_SLAB, ModBlocks.UNRAVELED_SLAB);
		createSimplePattern(DimensionalDoors.id("unraveled_stairs"), ModBlockTags.DECAY_UNRAVELED_STAIRS, ModBlocks.UNRAVELED_STAIRS);

		createSimplePattern(DimensionalDoors.id("cobweb"), BlockTags.WOOL, Blocks.COBWEB);
		createSimplePattern(DimensionalDoors.id("driftwood_leaves"), BlockTags.LEAVES, ModBlocks.DRIFTWOOD_LEAVES);
		createSimplePattern(DimensionalDoors.id("driftwood_sapling"), BlockTags.SAPLINGS, ModBlocks.DRIFTWOOD_SAPLING);
		createSimplePattern(DimensionalDoors.id("glass_pane"), ModBlockTags.DECAY_TO_GLASS_PANE, Blocks.GLASS_PANE);
		createSimplePattern(DimensionalDoors.id("moss_carpet"), BlockTags.WOOL_CARPETS, Blocks.MOSS_CARPET);
		createSimplePattern(DimensionalDoors.id("driftwood_trapdoor"), BlockTags.WOODEN_TRAPDOORS, ModBlocks.DRIFTWOOD_TRAPDOOR);
		createDoorPattern(DimensionalDoors.id("driftwood_trapdoor_door"), ModBlocks.DRIFTWOOD_DOOR, ModBlocks.DRIFTWOOD_TRAPDOOR);
		createSimplePattern(DimensionalDoors.id("rail"), ModBlockTags.DECAY_TO_RAIL, Blocks.RAIL);
		createSimplePattern(DimensionalDoors.id("rust"), ModBlockTags.DECAY_TO_RUST, ModBlocks.RUST);
		createSimplePattern(DimensionalDoors.id("unraveled_spike"), ModBlockTags.DECAY_TO_UNRAVELED_SPIKE, ModBlocks.UNRAVELED_SPIKE);
		createSimplePattern(DimensionalDoors.id("wither_rose"), ModBlockTags.DECAY_TO_WITHER_ROSE, Blocks.WITHER_ROSE);
		createSimplePattern(DimensionalDoors.id("water"), Blocks.SNOW, Blocks.WATER);
		createSimplePattern(DimensionalDoors.id("clay"), ModBlockTags.DECAY_TO_CLAY, Blocks.CLAY);
		createSimplePattern(DimensionalDoors.id("clay_fence"), ModBlockTags.DECAY_CLAY_FENCE, ModBlocks.CLAY_FENCE);
		createSimplePattern(DimensionalDoors.id("clay_gate"), ModBlockTags.DECAY_CLAY_GATE, ModBlocks.CLAY_GATE);
		createSimplePattern(DimensionalDoors.id("clay_button"), ModBlockTags.DECAY_CLAY_BUTTON, ModBlocks.CLAY_BUTTON);
		createSimplePattern(DimensionalDoors.id("clay_slab"), ModBlockTags.DECAY_CLAY_SLAB, ModBlocks.CLAY_SLAB);
		createSimplePattern(DimensionalDoors.id("clay_stairs"), ModBlockTags.DECAY_CLAY_STAIRS, ModBlocks.CLAY_STAIRS);
		createSimplePattern(DimensionalDoors.id("dark_sand"), ModBlockTags.DECAY_TO_DARK_SAND, ModBlocks.DARK_SAND);
	 	createSimplePattern(DimensionalDoors.id("dark_sand_fence"), ModBlockTags.DECAY_DARK_SAND_FENCE, ModBlocks.DARK_SAND_FENCE);
	 	createSimplePattern(DimensionalDoors.id("dark_sand_gate"), ModBlockTags.DECAY_DARK_SAND_GATE, ModBlocks.DARK_SAND_GATE);
	 	createSimplePattern(DimensionalDoors.id("dark_sand_button"), ModBlockTags.DECAY_DARK_SAND_BUTTON, ModBlocks.DARK_SAND_BUTTON);
	 	createSimplePattern(DimensionalDoors.id("dark_sand_slab"), ModBlockTags.DECAY_DARK_SAND_SLAB, ModBlocks.DARK_SAND_SLAB);
	 	createSimplePattern(DimensionalDoors.id("dark_sand_stairs"), ModBlockTags.DECAY_DARK_SAND_STAIRS, ModBlocks.DARK_SAND_STAIRS);
		createSimplePattern(DimensionalDoors.id("amalgam"), ModBlockTags.DECAY_TO_AMALGAM, ModBlocks.AMALGAM_BLOCK);

	}

	private DecayPatternData turnIntoSelf(Identifier identifier, Block before) {
        return new DecayPatternData(identifier, SimpleBlockDecayPredicate.builder().block(before).create(), SelfDecayProcessor.instance());
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
        return new DecayPatternData(id, SimpleBlockDecayPredicate.builder().block(before).create(), SimpleDecayProcesor.builder().block(after).entropy(1).create());
    }

	public DecayPatternData createSimplePattern(Identifier id, TagKey<Block> before, Block after) {
		return new DecayPatternData(id, SimpleTagDecayPredicate.builder().tag(before).create(), SimpleDecayProcesor.builder().block(after).entropy(1).create());
	}

	public DecayPatternData createDoorPattern(Identifier id, Block before, Block after) {
		return new DecayPatternData(id, SimpleBlockDecayPredicate.builder().block(before).create(), DoorDecayProccessor.builder().block(after).entropy(1).create());
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
