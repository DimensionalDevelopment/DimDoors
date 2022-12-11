package org.dimdev.dimdoors.item;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.dimdev.matrix.Matrix;
import org.dimdev.matrix.Registrar;
import org.dimdev.matrix.RegistryEntry;

import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
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
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.dimdev.dimdoors.block.door.data.DoorData;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.rift.targets.RandomTarget;
import org.dimdev.dimdoors.sound.ModSoundEvents;

import static org.dimdev.dimdoors.DimensionalDoors.id;

@SuppressWarnings("unused")
@Registrar(element = Item.class, modid = "dimdoors")
public final class ModItems {
	// DO NOT REMOVE!!!
	public static final Registry<Item> REGISTRY = Registries.ITEM;

	@RegistryEntry("stone_player") public static final Item STONE_PLAYER = createWithoutItemGroup(ModBlocks.STONE_PLAYER);

	@RegistryEntry("quartz_door") public static final Item QUARTZ_DOOR = create(ModBlocks.QUARTZ_DOOR);

	@RegistryEntry("gold_door") public static final Item GOLD_DOOR = create(ModBlocks.GOLD_DOOR);

	@RegistryEntry("stone_door") public static final Item STONE_DOOR = create(ModBlocks.STONE_DOOR);

	@RegistryEntry("wood_dimensional_trapdoor") public static final Item OAK_DIMENSIONAL_TRAPDOOR = create(new DimensionalTrapdoorItem(
			ModBlocks.OAK_DIMENSIONAL_TRAPDOOR,
			new Item.Settings().maxCount(1),
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

	@RegistryEntry("world_thread") public static final Item WORLD_THREAD = create(new Item(new Item.Settings()));

	@RegistryEntry("infrangible_fiber") public static final Item INFRANGIBLE_FIBER = create(new Item(new Item.Settings()));

	@RegistryEntry("frayed_filament") public static final Item FRAYED_FILAMENTS = create(new Item(new Item.Settings()));

	@RegistryEntry("rift_configuration_tool") public static final Item RIFT_CONFIGURATION_TOOL = create(new RiftConfigurationToolItem());

	@RegistryEntry("rift_blade") public static final Item RIFT_BLADE = create(new RiftBladeItem(new Item.Settings().maxDamage(100)));

	@RegistryEntry("rift_remover") public static final Item RIFT_REMOVER = create(new RiftRemoverItem(new Item.Settings().maxCount(1).maxDamage(100)));

	@RegistryEntry("rift_signature") public static final Item RIFT_SIGNATURE = create(new RiftSignatureItem(new Item.Settings().maxCount(1).maxDamage(1)));

	@RegistryEntry("stabilized_rift_signature") public static final Item STABILIZED_RIFT_SIGNATURE = create(new StabilizedRiftSignatureItem(new Item.Settings().maxCount(1).maxDamage(20)));

	@RegistryEntry("rift_stabilizer") public static final Item RIFT_STABILIZER = create(new RiftStabilizerItem(new Item.Settings().maxCount(1).maxDamage(6)));

	@RegistryEntry("rift_key") public static final Item RIFT_KEY = create(new RiftKeyItem(new Item.Settings().fireproof().maxCount(1)));

	@RegistryEntry("dimensional_eraser") public static final Item DIMENSIONAL_ERASER = create(new DimensionalEraserItem(new Item.Settings().maxDamage(100)));

	@RegistryEntry("monolith_spawner") public static final Item MONOLITH_SPAWNER = new SpawnEggItem(ModEntityTypes.MONOLITH, 0xffffff, 0xffffff, new Item.Settings());

	@RegistryEntry("world_thread_helmet") public static final Item WORLD_THREAD_HELMET = create(new ArmorItem(ModArmorMaterials.WORLD_THREAD, EquipmentSlot.HEAD, new Item.Settings()));

	@RegistryEntry("world_thread_chestplate") public static final Item WORLD_THREAD_CHESTPLATE = create(new ArmorItem(ModArmorMaterials.WORLD_THREAD, EquipmentSlot.CHEST, new Item.Settings()));

	@RegistryEntry("world_thread_leggings") public static final Item WORLD_THREAD_LEGGINGS = create(new ArmorItem(ModArmorMaterials.WORLD_THREAD, EquipmentSlot.LEGS, new Item.Settings()));

	@RegistryEntry("world_thread_boots") public static final Item WORLD_THREAD_BOOTS = create(new ArmorItem(ModArmorMaterials.WORLD_THREAD, EquipmentSlot.FEET, new Item.Settings()));

	@RegistryEntry("mask_wand") public static final Item MASK_WAND = create(new MaskWandItem(new Item.Settings().maxCount(100)/**/));

	@RegistryEntry("stable_fabric") public static final Item STABLE_FABRIC = create(new Item(new Item.Settings()));

	@RegistryEntry("white_fabric") public static final Item WHITE_FABRIC = create(ModBlocks.WHITE_FABRIC);

	@RegistryEntry("orange_fabric") public static final Item ORANGE_FABRIC = create(ModBlocks.ORANGE_FABRIC);

	@RegistryEntry("magenta_fabric") public static final Item MAGENTA_FABRIC = create(ModBlocks.MAGENTA_FABRIC);

	@RegistryEntry("light_blue_fabric") public static final Item LIGHT_BLUE_FABRIC = create(ModBlocks.LIGHT_BLUE_FABRIC);

	@RegistryEntry("yellow_fabric") public static final Item YELLOW_FABRIC = create(ModBlocks.YELLOW_FABRIC);

	@RegistryEntry("lime_fabric") public static final Item LIME_FABRIC = create(ModBlocks.LIME_FABRIC);

	@RegistryEntry("pink_fabric") public static final Item PINK_FABRIC = create(ModBlocks.PINK_FABRIC);

	@RegistryEntry("gray_fabric") public static final Item GRAY_FABRIC = create(ModBlocks.GRAY_FABRIC);

	@RegistryEntry("light_gray_fabric") public static final Item LIGHT_GRAY_FABRIC = create(ModBlocks.LIGHT_GRAY_FABRIC);

	@RegistryEntry("cyan_fabric") public static final Item CYAN_FABRIC = create(ModBlocks.CYAN_FABRIC);

	@RegistryEntry("purple_fabric") public static final Item PURPLE_FABRIC = create(ModBlocks.PURPLE_FABRIC);

	@RegistryEntry("blue_fabric") public static final Item BLUE_FABRIC = create(ModBlocks.BLUE_FABRIC);

	@RegistryEntry("brown_fabric") public static final Item BROWN_FABRIC = create(ModBlocks.BROWN_FABRIC);

	@RegistryEntry("green_fabric") public static final Item GREEN_FABRIC = create(ModBlocks.GREEN_FABRIC);

	@RegistryEntry("red_fabric") public static final Item RED_FABRIC = create(ModBlocks.RED_FABRIC);

	@RegistryEntry("black_fabric") public static final Item BLACK_FABRIC = create(ModBlocks.BLACK_FABRIC);

	@RegistryEntry("white_ancient_fabric") public static final Item WHITE_ANCIENT_FABRIC = create(ModBlocks.WHITE_ANCIENT_FABRIC);

	@RegistryEntry("orange_ancient_fabric") public static final Item ORANGE_ANCIENT_FABRIC = create(ModBlocks.ORANGE_ANCIENT_FABRIC);

	@RegistryEntry("magenta_ancient_fabric") public static final Item MAGENTA_ANCIENT_FABRIC = create(ModBlocks.MAGENTA_ANCIENT_FABRIC);

	@RegistryEntry("light_blue_ancient_fabric") public static final Item LIGHT_BLUE_ANCIENT_FABRIC = create(ModBlocks.LIGHT_BLUE_ANCIENT_FABRIC);

	@RegistryEntry("yellow_ancient_fabric") public static final Item YELLOW_ANCIENT_FABRIC = create(ModBlocks.YELLOW_ANCIENT_FABRIC);

	@RegistryEntry("lime_ancient_fabric") public static final Item LIME_ANCIENT_FABRIC = create(ModBlocks.LIME_ANCIENT_FABRIC);

	@RegistryEntry("pink_ancient_fabric") public static final Item PINK_ANCIENT_FABRIC = create(ModBlocks.PINK_ANCIENT_FABRIC);

	@RegistryEntry("gray_ancient_fabric") public static final Item GRAY_ANCIENT_FABRIC = create(ModBlocks.GRAY_ANCIENT_FABRIC);

	@RegistryEntry("light_gray_ancient_fabric") public static final Item LIGHT_GRAY_ANCIENT_FABRIC = create(ModBlocks.LIGHT_GRAY_ANCIENT_FABRIC);

	@RegistryEntry("cyan_ancient_fabric") public static final Item CYAN_ANCIENT_FABRIC = create(ModBlocks.CYAN_ANCIENT_FABRIC);

	@RegistryEntry("purple_ancient_fabric") public static final Item PURPLE_ANCIENT_FABRIC = create(ModBlocks.PURPLE_ANCIENT_FABRIC);

	@RegistryEntry("blue_ancient_fabric") public static final Item BLUE_ANCIENT_FABRIC = create(ModBlocks.BLUE_ANCIENT_FABRIC);

	@RegistryEntry("brown_ancient_fabric") public static final Item BROWN_ANCIENT_FABRIC = create(ModBlocks.BROWN_ANCIENT_FABRIC);

	@RegistryEntry("green_ancient_fabric") public static final Item GREEN_ANCIENT_FABRIC = create(ModBlocks.GREEN_ANCIENT_FABRIC);

	@RegistryEntry("red_ancient_fabric") public static final Item RED_ANCIENT_FABRIC = create(ModBlocks.RED_ANCIENT_FABRIC);

	@RegistryEntry("black_ancient_fabric") public static final Item BLACK_ANCIENT_FABRIC = create(ModBlocks.BLACK_ANCIENT_FABRIC);

	@RegistryEntry("decayed_block") public static final Item DECAYED_BLOCK = createWithoutItemGroup(ModBlocks.DECAYED_BLOCK);

	@RegistryEntry("unfolded_block") public static final Item UNFOLDED_BLOCK = createWithoutItemGroup(ModBlocks.UNFOLDED_BLOCK);

	@RegistryEntry("unwarped_block") public static final Item UNWARPED_BLOCK = createWithoutItemGroup(ModBlocks.UNWARPED_BLOCK);

	@RegistryEntry("unravelled_block") public static final Item UNRAVELLED_BLOCK = createWithoutItemGroup(ModBlocks.UNRAVELLED_BLOCK);

	@RegistryEntry("unravelled_fabric") public static final Item UNRAVELLED_FABRIC = create(ModBlocks.UNRAVELLED_FABRIC);

	@RegistryEntry("creepy_record") public static final Item CREEPY_RECORD = create(new net.minecraft.item.MusicDiscItem(10, ModSoundEvents.CREEPY, new Item.Settings(), 317));

	@RegistryEntry("white_void_record") public static final Item WHITE_VOID_RECORD = create(new net.minecraft.item.MusicDiscItem(10, ModSoundEvents.WHITE_VOID, new Item.Settings(), 225));

	@RegistryEntry("marking_plate") public static final Item MARKING_PLATE = createWithoutItemGroup(ModBlocks.MARKING_PLATE);

//	@RegistryEntry("eternal_fluid")//	public static final Item ETERNAL_FLUID = create(ModBlocks.ETERNAL_FLUID);

	@RegistryEntry("eternal_fluid_bucket") public static final Item ETERNAL_FLUID_BUCKET = create(new BucketItem(ModFluids.ETERNAL_FLUID, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));

	@RegistryEntry("solid_static") public static final Item SOLID_STATIC = create(ModBlocks.SOLID_STATIC);

	@RegistryEntry("tesselating_loom") public static final Item TESSELATING_LOOM = create(ModBlocks.TESSELATING_LOOM);

	@RegistryEntry("mask_shard") public static final Item MASK_SHARD = create(new Item(new Item.Settings()/**/));

	@RegistryEntry("fuzzy_fireball") public static final Item FUZZY_FIREBALL = create(new Item(new Item.Settings()));

	@RegistryEntry("fabric_of_finality") public static final Item FABRIC_OF_FINALITY = create(new Item(new Item.Settings()));

	@RegistryEntry("garment_of_reality") public static final Item GARMENT_OF_REALITY = create(new Item(new Item.Settings()));

	@RegistryEntry("reality_sponge") public static final Item REALITY_SPONGE = create(new Item(new Item.Settings()));

	@RegistryEntry("liminal_lint") public static final Item LIMINAL_LINT = create(new Item(new Item.Settings()));

	@RegistryEntry("enduring_fibers") public static final Item ENDURING_FIBERS = create(new Item(new Item.Settings()));

	@RegistryEntry("rift_pearl") public static final Item RIFT_PEARL = create(new Item(new Item.Settings()));

	@RegistryEntry("fabric_of_reality") public static final Item FABRIC_OF_REALITY = create(new Item(new Item.Settings()));

	@RegistryEntry("amalgam_lump") public static final Item AMALGAM_LUMP = new Item(new Item.Settings());

	@RegistryEntry("clod") public static final Item CLOD = new Item(new Item.Settings());

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
	@RegistryEntry("amalgam_ore") public static final Item AMALGAM_ORE = create(ModBlocks.AMALGAM_ORE);
	@RegistryEntry("clod_ore") public static final Item CLOD_ORE = create(ModBlocks.CLOD_ORE);
	@RegistryEntry("clod_block") public static final Item CLOD_BLOCK = create(ModBlocks.CLOD_BLOCK);
	@RegistryEntry("gravel_fence") public static final Item GRAVEL_FENCE = create(ModBlocks.GRAVEL_FENCE);
	@RegistryEntry("gravel_gate") public static final Item GRAVEL_GATE = create(ModBlocks.GRAVEL_GATE);
	@RegistryEntry("gravel_button") public static final Item GRAVEL_BUTTON = create(ModBlocks.GRAVEL_BUTTON);
	@RegistryEntry("gravel_slab") public static final Item GRAVEL_SLAB = create(ModBlocks.GRAVEL_SLAB);
	@RegistryEntry("gravel_stairs") public static final Item GRAVEL_STAIRS = create(ModBlocks.GRAVEL_STAIRS);
	@RegistryEntry("dark_sand") public static final Item DARK_SAND = create(ModBlocks.DARK_SAND);
	@RegistryEntry("dark_sand_fence") public static final Item DARK_SAND_FENCE = create(ModBlocks.DARK_SAND_FENCE);
	@RegistryEntry("dark_sand_gate") public static final Item DARK_SAND_GATE = create(ModBlocks.DARK_SAND_GATE);
	@RegistryEntry("dark_sand_button") public static final Item DARK_SAND_BUTTON = create(ModBlocks.DARK_SAND_BUTTON);
	@RegistryEntry("dark_sand_slab") public static final Item DARK_SAND_SLAB = create(ModBlocks.DARK_SAND_SLAB);
	@RegistryEntry("dark_sand_stairs") public static final Item DARK_SAND_STAIRS = create(ModBlocks.DARK_SAND_STAIRS);
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
	@RegistryEntry("unraveled_spike") public static final Item UNRAVELED_SPIKE = create(ModBlocks.UNRAVELED_SPIKE);

	public static final Set<Item> DOOR_ITEMS = new HashSet<>();

	public static final ItemGroup DIMENSIONAL_DOORS = FabricItemGroup.builder(id("dimensional_doors"))
			.icon(() -> new ItemStack(ModItems.RIFT_BLADE))
			.entries((enabledFeatures, entries, operatorEnabled) -> {
				for (Field field : ModItems.class.getFields()) {
					if (field.getType() == Item.class) {
						try {
							entries.add((Item) field.get(null));
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}
				for (Item item : DOOR_ITEMS) {
					entries.add(item);
				}
			})
			.build();

	private static Item createWithoutItemGroup(Block block) {
		return create(new BlockItem(block, (new Item.Settings())));
	}

	private static Item create(Block block) {
		return create(new BlockItem(block, (new Item.Settings())));
	}

	private static Item create(Item item) {
		if (item instanceof BlockItem) {
			((BlockItem) item).appendBlocks(Item.BLOCK_ITEMS, item);
		}

		return item;
	}

	public static void init() {
		for (Item item : Registries.ITEM) {
			if (item instanceof BlockItem blockItem) {
				if (blockItem.getBlock() instanceof DimensionalDoorBlock) {
					DOOR_ITEMS.add(item);
				}
			}
		}
		RegistryEntryAddedCallback.event(Registries.ITEM).register((rawId, id, item) -> {
			if (item instanceof BlockItem blockItem) {
				if (blockItem.getBlock() instanceof DimensionalDoorBlock) {
					DOOR_ITEMS.add(item);
				}
			}
		});
		Matrix.register(ModItems.class, REGISTRY);
	}
}
