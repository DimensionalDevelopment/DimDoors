package org.dimdev.dimdoors.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public enum ModArmorMaterials implements ArmorMaterial {
	WORLD_THREAD(5, 15, SoundEvents.ARMOR_EQUIP_LEATHER, new LazyLoadedValue<>(() -> Ingredient.of(ModItems.WORLD_THREAD.get())), "world_thread", new int[]{1, 2, 3, 1}, 0.0F, 0.0F),
	GARMENT_OF_REALITY(5, 15, SoundEvents.ARMOR_EQUIP_LEATHER, new LazyLoadedValue<>(() -> Ingredient.of(ModItems.INFRANGIBLE_FIBER.get())), "garment_of_reality", new int[]{1, 2, 3, 1}, 0.0F, 0.0F); //TODO: DEFINE TRAITS

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
	public int getDurabilityForType(ArmorItem.Type type) {
		return BASE_DURABILITY[type.getSlot().ordinal()] * this.durabilityMultiplier;
	}


	@Override
	public int getDefenseForType(ArmorItem.Type type) {
		return this.protectionAmounts[type.getSlot().ordinal()];
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
