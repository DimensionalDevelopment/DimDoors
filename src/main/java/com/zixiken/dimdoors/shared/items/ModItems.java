package com.zixiken.dimdoors.shared.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;

public class ModItems {

    // Regular doors
    public static final ItemDoorGold GOLD_DOOR = new ItemDoorGold();
    public static final ItemDoorQuartz QUARTZ_DOOR = new ItemDoorQuartz();

    // Dimensional doors
    public static final ItemDimDoor DIMENSIONAL_DOOR = new ItemDimDoor();
    public static final ItemDimDoorGold GOLD_DIMENSIONAL_DOOR = new ItemDimDoorGold();
    public static final ItemDimDoorPersonal PERSONAL_DIMENSIONAL_DOOR = new ItemDimDoorPersonal();
    public static final ItemDimDoorUnstable UNSTABLE_DIMENSIONAL_DOOR = new ItemDimDoorUnstable();
    public static final ItemDimDoorWarp WARP_DIMENSIONAL_DOOR = new ItemDimDoorWarp();

    // Fabric
    public static final ItemWorldThread WORLD_THREAD = new ItemWorldThread();
    public static final ItemStableFabric STABLE_FABRIC = new ItemStableFabric();

    // Tools
    public static final ItemRiftConnectionTool RIFT_CONNECTION_TOOL = new ItemRiftConnectionTool();
    public static final ItemRiftBlade RIFT_BLADE = new ItemRiftBlade();

    // This needs to exist to be used in ModelManager.java before items are registered (preinitialization)
    public static final ItemBlockFabric FABRIC = new ItemBlockFabric();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                QUARTZ_DOOR,
                PERSONAL_DIMENSIONAL_DOOR,
                GOLD_DOOR,
                GOLD_DIMENSIONAL_DOOR,
                DIMENSIONAL_DOOR,
                WARP_DIMENSIONAL_DOOR,
                STABLE_FABRIC,
                UNSTABLE_DIMENSIONAL_DOOR,
                WORLD_THREAD,
                RIFT_CONNECTION_TOOL,
                RIFT_BLADE);

        // ItemBlocks
        event.getRegistry().registerAll(
                FABRIC,
                new ItemBlock(ModBlocks.DIMENSIONAL_TRAPDOOR).setRegistryName(ModBlocks.DIMENSIONAL_TRAPDOOR.getRegistryName()),
                new ItemBlock(ModBlocks.RIFT).setRegistryName(ModBlocks.RIFT.getRegistryName()));
    }
}
