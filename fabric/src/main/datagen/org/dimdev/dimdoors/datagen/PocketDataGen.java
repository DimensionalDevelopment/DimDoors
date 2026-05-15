package org.dimdev.dimdoors.datagen;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.world.decay.DecayPatternHolder;
import org.dimdev.dimdoors.world.decay.pattern.DecayPattern;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class PocketDataGen implements DataProvider {
    private final PackOutput.PathProvider resolver;

    public PocketDataGen(PackOutput packOutput) {
        this.resolver = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "pockets/generators");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> list = new ArrayList<>();

        BiConsumer<ResourceLocation,PocketGenerator> consumer = (id, generator) -> {
            JsonElement object = JsonOps.INSTANCE.withEncoder(PocketGenerator.CODEC).apply(generator).getOrThrow();
            Path outputPath = resolver.json(patternHolder.id());
            list.add(DataProvider.saveStable(cache, object, outputPath));
        };

        generatePatterns(provider, consumer);

        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }
}
