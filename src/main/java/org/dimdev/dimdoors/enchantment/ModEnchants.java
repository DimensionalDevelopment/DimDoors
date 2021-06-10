package org.dimdev.dimdoors.enchantment;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.dimdev.matrix.Registrar;
import org.dimdev.matrix.RegistryEntry;

public class ModEnchants {
	public static Enchantment FRAYED_ENCHAMENT;
	public static void init() {
		FRAYED_ENCHAMENT = Registry.register(
				Registry.ENCHANTMENT,
				new Identifier("dimdoors", "frayed"),
				new FrayedEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND, EquipmentSlot.CHEST, EquipmentSlot.FEET, EquipmentSlot.HEAD, EquipmentSlot.LEGS})
		);
	}
}
