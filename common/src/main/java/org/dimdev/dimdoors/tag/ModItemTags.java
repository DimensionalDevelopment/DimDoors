package org.dimdev.dimdoors.tag;

import dev.eriksonn.aeronautics.index.AeroTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.dimdev.dimdoors.DimensionalDoors;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModItemTags {
    public static final TagKey<Item> LIMBO_GAZE_DEFYING = of("limbo_gaze_defying");
    public static final TagKey<Item> DRIFTWOOD_LOGS = of("driftwood_logs");
    public static final TagKey<Item> DIMENSIONAL_DOORS = of("dimensional_doors");
    public static final TagKey<Item> FABRIC = of("fabric");
    public static final TagKey<Item> ANCIENT_FABRIC = of("ancient_fabric");

    public static final Map<DyeColor, TagKey<Item>> DYES = Arrays.stream(DyeColor.values()).collect(Collectors.toMap(Function.identity(), ModItemTags::dye));

    private static TagKey<Item> dye(DyeColor dyeColor) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dyes/" + dyeColor.getSerializedName()));
    }


    private static TagKey<Item> of(String id) {
        return TagKey.create(Registries.ITEM, DimensionalDoors.id(id));
    }
}
