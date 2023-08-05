package org.dimdev.dimdoors.item;

import dev.architectury.core.item.ArchitecturyBucketItem;
import dev.architectury.core.item.ArchitecturyRecordItem;
import dev.architectury.core.item.ArchitecturySpawnEggItem;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.item.door.DimensionalDoorItem;
import org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar;
import org.dimdev.dimdoors.rift.targets.RandomTarget;
import org.dimdev.dimdoors.rift.targets.UnstableTarget;
import org.dimdev.dimdoors.sound.ModSoundEvents;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.dimdev.dimdoors.DimensionalDoors.id;

public final class ModItems {
	// DO NOT REMOVE!!!
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.ITEM);
//
//	public static final RegistrySupplier<Item> OAK_DIMENSIONAL_TRAPDOOR = register("wood_dimensional_trapdoor", properties -> new DimensionalTrapdoorItem(
//			ModBlocks.OAK_DIMENSIONAL_TRAPDOOR.get(),
//			properties.stacksTo(1),
//			rift -> rift.setDestination(
//					RandomTarget.builder()
//							.acceptedGroups(Collections.singleton(0))
//							.coordFactor(1)
//							.negativeDepthFactor(80)
//							.positiveDepthFactor(Double.MAX_VALUE)
//							.weightMaximum(100)
//							.noLink(false)
//							.newRiftWeight(0)
//							.build())
//	));

	public static final RegistrySupplier<Item> WORLD_THREAD = register("world_thread", Item::new);

	public static final RegistrySupplier<Item> INFRANGIBLE_FIBER = register("infrangible_fiber", properties -> new Item(properties.fireResistant()));

	public static final RegistrySupplier<Item> FRAYED_FILAMENTS = register("frayed_filament", Item::new);

	public static final RegistrySupplier<Item> RIFT_CONFIGURATION_TOOL = register("rift_configuration_tool", RiftConfigurationToolItem::new);

	public static final RegistrySupplier<Item> RIFT_BLADE = register("rift_blade", properties -> new RiftBladeItem(properties.durability(100)));

	public static final RegistrySupplier<Item> RIFT_REMOVER = register("rift_remover", properties -> new RiftRemoverItem(properties.stacksTo(1).durability(100)));

	public static final RegistrySupplier<Item> RIFT_SIGNATURE = register("rift_signature", properties -> new RiftSignatureItem(properties.stacksTo(1).durability(1)));

	public static final RegistrySupplier<Item> STABILIZED_RIFT_SIGNATURE = register("stabilized_rift_signature", properties -> new StabilizedRiftSignatureItem(properties.stacksTo(1).durability(20)));

	public static final RegistrySupplier<Item> RIFT_STABILIZER = register("rift_stabilizer", properties -> new RiftStabilizerItem(properties.stacksTo(1).durability(6)));

	public static final RegistrySupplier<Item> RIFT_KEY = register("rift_key", properties -> new RiftKeyItem(properties.fireResistant().stacksTo(1)));

	public static final RegistrySupplier<Item> DIMENSIONAL_ERASER = register("dimensional_eraser", properties -> new DimensionalEraserItem(properties.durability(100)));

	public static final RegistrySupplier<Item> MONOLITH_SPAWNER = register("monolith_spawner", properties -> new ArchitecturySpawnEggItem(ModEntityTypes.MONOLITH, 0xffffff, 0xffffff, properties));

	public static final RegistrySupplier<Item> WORLD_THREAD_HELMET = register("world_thread_helmet", properties -> new ArmorItem(ModArmorMaterials.WORLD_THREAD, ArmorItem.Type.HELMET, properties));

	public static final RegistrySupplier<Item> WORLD_THREAD_CHESTPLATE = register("world_thread_chestplate", properties -> new ArmorItem(ModArmorMaterials.WORLD_THREAD, ArmorItem.Type.CHESTPLATE, properties));

	public static final RegistrySupplier<Item> WORLD_THREAD_LEGGINGS = register("world_thread_leggings", properties -> new ArmorItem(ModArmorMaterials.WORLD_THREAD, ArmorItem.Type.LEGGINGS, properties));

	public static final RegistrySupplier<Item> WORLD_THREAD_BOOTS = register("world_thread_boots", properties -> new ArmorItem(ModArmorMaterials.WORLD_THREAD, ArmorItem.Type.BOOTS, properties));

	public static final RegistrySupplier<Item> MASK_WAND = register("mask_wand", properties -> new MaskWandItem(properties.stacksTo(100)));

	public static final RegistrySupplier<Item> STABLE_FABRIC = register("stable_fabric", Item::new);

	public static final RegistrySupplier<Item> CREEPY_RECORD = register("creepy_record", properties -> new ArchitecturyRecordItem(10, ModSoundEvents.CREEPY, properties, 317));

	public static final RegistrySupplier<Item> WHITE_VOID_RECORD = register("white_void_record", properties -> new ArchitecturyRecordItem(10, ModSoundEvents.WHITE_VOID, properties, 225));

	public static final RegistrySupplier<Item> ETERNAL_FLUID_BUCKET = register("eternal_fluid_bucket", properties -> new ArchitecturyBucketItem(ModFluids.ETERNAL_FLUID, properties.craftRemainder(Items.BUCKET).stacksTo(1)));

	public static final RegistrySupplier<Item> MASK_SHARD = register("mask_shard", Item::new);

	public static final RegistrySupplier<Item> FUZZY_FIREBALL = register("fuzzy_fireball", Item::new);

	public static final RegistrySupplier<Item> FABRIC_OF_FINALITY = register("fabric_of_finality", Item::new);

	public static final RegistrySupplier<Item> LIMINAL_LINT = register("liminal_lint", Item::new);

	public static final RegistrySupplier<Item> ENDURING_FIBERS = register("enduring_fibers", Item::new);

	public static final RegistrySupplier<Item> RIFT_PEARL = register("rift_pearl", Item::new);

	public static final RegistrySupplier<Item> AMALGAM_LUMP = register("amalgam_lump", Item::new);

	public static final RegistrySupplier<Item> CLOD = register("clod", Item::new);

	public static final RegistrySupplier<Item> GARMENT_OF_REALITY_HELMET = register("garment_of_reality_helmet", properties -> new ArmorItem(ModArmorMaterials.GARMENT_OF_REALITY, ArmorItem.Type.HELMET, properties));

	public static final RegistrySupplier<Item> GARMENT_OF_REALITY_CHESTPLATE = register("garment_of_reality_chestplate", properties -> new ArmorItem(ModArmorMaterials.GARMENT_OF_REALITY, ArmorItem.Type.CHESTPLATE, properties));

	public static final RegistrySupplier<Item> GARMENT_OF_REALITY_LEGGINGS = register("garment_of_reality_leggings", properties -> new ArmorItem(ModArmorMaterials.GARMENT_OF_REALITY, ArmorItem.Type.LEGGINGS, properties));

	public static final RegistrySupplier<Item> GARMENT_OF_REALITY_BOOTS = register("garment_of_reality_boots", properties -> new ArmorItem(ModArmorMaterials.GARMENT_OF_REALITY, ArmorItem.Type.BOOTS, properties));
	
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
