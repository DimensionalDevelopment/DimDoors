package org.dimdev.dimdoors.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class StringTheoryEnchantment extends Enchantment {
	public StringTheoryEnchantment(Rarity weight, EnchantmentCategory type, EquipmentSlot[] slotTypes) {
		super(weight, type, slotTypes);
	}

	@Override
	public int getMinCost(int level) {
		return 10000;
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	public boolean isTreasureOnly() {
		return true;
	}
}
