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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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

import static net.minecraft.world.level.block.Blocks.ANCIENT_DEBRIS;
import static net.minecraft.world.level.block.Blocks.ANVIL;
import static net.minecraft.world.level.block.Blocks.BAMBOO;
import static net.minecraft.world.level.block.Blocks.BARREL;
import static net.minecraft.world.level.block.Blocks.BEACON;
import static net.minecraft.world.level.block.Blocks.BONE_BLOCK;
import static net.minecraft.world.level.block.Blocks.BOOKSHELF;
import static net.minecraft.world.level.block.Blocks.CARVED_PUMPKIN;
import static net.minecraft.world.level.block.Blocks.CHEST;
import static net.minecraft.world.level.block.Blocks.CLAY;
import static net.minecraft.world.level.block.Blocks.COBBLESTONE;
import static net.minecraft.world.level.block.Blocks.COBWEB;
import static net.minecraft.world.level.block.Blocks.COMPOSTER;
import static net.minecraft.world.level.block.Blocks.CONDUIT;
import static net.minecraft.world.level.block.Blocks.CRIMSON_NYLIUM;
import static net.minecraft.world.level.block.Blocks.DIRT;
import static net.minecraft.world.level.block.Blocks.GLASS_PANE;
import static net.minecraft.world.level.block.Blocks.HONEYCOMB_BLOCK;
import static net.minecraft.world.level.block.Blocks.HONEY_BLOCK;
import static net.minecraft.world.level.block.Blocks.ICE;
import static net.minecraft.world.level.block.Blocks.IRON_BLOCK;
import static net.minecraft.world.level.block.Blocks.LECTERN;
import static net.minecraft.world.level.block.Blocks.MOSS_CARPET;
import static net.minecraft.world.level.block.Blocks.NETHERITE_BLOCK;
import static net.minecraft.world.level.block.Blocks.NETHER_WART_BLOCK;
import static net.minecraft.world.level.block.Blocks.PACKED_ICE;
import static net.minecraft.world.level.block.Blocks.PISTON;
import static net.minecraft.world.level.block.Blocks.PUMPKIN;
import static net.minecraft.world.level.block.Blocks.RAIL;
import static net.minecraft.world.level.block.Blocks.REDSTONE_LAMP;
import static net.minecraft.world.level.block.Blocks.SCAFFOLDING;
import static net.minecraft.world.level.block.Blocks.SKELETON_SKULL;
import static net.minecraft.world.level.block.Blocks.SKELETON_WALL_SKULL;
import static net.minecraft.world.level.block.Blocks.SLIME_BLOCK;
import static net.minecraft.world.level.block.Blocks.SNOW;
import static net.minecraft.world.level.block.Blocks.SPONGE;
import static net.minecraft.world.level.block.Blocks.STICKY_PISTON;
import static net.minecraft.world.level.block.Blocks.WARPED_NYLIUM;
import static net.minecraft.world.level.block.Blocks.WATER;
import static net.minecraft.world.level.block.Blocks.WITHER_ROSE;

public class LimboDecayProvider implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

	private final PackOutput.PathProvider decayPatternPathResolver;

	public LimboDecayProvider(FabricDataOutput output) {
		this.decayPatternPathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, "decay_patterns");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
		Set<ResourceLocation> generatedDecayPatterns = Sets.newHashSet();
		List<CompletableFuture<?>> list = new ArrayList<>();


        BiConsumer<ResourceLocation, JsonObject> consumer = (identifier, json)  -> {
            Path outputPath = decayPatternPathResolver.json(identifier);
			list.add(DataProvider.saveStable(cache, json, outputPath));
		};

		generatePatterns(consumer);

		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

	protected void generatePatterns(BiConsumer<ResourceLocation, JsonObject> consumer) {
		createSimplePattern(DimensionalDoors.resource("air"), ModBlockTags.DECAY_TO_AIR, Blocks.AIR).run(consumer);
		createSimplePattern(DimensionalDoors.resource("gritty_stone"), ModBlockTags.DECAY_TO_GRITTY_STONE, ModBlocks.GRITTY_STONE).run(consumer);
		createSimplePattern(DimensionalDoors.resource("leak"), Blocks.WATER, ModBlocks.LEAK).run(consumer);
		createSimplePattern(DimensionalDoors.resource("solid_static"), ModBlockTags.DECAY_TO_SOLID_STATIC, ModBlocks.SOLID_STATIC).run(consumer);
		createSimplePattern(DimensionalDoors.resource("unraveled_fabric"), Blocks.CLAY, ModBlocks.UNRAVELLED_BLOCK).run(consumer);
		createSimplePattern(DimensionalDoors.resource("unraveled_fence"), ModBlockTags.DECAY_UNRAVELED_FENCE, ModBlocks.UNRAVELED_FENCE).run(consumer);
		createSimplePattern(DimensionalDoors.resource("unraveled_gate"), ModBlockTags.DECAY_UNRAVELED_GATE, ModBlocks.UNRAVELED_GATE).run(consumer);
		createSimplePattern(DimensionalDoors.resource("unraveled_button"), ModBlockTags.DECAY_UNRAVELED_BUTTON, ModBlocks.UNRAVELED_BUTTON).run(consumer);
		createSimplePattern(DimensionalDoors.resource("unraveled_slab"), ModBlockTags.DECAY_UNRAVELED_SLAB, ModBlocks.UNRAVELED_SLAB).run(consumer);
		createSimplePattern(DimensionalDoors.resource("unraveled_stairs"), ModBlockTags.DECAY_UNRAVELED_STAIRS, ModBlocks.UNRAVELED_STAIRS).run(consumer);

		createSimplePattern(new ResourceLocation("dimdoors:cobweb"), BlockTags.WOOL, COBWEB).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:driftwood_leaves"), BlockTags.LEAVES, ModBlocks.DRIFTWOOD_LEAVES).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:driftwood_sapling"), BlockTags.SAPLINGS, ModBlocks.DRIFTWOOD_SAPLING).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:glass_pane"), ModBlockTags.DECAY_TO_GLASS_PANE, GLASS_PANE).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:moss_carpet"), BlockTags.WOOL_CARPETS, MOSS_CARPET).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:driftwood_trapdoor"), BlockTags.WOODEN_TRAPDOORS, ModBlocks.DRIFTWOOD_TRAPDOOR).run(consumer);
		createDoorPattern(new ResourceLocation("dimdoors:driftwood_trapdoor_door"), ModBlocks.DRIFTWOOD_DOOR, ModBlocks.DRIFTWOOD_TRAPDOOR).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:rail"), ModBlockTags.DECAY_TO_RAIL, RAIL).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:rust"), ModBlockTags.DECAY_TO_RUST, ModBlocks.RUST).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:unraveled_spike"), ModBlockTags.DECAY_TO_UNRAVELED_SPIKE, ModBlocks.UNRAVELED_SPIKE).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:wither_rose"), ModBlockTags.DECAY_TO_WITHER_ROSE, WITHER_ROSE).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:water"), SNOW, WATER).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:clay"), ModBlockTags.DECAY_TO_CLAY, CLAY).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:clay_fence"), ModBlockTags.DECAY_CLAY_FENCE, ModBlocks.CLAY_FENCE).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:clay_gate"), ModBlockTags.DECAY_CLAY_GATE, ModBlocks.CLAY_GATE).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:clay_button"), ModBlockTags.DECAY_CLAY_BUTTON, ModBlocks.CLAY_BUTTON).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:clay_slab"), ModBlockTags.DECAY_CLAY_SLAB, ModBlocks.CLAY_SLAB).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:clay_stairs"), ModBlockTags.DECAY_CLAY_STAIRS, ModBlocks.CLAY_STAIRS).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:dark_sand"), ModBlockTags.DECAY_TO_DARK_SAND, ModBlocks.DARK_SAND).run(consumer);
	 	createSimplePattern(new ResourceLocation("dimdoors:dark_sand_fence"), ModBlockTags.DECAY_DARK_SAND_FENCE, ModBlocks.DARK_SAND_FENCE).run(consumer);
	 	createSimplePattern(new ResourceLocation("dimdoors:dark_sand_gate"), ModBlockTags.DECAY_DARK_SAND_GATE, ModBlocks.DARK_SAND_GATE).run(consumer);
	 	createSimplePattern(new ResourceLocation("dimdoors:dark_sand_button"), ModBlockTags.DECAY_DARK_SAND_BUTTON, ModBlocks.DARK_SAND_BUTTON).run(consumer);
	 	createSimplePattern(new ResourceLocation("dimdoors:dark_sand_slab"), ModBlockTags.DECAY_DARK_SAND_SLAB, ModBlocks.DARK_SAND_SLAB).run(consumer);
	 	createSimplePattern(new ResourceLocation("dimdoors:dark_sand_stairs"), ModBlockTags.DECAY_DARK_SAND_STAIRS, ModBlocks.DARK_SAND_STAIRS).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:amalgam"), ModBlockTags.DECAY_TO_AMALGAM, ModBlocks.AMALGAM_BLOCK).run(consumer);

		createSimplePattern(new ResourceLocation("dimdoors:ice"), PACKED_ICE, ICE).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:iron_block"), ANVIL, IRON_BLOCK).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:ancient_debris"), NETHERITE_BLOCK, ANCIENT_DEBRIS).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:dirt"), ModBlockTags.DECAY_TO_DIRT, DIRT).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:crimson_nylium"), WARPED_NYLIUM, CRIMSON_NYLIUM).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:driftwood_plank"), ModBlockTags.DECAY_TO_DRIFTWOOD_PLANK, ModBlocks.DRIFTWOOD_PLANKS).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:driftwood_fence"), ModBlockTags.DECAY_TO_DRIFTWOOD_FENCE, ModBlocks.DRIFTWOOD_FENCE).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:driftwood_gate"), ModBlockTags.DECAY_TO_DRIFTWOOD_GATE, ModBlocks.DRIFTWOOD_GATE).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:driftwood_button"), ModBlockTags.DECAY_TO_DRIFTWOOD_BUTTON, ModBlocks.DRIFTWOOD_BUTTON).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:driftwood_slab"), ModBlockTags.DECAY_TO_DRIFTWOOD_SLAB, ModBlocks.DRIFTWOOD_SLAB).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:driftwood_stairs"), ModBlockTags.DECAY_TO_DRIFTWOOD_STAIRS, ModBlocks.DRIFTWOOD_STAIRS).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:composter"), BARREL, COMPOSTER).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:chest"), ModBlockTags.DECAY_TO_CHEST, CHEST).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:bone_block"), CONDUIT, BONE_BLOCK).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:skeleton_skull"), ModBlockTags.DECAY_TO_SKELETON_SKULL, SKELETON_SKULL).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:skeleton_wall_skull"), ModBlockTags.DECAY_TO_SKELETON_WALL_SKULL, SKELETON_WALL_SKULL).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:bamboo"), SCAFFOLDING, BAMBOO).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:pumpkin"), CARVED_PUMPKIN, PUMPKIN).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:slime_block"), HONEY_BLOCK, SLIME_BLOCK).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:honeycomb_block"), SPONGE, HONEYCOMB_BLOCK).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:lectern"), BOOKSHELF, LECTERN).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:piston"), STICKY_PISTON, PISTON).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:netherwart_block"), ModBlockTags.DECAY_TO_NETHERWART_BLOCK, NETHER_WART_BLOCK).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:redstone_lamp"), BEACON, REDSTONE_LAMP).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:amalgam_ore"), ModBlockTags.DECAY_TO_AMALGAM_ORE, ModBlocks.AMALGAM_ORE).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:clod_ore"), ModBlockTags.DECAY_TO_CLOD_ORE, ModBlocks.CLOD_ORE).run(consumer);
		createSimplePattern(new ResourceLocation("dimdoors:cobblestone"), ModBlockTags.DECAY_TO_COBBLESTONE, COBBLESTONE).run(consumer);

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

		createSimplePattern(id.apply(weathered), oxidized, weathered).run(consumer);
		createSimplePattern(id.apply(exposed), weathered, exposed).run(consumer);
		createSimplePattern(id.apply(regular), exposed, regular).run(consumer);

		createSimplePattern(id.apply(regularWaxed), regularWaxed, regular);
		createSimplePattern(id.apply(exposedWaxed), exposedWaxed, exposed);
		createSimplePattern(id.apply(weathered), weatheredWaxed, weathered);
		createSimplePattern(id.apply(oxidizedWaxed), oxidizedWaxed, oxidized);
	}

	private Block getBlock(ResourceLocation id) {
		return BuiltInRegistries.BLOCK.get(id);
	}

	private ResourceLocation getId(Block block) {
		return BuiltInRegistries.BLOCK.getKey(block);
	}

	private DecayPatternData turnIntoSelf(ResourceLocation identifier, Block before) {
        return new DecayPatternData(identifier, SimpleDecayPredicate.builder().block(before).create(), SelfDecayProcessor.instance());
    }

	private DecayPatternData turnIntoSelf(ResourceLocation identifier, TagKey<Block> tag) {
		return new DecayPatternData(identifier, SimpleTagDecayPredicate.builder().tag(tag).create(), SelfDecayProcessor.instance());
	}


    @Override
    public String getName() {
        return "Limbo Decay";
    }

    private static Path getOutput(Path rootOutput, ResourceLocation lootTableId) {
        return rootOutput.resolve("data/" + lootTableId.getNamespace() + "/decay_patterns/" + lootTableId.getPath() + ".json");
    }

    public DecayPatternData createSimplePattern(ResourceLocation id, Block before, Block after) {
        return new DecayPatternData(id, SimpleDecayPredicate.builder().block(before).create(), SimpleDecayProcesor.builder().block(after).entropy(1).create());
    }

	public DecayPatternData createSimplePattern(ResourceLocation id, TagKey<Block> before, Block after) {
		return new DecayPatternData(id, SimpleDecayPredicate.builder().tag(before).create(), SimpleDecayProcesor.builder().block(after).entropy(1).create());
	}

	public DecayPatternData createDoorPattern(ResourceLocation id, Block before, Block after) {
		return new DecayPatternData(id, SimpleDecayPredicate.builder().block(before).create(), DoubleDecayProcessor.builder().block(after).entropy(1).create());
	}

	public DecayPatternData createDoorPattern(ResourceLocation id, TagKey<Block> before, Block after) {
		return new DecayPatternData(id, SimpleDecayPredicate.builder().tag(before).create(), DoubleDecayProcessor.builder().block(after).entropy(1).create());
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
