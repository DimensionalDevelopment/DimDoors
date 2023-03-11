package org.dimdev.dimdoors.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public enum ModArmorMaterials implements ArmorMaterial {
	WORLD_THREAD(5, 15, SoundEvents.ARMOR_EQUIP_LEATHER, new LazyLoadedValue<>(() -> Ingredient.of(ModItems.WORLD_THREAD)), "world_thread", new int[]{1, 2, 3, 1}, 0.0F, 0.0F);

	private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
	private final int durabilityMultiplier;
	private final int enchantability;
	private final SoundEvent equipSound;
	private final LazyLoadedValue<Ingredient> repairIngredient;
	private final String name;
	private final int[] protectionAmounts;
	private final float toughness;
	private final float knockbackResistance;

	ModArmorMaterials(int durabilityMultiplier, int enchantability, SoundEvent equipSound, LazyLoadedValue<Ingredient> repairIngredient, String name, int[] protectionAmounts, float toughness, float knockbackResistance) {
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
	public int getDurabilityForSlot(EquipmentSlot slot) {
		return BASE_DURABILITY[slot.getIndex()] * this.durabilityMultiplier;
	}

	@Override
	public int getDefenseForSlot(EquipmentSlot slot) {
		return this.protectionAmounts[slot.getIndex()];
	}

	@Override
	public int getEnchantmentValue() {
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
