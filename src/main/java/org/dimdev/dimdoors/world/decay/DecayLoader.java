package org.dimdev.dimdoors.world.decay;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.Path;
import org.dimdev.dimdoors.api.util.SimpleTree;
import org.jetbrains.annotations.NotNull;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

public class DecayLoader implements SimpleSynchronousResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
    private static final DecayLoader INSTANCE = new DecayLoader();
    private SimpleTree<String, DecayPattern> patterns = new SimpleTree<>(String.class);

    private DecayLoader() {
    }

    public static DecayLoader getInstance() {
        return INSTANCE;
    }

    @Override
    public void reload(ResourceManager manager) {
        patterns.clear();
        CompletableFuture<SimpleTree<String, DecayPattern>> futurePatternMap = loadResourcePathFromJsonToTree(manager, "decay_patterns", this::loadPattern);
        patterns = futurePatternMap.join();
    }

    private DecayPattern loadPattern(JsonObject object) {
        return DecayPattern.deserialize(object);
    }

    private <T> CompletableFuture<SimpleTree<String, T>> loadResourcePathFromJsonToTree(ResourceManager manager, String startingPath, Function<JsonObject, T> reader) {
        int sub = startingPath.endsWith("/") ? 0 : 1;

        Collection<Identifier> ids = manager.findResources(startingPath, str -> str.endsWith(".json"));
        return CompletableFuture.supplyAsync(() -> {
            SimpleTree<String, T> tree = new SimpleTree<>(String.class);
            tree.putAll(ids.parallelStream().unordered().collect(Collectors.toConcurrentMap(
                    id -> Path.stringPath(id.getNamespace() + ":" + id.getPath().substring(0, id.getPath().lastIndexOf(".")).substring(startingPath.length() + sub)),
                    id -> {
                        try {
                            JsonElement json = GSON.fromJson(new InputStreamReader(manager.getResource(id).getInputStream()), JsonElement.class);
                            return reader.apply(json.getAsJsonObject());
                        } catch (IOException e) {
                            throw new RuntimeException("Error loading resource: " + id);
                        }
                    })));
            return tree;
        });
    }

    public @NotNull Collection<DecayPattern> getPatterns() {
        return patterns.values();
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier("dimdoors", "decay_pattern");
    }
}
