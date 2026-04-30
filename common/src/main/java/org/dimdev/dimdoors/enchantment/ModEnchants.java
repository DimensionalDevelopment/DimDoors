package org.dimdev.dimdoors.enchantment;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModEnchants {
    public static ResourceKey<Enchantment> STRING_THEORY_ENCHANTMENT = ResourceKey.create(Registries.ENCHANTMENT, DimensionalDoors.id("string_theory"));

    public static void bootstrap(BootstrapContext<Enchantment> context) {
    HolderGetter<DamageType> holderGetter = context.lookup(Registries.DAMAGE_TYPE);
    HolderGetter<Enchantment> holderGetter2 = context.lookup(Registries.ENCHANTMENT);
    HolderGetter<Item> holderGetter3 = context.lookup(Registries.ITEM);
    HolderGetter<Block> holderGetter4 = context.lookup(Registries.BLOCK);

    register(context, STRING_THEORY_ENCHANTMENT, Enchantment.enchantment(Enchantment.definition(
        holderGetter3.getOrThrow(ItemTags.ARMOR_ENCHANTABLE), 10, 4,
        Enchantment.dynamicCost(1, 11),
        Enchantment.dynamicCost(12, 11), 1,
        EquipmentSlotGroup.ARMOR))
        .exclusiveWith(holderGetter2.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE)));
    }

    private static void register(BootstrapContext<Enchantment> bootstrapContext, ResourceKey<Enchantment> resourceKey, Enchantment.Builder builder) {
    bootstrapContext.register(resourceKey, builder.build(resourceKey.location()));
    }
}
