package com.zixiken.dimdoors.blocks;

import com.zixiken.dimdoors.items.ItemBlockDimWall;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModBlocks {
    public static BlockDoorQuartz blockDoorQuartz;
    public static BlockDoorGold blockDoorGold;
    public static BlockDimDoorPersonal blockDimDoorPersonal;
    public static BlockDimDoorTransient blockDimDoorTransient;
    public static BlockDimDoorWarp blockDimDoorWarp;
    public static BlockDimDoorGold blockDimDoorGold;
    public static BlockDimDoorUnstable blockDimDoorChaos;
    public static BlockDimDoor blockDimDoor;
    public static BlockTransTrapdoor blockDimHatch;
    public static BlockDimWall blockDimWall;
    public static BlockRift blockRift;

    public static void registerBlocks() {
        GameRegistry.registerBlock(blockDoorQuartz = new BlockDoorQuartz(), null, BlockDoorQuartz.ID);
        GameRegistry.registerBlock(blockDimDoorPersonal = new BlockDimDoorPersonal(), null, BlockDimDoorPersonal.ID);
        GameRegistry.registerBlock(blockDoorGold = new BlockDoorGold(), null, BlockDoorGold.ID);
        GameRegistry.registerBlock(blockDimDoorGold = new BlockDimDoorGold(), null, BlockDimDoorGold.ID);
        GameRegistry.registerBlock(blockDimDoorChaos = new BlockDimDoorUnstable(), null, BlockDimDoorUnstable.ID);
        GameRegistry.registerBlock(blockDimDoorWarp = new BlockDimDoorWarp(), null, BlockDimDoorWarp.ID);
        GameRegistry.registerBlock(blockDimDoor = new BlockDimDoor(), null, BlockDimDoor.ID);
        GameRegistry.registerBlock(blockDimHatch = new BlockTransTrapdoor(), BlockTransTrapdoor.ID);
        GameRegistry.registerBlock(blockDimWall = new BlockDimWall(), ItemBlockDimWall.class, BlockDimWall.ID);
        GameRegistry.registerBlock(blockDimDoorTransient = new BlockDimDoorTransient(), BlockDimDoorTransient.ID);
        GameRegistry.registerBlock(blockRift = new BlockRift(), BlockRift.ID);
    }
}
