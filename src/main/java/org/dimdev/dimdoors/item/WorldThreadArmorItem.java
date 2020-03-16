package org.dimdev.dimdoors.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;

public class WorldThreadArmorItem extends ArmorItem {
    public static final ArmorMaterial MATERIAL = new ArmorMaterial() {
        @Override
        public int getDurability(EquipmentSlot equipmentSlot) {
            return 20;
        }

        @Override
        public int getProtectionAmount(EquipmentSlot equipmentSlot) {
            return new int[]{2, 3, 4, 5}[equipmentSlot.getEntitySlotId()];
        }

        @Override
        public int getEnchantability() {
            return 20;
        }

        @Override
        public SoundEvent getEquipSound() {
            return null;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.ofItems(ModItems.WORLD_THREAD);
        }

        @Override
        public String getName() {
            return "woven_world_thread";
        }

        @Override
        public float getToughness() {
            return 1;
        }

        @Override
        public float getKnockbackResistance() {
            return 0;
        }
    };

    public WorldThreadArmorItem(String name, EquipmentSlot equipmentSlot, Item.Settings settings) {
        super(MATERIAL, equipmentSlot, settings);
    }
}
