package com.zixiken.dimdoors.shared.items;

import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;

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
    public static ItemBlockFabric itemBlockFabric;
    public static ItemRiftConnectionTool itemRiftConnectionTool;
    public static ItemRiftBlade itemRiftBlade;

    public static void registerItems() {
        GameRegistry.register(itemDoorQuartz = new ItemDoorQuartz());
        GameRegistry.register(itemDimDoorPersonal = new ItemDimDoorPersonal());
        GameRegistry.register(itemDoorGold = new ItemDoorGold());
        GameRegistry.register(itemDimDoorGold = new ItemDimDoorGold());
        GameRegistry.register(itemDimDoor = new ItemDimDoor());
        GameRegistry.register(itemDimDoorWarp = new ItemDimDoorWarp());
        GameRegistry.register(itemStableFabric = new ItemStableFabric());
        GameRegistry.register(itemDimDoorChaos = new ItemDimDoorUnstable());
        GameRegistry.register(itemWorldThread = new ItemWorldThread());
        GameRegistry.register(itemRiftConnectionTool = new ItemRiftConnectionTool());
        GameRegistry.register(itemRiftBlade = new ItemRiftBlade());

        //ItemBlocks
        GameRegistry.register(itemBlockFabric = new ItemBlockFabric());
        GameRegistry.register(new ItemBlock(ModBlocks.blockDimHatch)
                .setRegistryName(ModBlocks.blockDimHatch.getRegistryName()));
        GameRegistry.register(new ItemBlock(ModBlocks.blockRift)
                .setRegistryName(ModBlocks.blockRift.getRegistryName()));
    }
}
