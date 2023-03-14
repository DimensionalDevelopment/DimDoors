package org.dimdev.dimdoors.enchantment;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import org.dimdev.dimdoors.Constants;

public class ModEnchants {
	private static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Constants.MODID);

	public static RegistryObject<StringTheoryEnchantment> STRING_THEORY_ENCHANTMENT = ENCHANTMENTS.register("string_theory", () -> new StringTheoryEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.WEARABLE, new EquipmentSlot[] {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD}));

	public static void init(IEventBus bus) {
		ENCHANTMENTS.register(bus);
	}
}
