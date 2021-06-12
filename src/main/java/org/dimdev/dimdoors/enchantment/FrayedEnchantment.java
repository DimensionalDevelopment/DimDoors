package org.dimdev.dimdoors.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class FrayedEnchantment extends Enchantment {
	public FrayedEnchantment(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
		super(weight, type, slotTypes);
	}
	@Override
	public int getMinPower(int level) {
		return 10000;
	}
	@Override
	public int getMaxLevel() {
		return 1;
	}
	@Override
	public boolean isCursed(){
		return true;
	}

}
