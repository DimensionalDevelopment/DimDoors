package org.dimdev.dimdoors.item;

import dev.architectury.registry.registries.DeferredRegister;
import kroppeb.stareval.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import org.dimdev.dimdoors.DimensionalDoors;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class ModArmorMaterials {

	public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.ARMOR_MATERIAL);
	public static final Holder<ArmorMaterial> WORLD_THREAD = register("world_thread", 5, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.of(ModItems.WORLD_THREAD.get()));
	public static final Holder<ArmorMaterial> GARMENT_OF_REALITY = register("garment_of_reality", 5, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.of(ModItems.INFRANGIBLE_FIBER.get())); //TODO: DEFINE TRAITS

	public static void init() {
		ARMOR_MATERIALS.register();
	}

	private static Holder<ArmorMaterial> register(String name, int durabilityMultiplier, int enchantmentValue, Holder<SoundEvent> equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
		List<ArmorMaterial.Layer> list = List.of(new ArmorMaterial.Layer(ResourceLocation.withDefaultNamespace(name)));
		return register(name, durabilityMultiplier, enchantmentValue, equipSound, toughness, knockbackResistance, repairIngredient, list);
	}

	private static Holder<ArmorMaterial> register(String name, int durabilityMultiplier, int enchantmentValue, Holder<SoundEvent> equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngridient, List<ArmorMaterial.Layer> layers) {
		var defense = Util.make(new EnumMap<ArmorItem.Type, Integer>(ArmorItem.Type.class), map -> {
			map.put(ArmorItem.Type.HELMET, 13 * durabilityMultiplier);
			map.put(ArmorItem.Type.CHESTPLATE, 16 * durabilityMultiplier);
			map.put(ArmorItem.Type.LEGGINGS, 15 * durabilityMultiplier);
			map.put(ArmorItem.Type.BOOTS, 11 * durabilityMultiplier);
		});


		EnumMap<ArmorItem.Type, Integer> enumMap = new EnumMap<>(ArmorItem.Type.class);

		for (ArmorItem.Type type : ArmorItem.Type.values()) {
			enumMap.put(type, defense.get(type));
		}

		return ARMOR_MATERIALS.register(name, () -> new ArmorMaterial(enumMap, enchantmentValue, equipSound, repairIngridient, layers, toughness, knockbackResistance));
	}
}
