package org.dimdev.dimdoors.item;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.block.Block;

import org.dimdev.dimdoors.Constants;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.registry.RegistryHandler;
import org.dimdev.dimdoors.rift.targets.RandomTarget;
import org.dimdev.dimdoors.sound.ModSoundEvents;

@SuppressWarnings("unused")
public final class ModItems {
	// DO NOT REMOVE!!!
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MODID);
	public static final RegistryObject<Item> STONE_PLAYER = ITEMS.register("stone_player",
			() -> createWithoutItemGroup(ModBlocks.STONE_PLAYER));

	public static final RegistryObject<Item> QUARTZ_DOOR = ITEMS.register("quartz_door",
			() -> createWithoutItemGroup(ModBlocks.QUARTZ_DOOR));

	public static final RegistryObject<Item> GOLD_DOOR = ITEMS.register("gold_door",
			() -> createWithoutItemGroup(ModBlocks.GOLD_DOOR));

	public static final RegistryObject<Item> STONE_DOOR = ITEMS.register("stone_door",
			() -> createWithoutItemGroup(ModBlocks.STONE_DOOR));

	public static final RegistryObject<Item> OAK_DIMENSIONAL_TRAPDOOR = ITEMS.register("wood_dimensional_trapdoor",
			() -> create(new DimensionalTrapdoorItem(
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
			)));

	public static final RegistryObject<Item> WORLD_THREAD = ITEMS.register("world_thread", ModItems::create);

	public static final RegistryObject<Item> INFRANGIBLE_FIBER = ITEMS.register("infrangible_fiber", ModItems::create);

	public static final RegistryObject<Item> FRAYED_FILAMENTS = ITEMS.register("frayed_filament", ModItems::create);

	public static final RegistryObject<Item> RIFT_CONFIGURATION_TOOL = ITEMS.register("rift_configuration_tool",
			() -> create(new RiftConfigurationToolItem()));

	public static final RegistryObject<Item> RIFT_BLADE = ITEMS.register("rift_blade",
			() -> create(new RiftBladeItem(new Item.Properties().durability(100))));

	public static final RegistryObject<Item> RIFT_REMOVER = ITEMS.register("rift_remover",
			() -> create(new RiftRemoverItem(new Item.Properties().stacksTo(1).durability(100))));

	public static final RegistryObject<Item> RIFT_SIGNATURE = ITEMS.register("rift_signature",
			() -> create(new RiftSignatureItem(new Item.Properties().stacksTo(1).durability(1))));

	public static final RegistryObject<Item> STABILIZED_RIFT_SIGNATURE = ITEMS.register("stabilized_rift_signature",
			() -> create(new StabilizedRiftSignatureItem(new Item.Properties().stacksTo(1).durability(20))));

	public static final RegistryObject<Item> RIFT_STABILIZER = ITEMS.register("rift_stabilizer",
			() -> create(new RiftStabilizerItem(new Item.Properties().stacksTo(1).durability(6))));

	public static final RegistryObject<Item> RIFT_KEY = ITEMS.register("rift_key",
			() -> create(new RiftKeyItem(new Item.Properties().fireResistant().stacksTo(1))));

	public static final RegistryObject<Item> DIMENSIONAL_ERASER = ITEMS.register("dimensional_eraser",
			() -> create(new DimensionalEraserItem(new Item.Properties().durability(100))));

	public static final RegistryObject<Item> MONOLITH_SPAWNER = ITEMS.register("monolith_spawner",
			() -> new ForgeSpawnEggItem(() -> ModEntityTypes.MONOLITH, 0xffffff, 0xffffff, new Item.Properties()));

	public static final RegistryObject<Item> WORLD_THREAD_HELMET = ITEMS.register("world_thread_helmet",
			() -> create(new ArmorItem(ModArmorMaterials.WORLD_THREAD, EquipmentSlot.HEAD, new Item.Properties())));

	public static final RegistryObject<Item> WORLD_THREAD_CHESTPLATE = ITEMS.register("world_thread_chestplate",
			() -> create(new ArmorItem(ModArmorMaterials.WORLD_THREAD, EquipmentSlot.CHEST, new Item.Properties())));

	public static final RegistryObject<Item> WORLD_THREAD_LEGGINGS = ITEMS.register("world_thread_leggings",
			() -> create(new ArmorItem(ModArmorMaterials.WORLD_THREAD, EquipmentSlot.LEGS, new Item.Properties())));

	public static final RegistryObject<Item> WORLD_THREAD_BOOTS = ITEMS.register("world_thread_boots",
			() -> create(new ArmorItem(ModArmorMaterials.WORLD_THREAD, EquipmentSlot.FEET, new Item.Properties())));

	public static final RegistryObject<Item> MASK_WAND = ITEMS.register("mask_wand",
			() -> create(new MaskWandItem(new Item.Properties().stacksTo(100)/**/)));

	public static final RegistryObject<Item> STABLE_FABRIC = ITEMS.register("stable_fabric",
			ModItems::create);

	public static final RegistryObject<Item> WHITE_FABRIC = ITEMS.register("white_fabric",
			() -> createWithoutItemGroup(ModBlocks.WHITE_FABRIC));

	public static final RegistryObject<Item> ORANGE_FABRIC = ITEMS.register("orange_fabric",
			() -> createWithoutItemGroup(ModBlocks.ORANGE_FABRIC));

	public static final RegistryObject<Item> MAGENTA_FABRIC = ITEMS.register("magenta_fabric",
			() -> createWithoutItemGroup(ModBlocks.MAGENTA_FABRIC));

	public static final RegistryObject<Item> LIGHT_BLUE_FABRIC = ITEMS.register("light_blue_fabric",
			() -> createWithoutItemGroup(ModBlocks.LIGHT_BLUE_FABRIC));

	public static final RegistryObject<Item> YELLOW_FABRIC = ITEMS.register("yellow_fabric",
			() -> createWithoutItemGroup(ModBlocks.YELLOW_FABRIC));

	public static final RegistryObject<Item> LIME_FABRIC = ITEMS.register("lime_fabric",
			() -> createWithoutItemGroup(ModBlocks.LIME_FABRIC));

	public static final RegistryObject<Item> PINK_FABRIC = ITEMS.register("pink_fabric",
			() -> createWithoutItemGroup(ModBlocks.PINK_FABRIC));

	public static final RegistryObject<Item> GRAY_FABRIC = ITEMS.register("gray_fabric",
			() -> createWithoutItemGroup(ModBlocks.GRAY_FABRIC));

	public static final RegistryObject<Item> LIGHT_GRAY_FABRIC = ITEMS.register("light_gray_fabric",
			() -> createWithoutItemGroup(ModBlocks.LIGHT_GRAY_FABRIC));

	public static final RegistryObject<Item> CYAN_FABRIC = ITEMS.register("cyan_fabric",
			() -> createWithoutItemGroup(ModBlocks.CYAN_FABRIC));

	public static final RegistryObject<Item> PURPLE_FABRIC = ITEMS.register("purple_fabric",
			() -> createWithoutItemGroup(ModBlocks.PURPLE_FABRIC));

	public static final RegistryObject<Item> BLUE_FABRIC = ITEMS.register("blue_fabric",
			() -> createWithoutItemGroup(ModBlocks.BLUE_FABRIC));

	public static final RegistryObject<Item> BROWN_FABRIC = ITEMS.register("brown_fabric",
			() -> createWithoutItemGroup(ModBlocks.BROWN_FABRIC));

	public static final RegistryObject<Item> GREEN_FABRIC = ITEMS.register("green_fabric",
			() -> createWithoutItemGroup(ModBlocks.GREEN_FABRIC));

	public static final RegistryObject<Item> RED_FABRIC = ITEMS.register("red_fabric",
			() -> createWithoutItemGroup(ModBlocks.RED_FABRIC));

	public static final RegistryObject<Item> BLACK_FABRIC = ITEMS.register("black_fabric",
			() -> createWithoutItemGroup(ModBlocks.BLACK_FABRIC));

	public static final RegistryObject<Item> WHITE_ANCIENT_FABRIC = ITEMS.register("white_ancient_fabric",
			() -> createWithoutItemGroup(ModBlocks.WHITE_ANCIENT_FABRIC));

	public static final RegistryObject<Item> ORANGE_ANCIENT_FABRIC = ITEMS.register("orange_ancient_fabric",
			() -> createWithoutItemGroup(ModBlocks.ORANGE_ANCIENT_FABRIC));

	public static final RegistryObject<Item> MAGENTA_ANCIENT_FABRIC = ITEMS.register("magenta_ancient_fabric",
			() -> createWithoutItemGroup(ModBlocks.MAGENTA_ANCIENT_FABRIC));

	public static final RegistryObject<Item> LIGHT_BLUE_ANCIENT_FABRIC = ITEMS.register("light_blue_ancient_fabric",
			() -> createWithoutItemGroup(ModBlocks.LIGHT_BLUE_ANCIENT_FABRIC));

	public static final RegistryObject<Item> YELLOW_ANCIENT_FABRIC = ITEMS.register("yellow_ancient_fabric",
			() -> createWithoutItemGroup(ModBlocks.YELLOW_ANCIENT_FABRIC));

	public static final RegistryObject<Item> LIME_ANCIENT_FABRIC = ITEMS.register("lime_ancient_fabric",
			() -> createWithoutItemGroup(ModBlocks.LIME_ANCIENT_FABRIC));

	public static final RegistryObject<Item> PINK_ANCIENT_FABRIC = ITEMS.register("pink_ancient_fabric",
			() -> createWithoutItemGroup(ModBlocks.PINK_ANCIENT_FABRIC));

	public static final RegistryObject<Item> GRAY_ANCIENT_FABRIC = ITEMS.register("gray_ancient_fabric",
			() -> createWithoutItemGroup(ModBlocks.GRAY_ANCIENT_FABRIC));

	public static final RegistryObject<Item> LIGHT_GRAY_ANCIENT_FABRIC = ITEMS.register("light_gray_ancient_fabric",
			() -> createWithoutItemGroup(ModBlocks.LIGHT_GRAY_ANCIENT_FABRIC));

	public static final RegistryObject<Item> CYAN_ANCIENT_FABRIC = ITEMS.register("cyan_ancient_fabric",
			() -> createWithoutItemGroup(ModBlocks.CYAN_ANCIENT_FABRIC));

	public static final RegistryObject<Item> PURPLE_ANCIENT_FABRIC = ITEMS.register("purple_ancient_fabric",
			() -> createWithoutItemGroup(ModBlocks.PURPLE_ANCIENT_FABRIC));

	public static final RegistryObject<Item> BLUE_ANCIENT_FABRIC = ITEMS.register("blue_ancient_fabric",
			() -> createWithoutItemGroup(ModBlocks.BLUE_ANCIENT_FABRIC));

	public static final RegistryObject<Item> BROWN_ANCIENT_FABRIC = ITEMS.register("brown_ancient_fabric",
			() -> createWithoutItemGroup(ModBlocks.BROWN_ANCIENT_FABRIC));

	public static final RegistryObject<Item> GREEN_ANCIENT_FABRIC = ITEMS.register("green_ancient_fabric",
			() -> createWithoutItemGroup(ModBlocks.GREEN_ANCIENT_FABRIC));

	public static final RegistryObject<Item> RED_ANCIENT_FABRIC = ITEMS.register("red_ancient_fabric",
			() -> createWithoutItemGroup(ModBlocks.RED_ANCIENT_FABRIC));

	public static final RegistryObject<Item> BLACK_ANCIENT_FABRIC = ITEMS.register("black_ancient_fabric",
			() -> createWithoutItemGroup(ModBlocks.BLACK_ANCIENT_FABRIC));

	public static final RegistryObject<Item> DECAYED_BLOCK = ITEMS.register("decayed_block",
			() -> createWithoutItemGroup(ModBlocks.DECAYED_BLOCK));

	public static final RegistryObject<Item> UNFOLDED_BLOCK = ITEMS.register("unfolded_block",
			() -> createWithoutItemGroup(ModBlocks.UNFOLDED_BLOCK));

	public static final RegistryObject<Item> UNWARPED_BLOCK = ITEMS.register("unwarped_block",
			() -> createWithoutItemGroup(ModBlocks.UNWARPED_BLOCK));

	public static final RegistryObject<Item> UNRAVELLED_BLOCK = ITEMS.register("unravelled_block",
			() -> createWithoutItemGroup(ModBlocks.UNRAVELLED_BLOCK));

	public static final RegistryObject<Item> UNRAVELLED_FABRIC = ITEMS.register("unravelled_fabric",
			() -> createWithoutItemGroup(ModBlocks.UNRAVELLED_FABRIC));

	public static final RegistryObject<Item> CREEPY_RECORD = ITEMS.register("creepy_record",
			() -> create(new RecordItem(10, () -> ModSoundEvents.CREEPY, new Item.Properties(), 317)));

	public static final RegistryObject<Item> WHITE_VOID_RECORD = ITEMS.register("white_void_record",
			() -> create(new RecordItem(10, () -> ModSoundEvents.WHITE_VOID, new Item.Properties(), 225)));

	public static final RegistryObject<Item> MARKING_PLATE = ITEMS.register("marking_plate",
			() -> createWithoutItemGroup(ModBlocks.MARKING_PLATE));

//	@RegistryEntry("eternal_fluid")//	public static final Item ETERNAL_FLUID = create(ModBlocks.ETERNAL_FLUID);

	public static final RegistryObject<Item> ETERNAL_FLUID_BUCKET = ITEMS.register("eternal_fluid_bucket",
			() -> create(new BucketItem(() -> ModFluids.ETERNAL_FLUID, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1))));

	public static final RegistryObject<Item> SOLID_STATIC = ITEMS.register("solid_static",
			() -> createWithoutItemGroup(ModBlocks.SOLID_STATIC));

	public static final RegistryObject<Item> TESSELATING_LOOM = ITEMS.register("tesselating_loom",
			() -> createWithoutItemGroup(ModBlocks.TESSELATING_LOOM));

	public static final RegistryObject<Item> MASK_SHARD = ITEMS.register("mask_shard",
			() -> create(new Item(new Item.Properties()/**/)));

	public static final RegistryObject<Item> FUZZY_FIREBALL = ITEMS.register("fuzzy_fireball",
			ModItems::create);

	public static final RegistryObject<Item> FABRIC_OF_FINALITY = ITEMS.register("fabric_of_finality",
			ModItems::create);

	public static final RegistryObject<Item> GARMENT_OF_REALITY = ITEMS.register("garment_of_reality",
			ModItems::create);

	public static final RegistryObject<Item> REALITY_SPONGE = ITEMS.register("reality_sponge",
			() -> createWithoutItemGroup(ModBlocks.REALITY_SPONGE));

	public static final RegistryObject<Item> LIMINAL_LINT = ITEMS.register("liminal_lint",
			ModItems::create);

	public static final RegistryObject<Item> ENDURING_FIBERS = ITEMS.register("enduring_fibers",
			ModItems::create);

	public static final RegistryObject<Item> RIFT_PEARL = ITEMS.register("rift_pearl",
			ModItems::create);

	public static final RegistryObject<Item> FABRIC_OF_REALITY = ITEMS.register("fabric_of_reality",
			ModItems::create);

	public static final RegistryObject<Item> AMALGAM_LUMP = ITEMS.register("amalgam_lump",
			ModItems::create);

	public static final RegistryObject<Item> CLOD = ITEMS.register("clod",
			ModItems::create);

	public static final RegistryObject<Item> DRIFTWOOD_LOG = ITEMS.register("driftwood_log",
			() -> createWithoutItemGroup(ModBlocks.DRIFTWOOD_LOG));
	public static final RegistryObject<Item> DRIFTWOOD_PLANKS = ITEMS.register("driftwood_planks",
			() -> createWithoutItemGroup(ModBlocks.DRIFTWOOD_PLANKS));
	public static final RegistryObject<Item> DRIFTWOOD_FENCE = ITEMS.register("driftwood_fence",
			() -> createWithoutItemGroup(ModBlocks.DRIFTWOOD_FENCE));
	public static final RegistryObject<Item> DRIFTWOOD_GATE = ITEMS.register("driftwood_gate",
			() -> createWithoutItemGroup(ModBlocks.DRIFTWOOD_GATE));
	public static final RegistryObject<Item> DRIFTWOOD_BUTTON = ITEMS.register("driftwood_button",
			() -> createWithoutItemGroup(ModBlocks.DRIFTWOOD_BUTTON));
	public static final RegistryObject<Item> DRIFTWOOD_SLAB = ITEMS.register("driftwood_slab",
			() -> createWithoutItemGroup(ModBlocks.DRIFTWOOD_SLAB));
	public static final RegistryObject<Item> DRIFTWOOD_STAIRS = ITEMS.register("driftwood_stairs",
			() -> createWithoutItemGroup(ModBlocks.DRIFTWOOD_STAIRS));
	public static final RegistryObject<Item> DRIFTWOOD_DOOR = ITEMS.register("driftwood_door",
			() -> createWithoutItemGroup(ModBlocks.DRIFTWOOD_DOOR));
	public static final RegistryObject<Item> DRIFTWOOD_TRAPDOOR = ITEMS.register("driftwood_trapdoor",
			() -> createWithoutItemGroup(ModBlocks.DRIFTWOOD_TRAPDOOR));
	public static final RegistryObject<Item> AMALGAM_BLOCK = ITEMS.register("amalgam_block",
			() -> createWithoutItemGroup(ModBlocks.AMALGAM_BLOCK));
	public static final RegistryObject<Item> AMALGAM_DOOR = ITEMS.register("amalgam_door",
			() -> createWithoutItemGroup(ModBlocks.AMALGAM_DOOR));
	public static final RegistryObject<Item> AMALGAM_TRAPDOOR = ITEMS.register("amalgam_trapdoor",
			() -> createWithoutItemGroup(ModBlocks.AMALGAM_TRAPDOOR));
	public static final RegistryObject<Item> RUST = ITEMS.register("rust",
			() -> createWithoutItemGroup(ModBlocks.RUST));
	public static final RegistryObject<Item> AMALGAM_SLAB = ITEMS.register("amalgam_slab",
			() -> createWithoutItemGroup(ModBlocks.AMALGAM_SLAB));
	public static final RegistryObject<Item> AMALGAM_ORE = ITEMS.register("amalgam_ore",
			() -> createWithoutItemGroup(ModBlocks.AMALGAM_ORE));
	public static final RegistryObject<Item> CLOD_ORE = ITEMS.register("clod_ore",
			() -> createWithoutItemGroup(ModBlocks.CLOD_ORE));
	public static final RegistryObject<Item> CLOD_BLOCK = ITEMS.register("clod_block",
			() -> createWithoutItemGroup(ModBlocks.CLOD_BLOCK));
	public static final RegistryObject<Item> GRAVEL_FENCE = ITEMS.register("gravel_fence",
			() -> createWithoutItemGroup(ModBlocks.GRAVEL_FENCE));
	public static final RegistryObject<Item> GRAVEL_GATE = ITEMS.register("gravel_gate",
			() -> createWithoutItemGroup(ModBlocks.GRAVEL_GATE));
	public static final RegistryObject<Item> GRAVEL_BUTTON = ITEMS.register("gravel_button",
			() -> createWithoutItemGroup(ModBlocks.GRAVEL_BUTTON));
	public static final RegistryObject<Item> GRAVEL_SLAB = ITEMS.register("gravel_slab",
			() -> createWithoutItemGroup(ModBlocks.GRAVEL_SLAB));
	public static final RegistryObject<Item> GRAVEL_STAIRS = ITEMS.register("gravel_stairs",
			() -> createWithoutItemGroup(ModBlocks.GRAVEL_STAIRS));
	public static final RegistryObject<Item> DARK_SAND = ITEMS.register("dark_sand",
			() -> createWithoutItemGroup(ModBlocks.DARK_SAND));
	public static final RegistryObject<Item> DARK_SAND_FENCE = ITEMS.register("dark_sand_fence",
			() -> createWithoutItemGroup(ModBlocks.DARK_SAND_FENCE));
	public static final RegistryObject<Item> DARK_SAND_GATE = ITEMS.register("dark_sand_gate",
			() -> createWithoutItemGroup(ModBlocks.DARK_SAND_GATE));
	public static final RegistryObject<Item> DARK_SAND_BUTTON = ITEMS.register("dark_sand_button",
			() -> createWithoutItemGroup(ModBlocks.DARK_SAND_BUTTON));
	public static final RegistryObject<Item> DARK_SAND_SLAB = ITEMS.register("dark_sand_slab",
			() -> createWithoutItemGroup(ModBlocks.DARK_SAND_SLAB));
	public static final RegistryObject<Item> DARK_SAND_STAIRS = ITEMS.register("dark_sand_stairs",
			() -> createWithoutItemGroup(ModBlocks.DARK_SAND_STAIRS));
	public static final RegistryObject<Item> CLAY_FENCE = ITEMS.register("clay_fence",
			() -> createWithoutItemGroup(ModBlocks.CLAY_FENCE));
	public static final RegistryObject<Item> CLAY_GATE = ITEMS.register("clay_gate",
			() -> createWithoutItemGroup(ModBlocks.CLAY_GATE));
	public static final RegistryObject<Item> CLAY_BUTTON = ITEMS.register("clay_button",
			() -> createWithoutItemGroup(ModBlocks.CLAY_BUTTON));
	public static final RegistryObject<Item> CLAY_SLAB = ITEMS.register("clay_slab",
			() -> createWithoutItemGroup(ModBlocks.CLAY_SLAB));
	public static final RegistryObject<Item> CLAY_STAIRS = ITEMS.register("clay_stairs",
			() -> createWithoutItemGroup(ModBlocks.CLAY_STAIRS));
	public static final RegistryObject<Item> MUD_FENCE = ITEMS.register("mud_fence",
			() -> createWithoutItemGroup(ModBlocks.MUD_FENCE));
	public static final RegistryObject<Item> MUD_GATE = ITEMS.register("mud_gate",
			() -> createWithoutItemGroup(ModBlocks.MUD_GATE));
	public static final RegistryObject<Item> MUD_BUTTON = ITEMS.register("mud_button",
			() -> createWithoutItemGroup(ModBlocks.MUD_BUTTON));
	public static final RegistryObject<Item> MUD_SLAB = ITEMS.register("mud_slab",
			() -> createWithoutItemGroup(ModBlocks.MUD_SLAB));
	public static final RegistryObject<Item> MUD_STAIRS = ITEMS.register("mud_stairs",
			() -> createWithoutItemGroup(ModBlocks.MUD_STAIRS));
	public static final RegistryObject<Item> UNRAVELED_SPIKE = ITEMS.register("unraveled_spike",
			() -> createWithoutItemGroup(ModBlocks.UNRAVELED_SPIKE));
	public static final RegistryObject<Item> UNRAVELED_FENCE = ITEMS.register("unraveled_fence",
			() -> createWithoutItemGroup(ModBlocks.UNRAVELED_FENCE));
	public static final RegistryObject<Item> UNRAVELED_GATE = ITEMS.register("unraveled_gate",
			() -> createWithoutItemGroup(ModBlocks.UNRAVELED_GATE));
	public static final RegistryObject<Item> UNRAVELED_BUTTON = ITEMS.register("unraveled_button",
			() -> createWithoutItemGroup(ModBlocks.UNRAVELED_BUTTON));
	public static final RegistryObject<Item> UNRAVELED_SLAB = ITEMS.register("unraveled_slab",
			() -> createWithoutItemGroup(ModBlocks.UNRAVELED_SLAB));
	public static final RegistryObject<Item> UNRAVELED_STAIRS = ITEMS.register("unraveled_stairs",
			() -> createWithoutItemGroup(ModBlocks.UNRAVELED_STAIRS));
	public static final Set<Item> DOOR_ITEMS = new HashSet<>();

	private static Item createWithoutItemGroup(Block block) {
		return create(new BlockItem(block, (new Item.Properties())));
	}

	private static Item create(Block block) {
		return create(new BlockItem(block, (new Item.Properties())));
	}
	
	private static Item create() {
		return create(new Item(new Item.Properties()));
	}

	private static Item create(Item item) {
		if (item instanceof BlockItem) {
			((BlockItem) item).registerBlocks(Item.BY_BLOCK, item);
		}
		RegistryHandler.addCreativeItem(item);
		return item;
	}

	public static void init(IEventBus bus) {
		ITEMS.register(bus);
	}
}
