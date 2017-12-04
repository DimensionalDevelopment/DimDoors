package com.zixiken.dimdoors.shared.blocks;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModBlocks {

    // Regular doors
    public final static BlockDoorGold GOLD_DOOR = new BlockDoorGold();
    public final static BlockDoorQuartz QUARTZ_DOOR = new BlockDoorQuartz();

    // Dimensional doors
    public final static BlockDimDoor DIMENSIONAL_DOOR = new BlockDimDoor();
    public final static BlockDimDoorGold GOLD_DIMENSIONAL_DOOR = new BlockDimDoorGold();
    public final static BlockDimDoorPersonal PERSONAL_DIMENSIONAL_DOOR = new BlockDimDoorPersonal();
    public final static BlockDimDoorUnstable UNSTABLE_DIMENSIONAL_DOOR = new BlockDimDoorUnstable();
    public final static BlockDimDoorTransient TRANSIENT_DIMENSIONAL_DOOR = new BlockDimDoorTransient();
    public final static BlockDimDoorWarp WARP_DIMENSIONAL_DOOR = new BlockDimDoorWarp();
    public final static BlockTransTrapdoor DIMENSIONAL_TRAPDOOR = new BlockTransTrapdoor();

    // Blocks
    public final static BlockFabric FABRIC = new BlockFabric();
    public final static BlockRift RIFT = new BlockRift();

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(QUARTZ_DOOR);
        event.getRegistry().register(PERSONAL_DIMENSIONAL_DOOR);
        event.getRegistry().register(GOLD_DOOR);
        event.getRegistry().register(GOLD_DIMENSIONAL_DOOR);
        event.getRegistry().register(UNSTABLE_DIMENSIONAL_DOOR);
        event.getRegistry().register(WARP_DIMENSIONAL_DOOR);
        event.getRegistry().register(DIMENSIONAL_DOOR);
        event.getRegistry().register(DIMENSIONAL_TRAPDOOR);
        event.getRegistry().register(FABRIC);
        event.getRegistry().register(TRANSIENT_DIMENSIONAL_DOOR);
        event.getRegistry().register(RIFT);
    }
}
