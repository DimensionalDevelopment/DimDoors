package org.dimdev.dimdoors.shared.blocks;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ModBlocks {

    // Regular doors
    public static final BlockDoorGold GOLD_DOOR = new BlockDoorGold();
    public static final BlockDoorQuartz QUARTZ_DOOR = new BlockDoorQuartz();

    // Dimensional doors
    public static final BlockDimensionalDoorIron DIMENSIONAL_DOOR = new BlockDimensionalDoorIron();
    public static final BlockDimensionalDoorGold GOLD_DIMENSIONAL_DOOR = new BlockDimensionalDoorGold();
    public static final BlockDimensionalDoorPersonal PERSONAL_DIMENSIONAL_DOOR = new BlockDimensionalDoorPersonal();
    public static final BlockDimensionalDoorUnstable UNSTABLE_DIMENSIONAL_DOOR = new BlockDimensionalDoorUnstable();
    public static final BlockDimensionalDoorTransient TRANSIENT_DIMENSIONAL_DOOR = new BlockDimensionalDoorTransient();
    public static final BlockDimensionalDoorWarp WARP_DIMENSIONAL_DOOR = new BlockDimensionalDoorWarp();
    public static final BlockDimensionalTrapdoorWood WOOD_DIMENSIONAL_TRAPDOOR = new BlockDimensionalTrapdoorWood();

    // Blocks
    public static final BlockFabric FABRIC = new BlockFabric();
    public static final BlockFloatingRift RIFT = new BlockFloatingRift();

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
                WOOD_DIMENSIONAL_TRAPDOOR,
                FABRIC,
                RIFT);
    }
}
