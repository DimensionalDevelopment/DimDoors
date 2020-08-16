package org.dimdev.dimdoors.item;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.EscapeTarget;
import org.dimdev.dimdoors.rift.targets.PrivatePocketExitTarget;
import org.dimdev.dimdoors.rift.targets.PrivatePocketTarget;
import org.dimdev.dimdoors.rift.targets.PublicPocketTarget;
import org.dimdev.dimdoors.rift.targets.RandomTarget;
import org.dimdev.dimdoors.sound.ModSoundEvents;
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
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;

@SuppressWarnings("unused")
public final class ModItems {
    public static final ItemGroup DIMENSIONAL_DOORS = FabricItemGroupBuilder
            .create(new Identifier("dimdoors", "dimensional_doors"))
            .icon(() -> new ItemStack(ModItems.IRON_DIMENSIONAL_DOOR))
            .build();

    public static final Item QUARTZ_DOOR = register(ModBlocks.QUARTZ_DOOR);
    public static final Item QUARTZ_DIMENSIONAL_DOOR = register(new DimensionalDoorItem(
            ModBlocks.QUARTZ_DIMENSIONAL_DOOR,
            new Item.Settings().group(DIMENSIONAL_DOORS).maxCount(1),
            rift -> {
                if (ModDimensions.isDimDoorsPocketDimension(rift.getWorld())) {
                    rift.setDestination(new PrivatePocketExitTarget()); // exit
                } else {
                    rift.setDestination(new PrivatePocketTarget()); // entrances
                }
            }
    ));

    public static final Item GOLD_DOOR = register(ModBlocks.GOLD_DOOR);
    public static final Item GOLD_DIMENSIONAL_DOOR = register(new DimensionalDoorItem(
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

    public static final Item IRON_DIMENSIONAL_DOOR = register(new DimensionalDoorItem(
            ModBlocks.IRON_DIMENSIONAL_DOOR,
            new Item.Settings().group(DIMENSIONAL_DOORS).maxCount(1),
            rift -> {
                PublicPocketTarget destination = new PublicPocketTarget();
                rift.setDestination(destination);
            }
    ));

//    public static final Item UNSTABLE_DIMENSIONAL_DOOR = register( new DimensionalDoorItem(
//            ModBlocks.IRON_DIMENSIONAL_DOOR,
//            new Item.Settings().group(DIMENSIONAL_DOORS).maxCount(1),
//            rift -> {
//                 TODO
//            }
//    ));

    public static final Item OAK_DIMENSIONAL_DOOR = register(new DimensionalDoorItem(
            ModBlocks.OAK_DIMENSIONAL_DOOR,
            new Item.Settings().group(DIMENSIONAL_DOORS).maxCount(1),
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
    ));

    public static final Item OAK_DIMENSIONAL_TRAPDOOR = register(new DimensionalTrapdoorItem(
            ModBlocks.OAK_DIMENSIONAL_TRAPDOOR,
            new Item.Settings().group(DIMENSIONAL_DOORS).maxCount(1),
            rift -> rift.setDestination(new EscapeTarget(false))
    ));

    public static final Item WORLD_THREAD = register(new Identifier("dimdoors:world_thread"), new Item(new Item.Settings().group(DIMENSIONAL_DOORS)));
    public static final Item RIFT_CONFIGURATION_TOOL = register(new Identifier("dimdoors:rift_configuration_tool"), new RiftConfigurationToolItem());
    public static final Item RIFT_BLADE = register(new Identifier("dimdoors:rift_blade"), new RiftBladeItem(new Item.Settings().maxDamage(100).group(DIMENSIONAL_DOORS)));
    public static final Item RIFT_REMOVER = register(new Identifier("dimdoors:rift_remover"), new RiftRemoverItem(new Item.Settings().maxCount(1).maxDamage(100).group(DIMENSIONAL_DOORS)));
    public static final Item RIFT_SIGNATURE = register(new Identifier("dimdoors:rift_signature"), new RiftSignatureItem(new Item.Settings().maxCount(1).maxDamage(1).group(DIMENSIONAL_DOORS)));
    public static final Item STABILIZED_RIFT_SIGNATURE = register(new Identifier("dimdoors:stabilized_rift_signature"), new StabilizedRiftSignatureItem(new Item.Settings().maxCount(1).maxDamage(20).group(DIMENSIONAL_DOORS)));
    public static final Item RIFT_STABILIZER = register(new Identifier("dimdoors:rift_stabilizer"), new RiftStabilizerItem(new Item.Settings().maxCount(1).maxDamage(6).group(DIMENSIONAL_DOORS)));

    public static final Item WORLD_THREAD_HELMET = register(new Identifier("dimdoors:world_thread_helmet"), new ArmorItem(ModArmorMaterials.WORLD_THREAD, EquipmentSlot.HEAD, new Item.Settings().group(DIMENSIONAL_DOORS)));
    public static final Item WORLD_THREAD_CHESTPLATE = register(new Identifier("dimdoors:world_thread_chestplate"), new ArmorItem(ModArmorMaterials.WORLD_THREAD, EquipmentSlot.CHEST, new Item.Settings().group(DIMENSIONAL_DOORS)));
    public static final Item WORLD_THREAD_LEGGINGS = register(new Identifier("dimdoors:world_thread_leggings"), new ArmorItem(ModArmorMaterials.WORLD_THREAD, EquipmentSlot.LEGS, new Item.Settings().group(DIMENSIONAL_DOORS)));
    public static final Item WORLD_THREAD_BOOTS = register(new Identifier("dimdoors:world_thread_boots"), new ArmorItem(ModArmorMaterials.WORLD_THREAD, EquipmentSlot.FEET, new Item.Settings().group(DIMENSIONAL_DOORS)));

    public static final Item STABLE_FABRIC = register(new Identifier("dimdoors:stable_fabric"), new Item(new Item.Settings().group(DIMENSIONAL_DOORS)));

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

    public static final Item UNRAVELLED_FABRIC = register(ModBlocks.UNRAVELLED_FABRIC);
    public static final Item CREEPY_RECORD = register(new Identifier("dimdoors:creepy_record"), new MusicDiscItem(10, ModSoundEvents.CREEPY, new Item.Settings().group(DIMENSIONAL_DOORS)));
    public static final Item WHITE_VOID_RECORD = register(new Identifier("dimdoors:white_void_record"), new MusicDiscItem(10, ModSoundEvents.CREEPY, new Item.Settings().group(DIMENSIONAL_DOORS)));
    public static final Item MARKING_PLATE = register(ModBlocks.MARKING_PLATE);
    public static final Item ETERNAL_FLUID = register(ModBlocks.ETERNAL_FLUID);
    public static final Item ETERNAL_FLUID_BUCKET = register(new Identifier("dimdoors:eternal_fluid_bucket"), new BucketItem(ModFluids.ETERNAL_FLUID, new Item.Settings().group(DIMENSIONAL_DOORS).recipeRemainder(Items.BUCKET).maxCount(1)));

    // Dimensional doors

    private static Item register(Block block) {
        return register(new BlockItem(block, (new Item.Settings()).group(DIMENSIONAL_DOORS)));
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

    public static void init() {
        // just loads the class
    }

    private static class MusicDiscItem extends net.minecraft.item.MusicDiscItem { // TODO: access transformers
        protected MusicDiscItem(int i, SoundEvent soundEvent, Settings settings) {
            super(i, soundEvent, settings);
        }
    }
}
