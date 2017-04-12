package com.zixiken.dimdoors.shared.blocks;

import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModBlocks {

    public static BlockDoorQuartz blockDoorQuartz;
    public static BlockDoorGold blockDoorGold;
    public static BlockDimDoorPersonal blockDimDoorPersonal;
    public static BlockDimDoorTransient blockDimDoorTransient;
    public static BlockDimDoorWarp blockDimDoorWarp;
    public static BlockDimDoorGold blockDimDoorGold;
    public static BlockDimDoorChaos blockDimDoorChaos;
    public static BlockDimDoor blockDimDoor;
    public static BlockTransTrapdoor blockDimHatch;
    public static BlockDimWall blockFabric;
    public static BlockRift blockRift;

    public static void registerBlocks() {
        GameRegistry.register(blockDoorQuartz = new BlockDoorQuartz());
        GameRegistry.register(blockDimDoorPersonal = new BlockDimDoorPersonal());
        GameRegistry.register(blockDoorGold = new BlockDoorGold());
        GameRegistry.register(blockDimDoorGold = new BlockDimDoorGold());
        GameRegistry.register(blockDimDoorChaos = new BlockDimDoorChaos());
        GameRegistry.register(blockDimDoorWarp = new BlockDimDoorWarp());
        GameRegistry.register(blockDimDoor = new BlockDimDoor());
        GameRegistry.register(blockDimHatch = new BlockTransTrapdoor());
        GameRegistry.register(blockFabric = new BlockDimWall());
        GameRegistry.register(blockDimDoorTransient = new BlockDimDoorTransient());
        GameRegistry.register(blockRift = new BlockRift());
    }
}
