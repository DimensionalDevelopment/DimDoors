package org.dimdev.dimdoors.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModEnchants {
	public static Enchantment STRING_THEORY_ENCHANTMENT;

	public static void init() {
		STRING_THEORY_ENCHANTMENT = Registry.register(
				Registries.ENCHANTMENT,
				DimensionalDoors.id("string_theory"),
				new StringTheoryEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.WEARABLE, new EquipmentSlot[] {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD})
		);
	}
}
