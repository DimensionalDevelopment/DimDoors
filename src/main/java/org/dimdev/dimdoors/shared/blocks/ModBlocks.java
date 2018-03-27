package org.dimdev.dimdoors.shared.blocks;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ModBlocks {

    // Regular doors
    public static final BlockDoorGold GOLD_DOOR = new BlockDoorGold();
    public static final BlockDoorQuartz QUARTZ_DOOR = new BlockDoorQuartz();

    // Dimensional doors
    public static final BlockDimensionalDoorIron IRON_DIMENSIONAL_DOOR = new BlockDimensionalDoorIron();
    public static final BlockDimensionalDoorGold GOLD_DIMENSIONAL_DOOR = new BlockDimensionalDoorGold();
    public static final BlockDimensionalDoorQuartz PERSONAL_DIMENSIONAL_DOOR = new BlockDimensionalDoorQuartz();
    public static final BlockDimensionalPortal DIMENSIONAL_PORTAL = new BlockDimensionalPortal();
    public static final BlockDimensionalDoorWood WARP_DIMENSIONAL_DOOR = new BlockDimensionalDoorWood();
    public static final BlockDimensionalTrapdoorWood WOOD_DIMENSIONAL_TRAPDOOR = new BlockDimensionalTrapdoorWood();

    // Blocks
    public static final BlockFabric FABRIC = new BlockFabric();
    public static final BlockFabricAncient ANCIENT_FABRIC = new BlockFabricAncient();
    public static final BlockFabricEternal ETERNAL_FABRIC = new BlockFabricEternal();
    public static final BlockFabricUnravelled UNRAVELLED_FABRIC = new BlockFabricUnravelled();
    public static final BlockFloatingRift RIFT = new BlockFloatingRift();

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(GOLD_DOOR,
                QUARTZ_DOOR,
                IRON_DIMENSIONAL_DOOR,
                GOLD_DIMENSIONAL_DOOR,
                PERSONAL_DIMENSIONAL_DOOR,
                DIMENSIONAL_PORTAL,
                WARP_DIMENSIONAL_DOOR,
                WOOD_DIMENSIONAL_TRAPDOOR,
                FABRIC,
                ANCIENT_FABRIC,
                UNRAVELLED_FABRIC,
                ETERNAL_FABRIC,
                RIFT);
    }
}
