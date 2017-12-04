package com.zixiken.dimdoors.shared.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;

public class ModItems {

    // Regular doors
    public final static ItemDoorGold GOLD_DOOR = new ItemDoorGold();
    public final static ItemDoorQuartz QUARTZ_DOOR = new ItemDoorQuartz();

    // Dimensional doors
    public final static ItemDimDoor DIMENSIONAL_DOOR = new ItemDimDoor();
    public final static ItemDimDoorGold GOLD_DIMENSIONAL_DOOR = new ItemDimDoorGold();
    public final static ItemDimDoorPersonal PERSONAL_DIMENSIONAL_DOOR = new ItemDimDoorPersonal();
    public final static ItemDimDoorUnstable UNSTABLE_DIMENSIONAL_DOOR = new ItemDimDoorUnstable();
    public final static ItemDimDoorWarp WARP_DIMENSIONAL_DOOR = new ItemDimDoorWarp();

    // Fabric
    public final static ItemWorldThread WORLD_THREAD = new ItemWorldThread();
    public final static ItemBlockFabric FABRIC = new ItemBlockFabric();
    public final static ItemStableFabric STABLE_FABRIC = new ItemStableFabric();

    // Tools
    public final static ItemRiftConnectionTool RIFT_CONNECTION_TOOL = new ItemRiftConnectionTool();
    public final static ItemRiftBlade RIFT_BLADE = new ItemRiftBlade();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(QUARTZ_DOOR);
        event.getRegistry().register(PERSONAL_DIMENSIONAL_DOOR);
        event.getRegistry().register(GOLD_DOOR);
        event.getRegistry().register(GOLD_DIMENSIONAL_DOOR);
        event.getRegistry().register(DIMENSIONAL_DOOR);
        event.getRegistry().register(WARP_DIMENSIONAL_DOOR);
        event.getRegistry().register(STABLE_FABRIC);
        event.getRegistry().register(UNSTABLE_DIMENSIONAL_DOOR);
        event.getRegistry().register(WORLD_THREAD);
        event.getRegistry().register(RIFT_CONNECTION_TOOL);
        event.getRegistry().register(RIFT_BLADE);

        // ItemBlocks
        event.getRegistry().register(FABRIC);
        event.getRegistry().register(new ItemBlock(ModBlocks.DIMENSIONAL_TRAPDOOR)
                .setRegistryName(ModBlocks.DIMENSIONAL_TRAPDOOR.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.RIFT)
                .setRegistryName(ModBlocks.RIFT.getRegistryName()));
    }
}
