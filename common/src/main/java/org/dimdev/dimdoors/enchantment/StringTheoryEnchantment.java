package org.dimdev.dimdoors.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;

public class StringTheoryEnchantment extends Enchantment {
	public StringTheoryEnchantment(EnchantmentDefinition definition) {
		super(definition);
	}

	public boolean isTreasureOnly() {
		return true;
	}
}
