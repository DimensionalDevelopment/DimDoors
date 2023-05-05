package org.dimdev.dimdoors.item;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import dev.architectury.core.item.ArchitecturyBucketItem;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.matrix.Matrix;
import org.dimdev.matrix.Registrar;
import org.dimdev.matrix.RegistryEntry;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;

import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.item.door.DimensionalTrapdoorItem;
import org.dimdev.dimdoors.rift.targets.RandomTarget;
import org.dimdev.dimdoors.sound.ModSoundEvents;

import static org.dimdev.dimdoors.DimensionalDoors.id;

public final class ModItems {
	// DO NOT REMOVE!!!
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.ITEM);

	public static final RegistrySupplier<Item> OAK_DIMENSIONAL_TRAPDOOR = register("wood_dimensional_trapdoor", () -> new DimensionalTrapdoorItem(
			ModBlocks.OAK_DIMENSIONAL_TRAPDOOR.get(),
			new Item.Properties().stacksTo(1),
			rift -> rift.setDestination(
					RandomTarget.builder()
							.acceptedGroups(Collections.singleton(0))
							.coordFactor(1)
							.negativeDepthFactor(80)
							.positiveDepthFactor(Double.MAX_VALUE)
							.weightMaximum(100)
							.noLink(false)
							.newRiftWeight(0)
							.build())
	));

	public static final RegistrySupplier<Item> WORLD_THREAD = register("world_thread", () -> new Item(new Item.Properties()));

	public static final RegistrySupplier<Item> INFRANGIBLE_FIBER = register("infrangible_fiber", () -> new Item(new Item.Properties()));

	public static final RegistrySupplier<Item> FRAYED_FILAMENTS = register("frayed_filament", () -> new Item(new Item.Properties()));

	public static final RegistrySupplier<Item> RIFT_CONFIGURATION_TOOL = register("rift_configuration_tool", RiftConfigurationToolItem::new);

	public static final RegistrySupplier<Item> RIFT_BLADE = register("rift_blade", () -> new RiftBladeItem(new Item.Properties().durability(100)));

	public static final RegistrySupplier<Item> RIFT_REMOVER = register("rift_remover", () -> new RiftRemoverItem(new Item.Properties().stacksTo(1).durability(100)));

	public static final RegistrySupplier<Item> RIFT_SIGNATURE = register("rift_signature", () -> new RiftSignatureItem(new Item.Properties().stacksTo(1).durability(1)));

	public static final RegistrySupplier<Item> STABILIZED_RIFT_SIGNATURE = register("stabilized_rift_signature", () -> new StabilizedRiftSignatureItem(new Item.Properties().stacksTo(1).durability(20)));

	public static final RegistrySupplier<Item> RIFT_STABILIZER = register("rift_stabilizer", () -> new RiftStabilizerItem(new Item.Properties().stacksTo(1).durability(6)));

	public static final RegistrySupplier<Item> RIFT_KEY = register("rift_key", () -> new RiftKeyItem(new Item.Properties().fireResistant().stacksTo(1)));

	public static final RegistrySupplier<Item> DIMENSIONAL_ERASER = register("dimensional_eraser", () -> new DimensionalEraserItem(new Item.Properties().durability(100)));

	public static final RegistrySupplier<Item> MONOLITH_SPAWNER = register("monolith_spawner", () -> new SpawnEggItem(ModEntityTypes.MONOLITH, 0xffffff, 0xffffff, new Item.Properties());

	public static final RegistrySupplier<Item> WORLD_THREAD_HELMET = register("world_thread_helmet", () -> new ArmorItem(ModArmorMaterials.WORLD_THREAD, ArmorItem.Type.HELMET, new Item.Properties()));

	public static final RegistrySupplier<Item> WORLD_THREAD_CHESTPLATE = register("world_thread_chestplate", () -> new ArmorItem(ModArmorMaterials.WORLD_THREAD, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

	public static final RegistrySupplier<Item> WORLD_THREAD_LEGGINGS = register("world_thread_leggings", () -> new ArmorItem(ModArmorMaterials.WORLD_THREAD, ArmorItem.Type.LEGGINGS, new Item.Properties()));

	public static final RegistrySupplier<Item> WORLD_THREAD_BOOTS = register("world_thread_boots", () -> new ArmorItem(ModArmorMaterials.WORLD_THREAD, ArmorItem.Type.BOOTS, new Item.Properties()));

	public static final RegistrySupplier<Item> MASK_WAND = register("mask_wand", () -> new MaskWandItem(new Item.Properties().stacksTo(100)/**/));

	public static final RegistrySupplier<Item> STABLE_FABRIC = register("stable_fabric", () -> new Item(new Item.Properties()));

	public static final RegistrySupplier<Item> CREEPY_RECORD = register("creepy_record", () -> new RecordItem(10, ModSoundEvents.CREEPY.get(), new Item.Properties(), 317));

	public static final RegistrySupplier<Item> WHITE_VOID_RECORD = register("white_void_record", () -> new RecordItem(10, ModSoundEvents.WHITE_VOID.get(), new Item.Properties(), 225));

	public static final Item ETERNAL_FLUID_BUCKET = register("eternal_fluid_bucket", () -> new ArchitecturyBucketItem(ModFluids.ETERNAL_FLUID, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

	public static final RegistrySupplier<Item> MASK_SHARD = register("mask_shard", () -> new Item(new Item.Properties()));

	public static final RegistrySupplier<Item> FUZZY_FIREBALL = register("fuzzy_fireball", () -> new Item(new Item.Properties()));

	public static final RegistrySupplier<Item> FABRIC_OF_FINALITY = register("fabric_of_finality", () -> new Item(new Item.Properties()));

	public static final RegistrySupplier<Item> LIMINAL_LINT = register("liminal_lint", () -> new Item(new Item.Properties()));

	public static final RegistrySupplier<Item> ENDURING_FIBERS = register("enduring_fibers", () -> new Item(new Item.Properties()));

	public static final RegistrySupplier<Item> RIFT_PEARL = register("rift_pearl", () -> new Item(new Item.Properties()));

	public static final RegistrySupplier<Item> FABRIC_OF_REALITY = register("fabric_of_reality", () -> new Item(new Item.Properties()));

	public static final RegistrySupplier<Item> AMALGAM_LUMP = register("amalgam_lump", () -> new Item(new Item.Properties()));

	public static final RegistrySupplier<Item> CLOD = register("clod", () -> new Item(new Item.Properties()));

	public static final RegistrySupplier<Item> GARMENT_OF_REALITY_HELMET = register("garment_of_reality_helmet", () -> new ArmorItem(ModArmorMaterials.GARMENT_OF_REALITY, ArmorItem.Type.HELMET, new Item.Properties()));

	public static final RegistrySupplier<Item> GARMENT_OF_REALITY_CHESTPLATE = register("garment_of_reality_chestplate", () -> new ArmorItem(ModArmorMaterials.GARMENT_OF_REALITY, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

	public static final RegistrySupplier<Item> GARMENT_OF_REALITY_LEGGINGS = register("garment_of_reality_leggings", () -> new ArmorItem(ModArmorMaterials.GARMENT_OF_REALITY, ArmorItem.Type.LEGGINGS, new Item.Properties()));

	public static final RegistrySupplier<Item> GARMENT_OF_REALITY_BOOTS = register("garment_of_reality_boots", () -> new ArmorItem(ModArmorMaterials.GARMENT_OF_REALITY, ArmorItem.Type.BOOTS, new Item.Properties()));
	
	public static final Set<Item> DOOR_ITEMS = new HashSet<>();

	public static final CreativeTabRegistry.TabSupplier DIMENSIONAL_DOORS = CreativeTabRegistry.create(id("dimensional_doors"), () -> new ItemStack(ModItems.RIFT_BLADE.get()));

	public static RegistrySupplier<Item> register(String name, Function<Item.Properties, Item> item) {
		return register(name, () -> item.apply(new Item.Properties().arch$tab(DIMENSIONAL_DOORS)));
	}
	public static RegistrySupplier<Item> register(String name, Supplier<Item> item) {
		return REGISTRY.register(name, item);
	}

	public static void init() {
		REGISTRY.register();
	}
}
