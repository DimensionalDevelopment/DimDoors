package org.dimdev.dimdoors.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;

public class WorldThreadArmorItem extends ArmorItem {
    public static final ArmorMaterial WOVEN_WORLD_THREAD = EnumHelper.addArmorMaterial(
            "woven_world_thread",
            "dimdoors:woven_world_thread",
            20,
            new int[]{2, 3, 4, 5},
            20,
            SoundEvents.ITEM_ARMOR_EQUIP_GENERIC,
            1.0f)
                                                                     .setRepairItem(new ItemStack(ModItems.WORLD_THREAD));

    public WorldThreadArmorItem(String name, int renderIndex, EquipmentSlot equipmentSlot) {
        super(WOVEN_WORLD_THREAD, renderIndex, equipmentSlot);
        setRegistryName("dimdoors", name);
        setTranslationKey(name);
        setCreativeTab(ModItemGroups.DIMENSIONAL_DOORS);
    }
}
