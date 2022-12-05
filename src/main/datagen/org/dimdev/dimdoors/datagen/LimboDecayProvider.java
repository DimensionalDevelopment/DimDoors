package org.dimdev.dimdoors.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiConsumer;

import static net.minecraft.block.Blocks.*;

public class LimboDecayProvider implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    private final DataGenerator generator;

    public LimboDecayProvider(DataGenerator generator) {
        this.generator  = generator;
    }

    @Override
    public void run(DataWriter cache) throws IOException {
        Path path = this.generator.getOutput();

        BiConsumer<Identifier, JsonObject> consumer = (identifier, json)  -> {
            Path outputPath = getOutput(path, identifier);

            try {
                DataProvider.writeToPath(cache, json, outputPath);
            } catch (IOException var6) {
                LOGGER.error("Couldn't save decay pattern {}", outputPath, var6);
        }
        };

		generatePatterns(consumer);
    }

	protected void generatePatterns(BiConsumer<Identifier, JsonObject> consumer) {
		createSimplePattern(new Identifier("dimdoors:air"), ModBlockTags.DECAY_TO_AIR, AIR);
		createSimplePattern(new Identifier("dimdoors:gritty_stone"), ModBlockTags.DECAY_TO_GRITTY_STONE, ModBlocks.GRITTY_STONE);
		createSimplePattern(new Identifier("dimdoors:leak"), WATER, ModBlocks.LEAK);
		createSimplePattern(new Identifier("dimdoors:solid_static"), ModBlockTags.DECAY_TO_SOLID_STATIC, ModBlocks.SOLID_STATIC);
		createSimplePattern(new Identifier("dimdoors:unraveled_fabric"), CLAY, ModBlocks.UNRAVELLED_BLOCK);
		createSimplePattern(new Identifier("dimdoors:unraveled_fence"), ModBlockTags.DECAY_UNRAVELED_FENCE, ModBlocks.UNRAVELED_FENCE);
		createSimplePattern(new Identifier("dimdoors:unraveled_gate"), ModBlockTags.DECAY_UNRAVELED_GATE, ModBlocks.UNRAVELED_GATE);
		createSimplePattern(new Identifier("dimdoors:unraveled_button"), ModBlockTags.DECAY_UNRAVELED_BUTTON, ModBlocks.UNRAVELED_BUTTON);
		createSimplePattern(new Identifier("dimdoors:unraveled_slab"), ModBlockTags.DECAY_UNRAVELED_SLAB, ModBlocks.UNRAVELED_SLAB);
		createSimplePattern(new Identifier("dimdoors:unraveled_stairs"), ModBlockTags.DECAY_UNRAVELED_STAIRS, ModBlocks.UNRAVELED_STAIRS);

		createSimplePattern(new Identifier("dimdoors:cobweb"), BlockTags.WOOL, COBWEB);
		createSimplePattern(new Identifier("dimdoors:driftwood_leaves"), BlockTags.LEAVES, ModBlocks.DRIFTWOOD_LEAVES);
		createSimplePattern(new Identifier("dimdoors:driftwood_sapling"), BlockTags.SAPLINGS, ModBlocks.DRIFTWOOD_SAPLING);
		createSimplePattern(new Identifier("dimdoors:glass_pane"), ModBlockTags.DECAY_TO_GLASS_PANE, GLASS_PANE);
		createSimplePattern(new Identifier("dimdoors:moss_carpet"), BlockTags.WOOL_CARPETS, MOSS_CARPET);
		createSimplePattern(new Identifier("dimdoors:driftwood_trapdoor"), BlockTags.WOODEN_TRAPDOORS, ModBlocks.DRIFTWOOD_TRAPDOOR);
		createDoorPattern(new Identifier("dimdoors:driftwood_trapdoor_door"), ModBlocks.DRIFTWOOD_DOOR, ModBlocks.DRIFTWOOD_TRAPDOOR);
		createSimplePattern(new Identifier("dimdoors:rail"), ModBlockTags.DECAY_TO_RAIL, RAIL);
		createSimplePattern(new Identifier("dimdoors:rust"), ModBlockTags.DECAY_TO_RUST, ModBlocks.RUST);
		createSimplePattern(new Identifier("dimdoors:unraveled_spike"), ModBlockTags.DECAY_TO_UNRAVELED_SPIKE, ModBlocks.UNRAVELED_SPIKE);
		createSimplePattern(new Identifier("dimdoors:wither_rose"), ModBlockTags.DECAY_TO_WITHER_ROSE, WITHER_ROSE);
		createSimplePattern(new Identifier("dimdoors:water"), SNOW, WATER);
		createSimplePattern(new Identifier("dimdoors:clay"), ModBlockTags.DECAY_TO_CLAY, CLAY);
		createSimplePattern(new Identifier("dimdoors:clay_fence"), ModBlockTags.DECAY_CLAY_FENCE, ModBlocks.CLAY_FENCE);
		createSimplePattern(new Identifier("dimdoors:clay_gate"), ModBlockTags.DECAY_CLAY_GATE, ModBlocks.CLAY_GATE);
		createSimplePattern(new Identifier("dimdoors:clay_button"), ModBlockTags.DECAY_CLAY_BUTTON, ModBlocks.CLAY_BUTTON);
		createSimplePattern(new Identifier("dimdoors:clay_slab"), ModBlockTags.DECAY_CLAY_SLAB, ModBlocks.CLAY_SLAB);
		createSimplePattern(new Identifier("dimdoors:clay_stairs"), ModBlockTags.DECAY_CLAY_STAIRS, ModBlocks.CLAY_STAIRS);
		createSimplePattern(new Identifier("dimdoors:dark_sand"), ModBlockTags.DECAY_TO_DARK_SAND, ModBlocks.DARK_SAND);
	 	createSimplePattern(new Identifier("dimdoors:dark_sand_fence"), ModBlockTags.DECAY_DARK_SAND_FENCE, ModBlocks.DARK_SAND_FENCE);
	 	createSimplePattern(new Identifier("dimdoors:dark_sand_gate"), ModBlockTags.DECAY_DARK_SAND_GATE, ModBlocks.DARK_SAND_GATE);
	 	createSimplePattern(new Identifier("dimdoors:dark_sand_button"), ModBlockTags.DECAY_DARK_SAND_BUTTON, ModBlocks.DARK_SAND_BUTTON);
	 	createSimplePattern(new Identifier("dimdoors:dark_sand_slab"), ModBlockTags.DECAY_DARK_SAND_SLAB, ModBlocks.DARK_SAND_SLAB);
	 	createSimplePattern(new Identifier("dimdoors:dark_sand_stairs"), ModBlockTags.DECAY_DARK_SAND_STAIRS, ModBlocks.DARK_SAND_STAIRS);
		createSimplePattern(new Identifier("dimdoors:amalgam"), ModBlockTags.DECAY_TO_AMALGAM, ModBlocks.AMALGAM_BLOCK);

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
