package com.zixiken.dimdoors.shared.blocks;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModBlocks {

    // Regular doors
    public static final BlockDoorGold GOLD_DOOR = new BlockDoorGold();
    public static final BlockDoorQuartz QUARTZ_DOOR = new BlockDoorQuartz();

    // Dimensional doors
    public static final BlockDimDoorIron DIMENSIONAL_DOOR = new BlockDimDoorIron();
    public static final BlockDimDoorGold GOLD_DIMENSIONAL_DOOR = new BlockDimDoorGold();
    public static final BlockDimDoorPersonal PERSONAL_DIMENSIONAL_DOOR = new BlockDimDoorPersonal();
    public static final BlockDimDoorUnstable UNSTABLE_DIMENSIONAL_DOOR = new BlockDimDoorUnstable();
    public static final BlockDimDoorTransient TRANSIENT_DIMENSIONAL_DOOR = new BlockDimDoorTransient();
    public static final BlockDimDoorWarp WARP_DIMENSIONAL_DOOR = new BlockDimDoorWarp();
    public static final BlockDimTrapdoor DIMENSIONAL_TRAPDOOR = new BlockDimTrapdoor();

    // Blocks
    public static final BlockFabric FABRIC = new BlockFabric();
    public static final BlockRift RIFT = new BlockRift();

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                GOLD_DOOR,
                QUARTZ_DOOR,
                DIMENSIONAL_DOOR,
                GOLD_DIMENSIONAL_DOOR,
                PERSONAL_DIMENSIONAL_DOOR,
                UNSTABLE_DIMENSIONAL_DOOR,
                TRANSIENT_DIMENSIONAL_DOOR,
                WARP_DIMENSIONAL_DOOR,
                DIMENSIONAL_TRAPDOOR,
                FABRIC,
                RIFT);
    }
}
