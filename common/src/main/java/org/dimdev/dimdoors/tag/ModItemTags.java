package org.dimdev.dimdoors.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModItemTags {
    public static final TagKey<Item> LIMBO_GAZE_DEFYING = of("limbo_gaze_defying");
    public static final TagKey<Item> DRIFTWOOD_LOGS = of("driftwood_logs");


    private static TagKey<Item> of(String id) {
        return TagKey.create(Registries.ITEM, DimensionalDoors.id(id));
    }
}
