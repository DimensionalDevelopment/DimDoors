package org.dimdev.dimdoors.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

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
            return SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.ofItems(ModItems.WORLD_THREAD);
        }

        @Override
        public String getName() {
            return "world_thread";
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

    public WorldThreadArmorItem(EquipmentSlot equipmentSlot, Item.Settings settings) {
        super(MATERIAL, equipmentSlot, settings);
    }
}
