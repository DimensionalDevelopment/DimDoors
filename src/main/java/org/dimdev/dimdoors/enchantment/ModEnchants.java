package org.dimdev.dimdoors.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEnchants {
	public static Enchantment FRAYED_ENCHANTMENT;
	public static Enchantment STRING_THEORY_ENCHANTMENT;
	public static void init() {
		FRAYED_ENCHANTMENT = Registry.register(
				Registry.ENCHANTMENT,
				new Identifier("dimdoors", "frayed"),
				new FrayedEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND, EquipmentSlot.CHEST, EquipmentSlot.FEET, EquipmentSlot.HEAD, EquipmentSlot.LEGS})
		);
		STRING_THEORY_ENCHANTMENT = Registry.register(
				Registry.ENCHANTMENT,
				new Identifier("dimdoors", "string_theory"),
				new StringTheoryEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.WEARABLE, new EquipmentSlot[] {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD})
		);
	}
}
