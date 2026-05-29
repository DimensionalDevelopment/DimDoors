package org.dimdev.dimdoors.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModEnchantmentTags {
    public static final TagKey<Enchantment> DUNGEON_LOOT = of("dungeon_loot");

    private static TagKey<Enchantment> of(String id) {
        return TagKey.create(Registries.ENCHANTMENT, DimensionalDoors.id(id));
    }
}
