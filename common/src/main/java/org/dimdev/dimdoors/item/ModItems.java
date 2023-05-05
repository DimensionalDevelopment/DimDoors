package org.dimdev.dimdoors.item;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

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

@SuppressWarnings("unused")
@Registrar(element = Item.class, modid = "dimdoors")
public final class ModItems {
	// DO NOT REMOVE!!!
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.ITEM);

	public static final RegistrySupplier<Item> STONE_PLAYER = registerWithoutTab("stone_player", ModBlocks.STONE_PLAYER);

	@RegistryEntry("quartz_door") public static final Item QUARTZ_DOOR = create(ModBlocks.QUARTZ_DOOR);

	@RegistryEntry("gold_door") public static final Item GOLD_DOOR = create(ModBlocks.GOLD_DOOR);

	@RegistryEntry("stone_door") public static final Item STONE_DOOR = create(ModBlocks.STONE_DOOR);

	@RegistryEntry("wood_dimensional_trapdoor") public static final Item OAK_DIMENSIONAL_TRAPDOOR = create(new DimensionalTrapdoorItem(
			ModBlocks.OAK_DIMENSIONAL_TRAPDOOR,
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

	public static final RegistrySupplier<Item> WORLD_THREAD = create("world_thread", () -> new Item(new Item.Properties()));

	public static final RegistrySupplier<Item> INFRANGIBLE_FIBER = create("infrangible_fiber", () -> new Item(new Item.Properties()));

	public static final RegistrySupplier<Item> FRAYED_FILAMENTS = create("frayed_filament", () -> new Item(new Item.Properties()));

	public static final RegistrySupplier<Item> RIFT_CONFIGURATION_TOOL = create("rift_configuration_tool", () -> new RiftConfigurationToolItem());

	public static final RegistrySupplier<Item> RIFT_BLADE = create("rift_blade", () -> new RiftBladeItem(new Item.Properties().durability(100)));

	public static final RegistrySupplier<Item> RIFT_REMOVER = create("rift_remover", () -> new RiftRemoverItem(new Item.Properties().stacksTo(1).durability(100)));

	public static final RegistrySupplier<Item> RIFT_SIGNATURE = create("rift_signature", () -> new RiftSignatureItem(new Item.Properties().stacksTo(1).maxDamage(1)));

	public static final RegistrySupplier<Item> STABILIZED_RIFT_SIGNATURE = create("stabilized_rift_signature", () -> new StabilizedRiftSignatureItem(new Item.Properties().stacksTo(1).maxDamage(20)));

	public static final RegistrySupplier<Item> RIFT_STABILIZER = create("rift_stabilizer", () -> new RiftStabilizerItem(new Item.Properties().stacksTo(1).maxDamage(6)));

	public static final RegistrySupplier<Item> RIFT_KEY = create("rift_key", () -> new RiftKeyItem(new Item.Properties().fireproof().stacksTo(1)));

	public static final RegistrySupplier<Item> DIMENSIONAL_ERASER = create("dimensional_eraser", () -> new DimensionalEraserItem(new Item.Properties().maxDamage(100)));

	public static final RegistrySupplier<Item> MONOLITH_SPAWNER = new "monolith_spawner", () -> SpawnEggItem(ModEntityTypes.MONOLITH, 0xffffff, 0xffffff, new Item.Properties());

	public static final RegistrySupplier<Item> WORLD_THREAD_HELMET = create("world_thread_helmet", () -> new ArmorItem(ArmorItem(ModArmorMaterials.WORLD_THREAD, ArmorItem.Type.HELMET, new Item.Properties()));

	public static final RegistrySupplier<Item> WORLD_THREAD_CHESTPLATE = create("world_thread_chestplate", () -> new ArmorItem(ModArmorMaterials.WORLD_THREAD, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

	public static final RegistrySupplier<Item> WORLD_THREAD_LEGGINGS = create("world_thread_leggings", () -> new ArmorItem(ModArmorMaterials.WORLD_THREAD, ArmorItem.Type.LEGGINGS, new Item.Properties()));

	public static final RegistrySupplier<Item> WORLD_THREAD_BOOTS = create("world_thread_boots", () -> new ArmorItem(ModArmorMaterials.WORLD_THREAD, ArmorItem.Type.BOOTS, new Item.Properties()));

	public static final RegistrySupplier<Item> MASK_WAND = create("mask_wand", () -> new MaskWandItem(new Item.Properties().stacksTo(100)/**/));

	public static final RegistrySupplier<Item> STABLE_FABRIC = create("stable_fabric", () -> new Item(new Item.Properties()));

	public static final RegistrySupplier<Item> WHITE_FABRIC = create("white_fabric", () -> ModBlocks.WHITE_FABRIC);

	public static final RegistrySupplier<Item> ORANGE_FABRIC = create("orange_fabric", () -> ModBlocks.ORANGE_FABRIC);

	public static final RegistrySupplier<Item> MAGENTA_FABRIC = create("magenta_fabric", () -> ModBlocks.MAGENTA_FABRIC);

	public static final RegistrySupplier<Item> LIGHT_BLUE_FABRIC = create("light_blue_fabric", () -> ModBlocks.LIGHT_BLUE_FABRIC);

	public static final RegistrySupplier<Item> YELLOW_FABRIC = create("yellow_fabric", () -> ModBlocks.YELLOW_FABRIC);

	public static final RegistrySupplier<Item> LIME_FABRIC = create("lime_fabric", () -> ModBlocks.LIME_FABRIC);

	public static final RegistrySupplier<Item> PINK_FABRIC = create("pink_fabric", () -> ModBlocks.PINK_FABRIC);

	public static final RegistrySupplier<Item> GRAY_FABRIC = create("gray_fabric", () -> ModBlocks.GRAY_FABRIC);

	public static final RegistrySupplier<Item> LIGHT_GRAY_FABRIC = create("light_gray_fabric", () -> ModBlocks.LIGHT_GRAY_FABRIC);

	public static final RegistrySupplier<Item> CYAN_FABRIC = create("cyan_fabric", () -> ModBlocks.CYAN_FABRIC);

	public static final RegistrySupplier<Item> PURPLE_FABRIC = create("purple_fabric", () -> ModBlocks.PURPLE_FABRIC);

	public static final RegistrySupplier<Item> BLUE_FABRIC = create("blue_fabric", () -> ModBlocks.BLUE_FABRIC);

	public static final RegistrySupplier<Item> BROWN_FABRIC = create("brown_fabric", () -> ModBlocks.BROWN_FABRIC);

	public static final RegistrySupplier<Item> GREEN_FABRIC = create("green_fabric", () -> ModBlocks.GREEN_FABRIC);

	public static final RegistrySupplier<Item> RED_FABRIC = create("red_fabric", () -> ModBlocks.RED_FABRIC);

	public static final RegistrySupplier<Item> BLACK_FABRIC = create("black_fabric", () -> ModBlocks.BLACK_FABRIC);

	public static final RegistrySupplier<Item> WHITE_ANCIENT_FABRIC = create("white_ancient_fabric", () -> ModBlocks.WHITE_ANCIENT_FABRIC);

	public static final RegistrySupplier<Item> ORANGE_ANCIENT_FABRIC = create("orange_ancient_fabric", () -> ModBlocks.ORANGE_ANCIENT_FABRIC);

	public static final RegistrySupplier<Item> MAGENTA_ANCIENT_FABRIC = create("magenta_ancient_fabric", () -> ModBlocks.MAGENTA_ANCIENT_FABRIC);

	public static final RegistrySupplier<Item> LIGHT_BLUE_ANCIENT_FABRIC = create("light_blue_ancient_fabric", () -> ModBlocks.LIGHT_BLUE_ANCIENT_FABRIC);

	public static final RegistrySupplier<Item> YELLOW_ANCIENT_FABRIC = create("yellow_ancient_fabric", () -> ModBlocks.YELLOW_ANCIENT_FABRIC);

	public static final RegistrySupplier<Item> LIME_ANCIENT_FABRIC = create("lime_ancient_fabric", () -> ModBlocks.LIME_ANCIENT_FABRIC);

	public static final RegistrySupplier<Item> PINK_ANCIENT_FABRIC = create("pink_ancient_fabric", () -> ModBlocks.PINK_ANCIENT_FABRIC);

	public static final RegistrySupplier<Item> GRAY_ANCIENT_FABRIC = create("gray_ancient_fabric", () -> ModBlocks.GRAY_ANCIENT_FABRIC);

	public static final RegistrySupplier<Item> LIGHT_GRAY_ANCIENT_FABRIC = create("light_gray_ancient_fabric", () -> ModBlocks.LIGHT_GRAY_ANCIENT_FABRIC);

	public static final RegistrySupplier<Item> CYAN_ANCIENT_FABRIC = create("cyan_ancient_fabric", () -> ModBlocks.CYAN_ANCIENT_FABRIC);

	public static final RegistrySupplier<Item> PURPLE_ANCIENT_FABRIC = create("purple_ancient_fabric", () -> ModBlocks.PURPLE_ANCIENT_FABRIC);

	public static final RegistrySupplier<Item> BLUE_ANCIENT_FABRIC = create("blue_ancient_fabric", () -> ModBlocks.BLUE_ANCIENT_FABRIC);

	public static final RegistrySupplier<Item> BROWN_ANCIENT_FABRIC = create("brown_ancient_fabric", () -> ModBlocks.BROWN_ANCIENT_FABRIC);

	public static final RegistrySupplier<Item> GREEN_ANCIENT_FABRIC = create("green_ancient_fabric", () -> ModBlocks.GREEN_ANCIENT_FABRIC);

	public static final RegistrySupplier<Item> RED_ANCIENT_FABRIC = create("red_ancient_fabric", () -> ModBlocks.RED_ANCIENT_FABRIC);

	public static final RegistrySupplier<Item> BLACK_ANCIENT_FABRIC = create("black_ancient_fabric", () -> ModBlocks.BLACK_ANCIENT_FABRIC);

	public static final RegistrySupplier<Item> DECAYED_BLOCK = createWithoutItemGroup("decayed_block", () -> ModBlocks.DECAYED_BLOCK);

	public static final RegistrySupplier<Item> UNFOLDED_BLOCK = createWithoutItemGroup("unfolded_block", () -> ModBlocks.UNFOLDED_BLOCK);

	public static final RegistrySupplier<Item> UNWARPED_BLOCK = createWithoutItemGroup("unwarped_block", () -> ModBlocks.UNWARPED_BLOCK);

	public static final RegistrySupplier<Item> UNRAVELLED_BLOCK = createWithoutItemGroup("unravelled_block", () -> ModBlocks.UNRAVELLED_BLOCK);

	public static final RegistrySupplier<Item> UNRAVELLED_FABRIC = create("unravelled_fabric", () -> ModBlocks.UNRAVELLED_FABRIC);

	public static final RegistrySupplier<Item> CREEPY_RECORD = create("creepy_record", () -> new net.minecraft.item.MusicDiscItem(10, ModSoundEvents.CREEPY, new Item.Properties(), 317));

	public static final RegistrySupplier<Item> WHITE_VOID_RECORD = create("white_void_record", () -> new net.minecraft.item.MusicDiscItem(10, ModSoundEvents.WHITE_VOID, new Item.Properties(), 225));

	public static final RegistrySupplier<Item> MARKING_PLATE = createWithoutItemGroup("marking_plate", () -> ModBlocks.MARKING_PLATE);

//	@RegistryEntry("eternal_fluid")//	public static final Item ETERNAL_FLUID = create(ModBlocks.ETERNAL_FLUID);

	@RegistryEntry("eternal_fluid_bucket") public static final Item ETERNAL_FLUID_BUCKET = create(new BucketItem(ModFluids.ETERNAL_FLUID, new Item.Properties().recipeRemainder(Items.BUCKET).stacksTo(1)));

	@RegistryEntry("solid_static") public static final Item SOLID_STATIC = create(ModBlocks.SOLID_STATIC);

	@RegistryEntry("tesselating_loom") public static final Item TESSELATING_LOOM = create(ModBlocks.TESSELATING_LOOM);

	@RegistryEntry("mask_shard") public static final Item MASK_SHARD = create(new Item(new Item.Properties()/**/));

	@RegistryEntry("fuzzy_fireball") public static final Item FUZZY_FIREBALL = create(new Item(new Item.Properties()));

	@RegistryEntry("fabric_of_finality") public static final Item FABRIC_OF_FINALITY = create(new Item(new Item.Properties()));

	@RegistryEntry("reality_sponge") public static final Item REALITY_SPONGE = create(ModBlocks.REALITY_SPONGE);

	@RegistryEntry("liminal_lint") public static final Item LIMINAL_LINT = create(new Item(new Item.Properties()));

	@RegistryEntry("enduring_fibers") public static final Item ENDURING_FIBERS = create(new Item(new Item.Properties()));

	@RegistryEntry("rift_pearl") public static final Item RIFT_PEARL = create(new Item(new Item.Properties()));

	@RegistryEntry("fabric_of_reality") public static final Item FABRIC_OF_REALITY = create(new Item(new Item.Properties()));

	@RegistryEntry("amalgam_lump") public static final Item AMALGAM_LUMP = new Item(new Item.Properties());

	@RegistryEntry("clod") public static final Item CLOD = new Item(new Item.Properties());

	@RegistryEntry("garment_of_reality_helmet") public static final Item GARMENT_OF_REALITY_HELMET = create(new ArmorItem(ModArmorMaterials.GARMENT_OF_REALITY, ArmorItem.Type.HELMET, new Item.Properties()));

	@RegistryEntry("garment_of_reality_chestplate") public static final Item GARMENT_OF_REALITY_CHESTPLATE = create(new ArmorItem(ModArmorMaterials.GARMENT_OF_REALITY, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

	@RegistryEntry("garment_of_reality_leggings") public static final Item GARMENT_OF_REALITY_LEGGINGS = create(new ArmorItem(ModArmorMaterials.GARMENT_OF_REALITY, ArmorItem.Type.LEGGINGS, new Item.Properties()));

	@RegistryEntry("garment_of_reality_boots") public static final Item GARMENT_OF_REALITY_BOOTS = create(new ArmorItem(ModArmorMaterials.GARMENT_OF_REALITY, ArmorItem.Type.BOOTS, new Item.Properties()));

	@RegistryEntry("driftwood_log") public static final Item DRIFTWOOD_LOG = create(ModBlocks.DRIFTWOOD_LOG);
	@RegistryEntry("driftwood_planks") public static final Item DRIFTWOOD_PLANKS = create(ModBlocks.DRIFTWOOD_PLANKS);
	@RegistryEntry("driftwood_fence") public static final Item DRIFTWOOD_FENCE = create(ModBlocks.DRIFTWOOD_FENCE);
	@RegistryEntry("driftwood_gate") public static final Item DRIFTWOOD_GATE = create(ModBlocks.DRIFTWOOD_GATE);
	@RegistryEntry("driftwood_button") public static final Item DRIFTWOOD_BUTTON = create(ModBlocks.DRIFTWOOD_BUTTON);
	@RegistryEntry("driftwood_slab") public static final Item DRIFTWOOD_SLAB = create(ModBlocks.DRIFTWOOD_SLAB);
	@RegistryEntry("driftwood_stairs") public static final Item DRIFTWOOD_STAIRS = create(ModBlocks.DRIFTWOOD_STAIRS);
	@RegistryEntry("driftwood_door") public static final Item DRIFTWOOD_DOOR = create(ModBlocks.DRIFTWOOD_DOOR);
	@RegistryEntry("driftwood_trapdoor") public static final Item DRIFTWOOD_TRAPDOOR = create(ModBlocks.DRIFTWOOD_TRAPDOOR);

	@RegistryEntry("amalgam_block") public static final Item AMALGAM_BLOCK = create(ModBlocks.AMALGAM_BLOCK);
	@RegistryEntry("amalgam_door") public static final Item AMALGAM_DOOR = create(ModBlocks.AMALGAM_DOOR);
	@RegistryEntry("amalgam_trapdoor") public static final Item AMALGAM_TRAPDOOR = create(ModBlocks.AMALGAM_TRAPDOOR);
	@RegistryEntry("rust") public static final Item RUST = create(ModBlocks.RUST);
	@RegistryEntry("amalgam_slab") public static final Item AMALGAM_SLAB = create(ModBlocks.AMALGAM_SLAB);
	@RegistryEntry("amalgam_stairs") public static final Item AMALGAM_STAIRS = create(ModBlocks.AMALGAM_STAIRS);
	@RegistryEntry("amalgam_ore") public static final Item AMALGAM_ORE = create(ModBlocks.AMALGAM_ORE);
	@RegistryEntry("clod_ore") public static final Item CLOD_ORE = create(ModBlocks.CLOD_ORE);

	@RegistryEntry("clod_block") public static final Item CLOD_BLOCK = create(ModBlocks.CLOD_BLOCK);
	@RegistryEntry("gravel_fence") public static final Item GRAVEL_FENCE = create(ModBlocks.GRAVEL_FENCE);
	@RegistryEntry("gravel_gate") public static final Item GRAVEL_GATE = create(ModBlocks.GRAVEL_GATE);
	@RegistryEntry("gravel_button") public static final Item GRAVEL_BUTTON = create(ModBlocks.GRAVEL_BUTTON);
	@RegistryEntry("gravel_slab") public static final Item GRAVEL_SLAB = create(ModBlocks.GRAVEL_SLAB);
	@RegistryEntry("gravel_stairs") public static final Item GRAVEL_STAIRS = create(ModBlocks.GRAVEL_STAIRS);
	@RegistryEntry("gravel_wall") public static final Item GRAVEL_WALL = create(ModBlocks.GRAVEL_WALL);

	@RegistryEntry("dark_sand") public static final Item DARK_SAND = create(ModBlocks.DARK_SAND);
	@RegistryEntry("dark_sand_fence") public static final Item DARK_SAND_FENCE = create(ModBlocks.DARK_SAND_FENCE);
	@RegistryEntry("dark_sand_gate") public static final Item DARK_SAND_GATE = create(ModBlocks.DARK_SAND_GATE);
	@RegistryEntry("dark_sand_button") public static final Item DARK_SAND_BUTTON = create(ModBlocks.DARK_SAND_BUTTON);
	@RegistryEntry("dark_sand_slab") public static final Item DARK_SAND_SLAB = create(ModBlocks.DARK_SAND_SLAB);
	@RegistryEntry("dark_sand_stairs") public static final Item DARK_SAND_STAIRS = create(ModBlocks.DARK_SAND_STAIRS);
	@RegistryEntry("dark_sand_wall") public static final Item DARK_SAND_WALL = create(ModBlocks.DARK_SAND_WALL);

	@RegistryEntry("clay_fence") public static final Item CLAY_FENCE = create(ModBlocks.CLAY_FENCE);
	@RegistryEntry("clay_gate") public static final Item CLAY_GATE = create(ModBlocks.CLAY_GATE);
	@RegistryEntry("clay_button") public static final Item CLAY_BUTTON = create(ModBlocks.CLAY_BUTTON);
	@RegistryEntry("clay_slab") public static final Item CLAY_SLAB = create(ModBlocks.CLAY_SLAB);
	@RegistryEntry("clay_stairs") public static final Item CLAY_STAIRS = create(ModBlocks.CLAY_STAIRS);

	@RegistryEntry("mud_fence") public static final Item MUD_FENCE = create(ModBlocks.MUD_FENCE);
	@RegistryEntry("mud_gate") public static final Item MUD_GATE = create(ModBlocks.MUD_GATE);
	@RegistryEntry("mud_button") public static final Item MUD_BUTTON = create(ModBlocks.MUD_BUTTON);
	@RegistryEntry("mud_slab") public static final Item MUD_SLAB = create(ModBlocks.MUD_SLAB);
	@RegistryEntry("mud_stairs") public static final Item MUD_STAIRS = create(ModBlocks.MUD_STAIRS);

	@RegistryEntry("deepslate_slab") public static final Item DEEPSLATE_SLAB = create(ModBlocks.DEEPSLATE_SLAB);
	@RegistryEntry("deepslate_stairs") public static final Item DEEPSLATE_STAIRS = create(ModBlocks.DEEPSLATE_STAIRS);
	@RegistryEntry("deepslate_wall") public static final Item DEEPSLATE_WALL = create(ModBlocks.DEEPSLATE_WALL);

	@RegistryEntry("red_sand_slab") public static final Item RED_SAND_SLAB = create(ModBlocks.RED_SAND_SLAB);
	@RegistryEntry("red_sand_stairs") public static final Item RED_SAND_STAIRS = create(ModBlocks.RED_SAND_STAIRS);
	@RegistryEntry("red_sand_wall") public static final Item RED_SAND_WALL = create(ModBlocks.RED_SAND_WALL);

	@RegistryEntry("sand_slab") public static final Item SAND_SLAB = create(ModBlocks.SAND_SLAB);
	@RegistryEntry("sand_stairs") public static final Item SAND_STAIRS = create(ModBlocks.SAND_STAIRS);
	@RegistryEntry("sand_wall") public static final Item SAND_WALL = create(ModBlocks.SAND_WALL);

	@RegistryEntry("end_stone_slab") public static final Item END_STONE_SLAB = create(ModBlocks.END_STONE_SLAB);
	@RegistryEntry("end_stone_stairs") public static final Item END_STONE_STAIRS = create(ModBlocks.END_STONE_STAIRS);
	@RegistryEntry("end_stone_wall") public static final Item END_STONE_WALL = create(ModBlocks.END_STONE_WALL);

	@RegistryEntry("netherrack_fence") public static final Item NETHERRACK_FENCE = create(ModBlocks.NETHERRACK_FENCE);
	@RegistryEntry("netherrack_slab") public static final Item NETHERRACK_SLAB = create(ModBlocks.NETHERRACK_SLAB);
	@RegistryEntry("netherrack_stairs") public static final Item NETHERRACK_STAIRS = create(ModBlocks.NETHERRACK_STAIRS);
	@RegistryEntry("netherrack_wall") public static final Item NETHERRACK_WALL = create(ModBlocks.NETHERRACK_WALL);

	@RegistryEntry("unraveled_spike") public static final Item UNRAVELED_SPIKE = create(ModBlocks.UNRAVELED_SPIKE);
	@RegistryEntry("unraveled_fence") public static final Item UNRAVELED_FENCE = create(ModBlocks.UNRAVELED_FENCE);
	@RegistryEntry("unraveled_gate") public static final Item UNRAVELED_GATE = create(ModBlocks.UNRAVELED_GATE);
	@RegistryEntry("unraveled_button") public static final Item UNRAVELED_BUTTON = create(ModBlocks.UNRAVELED_BUTTON);
	@RegistryEntry("unraveled_slab") public static final Item UNRAVELED_SLAB = create(ModBlocks.UNRAVELED_SLAB);
	@RegistryEntry("unraveled_stairs") public static final Item UNRAVELED_STAIRS = create(ModBlocks.UNRAVELED_STAIRS);
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
