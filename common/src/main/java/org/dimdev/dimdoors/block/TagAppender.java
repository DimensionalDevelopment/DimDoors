package org.dimdev.dimdoors.block;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagAppender {
    public static final TagKey<Block> EXAMPLE_TAG = TagKey.create(
        BuiltInRegistries.BLOCK.key(),
            Identifier.fromNamespaceAndPath("yourmod", "example_tag")
    );

    public static void appendToTag() {
        var registry = BuiltInRegistries.BLOCK;

        // Get current (after datapacks)
        List<Holder<Block>> current = registry.getTag(EXAMPLE_TAG)
            .map(tag -> tag.stream().toList())
            .orElseGet(ArrayList::new);

        // Your additions (use holders!)
        List<Holder<Block>> updated = new ArrayList<>(current);
        updated.add(registry.getHolderOrThrow(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("yourmod", "your_block"))));

        // Prepare map for bindTags
        Map<TagKey<Block>, List<Holder<Block>>> map = new HashMap<>();
        map.put(EXAMPLE_TAG, updated);

        // Apply (works post-freeze on both loaders)
        registry.bindTags(map);
    }
}