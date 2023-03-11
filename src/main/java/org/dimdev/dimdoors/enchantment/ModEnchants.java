package org.dimdev.dimdoors.enchantment;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModEnchants {
	public static Enchantment STRING_THEORY_ENCHANTMENT;

	public static void init() {
		STRING_THEORY_ENCHANTMENT = Registry.register(
				BuiltInRegistries.ENCHANTMENT,
				DimensionalDoors.id("string_theory"),
				new StringTheoryEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.WEARABLE, new EquipmentSlot[] {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD})
		);
	}
}
