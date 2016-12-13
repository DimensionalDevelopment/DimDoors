package com.zixiken.dimdoors.items;

import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import com.zixiken.dimdoors.blocks.ModBlocks;

public class ModItems {
    public static ItemDimDoorGold itemDimDoorGold;
    public static ItemDoorGold itemDoorGold;
    public static ItemWorldThread itemWorldThread;
    public static ItemDimDoor itemDimDoor;
    public static ItemDimDoorWarp itemDimDoorWarp;
    public static ItemStableFabric itemStableFabric;
    public static ItemDimDoorUnstable itemDimDoorChaos;
    public static ItemDoorQuartz itemDoorQuartz;
    public static ItemDimDoorPersonal itemDimDoorPersonal;
    public static ItemBlockDimWall itemBlockDimWall;

    public static void registerItems() {
        GameRegistry.registerItem(itemDoorQuartz = new ItemDoorQuartz(), ItemDoorQuartz.ID);
        GameRegistry.registerItem(itemDimDoorPersonal = new ItemDimDoorPersonal(), ItemDimDoorPersonal.ID);
        GameRegistry.registerItem(itemDoorGold = new ItemDoorGold(), ItemDoorGold.ID);
        GameRegistry.registerItem(itemDimDoorGold = new ItemDimDoorGold(), ItemDimDoorGold.ID);
        GameRegistry.registerItem(itemDimDoor = new ItemDimDoor(), ItemDimDoor.ID);
        GameRegistry.registerItem(itemDimDoorWarp = new ItemDimDoorWarp(), ItemDimDoorWarp.ID);
        GameRegistry.registerItem(itemStableFabric = new ItemStableFabric(), ItemStableFabric.ID);
        GameRegistry.registerItem(itemDimDoorChaos = new ItemDimDoorUnstable(), ItemDimDoorUnstable.ID);
        GameRegistry.registerItem(itemWorldThread = new ItemWorldThread(), ItemWorldThread.ID);

        //ItemBlocks
        GameRegistry.register(itemBlockDimWall = new ItemBlockDimWall());
        GameRegistry.register(new ItemBlock(ModBlocks.blockDimHatch)
                .setRegistryName(ModBlocks.blockDimHatch.getRegistryName()));
        GameRegistry.register(new ItemBlock(ModBlocks.blockRift)
                .setRegistryName(ModBlocks.blockRift.getRegistryName()));
    }
}
