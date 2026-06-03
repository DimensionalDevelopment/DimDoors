package org.dimdev.dimdoors.item;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import org.dimdev.dimdoors.DimensionalDoors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ModArmorMaterials {
    public static final ArmorMaterial GARMENT_OF_REALITY = register(
        "garment_of_reality",
        15,
        SoundEvents.ARMOR_EQUIP_LEATHER,
        () -> () -> Ingredient.of(Items.STONE),
        new int[]{1, 2, 3, 1},
        0.0F,
        0.0F); //TODO: DEFINE TRAITS

    public static final ArmorMaterial WORLD_THREAD = register(
        "world_thread",
        15,
        SoundEvents.ARMOR_EQUIP_LEATHER,
        () -> () -> Ingredient.of(Items.STONE),
        new int[]{1, 2, 3, 1},
        0.0F,
        0.0F);

    public static ArmorMaterial register(String name, int enchantability, Holder<SoundEvent> equipSound, Supplier<Supplier<Ingredient>> repairIngredient, int[] protectionAmounts, float toughness, float knockbackResistance) {
        var id = DimensionalDoors.id(name);

        Map<ArmorType, Integer> map = Util.make(new HashMap<>(), typeMap -> {
            for (int i = 0; i < protectionAmounts.length; i++) {
                var type = ArmorType.values()[i];
                typeMap.put(type, protectionAmounts[i]);
            }
        });

        return new ArmorMaterial(map, enchantability, equipSound, repairIngredient.get(), List.of(new ArmorMaterial.Layer(id)), toughness, knockbackResistance);
    }

    public static void init() {
    }
}
