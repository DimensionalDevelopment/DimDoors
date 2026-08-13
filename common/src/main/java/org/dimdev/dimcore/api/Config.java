package org.dimdev.dimcore.api;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(new TypeToken<ResourceKey<Level>>() {}.getType(), new LevelKeyAdapter())
            .create();


    public static <T extends Config> T load(ISided<?> sided, Class<T> configClass) {
        return load(configClass, sided.configPath());
    }


    private static <T extends Config> T load(Class<T> configClass, Path configPath) {
        if (!Files.exists(configPath)) {
            T config = createInstance(configClass);
            save(config, configPath);
            return config;
        }

        try (var reader = Files.newBufferedReader(configPath)) {
            T config = GSON.fromJson(reader, configClass);
            return config == null ? createInstance(configClass) : config;
        } catch (IOException | JsonParseException e) {
            throw new IllegalStateException("Failed to load " + configPath.getFileName() + " config from " + configPath, e);
        }
    }

    public static <T extends Config> T createInstance(Class<T> configClass) {
        try {
            Constructor<T> constructor = configClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException("Failed to create config instance for " + configClass.getName(), e);
        }
    }

    public void save(ISided<?> sided) {
        save(this, sided.configPath());
    }

    private static void save(Config config, Path configPath) {
        try {
            Files.createDirectories(configPath.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
                writer.write(GSON.toJson(config));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save " + configPath.getFileName() + " to " + configPath, e);
        }
    }

    public static final class LevelKeyAdapter implements JsonSerializer<ResourceKey<Level>>, JsonDeserializer<ResourceKey<Level>> {

        @Override
        public ResourceKey<Level> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(json.getAsJsonPrimitive().getAsString()));
        }

        @Override
        public JsonElement serialize(ResourceKey<Level> src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.location().toString());
        }
    }
}
