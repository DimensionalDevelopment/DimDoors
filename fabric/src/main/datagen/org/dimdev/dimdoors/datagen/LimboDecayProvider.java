package org.dimdev.dimdoors.datagen;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Fluid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.decay.DecayPatternHolder;
import org.dimdev.dimdoors.world.decay.conditions.DecayCondition;
import org.dimdev.dimdoors.world.decay.conditions.DimensionDecayCondition;
import org.dimdev.dimdoors.world.decay.conditions.FluidDecayCondition;
import org.dimdev.dimdoors.world.decay.conditions.SimpleDecayCondition;
import org.dimdev.dimdoors.world.decay.pattern.CompoundDecayPattern;
import org.dimdev.dimdoors.world.decay.pattern.DecayPattern;
import org.dimdev.dimdoors.world.decay.pattern.PaintingDecayPattern;
import org.dimdev.dimdoors.world.decay.results.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

abstract public class LimboDecayProvider implements DataProvider {
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

    abstract protected void generatePatterns(HolderLookup.Provider provider, Consumer<DecayPatternHolder> consumer);

    protected DecayPatternHolder.Builder addPaintingPattern(ResourceKey<PaintingVariant> key, TagKey<PaintingVariant> decaysInto) {
        return DecayPatternHolder.builder(key.location()).pattern(PaintingDecayPattern.builder().from(decaysInto).to(key));
    }

    protected DecayPatternHolder.Builder addPattern(Object to, Object from) {
        var id = getId(to);

        if(id == null) {
            return null;
        }

        return addPattern(DimensionalDoors.id(getId(to)), to, from);
    }

    protected DecayPatternHolder.Builder addPattern(ResourceLocation id, Object to, Object from) {
        return createPatterData(id, from, to);
    }

    protected DecayCondition getPredicate(Object object) {
        if (object instanceof TagKey<?> tag) {
            if (tag.isFor(Registries.BLOCK)) return SimpleDecayCondition.of((TagKey<Block>) tag);
            else if (tag.isFor(Registries.FLUID)) return FluidDecayCondition.of((TagKey<Fluid>) tag);
            else if (tag.isFor(Registries.DIMENSION_TYPE)) return DimensionDecayCondition.of((TagKey<DimensionType>) tag);

        } else if(object instanceof ResourceKey<?> key) {
            if (key.isFor(Registries.BLOCK)) return SimpleDecayCondition.of((ResourceKey<Block>) key);
            else if (key.isFor(Registries.FLUID)) return FluidDecayCondition.of((ResourceKey<Fluid>) key);
            else if (key.isFor(Registries.DIMENSION_TYPE)) return DimensionDecayCondition.of((ResourceKey<DimensionType>) key);


        } else if(object instanceof Supplier<?> supplier) {
            var obj = supplier.get();

            if(obj instanceof Block block) {
                return SimpleDecayCondition.of(block.builtInRegistryHolder().key());
            } else if(obj instanceof Fluid fluid) {
                return FluidDecayCondition.of(fluid.builtInRegistryHolder().key());
            }

        } else if (object instanceof Block block) {
            return SimpleDecayCondition.of(block.builtInRegistryHolder().key());
        } else if (object instanceof Fluid fluid) {
            return FluidDecayCondition.of(fluid.builtInRegistryHolder().key());
        }

        return DecayCondition.NONE;
    }

    protected String getId(Object object) {
        if(object instanceof ResourceKey<?> key) {
            return key.registryKey().location().getPath();
        } else if (object instanceof Block block) {
            return block.builtInRegistryHolder().key().location().getPath();
        } else if (object instanceof Fluid fluid) {
            return fluid.builtInRegistryHolder().key().location().getPath();
        }

        return null;
    }

    protected DecayResult getProcessor(Object object) {
        return getProcessor(object, 1);
    }

    protected DecayResult getProcessor(Object object, int entropy) {
        if (object instanceof Supplier<?> supplier) {
            object = supplier.get();
        }

        if(object instanceof Block block) return new SingleBlockDecayResult(entropy, 0.0f, block);
        else if(object instanceof Fluid fluid) return new FluidDecayResult(entropy, 0.0f, fluid);
        else return NoneDecayResult.instance();
    }

    protected void createOxidizationChain(Consumer<DecayPatternHolder> consumer, HolderLookup.Provider provider, Block... blocks) {
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

    protected Block getBlock(ResourceLocation id) {
        return BuiltInRegistries.BLOCK.get(id);
    }

    protected ResourceLocation getBlockId(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    protected DecayPatternHolder turnIntoSelf(ResourceLocation ResourceLocation, Object before) {
        return new DecayPatternHolder(ResourceLocation, new CompoundDecayPattern(List.of(getPredicate(before)), SelfDecayResult.instance()));
    }

    protected static Path getOutput(Path rootOutput, ResourceLocation lootTableId) {
        return rootOutput.resolve("data/" + lootTableId.getNamespace() + "/decay_patterns/" + lootTableId.getPath() + ".json");
    }

    protected DecayPatternHolder.Builder createPatterData(ResourceLocation id, Object before, Object after) {
        return DecayPatternHolder.builder(id).pattern(CompoundDecayPattern.builder().condition(getPredicate(before)).result(getProcessor(after)));
    }

    protected void addDoublePattern(Object before, Block after) {
        addDoublePattern(DimensionalDoors.id(getId(after)), after, before);
    }

    public DecayPatternHolder.Builder addDoublePattern(ResourceLocation id, Object after, Object before) {
        Block block = (Block) after;

        return DecayPatternHolder.builder(id).pattern(CompoundDecayPattern.builder().condition(getPredicate(before)).result(new DoubleBlockDecayResult(1, 0.0f, block)));
    }

    public DecayPatternHolder createDoublePattern(ResourceLocation id, Object before, Block after) {
        return new DecayPatternHolder(id, new CompoundDecayPattern(List.of(getPredicate(before)), new DoubleBlockDecayResult(1, 0.0f, after)));
    }
}
