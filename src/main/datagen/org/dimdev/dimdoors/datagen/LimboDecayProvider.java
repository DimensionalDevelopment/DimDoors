package org.dimdev.dimdoors.datagen;

import static net.minecraft.block.Blocks.ACACIA_LOG;
import static net.minecraft.block.Blocks.ACACIA_PLANKS;
import static net.minecraft.block.Blocks.ACACIA_WOOD;
import static net.minecraft.block.Blocks.ANDESITE;
import static net.minecraft.block.Blocks.BIRCH_LOG;
import static net.minecraft.block.Blocks.BIRCH_PLANKS;
import static net.minecraft.block.Blocks.BIRCH_WOOD;
import static net.minecraft.block.Blocks.BLACKSTONE;
import static net.minecraft.block.Blocks.COAL_BLOCK;
import static net.minecraft.block.Blocks.COAL_ORE;
import static net.minecraft.block.Blocks.COBBLESTONE;
import static net.minecraft.block.Blocks.CRACKED_STONE_BRICKS;
import static net.minecraft.block.Blocks.DARK_OAK_LOG;
import static net.minecraft.block.Blocks.DARK_OAK_PLANKS;
import static net.minecraft.block.Blocks.DARK_OAK_WOOD;
import static net.minecraft.block.Blocks.DIORITE;
import static net.minecraft.block.Blocks.DIRT;
import static net.minecraft.block.Blocks.DIRT_PATH;
import static net.minecraft.block.Blocks.EMERALD_BLOCK;
import static net.minecraft.block.Blocks.EMERALD_ORE;
import static net.minecraft.block.Blocks.END_STONE;
import static net.minecraft.block.Blocks.END_STONE_BRICKS;
import static net.minecraft.block.Blocks.FARMLAND;
import static net.minecraft.block.Blocks.GLASS;
import static net.minecraft.block.Blocks.GOLD_BLOCK;
import static net.minecraft.block.Blocks.GOLD_ORE;
import static net.minecraft.block.Blocks.GRANITE;
import static net.minecraft.block.Blocks.GRASS_BLOCK;
import static net.minecraft.block.Blocks.GRAVEL;
import static net.minecraft.block.Blocks.IRON_BLOCK;
import static net.minecraft.block.Blocks.IRON_ORE;
import static net.minecraft.block.Blocks.JUNGLE_LOG;
import static net.minecraft.block.Blocks.JUNGLE_PLANKS;
import static net.minecraft.block.Blocks.JUNGLE_WOOD;
import static net.minecraft.block.Blocks.LAPIS_BLOCK;
import static net.minecraft.block.Blocks.LAPIS_ORE;
import static net.minecraft.block.Blocks.OAK_LOG;
import static net.minecraft.block.Blocks.OAK_PLANKS;
import static net.minecraft.block.Blocks.OAK_WOOD;
import static net.minecraft.block.Blocks.PODZOL;
import static net.minecraft.block.Blocks.POLISHED_ANDESITE;
import static net.minecraft.block.Blocks.POLISHED_BLACKSTONE;
import static net.minecraft.block.Blocks.POLISHED_DIORITE;
import static net.minecraft.block.Blocks.POLISHED_GRANITE;
import static net.minecraft.block.Blocks.REDSTONE_BLOCK;
import static net.minecraft.block.Blocks.REDSTONE_ORE;
import static net.minecraft.block.Blocks.SAND;
import static net.minecraft.block.Blocks.SANDSTONE;
import static net.minecraft.block.Blocks.SPRUCE_LOG;
import static net.minecraft.block.Blocks.SPRUCE_PLANKS;
import static net.minecraft.block.Blocks.SPRUCE_WOOD;
import static net.minecraft.block.Blocks.STONE;
import static net.minecraft.block.Blocks.STONE_BRICKS;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.world.decay.DecayPredicate;
import org.dimdev.dimdoors.world.decay.DecayProcessor;
import org.dimdev.dimdoors.world.decay.predicates.SimpleDecayPredicate;
import org.dimdev.dimdoors.world.decay.processors.SelfDecayProcessor;
import org.dimdev.dimdoors.world.decay.processors.SimpleDecayProcesor;

import net.minecraft.block.Block;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiConsumer;

public class LimboDecayProvider implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    private final DataGenerator generator;

    public LimboDecayProvider(DataGenerator generator) {
        this.generator  = generator;
    }

    @Override
    public void run(DataCache cache) throws IOException {
        Path path = this.generator.getOutput();

        BiConsumer<Identifier, JsonObject> consumer = (identifier, json)  -> {
            Path outputPath = getOutput(path, identifier);

            try {
                DataProvider.writeToPath(GSON, cache, json, outputPath);
            } catch (IOException var6) {
                LOGGER.error("Couldn't save decay pattern {}", outputPath, var6);
        }
        };

		generatePatterns(consumer);
    }

	protected void generatePatterns(BiConsumer<Identifier, JsonObject> consumer) {
		createSimplePattern(new Identifier("dimdoors:stone"), STONE, COBBLESTONE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:cobblestone"), COBBLESTONE, GRAVEL).run(consumer);
		createSimplePattern(new Identifier("dimdoors:gravel"), GRAVEL, SAND).run(consumer);
		turnIntoSelf(new Identifier("dimdoors:sand"), SAND).run(consumer);
		createSimplePattern(new Identifier("dimdoors:glass"), GLASS, SAND).run(consumer);
		createSimplePattern(new Identifier("dimdoors:grass_block"), GRASS_BLOCK, DIRT).run(consumer);
		createSimplePattern(new Identifier("dimdoors:dirt"), DIRT, SAND).run(consumer);
		createSimplePattern(new Identifier("dimdoors:redstone_block"), REDSTONE_BLOCK, REDSTONE_ORE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:redstone_ore"), REDSTONE_ORE, STONE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:emerald_block"), EMERALD_BLOCK, EMERALD_ORE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:emerald_ore"), EMERALD_ORE, STONE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:coal_block"), COAL_BLOCK, COAL_ORE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:coal_ore"), COAL_ORE, STONE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:iron_block"), IRON_BLOCK, IRON_ORE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:iron_ore"), IRON_ORE, STONE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:lapis_block"), LAPIS_BLOCK, LAPIS_ORE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:lapis_ore"), LAPIS_ORE, STONE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:gold_block"), GOLD_BLOCK, GOLD_ORE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:gold_ore"), GOLD_ORE, STONE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:sandstone"), SANDSTONE, SAND).run(consumer);
		createSimplePattern(new Identifier("dimdoors:end_stone_bricks"), END_STONE_BRICKS, END_STONE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:dirt_path"), DIRT_PATH, DIRT).run(consumer);
		createSimplePattern(new Identifier("dimdoors:polished_granite"), POLISHED_GRANITE, GRANITE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:polished_andesite"), POLISHED_ANDESITE, ANDESITE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:andesite"), ANDESITE, DIORITE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:polished_diorite"), POLISHED_DIORITE, DIORITE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:granite"), GRANITE, DIORITE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:diorite"), DIORITE, COBBLESTONE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:polished_blackstone"), POLISHED_BLACKSTONE, BLACKSTONE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:blackstone"), BLACKSTONE, COBBLESTONE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:podzol"), PODZOL, DIRT).run(consumer);
		createSimplePattern(new Identifier("dimdoors:farmland"), FARMLAND, DIRT).run(consumer);
		createSimplePattern(new Identifier("dimdoors:stone_bricks"), STONE_BRICKS, CRACKED_STONE_BRICKS).run(consumer);
		createSimplePattern(new Identifier("dimdoors:cracked_stone_bricks"), CRACKED_STONE_BRICKS, DIORITE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:end_stone"), END_STONE, SANDSTONE).run(consumer);
		createSimplePattern(new Identifier("dimdoors:oak_log"), OAK_LOG, OAK_PLANKS).run(consumer);
		createSimplePattern(new Identifier("dimdoors:birch_log"), BIRCH_LOG, BIRCH_PLANKS).run(consumer);
		createSimplePattern(new Identifier("dimdoors:spruce_log"), SPRUCE_LOG, SPRUCE_PLANKS).run(consumer);
		createSimplePattern(new Identifier("dimdoors:jungle_log"), JUNGLE_LOG, JUNGLE_PLANKS).run(consumer);
		createSimplePattern(new Identifier("dimdoors:acacia_log"), ACACIA_LOG, ACACIA_PLANKS).run(consumer);
		createSimplePattern(new Identifier("dimdoors:dark_oak_log"), DARK_OAK_LOG, DARK_OAK_PLANKS).run(consumer);
		createSimplePattern(new Identifier("dimdoors:oak_wood"), OAK_WOOD, OAK_LOG).run(consumer);
		createSimplePattern(new Identifier("dimdoors:birch_wood"), BIRCH_WOOD, BIRCH_LOG).run(consumer);
		createSimplePattern(new Identifier("dimdoors:spruce_wood"), SPRUCE_WOOD, SPRUCE_LOG).run(consumer);
		createSimplePattern(new Identifier("dimdoors:jungle_wood"), JUNGLE_WOOD, JUNGLE_LOG).run(consumer);
		createSimplePattern(new Identifier("dimdoors:acacia_wood"), ACACIA_WOOD, ACACIA_LOG).run(consumer);
		createSimplePattern(new Identifier("dimdoors:dark_oak_wood"), DARK_OAK_WOOD, DARK_OAK_LOG).run(consumer);
	}

	private DecayPatternData turnIntoSelf(Identifier identifier, Block before) {
        return new DecayPatternData(identifier, SimpleDecayPredicate.builder().block(before).create(), SelfDecayProcessor.instance());
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
