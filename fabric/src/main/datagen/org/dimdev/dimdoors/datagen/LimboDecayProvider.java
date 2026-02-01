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
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.tag.ModBlockTags;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.decay.DecayCondition;
import org.dimdev.dimdoors.world.decay.DecayPattern;
import org.dimdev.dimdoors.world.decay.DecayPatternHolder;
import org.dimdev.dimdoors.world.decay.DecayResult;
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
import java.util.function.Function;
import java.util.stream.Stream;

public class LimboDecayProvider implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

	private final PackOutput.PathProvider decayPatternPathResolver;
    private ArrayList<DecayPatternHolder> patternList;

    public LimboDecayProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> providerCompletableFuture) {
		this.decayPatternPathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, "decay_patterns");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
		Set<ResourceLocation> generatedDecayPatterns = Sets.newHashSet();
		List<CompletableFuture<?>> list = new ArrayList<>();

        

        Consumer<DecayPatternHolder> consumer = (patternHolder)  -> {
            JsonElement object = JsonOps.INSTANCE.withEncoder(DecayPattern.CODEC).apply(patternHolder.value()).getOrThrow();
            
            Path outputPath = decayPatternPathResolver.json(patternHolder.id());
			list.add(DataProvider.saveStable(cache, object, outputPath));
		};
        
        patternList = new ArrayList<DecayPatternHolder>();

		generatePatterns();
        
        patternList.forEach(consumer);

		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

	protected void generatePatterns() {
        addPattern(Blocks.AIR, ModBlockTags.DECAYS_TO_AIR);

        addPattern(Blocks.AIR, ModBlockTags.DECAYS_TO_AIR);
        addPattern(ModBlocks.GRITTY_STONE, ModBlockTags.DECAYS_TO_GRITTY_STONE);
        addPattern(ModBlocks.LEAK, Fluids.WATER);
		addPattern(new DecayPatternHolder(DimensionalDoors.id("solid_static"), new DecayPattern(List.of(DimensionDecayCondition.of(ModDimensions.LIMBO_TYPE_KEY), SimpleDecayCondition.of(ModBlockTags.DECAYS_TO_SOLID_STATIC)), new BlockDecayImplResult(1, DEFAULT, ModBlocks.SOLID_STATIC.get()))));
		addPattern(new DecayPatternHolder(DimensionalDoors.id("black_ancient_fabric"), new DecayPattern(List.of(DimensionDecayCondition.of(ModDimensions.LIMBO_TYPE_KEY, true), SimpleDecayCondition.of(Blocks.BEDROCK.builtInRegistryHolder().key())), new BlockDecayImplResult(1, DEFAULT, ModBlocks.BLACK_ANCIENT_FABRIC.get()))));
		addPattern(ModBlocks.UNRAVELLED_FABRIC, ModBlockTags.DECAYS_TO_UNRAVELED_FABRIC);
		addPattern(ModBlocks.UNRAVELED_FENCE, ModBlockTags.DECAYS_TO_UNRAVELED_FENCE);
		addPattern(ModBlocks.UNRAVELED_GATE, ModBlockTags.DECAYS_TO_UNRAVELED_GATE);
		addPattern(ModBlocks.UNRAVELED_BUTTON, ModBlockTags.DECAYS_TO_UNRAVELED_BUTTON);
		addPattern(ModBlocks.UNRAVELED_SLAB, ModBlockTags.DECAYS_TO_UNRAVELED_SLAB);
		addPattern(ModBlocks.UNRAVELED_STAIRS, ModBlockTags.DECAYS_TO_UNRAVELED_STAIRS);

		addPattern(Blocks.COBWEB, BlockTags.WOOL);
		addPattern(ModBlocks.DRIFTWOOD_LEAVES, BlockTags.LEAVES);
		addPattern(ModBlocks.DRIFTWOOD_SAPLING, BlockTags.SAPLINGS);
		addPattern(Blocks.GLASS_PANE, ModBlockTags.DECAYS_TO_TO_GLASS_PANE);
		addPattern(Blocks.MOSS_CARPET, ModBlockTags.DECAYS_TO_MOSS_CARPET);
		addPattern(ModBlocks.DRIFTWOOD_TRAPDOOR, BlockTags.WOODEN_TRAPDOORS);
		addDoublePattern(DimensionalDoors.id("driftwood_trapdoor_door"), ModBlocks.DRIFTWOOD_TRAPDOOR, ModBlocks.DRIFTWOOD_DOOR);
		addPattern(Blocks.RAIL, ModBlockTags.DECAYS_TO_RAIL);
		addPattern(ModBlocks.RUST, ModBlockTags.DECAYS_TO_RUST);
		addPattern(ModBlocks.UNRAVELED_SPIKE, ModBlockTags.DECAYS_TO_UNRAVELED_SPIKE);
		addPattern(Blocks.WITHER_ROSE, ModBlockTags.DECAYS_TO_WITHER_ROSE);
		addPattern(Fluids.WATER, Blocks.SNOW);
		addPattern(Blocks.CLAY, ModBlockTags.DECAYS_TO_CLAY);
		addPattern(ModBlocks.CLAY_FENCE, ModBlockTags.DECAYS_TO_CLAY_FENCE);
		addPattern(ModBlocks.CLAY_GATE, ModBlockTags.DECAYS_TO_CLAY_GATE);
		addPattern(ModBlocks.CLAY_WALL, ModBlockTags.DECAYS_TO_CLAY_WALL);
		addPattern(ModBlocks.CLAY_BUTTON, ModBlockTags.DECAYS_TO_CLAY_BUTTON);
		addPattern(ModBlocks.CLAY_SLAB, ModBlockTags.DECAYS_TO_CLAY_SLAB);
		addPattern(ModBlocks.CLAY_STAIRS, ModBlockTags.DECAYS_TO_CLAY_STAIRS);
		addPattern(ModBlocks.DARK_SAND, ModBlockTags.DECAYS_TO_DARK_SAND);
	 	addPattern(ModBlocks.DARK_SAND_FENCE, ModBlocks.GRAVEL_FENCE);
	 	addPattern(ModBlocks.DARK_SAND_BUTTON, ModBlockTags.DECAYS_TO_DARK_SAND_BUTTON);
	 	addPattern(ModBlocks.DARK_SAND_SLAB, ModBlockTags.DECAYS_TO_DARK_SAND_SLAB);
	 	addPattern(ModBlocks.DARK_SAND_STAIRS, ModBlockTags.DECAYS_TO_DARK_SAND_STAIRS);

		addPattern(Blocks.WHITE_WOOL, Blocks.TARGET);
		addDoublePattern(DimensionalDoors.id("wool_bed"), Blocks.WHITE_WOOL, BlockTags.BEDS);
		addPattern(ModBlocks.DRIFTWOOD_DOOR, ModBlockTags.DECAYS_TO_DRIFTWOOD_DOOR);
		addPattern(ModBlocks.AMALGAM_BLOCK, ModBlockTags.DECAYS_TO_AMALGAM);
		addPattern(ModBlocks.AMALGAM_SLAB, Blocks.CUT_COPPER_SLAB);
		addPattern(ModBlocks.AMALGAM_STAIRS, Blocks.CUT_COPPER_STAIRS);
		addPattern(Blocks.MUD, ModBlockTags.DECAYS_TO_MUD);
		addPattern(ModBlocks.MUD_FENCE, ModBlockTags.DECAYS_TO_MUD_FENCE);
		addPattern(ModBlocks.MUD_GATE, ModBlockTags.DECAYS_TO_MUD_GATE);
		addPattern(ModBlocks.MUD_BUTTON, ModBlockTags.DECAYS_TO_MUD_BUTTON);
		addPattern(ModBlocks.MUD_SLAB, ModBlockTags.DECAYS_TO_MUD_SLAB);
		addPattern(ModBlocks.MUD_STAIRS, ModBlockTags.DECAYS_TO_MUD_STAIRS);
		Stream.of(DyeColor.values()).map(DyeColor::getSerializedName).forEach(name -> {
			addPattern(getBlock(ResourceLocation.tryParse(name + "_terracotta")), getBlock(ResourceLocation.tryParse(name + "_glazed_terracotta")));
			addPattern(getBlock(ResourceLocation.tryParse(name + "_concrete")), getBlock(ResourceLocation.tryParse(name + "_concrete_powder")));
		});
		addPattern(Blocks.GLASS, ModBlockTags.DECAYS_TO_GLASS);
		addPattern(Blocks.GRAVEL, ModBlockTags.DECAYS_TO_GRAVEL);
		addPattern(ModBlocks.GRAVEL_FENCE, ModBlockTags.DECAYS_TO_GRAVEL_FENCE);
		addPattern(ModBlocks.GRAVEL_BUTTON, ModBlockTags.DECAYS_TO_GRAVEL_BUTTON);
		addPattern(ModBlocks.GRAVEL_SLAB, ModBlockTags.DECAYS_TO_GRAVEL_SLAB);
		addPattern(ModBlocks.GRAVEL_STAIRS, ModBlockTags.DECAYS_TO_GRAVEL_STAIRS);
		addPattern(ModBlocks.GRAVEL_WALL, ModBlockTags.DECAYS_TO_GRAVEL_WALL);
		addPattern(Blocks.RED_SAND, Blocks.RED_SANDSTONE);
		addPattern(ModBlocks.RED_SAND_SLAB, ModBlockTags.DECAYS_TO_RED_SAND_SLAB);
		addPattern(ModBlocks.RED_SAND_STAIRS, ModBlockTags.DECAYS_TO_RED_SAND_STAIRS);
		addPattern(ModBlocks.RED_SAND_WALL, ModBlockTags.DECAYS_TO_RED_SAND_WALL);
		addPattern(Blocks.SAND, ModBlockTags.DECAYS_TO_SAND);
		addPattern(ModBlocks.SAND_SLAB, ModBlockTags.DECAYS_TO_SAND_SLAB);
		addPattern(ModBlocks.SAND_STAIRS, ModBlockTags.DECAYS_TO_SAND_STAIRS);
		addPattern(ModBlocks.SAND_WALL, ModBlockTags.DECAYS_TO_SAND_WALL);
		addPattern(Blocks.SOUL_SAND, Blocks.SOUL_SOIL);

		addPattern(Blocks.ICE, Blocks.PACKED_ICE);
		addPattern(Blocks.IRON_BLOCK, Blocks.ANVIL);

		createOxidizationChain(
                Blocks.COPPER_BLOCK, Blocks.WAXED_COPPER_BLOCK,
                Blocks.EXPOSED_COPPER, Blocks.WAXED_EXPOSED_COPPER,
                Blocks.WEATHERED_COPPER, Blocks.WAXED_WEATHERED_COPPER,
                Blocks.OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_COPPER
        );
		createOxidizationChain(
                Blocks.CUT_COPPER, Blocks.WAXED_CUT_COPPER,
                Blocks.EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER,
                Blocks.WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER,
                Blocks.OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER
                );
		createOxidizationChain(
                Blocks.CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER_SLAB,
                Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB,
                Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB,
                Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB
        );
		createOxidizationChain(
                Blocks.CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER_STAIRS,
                Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS,
                Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS,
                Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS
                );

        createOxidizationChain(
                Blocks.COPPER_BULB, Blocks.WAXED_COPPER_BULB,
                Blocks.EXPOSED_COPPER_BULB, Blocks.WAXED_EXPOSED_COPPER_BULB,
                Blocks.WEATHERED_COPPER_BULB, Blocks.WAXED_WEATHERED_COPPER_BULB,
                Blocks.OXIDIZED_COPPER_BULB, Blocks.WAXED_OXIDIZED_COPPER_BULB
                );

        createOxidizationChain(
                Blocks.COPPER_DOOR, Blocks.WAXED_COPPER_DOOR,
                Blocks.EXPOSED_COPPER_DOOR, Blocks.WAXED_EXPOSED_COPPER_DOOR,
                Blocks.WEATHERED_COPPER_DOOR, Blocks.WAXED_WEATHERED_COPPER_DOOR,
                Blocks.OXIDIZED_COPPER_DOOR, Blocks.WAXED_OXIDIZED_COPPER_DOOR
        );

        createOxidizationChain(
                Blocks.COPPER_DOOR, Blocks.WAXED_COPPER_DOOR,
                Blocks.EXPOSED_COPPER_DOOR, Blocks.WAXED_EXPOSED_COPPER_DOOR,
                Blocks.WEATHERED_COPPER_DOOR, Blocks.WAXED_WEATHERED_COPPER_DOOR,
                Blocks.OXIDIZED_COPPER_DOOR, Blocks.WAXED_OXIDIZED_COPPER_DOOR
                );


        addPattern(Blocks.ANCIENT_DEBRIS, Blocks.NETHERITE_BLOCK);
		addPattern(Blocks.DIRT, ModBlockTags.DECAYS_TO_DIRT);
		addPattern(Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM);
		addPattern(ModBlocks.DRIFTWOOD_PLANKS, ModBlockTags.DECAYS_TO_DRIFTWOOD_PLANK);
		addPattern(ModBlocks.DRIFTWOOD_FENCE, ModBlockTags.DECAYS_TO_DRIFTWOOD_FENCE);
		addPattern(ModBlocks.DRIFTWOOD_GATE, ModBlockTags.DECAYS_TO_DRIFTWOOD_GATE);
		addPattern(ModBlocks.DRIFTWOOD_BUTTON, ModBlockTags.DECAYS_TO_DRIFTWOOD_BUTTON);
		addPattern(ModBlocks.DRIFTWOOD_SLAB, ModBlockTags.DECAYS_TO_DRIFTWOOD_SLAB);
		addPattern(ModBlocks.DRIFTWOOD_STAIRS, ModBlockTags.DECAYS_TO_DRIFTWOOD_STAIRS);
		addPattern(Blocks.COMPOSTER, Blocks.BARREL);
		addPattern(Blocks.CHEST, ModBlockTags.DECAYS_TO_CHEST);
		addPattern(Blocks.BONE_BLOCK, Blocks.CONDUIT);
		addPattern(Blocks.SKELETON_SKULL, ModBlockTags.DECAYS_TO_SKELETON_SKULL);
		addPattern(Blocks.SKELETON_WALL_SKULL, ModBlockTags.DECAYS_TO_SKELETON_WALL_SKULL);
		addPattern(Blocks.BAMBOO, Blocks.SCAFFOLDING);
		addPattern(Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN);
		addPattern(Blocks.SLIME_BLOCK, Blocks.HONEY_BLOCK);
		addPattern(Blocks.HONEYCOMB_BLOCK, Blocks.SPONGE);
		addPattern(Blocks.LECTERN, Blocks.BOOKSHELF);
		addPattern(Blocks.PISTON, Blocks.STICKY_PISTON);
		addPattern(Blocks.NETHER_WART_BLOCK, ModBlockTags.DECAYS_TO_NETHERWART_BLOCK);
		addPattern(Blocks.REDSTONE_LAMP, Blocks.BEACON);
		addPattern(ModBlocks.AMALGAM_ORE, ModBlockTags.DECAYS_TO_AMALGAM_ORE);
		addPattern(ModBlocks.CLOD_ORE, ModBlockTags.DECAYS_TO_CLOD_ORE);
		addPattern(Blocks.COBBLESTONE, ModBlockTags.DECAYS_TO_COBBLESTONE);
		addPattern(Blocks.COBBLESTONE_SLAB, ModBlockTags.DECAYS_TO_COBBLESTONE_SLAB);
		addPattern(Blocks.COBBLESTONE_STAIRS, ModBlockTags.DECAYS_TO_COBBLESTONE_STAIRS);
		addPattern(Blocks.COBBLESTONE_WALL, ModBlockTags.DECAYS_TO_COBBLESTONE_WALL);
		addPattern(Blocks.RED_SANDSTONE, ModBlockTags.DECAYS_TO_RED_SANDSTONE);
		addPattern(Blocks.SANDSTONE, ModBlockTags.DECAYS_TO_SANDSTONE);

		addPattern(Blocks.PACKED_ICE, Blocks.BLUE_ICE);
		addPattern(ModBlocks.DRIFTWOOD_WOOD, ModBlockTags.DECAYS_TO_DRIFTWOOD_WOOD);
		addPattern(ModBlocks.DRIFTWOOD_LOG, ModBlockTags.DECAYS_TO_DRIFTWOOD_LOG);
		addPattern(Blocks.BARREL, Blocks.BEEHIVE);
		addPattern(Blocks.CARVED_PUMPKIN, Blocks.JACK_O_LANTERN);
		addPattern(Blocks.SPONGE, Blocks.WET_SPONGE);
		addPattern(Blocks.COAL_ORE, Blocks.DIAMOND_ORE);
		addPattern(Blocks.ANDESITE, Blocks.POLISHED_ANDESITE);
		addPattern(Blocks.ANDESITE_SLAB, Blocks.POLISHED_ANDESITE_SLAB);
		addPattern(Blocks.ANDESITE_STAIRS, Blocks.POLISHED_ANDESITE_STAIRS);
		addPattern(Blocks.BASALT, ModBlockTags.DECAYS_TO_BASALT);
		addPattern(DimensionalDoors.id("basalt_lava"), Blocks.BASALT, Fluids.LAVA);
		addPattern(Blocks.BLACKSTONE, ModBlockTags.DECAYS_TO_BLACKSTONE);
		addPattern(Blocks.BLACKSTONE_SLAB, ModBlockTags.DECAYS_TO_BLACKSTONE_SLAB);
		addPattern(Blocks.BLACKSTONE_STAIRS, ModBlockTags.DECAYS_TO_BLACKSTONE_STAIRS);
		addPattern(Blocks.BLACKSTONE_WALL, ModBlockTags.DECAYS_TO_BLACKSTONE_WALL);
		addPattern(Blocks.DEEPSLATE, ModBlockTags.DECAYS_TO_DEEPSLATE);
		addPattern(ModBlocks.DEEPSLATE_SLAB, ModBlockTags.DECAYS_TO_DEEPSLATE_SLAB);
		addPattern(ModBlocks.DEEPSLATE_STAIRS, ModBlockTags.DECAYS_TO_DEEPSLATE_STAIRS);
		addPattern(ModBlocks.DEEPSLATE_WALL, ModBlockTags.DECAYS_TO_DEEPSLATE_WALL);
		addPattern(Blocks.DIORITE, ModBlockTags.DECAYS_TO_DIORITE);
		addPattern(Blocks.DIORITE_SLAB, ModBlockTags.DECAYS_TO_DIORITE_SLAB);
		addPattern(Blocks.DIORITE_STAIRS, ModBlockTags.DECAYS_TO_DIORITE_STAIRS);
		addPattern(Blocks.DIORITE_WALL, ModBlockTags.DECAYS_TO_DIORITE_WALL);
		addPattern(Blocks.END_STONE, ModBlockTags.DECAYS_TO_ENDSTONE);
		addPattern(ModBlocks.END_STONE_SLAB, ModBlockTags.DECAYS_TO_ENDSTONE_SLAB);
		addPattern(ModBlocks.END_STONE_STAIRS, ModBlockTags.DECAYS_TO_ENDSTONE_STAIRS);
		addPattern(ModBlocks.END_STONE_WALL, ModBlockTags.DECAYS_TO_ENDSTONE_WALL);
		addPattern(Blocks.FURNACE, ModBlockTags.DECAYS_TO_FURNACE);
		addPattern(Blocks.GRANITE, ModBlockTags.DECAYS_TO_GRANITE);
		addPattern(Blocks.GRANITE_SLAB, ModBlockTags.DECAYS_TO_GRANITE_SLAB);
		addPattern(Blocks.GRANITE_STAIRS, ModBlockTags.DECAYS_TO_GRANITE_STAIRS);
		addPattern(Blocks.GRANITE, ModBlockTags.DECAYS_TO_GRANITE);
		addPattern(Blocks.GRANITE_SLAB, ModBlockTags.DECAYS_TO_GRANITE_SLAB);
		addPattern(Blocks.GRANITE_STAIRS, ModBlockTags.DECAYS_TO_GRANITE_STAIRS);
		addPattern(Blocks.NETHERRACK, ModBlockTags.DECAYS_TO_NETHERRACK);
		addPattern(ModBlocks.NETHERRACK_FENCE, ModBlockTags.DECAYS_TO_NETHERRACK_FENCE);
		addPattern(ModBlocks.NETHERRACK_SLAB, ModBlockTags.DECAYS_TO_NETHERRACK_SLAB);
		addPattern(ModBlocks.NETHERRACK_STAIRS, ModBlockTags.DECAYS_TO_NETHERRACK_STAIRS);
		addPattern(ModBlocks.NETHERRACK_WALL, ModBlockTags.DECAYS_TO_NETHERRACK_WALL);
		addPattern(Blocks.PRISMARINE, ModBlockTags.DECAYS_TO_PRISMARINE);
		addPattern(Blocks.PRISMARINE_SLAB, ModBlockTags.DECAYS_TO_PRISMARINE_SLAB);
		addPattern(Blocks.PRISMARINE_STAIRS, ModBlockTags.DECAYS_TO_PRISMARINE_STAIRS);
		addPattern(Blocks.PRISMARINE_WALL, ModBlockTags.DECAYS_TO_PRISMARINE_WALL);
		addPattern(Blocks.STONE, ModBlockTags.DECAYS_TO_STONE);

		addPattern(Fluids.LAVA, Blocks.MAGMA_BLOCK);
		addPattern(Blocks.DROPPER, Blocks.DISPENSER);
		addPattern(Blocks.DARK_PRISMARINE, ModBlockTags.DECAYS_TO_DARK_PRISMARINE);
		addPattern(Blocks.DARK_PRISMARINE_SLAB, ModBlockTags.DECAYS_TO_DARK_PRISMARINE_SLAB);
		addPattern(Blocks.DARK_PRISMARINE_STAIRS, ModBlockTags.DECAYS_TO_DARK_PRISMARINE_STAIRS);
		addPattern(ModBlocks.CLOD_BLOCK, ModBlockTags.DECAYS_TO_CLOD_BLOCK);
		addPattern(Blocks.OBSIDIAN, ModBlockTags.DECAYS_TO_OBSIDIAN);
		addPattern(Blocks.STONE_BRICKS, ModBlockTags.DECAYS_TO_STONE_BRICKS);
		addPattern(Blocks.STONE_BRICK_SLAB, ModBlockTags.DECAYS_TO_STONE_BRICK_SLAB);
		addPattern(Blocks.STONE_BRICK_STAIRS, ModBlockTags.DECAYS_TO_STONE_BRICK_STAIRS);
		addPattern(Blocks.STONE_BRICK_WALL, ModBlockTags.DECAYS_TO_STONE_BRICK_WALL);
		addPattern(Blocks.CRYING_OBSIDIAN, Blocks.RESPAWN_ANCHOR);
	}

    private void addPattern(Object to, Object from) {
        var id = getId(to);

        if(id == null) {
            return;
        }

        addPattern(DimensionalDoors.id(getId(to)), to, from);
    }

    private void addPattern(ResourceLocation id, Object to, Object from) {
        addPattern(createPatterData(id, from, to));
    }

    private void addPattern(DecayPatternHolder holder) {
        patternList.add(holder);
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

	private void createOxidizationChain(Block... blocks) {
        for (int i = 0; i < blocks.length - 2; i += 2) {
            var from = blocks[i];
            var fromWaxed = blocks[i+1];
            var to = blocks[i+2];
            var toWaxed = blocks[i+3];

            addPattern(to, from);
            addPattern(DimensionalDoors.id("dewaxed_" + getId(from)), from, fromWaxed);
            addPattern(DimensionalDoors.id("dewaxed_" + getId(to)), to, toWaxed);
        }
	}

	private Block getBlock(ResourceLocation id) {
		return BuiltInRegistries.BLOCK.get(id);
	}

	private ResourceLocation getBlockId(Block block) {
		return BuiltInRegistries.BLOCK.getKey(block);
	}

	private DecayPatternHolder turnIntoSelf(ResourceLocation ResourceLocation, Object before) {
		return new DecayPatternHolder(ResourceLocation, new DecayPattern(List.of(getPredicate(before)), SelfDecayResult.instance()));
	}

    @Override
    public String getName() {
        return "Limbo Decay";
    }

    private static Path getOutput(Path rootOutput, ResourceLocation lootTableId) {
        return rootOutput.resolve("data/" + lootTableId.getNamespace() + "/decay_patterns/" + lootTableId.getPath() + ".json");
    }

    public DecayPatternHolder createPatterData(ResourceLocation id, Object before, Object after) {
        return new DecayPatternHolder(id, new DecayPattern(List.of(getPredicate(before)), getProcessor(after)));
    }

    public void addDoublePattern(Object before, Block after) {
        addDoublePattern(DimensionalDoors.id(getId(after)), after, before);
    }

    public void addDoublePattern(ResourceLocation id, Object after, Object before) {
        Block block = after instanceof RegistrySupplier<?> supplier ? (Block) supplier.get() : (Block) after;

        patternList.add(new DecayPatternHolder(id, new DecayPattern(List.of(getPredicate(before)), new DoubleBlockDecayResult(1, DEFAULT, block))));
    }

	public DecayPatternHolder createDoublePattern(ResourceLocation id, Object before, Block after) {
		return new DecayPatternHolder(id, new DecayPattern(List.of(getPredicate(before)), new DoubleBlockDecayResult(1, DEFAULT, after)));
	}
}
