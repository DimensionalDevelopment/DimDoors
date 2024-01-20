package org.dimdev.dimdoors.item;

import dev.architectury.core.item.ArchitecturyBucketItem;
import dev.architectury.core.item.ArchitecturyRecordItem;
import dev.architectury.core.item.ArchitecturySpawnEggItem;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.sound.ModSoundEvents;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

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

	public static final RegistrySupplier<Item> WORLD_THREAD = registerRegular("world_thread", Item::new);

	public static final RegistrySupplier<Item> INFRANGIBLE_FIBER = registerRegular("infrangible_fiber", properties -> new Item(properties.fireResistant()));

	public static final RegistrySupplier<Item> FRAYED_FILAMENTS = registerRegular("frayed_filament", Item::new);

	public static final RegistrySupplier<Item> RIFT_CONFIGURATION_TOOL = register("rift_configuration_tool", RiftConfigurationToolItem::new);

	public static final RegistrySupplier<Item> RIFT_BLADE = registerRegular("rift_blade", properties -> new RiftBladeItem(properties.durability(100)));

	public static final RegistrySupplier<Item> RIFT_REMOVER = registerRegular("rift_remover", properties -> new RiftRemoverItem(properties.stacksTo(1).durability(100)));

	public static final RegistrySupplier<Item> RIFT_SIGNATURE = registerRegular("rift_signature", properties -> new RiftSignatureItem(properties.stacksTo(1).durability(1),true));

	public static final RegistrySupplier<Item> STABILIZED_RIFT_SIGNATURE = registerRegular("stabilized_rift_signature", properties -> new StabilizedRiftSignatureItem(properties.stacksTo(1).durability(20)));

	public static final RegistrySupplier<Item> RIFT_STABILIZER = registerRegular("rift_stabilizer", properties -> new RiftStabilizerItem(properties.stacksTo(1).durability(6)));

	public static final RegistrySupplier<Item> RIFT_KEY = registerRegular("rift_key", properties -> new RiftKeyItem(properties.fireResistant().stacksTo(1)));

	public static final RegistrySupplier<Item> DIMENSIONAL_ERASER = registerRegular("dimensional_eraser", properties -> new DimensionalEraserItem(properties.durability(100)));

	public static final RegistrySupplier<Item> MONOLITH_SPAWNER = registerRegular("monolith_spawner", properties -> new ArchitecturySpawnEggItem(ModEntityTypes.MONOLITH, 0xffffff, 0xffffff, properties));

	public static final RegistrySupplier<Item> WORLD_THREAD_HELMET = registerRegular("world_thread_helmet", properties -> new ArmorItem(ModArmorMaterials.WORLD_THREAD, ArmorItem.Type.HELMET, properties));

	public static final RegistrySupplier<Item> WORLD_THREAD_CHESTPLATE = registerRegular("world_thread_chestplate", properties -> new ArmorItem(ModArmorMaterials.WORLD_THREAD, ArmorItem.Type.CHESTPLATE, properties));

	public static final RegistrySupplier<Item> WORLD_THREAD_LEGGINGS = registerRegular("world_thread_leggings", properties -> new ArmorItem(ModArmorMaterials.WORLD_THREAD, ArmorItem.Type.LEGGINGS, properties));

	public static final RegistrySupplier<Item> WORLD_THREAD_BOOTS = registerRegular("world_thread_boots", properties -> new ArmorItem(ModArmorMaterials.WORLD_THREAD, ArmorItem.Type.BOOTS, properties));

	public static final RegistrySupplier<Item> MASK_WAND = registerRegular("mask_wand", properties -> new MaskWandItem(properties.stacksTo(100)));

	public static final RegistrySupplier<Item> STABLE_FABRIC = registerRegular("stable_fabric", Item::new);

	public static final RegistrySupplier<Item> CREEPY_RECORD = registerRegular("creepy_record", properties -> new ArchitecturyRecordItem(10, ModSoundEvents.CREEPY, properties, 317));

	public static final RegistrySupplier<Item> WHITE_VOID_RECORD = registerRegular("white_void_record", properties -> new ArchitecturyRecordItem(10, ModSoundEvents.WHITE_VOID, properties, 225));

	public static final RegistrySupplier<Item> ETERNAL_FLUID_BUCKET = registerRegular("eternal_fluid_bucket", properties -> new ArchitecturyBucketItem(ModFluids.ETERNAL_FLUID, properties.craftRemainder(Items.BUCKET).stacksTo(1)));
	public static final RegistrySupplier<Item> LEAK_BUCKET = registerDecay("leak_bucket", properties -> new ArchitecturyBucketItem(ModFluids.LEAK, properties.craftRemainder(Items.BUCKET).stacksTo(1)));

	public static final RegistrySupplier<Item> MASK_SHARD = registerRegular("mask_shard", Item::new);

	public static final RegistrySupplier<Item> FUZZY_FIREBALL = registerRegular("fuzzy_fireball", Item::new);

	public static final RegistrySupplier<Item> FABRIC_OF_FINALITY = registerRegular("fabric_of_finality", Item::new);

	public static final RegistrySupplier<Item> LIMINAL_LINT = registerRegular("liminal_lint", Item::new);

	public static final RegistrySupplier<Item> ENDURING_FIBERS = registerRegular("enduring_fibers", Item::new);

	public static final RegistrySupplier<Item> RIFT_PEARL = registerRegular("rift_pearl", Item::new);

	public static final RegistrySupplier<Item> AMALGAM_LUMP = registerDecay("amalgam_lump", Item::new);

	public static final RegistrySupplier<Item> CLOD = registerDecay("clod", Item::new);

	public static final RegistrySupplier<Item> GARMENT_OF_REALITY_HELMET = registerRegular("garment_of_reality_helmet", properties -> new ArmorItem(ModArmorMaterials.GARMENT_OF_REALITY, ArmorItem.Type.HELMET, properties));

	public static final RegistrySupplier<Item> GARMENT_OF_REALITY_CHESTPLATE = registerRegular("garment_of_reality_chestplate", properties -> new ArmorItem(ModArmorMaterials.GARMENT_OF_REALITY, ArmorItem.Type.CHESTPLATE, properties));

	public static final RegistrySupplier<Item> GARMENT_OF_REALITY_LEGGINGS = registerRegular("garment_of_reality_leggings", properties -> new ArmorItem(ModArmorMaterials.GARMENT_OF_REALITY, ArmorItem.Type.LEGGINGS, properties));

	public static final RegistrySupplier<Item> GARMENT_OF_REALITY_BOOTS = registerRegular("garment_of_reality_boots", properties -> new ArmorItem(ModArmorMaterials.GARMENT_OF_REALITY, ArmorItem.Type.BOOTS, properties));
	
	public static final Set<Item> DOOR_ITEMS = new HashSet<>();

	public static DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.CREATIVE_MODE_TAB);
	public static final RegistrySupplier<CreativeModeTab> DIMENSIONAL_DOORS = CREATIVE_TABS.register("dimensional_doors", () -> CreativeTabRegistry.create(builder -> builder.icon(() -> new ItemStack(ModItems.RIFT_BLADE.get())).title(Component.translatable("itemGroup.dimdoors.dimensional_doors"))));
	public static final RegistrySupplier<CreativeModeTab> DECAY = CREATIVE_TABS.register("decay", () -> CreativeTabRegistry.create(builder -> builder.icon(() -> new ItemStack(ModBlocks.UNRAVELED_FENCE.get())).title(Component.translatable("itemGroup.dimdoors.decay"))));

	public static RegistrySupplier<Item> registerRegular(String name, Function<Item.Properties, Item> item) {
		return register(name, () -> item.apply(new Item.Properties().arch$tab(DIMENSIONAL_DOORS)));
	}

	public static RegistrySupplier<Item> registerDecay(String name, Function<Item.Properties, Item> item) {
		return register(name, () -> item.apply(new Item.Properties().arch$tab(DECAY)));
	}

	public static RegistrySupplier<Item> register(String name, Supplier<Item> item) {
		return REGISTRY.register(name, item);
	}

	public static void init() {
		CREATIVE_TABS.register();
		REGISTRY.register();
	}
}
