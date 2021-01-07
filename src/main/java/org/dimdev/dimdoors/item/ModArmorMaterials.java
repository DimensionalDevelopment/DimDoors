package org.dimdev.dimdoors.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Lazy;

public enum ModArmorMaterials implements ArmorMaterial {
	WORLD_THREAD(5, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, new Lazy<>(() -> Ingredient.ofItems(ModItems.WORLD_THREAD)), "world_thread", new int[]{1, 2, 3, 1}, 0.0F, 0.0F);

	private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
	private final int durabilityMultiplier;
	private final int enchantability;
	private final SoundEvent equipSound;
	private final Lazy<Ingredient> repairIngredient;
	private final String name;
	private final int[] protectionAmounts;
	private final float toughness;
	private final float knockbackResistance;

	ModArmorMaterials(int durabilityMultiplier, int enchantability, SoundEvent equipSound, Lazy<Ingredient> repairIngredient, String name, int[] protectionAmounts, float toughness, float knockbackResistance) {
		this.durabilityMultiplier = durabilityMultiplier;
		this.enchantability = enchantability;
		this.equipSound = equipSound;
		this.repairIngredient = repairIngredient;
		this.name = name;
		this.protectionAmounts = protectionAmounts;
		this.toughness = toughness;
		this.knockbackResistance = knockbackResistance;
	}

	@Override
	public int getDurability(EquipmentSlot slot) {
		return BASE_DURABILITY[slot.getEntitySlotId()] * this.durabilityMultiplier;
	}

	@Override
	public int getProtectionAmount(EquipmentSlot slot) {
		return this.protectionAmounts[slot.getEntitySlotId()];
	}

	@Override
	public int getEnchantability() {
		return this.enchantability;
	}

	@Override
	public SoundEvent getEquipSound() {
		return this.equipSound;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return this.repairIngredient.get();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public float getToughness() {
		return this.toughness;
	}

	@Override
	public float getKnockbackResistance() {
		return this.knockbackResistance;
	}
}
