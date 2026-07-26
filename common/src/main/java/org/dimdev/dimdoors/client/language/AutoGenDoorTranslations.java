package org.dimdev.dimdoors.client.language;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;
import org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

public final class AutoGenDoorTranslations {
    private static final String GENERATED_BLOCK_PREFIX_KEY = "dimdoors.autogen_block_prefix";
    private static final String GENERATED_ITEM_PREFIX_KEY = "dimdoors.autogen_item_prefix";
    private static final String FALLBACK_BLOCK_PREFIX = "Dimensional %";
    private static final String FALLBACK_ITEM_PREFIX = "Dimensional ";
    private static final Map<String, String> loadedTranslations = new HashMap<>();
    private static final Map<String, String> generatedTranslations = new HashMap<>();

    private AutoGenDoorTranslations() {
    }

    public static void beginReload() {
        loadedTranslations.clear();
        generatedTranslations.clear();
    }

    public static void recordTranslation(String key, String value) {
        loadedTranslations.put(key, value);
    }

    public static void apply(BiConsumer<String, String> output) {
        apply(output, false);
    }

    public static void apply(BiConsumer<String, String> output, boolean overwriteExisting) {
        DimensionalDoorBlockRegistrar blockRegistrar = DimensionalDoors.getDimensionalDoorBlockRegistrar();
        if (blockRegistrar != null) {
            for (Map.Entry<ResourceLocation, ResourceLocation> entry : blockRegistrar.getGeneratedBlockMappings().entrySet()) {
                addGeneratedTranslation(
                        output,
                        Util.makeDescriptionId("block", entry.getKey()),
                        Util.makeDescriptionId("block", entry.getValue()),
                        loadedTranslations.getOrDefault(GENERATED_BLOCK_PREFIX_KEY, FALLBACK_BLOCK_PREFIX),
                        FALLBACK_BLOCK_PREFIX,
                        shorthandNameKey(entry.getValue()),
                        entry.getValue(),
                        overwriteExisting
                );
                addGeneratedShorthandTranslation(
                        output,
                        Util.makeDescriptionId("block", entry.getKey()),
                        shorthandInfoKeyPrefix(entry.getValue())
                );
            }
        }

        DimensionalDoorItemRegistrar itemRegistrar = DimensionalDoors.getDimensionalDoorItemRegistrar();
        if (itemRegistrar != null) {
            for (Map.Entry<ResourceLocation, ResourceLocation> entry : itemRegistrar.getGeneratedItemMappings().entrySet()) {
                addGeneratedTranslation(
                        output,
                        Util.makeDescriptionId("item", entry.getKey()),
                        Util.makeDescriptionId("item", entry.getValue()),
                        loadedTranslations.getOrDefault(GENERATED_ITEM_PREFIX_KEY, FALLBACK_ITEM_PREFIX),
                        FALLBACK_ITEM_PREFIX,
                        shorthandNameKey(entry.getValue()),
                        entry.getValue(),
                        overwriteExisting
                );
            }
        }
    }

    public static Map<String, String> getGeneratedTranslations() {
        return Map.copyOf(generatedTranslations);
    }

    private static void addGeneratedTranslation(BiConsumer<String, String> output, String generatedKey, String originalKey, String prefix, String fallbackPrefix, String shorthandKey, ResourceLocation originalId, boolean overwriteExisting) {
        String existing = loadedTranslations.get(generatedKey);
        String previousGenerated = generatedTranslations.get(generatedKey);
        String value;

        if (loadedTranslations.containsKey(shorthandKey)) {
            value = loadedTranslations.get(shorthandKey);
        } else if (!overwriteExisting && existing != null && !Objects.equals(existing, previousGenerated)) {
            value = existing;
        } else {
            String originalName = loadedTranslations.getOrDefault(originalKey, fallbackName(originalId));
            value = assemble(prefix, fallbackPrefix, originalName);
        }

        output.accept(generatedKey, value);
        loadedTranslations.put(generatedKey, value);
        generatedTranslations.put(generatedKey, value);
    }

    private static void addGeneratedShorthandTranslation(BiConsumer<String, String> output, String generatedKey, String shorthandKeyPrefix) {
        loadedTranslations.entrySet().stream()
                .filter(entry -> isInfoShorthand(entry.getKey(), shorthandKeyPrefix))
                .forEach(entry -> {
                    String generatedInfoKey = generatedKey + ".info" + entry.getKey().substring(shorthandKeyPrefix.length());
                    output.accept(generatedInfoKey, entry.getValue());
                    generatedTranslations.put(generatedInfoKey, entry.getValue());
                });
    }

    private static boolean isInfoShorthand(String key, String shorthandKeyPrefix) {
        if (!key.startsWith(shorthandKeyPrefix)) {
            return false;
        }

        String suffix = key.substring(shorthandKeyPrefix.length());
        return suffix.isEmpty() || suffix.chars().allMatch(Character::isDigit);
    }

    private static String assemble(String prefix, String fallbackPrefix, String originalName) {
        String pattern = prefix == null || prefix.isEmpty() ? fallbackPrefix : prefix;
        if (pattern.contains("%")) {
            return pattern.replace("%", originalName);
        }

        return pattern + originalName;
    }

    private static String shorthandNameKey(ResourceLocation originalId) {
        return "dimdoors.autogen." + originalId.getNamespace() + "." + originalId.getPath().replace('/', '.') + ".name";
    }

    private static String shorthandInfoKeyPrefix(ResourceLocation originalId) {
        return "dimdoors.autogen." + originalId.getNamespace() + "." + originalId.getPath().replace('/', '.') + ".info";
    }

    private static String fallbackName(ResourceLocation id) {
        String path = id.getPath();
        int slash = path.lastIndexOf('/');
        if (slash >= 0) {
            path = path.substring(slash + 1);
        }

        StringBuilder name = new StringBuilder(path.length());
        boolean capitalize = true;
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (c == '_' || c == '-' || c == '.') {
                name.append(' ');
                capitalize = true;
            } else if (capitalize) {
                name.append(Character.toUpperCase(c));
                capitalize = false;
            } else {
                name.append(c);
            }
        }

        return name.toString();
    }
}
