package org.dimdev.dimdoors.item;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.EscapeTarget;
import org.dimdev.dimdoors.rift.targets.PrivatePocketExitTarget;
import org.dimdev.dimdoors.rift.targets.PrivatePocketTarget;
import org.dimdev.dimdoors.rift.targets.PublicPocketTarget;
import org.dimdev.dimdoors.rift.targets.RandomTarget;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.util.registry.Registrar;
import org.dimdev.dimdoors.util.registry.RegistryObject;
import org.dimdev.dimdoors.world.ModDimensions;

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
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;

@SuppressWarnings("unused")
@Registrar(element = Item.class)
public final class ModItems {
	// DO NOT REMOVE!!!
	public static final Registry<Item> REGISTRY = Registry.ITEM;

	public static final ItemGroup DIMENSIONAL_DOORS = FabricItemGroupBuilder
			.create(new Identifier("dimdoors", "dimensional_doors"))
			.icon(() -> new ItemStack(ModItems.IRON_DIMENSIONAL_DOOR))
			.build();

	@RegistryObject("quartz_door")
	public static final Item QUARTZ_DOOR = create(ModBlocks.QUARTZ_DOOR);

	@RegistryObject("quartz_dimensional_door")
	public static final Item QUARTZ_DIMENSIONAL_DOOR = create(new DimensionalDoorItem(
			ModBlocks.QUARTZ_DIMENSIONAL_DOOR,
			new Item.Settings().group(DIMENSIONAL_DOORS).maxCount(1),
			rift -> {
				if (ModDimensions.isPocketDimension(rift.getWorld())) {
					rift.setDestination(new PrivatePocketExitTarget()); // exit
				} else {
					rift.setDestination(new PrivatePocketTarget()); // entrances
				}
			}
	));

	@RegistryObject("gold_door")
	public static final Item GOLD_DOOR = create(ModBlocks.GOLD_DOOR);

	@RegistryObject("gold_dimensional_door")
	public static final Item GOLD_DIMENSIONAL_DOOR = create(new DimensionalDoorItem(
			ModBlocks.GOLD_DIMENSIONAL_DOOR,
			new Item.Settings().group(DIMENSIONAL_DOORS).maxCount(1),
			rift -> {
				rift.setProperties(LinkProperties.builder()
						.groups(new HashSet<>(Arrays.asList(0, 1)))
						.linksRemaining(1).build());

				rift.setDestination(RandomTarget.builder()
						.acceptedGroups(Collections.singleton(0))
						.coordFactor(1)
						.negativeDepthFactor(10000)
						.positiveDepthFactor(80)
						.weightMaximum(100)
						.noLink(false)
						.noLinkBack(false)
						.newRiftWeight(1).build());
			}
	));

	@RegistryObject("iron_dimensional_door")
	public static final Item IRON_DIMENSIONAL_DOOR = create(new DimensionalDoorItem(
			ModBlocks.IRON_DIMENSIONAL_DOOR,
			new Item.Settings().group(DIMENSIONAL_DOORS).maxCount(1),
			rift -> {
				PublicPocketTarget destination = new PublicPocketTarget();
				rift.setDestination(destination);
			}
	));

	@RegistryObject("unstable_dimensional_door")
    public static final Item UNSTABLE_DIMENSIONAL_DOOR = create(new DimensionalDoorItem(
            ModBlocks.IRON_DIMENSIONAL_DOOR,
            new Item.Settings().group(DIMENSIONAL_DOORS).maxCount(1),
            rift -> rift.setDestination(RandomTarget.builder()
					.acceptedGroups(Collections.singleton(0))
					.coordFactor(1)
					.negativeDepthFactor(80)
					.positiveDepthFactor(Double.MAX_VALUE)
					.weightMaximum(100)
					.noLink(false)
					.noLinkBack(false)
					.newRiftWeight(0.5F).build())
    ));

	@RegistryObject("oak_dimensional_door")
	public static final Item OAK_DIMENSIONAL_DOOR = create(new DimensionalDoorItem(
			ModBlocks.OAK_DIMENSIONAL_DOOR,
			new Item.Settings().group(DIMENSIONAL_DOORS).maxCount(1),
			rift -> rift.setDestination(
					RandomTarget.builder()
							.acceptedGroups(Collections.singleton(0))
							.coordFactor(1)
							.negativeDepthFactor(80)
							.positiveDepthFactor(Double.MAX_VALUE)
							.weightMaximum(100)
							.noLink(false).newRiftWeight(0).build()
			)
	));

	@RegistryObject("wood_dimensional_trapdoor")
	public static final Item OAK_DIMENSIONAL_TRAPDOOR = create(new DimensionalTrapdoorItem(
			ModBlocks.OAK_DIMENSIONAL_TRAPDOOR,
			new Item.Settings().group(DIMENSIONAL_DOORS).maxCount(1),
			rift -> rift.setDestination(new EscapeTarget(false))
	));


	@RegistryObject("world_thread")
	public static final Item WORLD_THREAD = create(new Item(new Item.Settings().group(DIMENSIONAL_DOORS)));

	@RegistryObject("rift_configuration_tool")
	public static final Item RIFT_CONFIGURATION_TOOL = create(new RiftConfigurationToolItem());

	@RegistryObject("rift_blade")
	public static final Item RIFT_BLADE = create(new RiftBladeItem(new Item.Settings().maxDamage(100).group(DIMENSIONAL_DOORS)));

	@RegistryObject("rift_remover")
	public static final Item RIFT_REMOVER = create(new RiftRemoverItem(new Item.Settings().maxCount(1).maxDamage(100).group(DIMENSIONAL_DOORS)));

	@RegistryObject("rift_signature")
	public static final Item RIFT_SIGNATURE = create(new RiftSignatureItem(new Item.Settings().maxCount(1).maxDamage(1).group(DIMENSIONAL_DOORS)));

	@RegistryObject("stabilized_rift_signature")
	public static final Item STABILIZED_RIFT_SIGNATURE = create(new StabilizedRiftSignatureItem(new Item.Settings().maxCount(1).maxDamage(20).group(DIMENSIONAL_DOORS)));

	@RegistryObject("rift_stabilizer")
	public static final Item RIFT_STABILIZER = create(new RiftStabilizerItem(new Item.Settings().maxCount(1).maxDamage(6).group(DIMENSIONAL_DOORS)));

	@RegistryObject("dimensional_eraser")
	public static final Item DIMENSIONAL_ERASER = create(new DimensionalEraserItem(new Item.Settings().maxDamage(100).group(DIMENSIONAL_DOORS)));

	@RegistryObject("monolith_spawner")
	public static final Item MONOLITH_SPAWNER = new SpawnEggItem(ModEntityTypes.MONOLITH, 0xffffff, 0xffffff, new Item.Settings().group(ItemGroup.MISC));

	@RegistryObject("world_thread_helmet")
	public static final Item WORLD_THREAD_HELMET = create(new ArmorItem(ModArmorMaterials.WORLD_THREAD, EquipmentSlot.HEAD, new Item.Settings().group(DIMENSIONAL_DOORS)));

	@RegistryObject("world_thread_chestplate")
	public static final Item WORLD_THREAD_CHESTPLATE = create(new ArmorItem(ModArmorMaterials.WORLD_THREAD, EquipmentSlot.CHEST, new Item.Settings().group(DIMENSIONAL_DOORS)));

	@RegistryObject("world_thread_leggings")
	public static final Item WORLD_THREAD_LEGGINGS = create(new ArmorItem(ModArmorMaterials.WORLD_THREAD, EquipmentSlot.LEGS, new Item.Settings().group(DIMENSIONAL_DOORS)));

	@RegistryObject("world_thread_boots")
	public static final Item WORLD_THREAD_BOOTS = create(new ArmorItem(ModArmorMaterials.WORLD_THREAD, EquipmentSlot.FEET, new Item.Settings().group(DIMENSIONAL_DOORS)));


	@RegistryObject("stable_fabric")
	public static final Item STABLE_FABRIC = create(new Item(new Item.Settings().group(DIMENSIONAL_DOORS)));

	@RegistryObject("white_fabric")
	public static final Item WHITE_FABRIC = create(ModBlocks.WHITE_FABRIC);

	@RegistryObject("orange_fabric")
	public static final Item ORANGE_FABRIC = create(ModBlocks.ORANGE_FABRIC);

	@RegistryObject("magenta_fabric")
	public static final Item MAGENTA_FABRIC = create(ModBlocks.MAGENTA_FABRIC);

	@RegistryObject("light_blue_fabric")
	public static final Item LIGHT_BLUE_FABRIC = create(ModBlocks.LIGHT_BLUE_FABRIC);

	@RegistryObject("yellow_fabric")
	public static final Item YELLOW_FABRIC = create(ModBlocks.YELLOW_FABRIC);

	@RegistryObject("lime_fabric")
	public static final Item LIME_FABRIC = create(ModBlocks.LIME_FABRIC);

	@RegistryObject("pink_fabric")
	public static final Item PINK_FABRIC = create(ModBlocks.PINK_FABRIC);

	@RegistryObject("gray_fabric")
	public static final Item GRAY_FABRIC = create(ModBlocks.GRAY_FABRIC);

	@RegistryObject("light_gray_fabric")
	public static final Item LIGHT_GRAY_FABRIC = create(ModBlocks.LIGHT_GRAY_FABRIC);

	@RegistryObject("cyan_fabric")
	public static final Item CYAN_FABRIC = create(ModBlocks.CYAN_FABRIC);

	@RegistryObject("purple_fabric")
	public static final Item PURPLE_FABRIC = create(ModBlocks.PURPLE_FABRIC);

	@RegistryObject("blue_fabric")
	public static final Item BLUE_FABRIC = create(ModBlocks.BLUE_FABRIC);

	@RegistryObject("brown_fabric")
	public static final Item BROWN_FABRIC = create(ModBlocks.BROWN_FABRIC);

	@RegistryObject("green_fabric")
	public static final Item GREEN_FABRIC = create(ModBlocks.GREEN_FABRIC);

	@RegistryObject("red_fabric")
	public static final Item RED_FABRIC = create(ModBlocks.RED_FABRIC);

	@RegistryObject("black_fabric")
	public static final Item BLACK_FABRIC = create(ModBlocks.BLACK_FABRIC);

	@RegistryObject("white_ancient_fabric")
	public static final Item WHITE_ANCIENT_FABRIC = create(ModBlocks.WHITE_ANCIENT_FABRIC);

	@RegistryObject("orange_ancient_fabric")
	public static final Item ORANGE_ANCIENT_FABRIC = create(ModBlocks.ORANGE_ANCIENT_FABRIC);

	@RegistryObject("magenta_ancient_fabric")
	public static final Item MAGENTA_ANCIENT_FABRIC = create(ModBlocks.MAGENTA_ANCIENT_FABRIC);

	@RegistryObject("light_blue_ancient_fabric")
	public static final Item LIGHT_BLUE_ANCIENT_FABRIC = create(ModBlocks.LIGHT_BLUE_ANCIENT_FABRIC);

	@RegistryObject("yellow_ancient_fabric")
	public static final Item YELLOW_ANCIENT_FABRIC = create(ModBlocks.YELLOW_ANCIENT_FABRIC);

	@RegistryObject("lime_ancient_fabric")
	public static final Item LIME_ANCIENT_FABRIC = create(ModBlocks.LIME_ANCIENT_FABRIC);

	@RegistryObject("pink_ancient_fabric")
	public static final Item PINK_ANCIENT_FABRIC = create(ModBlocks.PINK_ANCIENT_FABRIC);

	@RegistryObject("gray_ancient_fabric")
	public static final Item GRAY_ANCIENT_FABRIC = create(ModBlocks.GRAY_ANCIENT_FABRIC);

	@RegistryObject("light_gray_ancient_fabric")
	public static final Item LIGHT_GRAY_ANCIENT_FABRIC = create(ModBlocks.LIGHT_GRAY_ANCIENT_FABRIC);

	@RegistryObject("cyan_ancient_fabric")
	public static final Item CYAN_ANCIENT_FABRIC = create(ModBlocks.CYAN_ANCIENT_FABRIC);

	@RegistryObject("purple_ancient_fabric")
	public static final Item PURPLE_ANCIENT_FABRIC = create(ModBlocks.PURPLE_ANCIENT_FABRIC);

	@RegistryObject("blue_ancient_fabric")
	public static final Item BLUE_ANCIENT_FABRIC = create(ModBlocks.BLUE_ANCIENT_FABRIC);

	@RegistryObject("brown_ancient_fabric")
	public static final Item BROWN_ANCIENT_FABRIC = create(ModBlocks.BROWN_ANCIENT_FABRIC);

	@RegistryObject("green_ancient_fabric")
	public static final Item GREEN_ANCIENT_FABRIC = create(ModBlocks.GREEN_ANCIENT_FABRIC);

	@RegistryObject("red_ancient_fabric")
	public static final Item RED_ANCIENT_FABRIC = create(ModBlocks.RED_ANCIENT_FABRIC);

	@RegistryObject("black_ancient_fabric")
	public static final Item BLACK_ANCIENT_FABRIC = create(ModBlocks.BLACK_ANCIENT_FABRIC);


	@RegistryObject("unravelled_fabric")
	public static final Item UNRAVELLED_FABRIC = create(ModBlocks.UNRAVELLED_FABRIC);

	@RegistryObject("creepy_record")
	public static final Item CREEPY_RECORD = create(new MusicDiscItem(10, ModSoundEvents.CREEPY, new Item.Settings().group(DIMENSIONAL_DOORS)));

	@RegistryObject("white_void_record")
	public static final Item WHITE_VOID_RECORD = create(new MusicDiscItem(10, ModSoundEvents.WHITE_VOID, new Item.Settings().group(DIMENSIONAL_DOORS)));

	@RegistryObject("marking_plate")
	public static final Item MARKING_PLATE = create(ModBlocks.MARKING_PLATE);

	@RegistryObject("eternal_fluid")
	public static final Item ETERNAL_FLUID = create(ModBlocks.ETERNAL_FLUID);

	@RegistryObject("eternal_fluid_bucket")
	public static final Item ETERNAL_FLUID_BUCKET = create(new BucketItem(ModFluids.ETERNAL_FLUID, new Item.Settings().group(DIMENSIONAL_DOORS).recipeRemainder(Items.BUCKET).maxCount(1)));

	private static Item create(Block block) {
		return create(new BlockItem(block, (new Item.Settings()).group(DIMENSIONAL_DOORS)));
	}

//	private static Item create(BlockItem blockItem) {
//		return create(blockItem.getBlock(), blockItem);
//	}
//
//	private static Item create(Block block, Item item) {
//		return create(item);
//	}

	private static Item create(Item item) {
		if (item instanceof BlockItem) {
			((BlockItem) item).appendBlocks(Item.BLOCK_ITEMS, item);
		}

		return item;
	}

	public static void init() {
		Registrar.REGISTER.accept(ModItems.class);
	}

	private static class MusicDiscItem extends net.minecraft.item.MusicDiscItem {
		protected MusicDiscItem(int comparatorOutput, SoundEvent soundEvent, Settings settings) {
			super(comparatorOutput, soundEvent, settings);
		}
	}
}
