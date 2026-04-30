package org.dimdev.dimdoors.item;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
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

        Map<ArmorItem.Type, Integer> map = Util.make(new HashMap<>(), typeMap -> {
            for (int i = 0; i < protectionAmounts.length; i++) {
                var type = ArmorItem.Type.values()[i];
                typeMap.put(type, protectionAmounts[i]);
            }
        });

        return DimensionalDoors.getSided().registerArmorMaterial(name, new ArmorMaterial(map, enchantability, equipSound, repairIngredient.get(), List.of(new ArmorMaterial.Layer(id)), toughness, knockbackResistance));
    }

    public static void init() {
    }
}
