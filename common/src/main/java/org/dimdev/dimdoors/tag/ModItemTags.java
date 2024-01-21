package org.dimdev.dimdoors.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModItemTags {
    public static final TagKey<Item> DIAMONDS = of("diamonds");

    public static final TagKey<Item> GOLD_INGOTS = of("gold_ingots");
    public static final TagKey<Item> IRON_INGOTS = of("iron_ingots");

    public static final TagKey<Item> LIMBO_GAZE_DEFYING = of("limbo_gaze_defying");

    private static TagKey<Item> of(String id) {
        return TagKey.create(Registries.ITEM, DimensionalDoors.id(id));
    }
}
