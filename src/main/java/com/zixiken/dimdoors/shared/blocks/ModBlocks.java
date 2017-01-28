package com.zixiken.dimdoors.shared.blocks;

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
    public static BlockLimbo blockLimbo;

    public static void registerBlocks() {
        GameRegistry.register(blockDoorQuartz = new BlockDoorQuartz());
        GameRegistry.register(blockDimDoorPersonal = new BlockDimDoorPersonal());
        GameRegistry.register(blockDoorGold = new BlockDoorGold());
        GameRegistry.register(blockDimDoorGold = new BlockDimDoorGold());
        GameRegistry.register(blockDimDoorChaos = new BlockDimDoorUnstable());
        GameRegistry.register(blockDimDoorWarp = new BlockDimDoorWarp());
        GameRegistry.register(blockDimDoor = new BlockDimDoor());
        GameRegistry.register(blockDimHatch = new BlockTransTrapdoor());
        GameRegistry.register(blockDimWall = new BlockDimWall());
        GameRegistry.register(blockDimDoorTransient = new BlockDimDoorTransient());
        GameRegistry.register(blockRift = new BlockRift());
        GameRegistry.register(blockLimbo = new BlockLimbo());
    }
}
