package org.dimdev.dimdoors.item;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public final class ModItemGroups {
    public static final ItemGroup DIMENSIONAL_DOORS = FabricItemGroupBuilder
            .create(new Identifier("dimdoors", "dimensional_doors"))
            .icon(() -> new ItemStack(ModItems.IRON_DIMENSIONAL_DOOR))
            .appendItems(items -> items.addAll(Lists.newArrayList(
                    new ItemStack(ModItems.QUARTZ_DOOR),
                    new ItemStack(ModItems.QUARTZ_DIMENSIONAL_DOOR),
                    new ItemStack(ModItems.GOLD_DOOR),
                    new ItemStack(ModItems.GOLD_DIMENSIONAL_DOOR),
                    new ItemStack(ModItems.IRON_DIMENSIONAL_DOOR),
                    new ItemStack(ModItems.OAK_DIMENSIONAL_DOOR),
//                    new ItemStack(ModItems.UNSTABLE_DIMENSIONAL_DOOR),
                    new ItemStack(ModItems.OAK_DIMENSIONAL_TRAPDOOR),
                    new ItemStack(ModItems.WORLD_THREAD),
                    new ItemStack(ModItems.RIFT_CONFIGURATION_TOOL),
                    new ItemStack(ModItems.RIFT_BLADE),
                    new ItemStack(ModItems.RIFT_REMOVER),
                    new ItemStack(ModItems.RIFT_SIGNATURE),
                    new ItemStack(ModItems.STABILIZED_RIFT_SIGNATURE),
                    new ItemStack(ModItems.RIFT_STABILIZER),
                    new ItemStack(ModItems.WORLD_THREAD_HELMET),
                    new ItemStack(ModItems.WORLD_THREAD_CHESTPLATE),
                    new ItemStack(ModItems.WORLD_THREAD_LEGGINGS),
                    new ItemStack(ModItems.WORLD_THREAD_BOOTS),
                    new ItemStack(ModItems.STABLE_FABRIC),
                    new ItemStack(ModItems.WHITE_FABRIC),
                    new ItemStack(ModItems.ORANGE_FABRIC),
                    new ItemStack(ModItems.MAGENTA_FABRIC),
                    new ItemStack(ModItems.LIGHT_BLUE_FABRIC),
                    new ItemStack(ModItems.YELLOW_FABRIC),
                    new ItemStack(ModItems.LIME_FABRIC),
                    new ItemStack(ModItems.PINK_FABRIC),
                    new ItemStack(ModItems.GRAY_FABRIC),
                    new ItemStack(ModItems.LIGHT_GRAY_FABRIC),
                    new ItemStack(ModItems.CYAN_FABRIC),
                    new ItemStack(ModItems.PURPLE_FABRIC),
                    new ItemStack(ModItems.BLUE_FABRIC),
                    new ItemStack(ModItems.BROWN_FABRIC),
                    new ItemStack(ModItems.GREEN_FABRIC),
                    new ItemStack(ModItems.RED_FABRIC),
                    new ItemStack(ModItems.BLACK_FABRIC),

                    new ItemStack(ModItems.WHITE_ANCIENT_FABRIC),
                    new ItemStack(ModItems.ORANGE_ANCIENT_FABRIC),
                    new ItemStack(ModItems.MAGENTA_ANCIENT_FABRIC),
                    new ItemStack(ModItems.LIGHT_BLUE_ANCIENT_FABRIC),
                    new ItemStack(ModItems.YELLOW_ANCIENT_FABRIC),
                    new ItemStack(ModItems.LIME_ANCIENT_FABRIC),
                    new ItemStack(ModItems.PINK_ANCIENT_FABRIC),
                    new ItemStack(ModItems.GRAY_ANCIENT_FABRIC),
                    new ItemStack(ModItems.LIGHT_GRAY_ANCIENT_FABRIC),
                    new ItemStack(ModItems.CYAN_ANCIENT_FABRIC),
                    new ItemStack(ModItems.PURPLE_ANCIENT_FABRIC),
                    new ItemStack(ModItems.BLUE_ANCIENT_FABRIC),
                    new ItemStack(ModItems.BROWN_ANCIENT_FABRIC),
                    new ItemStack(ModItems.GREEN_ANCIENT_FABRIC),
                    new ItemStack(ModItems.RED_ANCIENT_FABRIC),
                    new ItemStack(ModItems.BLACK_ANCIENT_FABRIC),
                    new ItemStack(ModItems.UNRAVELLED_FABRIC),
                    new ItemStack(ModItems.CREEPY_RECORD),
                    new ItemStack(ModItems.MARKING_PLATE)
            )))
            .build();
}
