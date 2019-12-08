package org.dimdev.dimdoors.item;

import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.*;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.world.pocketdimension.PersonalPocketDimension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public final class ModItems {
    public static final Item GOLD_DOOR = register(ModBlocks.GOLD_DOOR);
    public static final Item QUARTZ_DOOR = register(ModBlocks.QUARTZ_DOOR);

    public static final Item IRON_DIMENSIONAL_DOOR = new DimensionalDoorItem(
            ModBlocks.IRON_DIMENSIONAL_DOOR,
            new Item.Settings().group(ModItemGroups.DIMENSIONAL_DOORS).maxCount(1),
            rift -> {
                PublicPocketTarget destination = new PublicPocketTarget();
                rift.setDestination(destination);
            }
    );

    public static final Item GOLD_DIMENSIONAL_DOOR = new DimensionalDoorItem(
            ModBlocks.GOLD_DIMENSIONAL_DOOR,
            new Item.Settings().group(ModItemGroups.DIMENSIONAL_DOORS).maxCount(1),
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
    );

    public static final Item QUARTZ_DIMENSIONAL_DOOR = new DimensionalDoorItem(
            ModBlocks.QUARTZ_DIMENSIONAL_DOOR,
            new Item.Settings().group(ModItemGroups.DIMENSIONAL_DOORS).maxCount(1),
            rift -> {
                if (rift.getWorld().dimension instanceof PersonalPocketDimension) {
                    rift.setDestination(new PrivatePocketExitTarget()); // exit
                } else {
                    rift.setDestination(new PrivatePocketTarget()); // entrances
                }
            }
    );

    public static final Item UNSTABLE_DIMENSIONAL_DOOR = new DimensionalDoorItem(
            ModBlocks.IRON_DIMENSIONAL_DOOR,
            new Item.Settings().group(ModItemGroups.DIMENSIONAL_DOORS).maxCount(1),
            rift -> {
                // TODO
            }
    );

    public static final Item WOOD_DIMENSIONAL_DOOR = new DimensionalDoorItem(
            ModBlocks.WOOD_DIMENSIONAL_DOOR,
            new Item.Settings().group(ModItemGroups.DIMENSIONAL_DOORS).maxCount(1),
            rift -> rift.setDestination(
                    RandomTarget
                            .builder()
                            .acceptedGroups(Collections.singleton(0))
                            .coordFactor(1)
                            .negativeDepthFactor(80)
                            .positiveDepthFactor(Double.MAX_VALUE)
                            .weightMaximum(100)
                            .noLink(false).newRiftWeight(0).build()
            )
    );

    public static final Item WOOD_DIMENSIONAL_TRAPDOOR = new DimensionalTrapdoorItem(
            ModBlocks.WOOD_DIMENSIONAL_DOOR,
            new Item.Settings().group(ModItemGroups.DIMENSIONAL_DOORS).maxCount(1),
            rift -> rift.setDestination(new EscapeTarget(false))
    );

    public static final Item WHITE_FABRIC = register(ModBlocks.WHITE_FABRIC);
    public static final Item ORANGE_FABRIC = register(ModBlocks.ORANGE_FABRIC);
    public static final Item MAGENTA_FABRIC = register(ModBlocks.MAGENTA_FABRIC);
    public static final Item LIGHT_BLUE_FABRIC = register(ModBlocks.LIGHT_BLUE_FABRIC);
    public static final Item YELLOW_FABRIC = register(ModBlocks.YELLOW_FABRIC);
    public static final Item LIME_FABRIC = register(ModBlocks.LIME_FABRIC);
    public static final Item PINK_FABRIC = register(ModBlocks.PINK_FABRIC);
    public static final Item GRAY_FABRIC = register(ModBlocks.GRAY_FABRIC);
    public static final Item LIGHT_GRAY_FABRIC = register(ModBlocks.LIGHT_GRAY_FABRIC);
    public static final Item CYAN_FABRIC = register(ModBlocks.CYAN_FABRIC);
    public static final Item PURPLE_FABRIC = register(ModBlocks.PURPLE_FABRIC);
    public static final Item BLUE_FABRIC = register(ModBlocks.BLUE_FABRIC);
    public static final Item BROWN_FABRIC = register(ModBlocks.BROWN_FABRIC);
    public static final Item GREEN_FABRIC = register(ModBlocks.GREEN_FABRIC);
    public static final Item RED_FABRIC = register(ModBlocks.RED_FABRIC);
    public static final Item BLACK_FABRIC = register(ModBlocks.BLACK_FABRIC);

    public static final Item WHITE_ANCIENT_FABRIC = register(ModBlocks.WHITE_ANCIENT_FABRIC);
    public static final Item ORANGE_ANCIENT_FABRIC = register(ModBlocks.ORANGE_ANCIENT_FABRIC);
    public static final Item MAGENTA_ANCIENT_FABRIC = register(ModBlocks.MAGENTA_ANCIENT_FABRIC);
    public static final Item LIGHT_BLUE_ANCIENT_FABRIC = register(ModBlocks.LIGHT_BLUE_ANCIENT_FABRIC);
    public static final Item YELLOW_ANCIENT_FABRIC = register(ModBlocks.YELLOW_ANCIENT_FABRIC);
    public static final Item LIME_ANCIENT_FABRIC = register(ModBlocks.LIME_ANCIENT_FABRIC);
    public static final Item PINK_ANCIENT_FABRIC = register(ModBlocks.PINK_ANCIENT_FABRIC);
    public static final Item GRAY_ANCIENT_FABRIC = register(ModBlocks.GRAY_ANCIENT_FABRIC);
    public static final Item LIGHT_GRAY_ANCIENT_FABRIC = register(ModBlocks.LIGHT_GRAY_ANCIENT_FABRIC);
    public static final Item CYAN_ANCIENT_FABRIC = register(ModBlocks.CYAN_ANCIENT_FABRIC);
    public static final Item PURPLE_ANCIENT_FABRIC = register(ModBlocks.PURPLE_ANCIENT_FABRIC);
    public static final Item BLUE_ANCIENT_FABRIC = register(ModBlocks.BLUE_ANCIENT_FABRIC);
    public static final Item BROWN_ANCIENT_FABRIC = register(ModBlocks.BROWN_ANCIENT_FABRIC);
    public static final Item GREEN_ANCIENT_FABRIC = register(ModBlocks.GREEN_ANCIENT_FABRIC);
    public static final Item RED_ANCIENT_FABRIC = register(ModBlocks.RED_ANCIENT_FABRIC);
    public static final Item BLACK_ANCIENT_FABRIC = register(ModBlocks.BLACK_ANCIENT_FABRIC);

    public static final Item ETERNAL_FABRIC = register(ModBlocks.ETERNAL_FABRIC);
    public static final Item UNRAVELLED_FABRIC = register(ModBlocks.UNRAVELLED_FABRIC);

    public static final Item MARKING_PLATE = register(ModBlocks.MARKING_PLATE);

    // Dimensional doors

    public static final Item WORLD_THREAD = new Item(new Item.Settings());
    public static final Item STABLE_FABRIC = new Item(new Item.Settings());
    public static final Item RIFT_CONFIGURATION_TOOL = new RiftConfigurationToolItem();
    public static final Item RIFT_BLADE = new RiftBladeItem(new Item.Settings().maxDamage(100));
    public static final Item RIFT_REMOVER = new RiftRemoverItem(new Item.Settings().maxDamage(100));
    public static final Item RIFT_SIGNATURE = new RiftSignatureItem(new Item.Settings().maxDamage(1));
    public static final Item STABILIZED_RIFT_SIGNATURE = new StabilizedRiftSignatureItem(new Item.Settings().maxDamage(20));
    public static final Item RIFT_STABILIZER = new RiftStabilizerItem(new Item.Settings().maxDamage(6));
    public static final Item WOVEN_WORLD_THREAD_HELMET = new WorldThreadArmorItem("world_thread_helmet", 1, EquipmentSlot.HEAD);
    public static final Item WOVEN_WORLD_THREAD_CHESTPLATE = new WorldThreadArmorItem("world_thread_chestplate", 1, EquipmentSlot.CHEST);
    public static final Item WOVEN_WORLD_THREAD_LEGGINGS = new WorldThreadArmorItem("world_thread_leggings", 2, EquipmentSlot.LEGS);
    public static final Item WOVEN_WORLD_THREAD_BOOTS = new WorldThreadArmorItem("world_thread_boots", 1, EquipmentSlot.FEET);
    public static final Item CREEPY_RECORD = new MusicDiscItem(10, ModSoundEvents.CREEPY, new Item.Settings().group(ModItemGroups.DIMENSIONAL_DOORS));
    public static final Item WHITE_VOID_RECORD = new MusicDiscItem(10, ModSoundEvents.CREEPY, new Item.Settings().group(ModItemGroups.DIMENSIONAL_DOORS));

    private static Item register(Block block) {
        return register(new BlockItem(block, new Item.Settings()));
    }

    private static Item register(Block block, ItemGroup itemGroup) {
        return register(new BlockItem(block, (new Item.Settings()).group(itemGroup)));
    }

    private static Item register(BlockItem blockItem) {
        return register(blockItem.getBlock(), blockItem);
    }

    protected static Item register(Block block, Item item) {
        return register(Registry.BLOCK.getId(block), item);
    }

    private static Item register(Identifier identifier, Item item) {
        if (item instanceof BlockItem) {
            ((BlockItem) item).appendBlocks(Item.BLOCK_ITEMS, item);
        }

        return Registry.register(Registry.ITEM, identifier, item);
    }

    private static class MusicDiscItem extends net.minecraft.item.MusicDiscItem { // TODO: access transformers
        protected MusicDiscItem(int i, SoundEvent soundEvent, Settings settings) {
            super(i, soundEvent, settings);
        }
    }
}
