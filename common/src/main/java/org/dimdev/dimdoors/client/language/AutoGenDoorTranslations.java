package org.dimdev.dimdoors.client.language;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public final class AutoGenDoorTranslations {
    private static final String AUTOGEN_PREFIX = "autogen.";
    private static final String BLOCK_PREFIX_KEY = AUTOGEN_PREFIX + "block_prefix";
    private static final String ITEM_PREFIX_KEY = AUTOGEN_PREFIX + "item_prefix";

    private static final String DEFAULT_BLOCK_PREFIX = "Dimensional %";
    private static final String DEFAULT_ITEM_PREFIX = "Dimensional ";

    private static final Map<String, String> loadedTranslations = new HashMap<>();
    private static final Map<String, String> generatedTranslations = new HashMap<>();
    private static final Map<String, Map<String, String>> infoTranslations = new HashMap<>();

    private AutoGenDoorTranslations() {
    }

    public static void beginReload() {
        loadedTranslations.clear();
        generatedTranslations.clear();
        infoTranslations.clear();
    }

    public static void recordTranslation(String key, String value) {
        loadedTranslations.put(key, value);
        indexInfoTranslation(key, value);
    }

    public static void apply(BiConsumer<String, String> output) {
        apply(output, false);
    }

    public static void apply(
            BiConsumer<String, String> output,
            boolean overwriteExisting
    ) {
        var blockRegistrar = DimensionalDoors.getDimensionalDoorBlockRegistrar();
        if (blockRegistrar != null) {
            applyMappings(
                    output,
                    blockRegistrar.getGeneratedBlockMappings(),
                    "block",
                    BLOCK_PREFIX_KEY,
                    DEFAULT_BLOCK_PREFIX,
                    overwriteExisting,
                    true
            );
        }

        var itemRegistrar = DimensionalDoors.getDimensionalDoorItemRegistrar();
        if (itemRegistrar != null) {
            applyMappings(
                    output,
                    itemRegistrar.getGeneratedItemMappings(),
                    "item",
                    ITEM_PREFIX_KEY,
                    DEFAULT_ITEM_PREFIX,
                    overwriteExisting,
                    false
            );
        }
    }

    public static Map<String, String> getGeneratedTranslations() {
        return Map.copyOf(generatedTranslations);
    }

    private static void applyMappings(
            BiConsumer<String, String> output,
            Map<ResourceLocation, ResourceLocation> mappings,
            String type,
            String prefixKey,
            String fallbackPrefix,
            boolean overwriteExisting,
            boolean copyInfo
    ) {
        String prefix = loadedTranslations.get(prefixKey);
        if (prefix == null || prefix.isEmpty()) {
            prefix = fallbackPrefix;
        }

        String finalPrefix = prefix;

        mappings.forEach((generatedId, originalId) -> {
            String generatedKey = Util.makeDescriptionId(type, generatedId);

            addGeneratedTranslation(
                    output,
                    generatedKey,
                    Util.makeDescriptionId(type, originalId),
                    finalPrefix,
                    originalId,
                    overwriteExisting
            );

            if (copyInfo) {
                addGeneratedInfoTranslations(output, generatedKey, originalId);
            }
        });
    }

    private static void addGeneratedTranslation(
            BiConsumer<String, String> output,
            String generatedKey,
            String originalKey,
            String prefix,
            ResourceLocation originalId,
            boolean overwriteExisting
    ) {
        String shorthand = loadedTranslations.get(autogenKey(originalId, "name"));
        String existing = loadedTranslations.get(generatedKey);
        String previousGenerated = generatedTranslations.get(generatedKey);

        String value;
        if (shorthand != null) {
            value = shorthand;
        } else if (
                !overwriteExisting
                        && existing != null
                        && !Objects.equals(existing, previousGenerated)
        ) {
            value = existing;
        } else {
            String originalName = loadedTranslations.getOrDefault(
                    originalKey,
                    fallbackName(originalId)
            );

            value = assemble(prefix, originalName);
        }

        output.accept(generatedKey, value);
        loadedTranslations.put(generatedKey, value);
        generatedTranslations.put(generatedKey, value);
    }

    private static void addGeneratedInfoTranslations(
            BiConsumer<String, String> output,
            String generatedKey,
            ResourceLocation originalId
    ) {
        Map<String, String> translations =
                infoTranslations.get(autogenKey(originalId, "info"));

        if (translations == null) {
            return;
        }

        translations.forEach((suffix, value) -> {
            String generatedInfoKey = generatedKey + ".info" + suffix;

            output.accept(generatedInfoKey, value);
            loadedTranslations.put(generatedInfoKey, value);
            generatedTranslations.put(generatedInfoKey, value);
        });
    }

    private static void indexInfoTranslation(String key, String value) {
        if (!key.startsWith(AUTOGEN_PREFIX)) {
            return;
        }

        int infoIndex = key.lastIndexOf(".info");
        if (infoIndex < 0) {
            return;
        }

        String suffix = key.substring(infoIndex + ".info".length());
        if (!suffix.isEmpty() && !suffix.chars().allMatch(Character::isDigit)) {
            return;
        }

        if (suffix.equals("0")) {
            suffix = "";
        }

        String prefix = key.substring(0, infoIndex + ".info".length());

        infoTranslations
                .computeIfAbsent(prefix, ignored -> new HashMap<>())
                .put(suffix, value);
    }

    private static String assemble(String prefix, String originalName) {
        return prefix.contains("%")
                ? prefix.replace("%", originalName)
                : prefix + originalName;
    }

    private static String autogenKey(ResourceLocation id, String suffix) {
        return AUTOGEN_PREFIX
                + id.getNamespace()
                + "."
                + id.getPath().replace('/', '.')
                + "."
                + suffix;
    }

    private static String fallbackName(ResourceLocation id) {
        String path = id.getPath();
        String name = path.substring(path.lastIndexOf('/') + 1)
                .replace('_', ' ')
                .replace('-', ' ')
                .replace('.', ' ');

        return Arrays.stream(name.split(" "))
                .filter(word -> !word.isEmpty())
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }
}