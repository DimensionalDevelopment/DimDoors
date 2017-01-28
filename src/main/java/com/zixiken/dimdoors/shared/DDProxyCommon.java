package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.shared.blocks.BlockDimDoorBase;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import com.zixiken.dimdoors.shared.items.ModItems;
import com.zixiken.dimdoors.shared.tileentities.TileEntityDimDoor;
import com.zixiken.dimdoors.shared.tileentities.TileEntityDimDoorGold;
import com.zixiken.dimdoors.shared.tileentities.TileEntityRift;
import com.zixiken.dimdoors.shared.tileentities.TileEntityTransTrapdoor;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public abstract class DDProxyCommon implements IDDProxy {

    @Override
    public void onPreInitialization(FMLPreInitializationEvent event) {
        DimDoorDimensions.init();
        ModBlocks.registerBlocks();
        ModItems.registerItems();

        GameRegistry.registerTileEntity(TileEntityDimDoor.class, "TileEntityDimDoor");
        GameRegistry.registerTileEntity(TileEntityRift.class, "TileEntityRift");
        GameRegistry.registerTileEntity(TileEntityTransTrapdoor.class, "TileEntityDimHatch");
        GameRegistry.registerTileEntity(TileEntityDimDoorGold.class, "TileEntityDimDoorGold");
    }

    @Override
    public void onInitialization(FMLInitializationEvent event) {
        CraftingManager.registerRecipes();
    }

    public void updateDoorTE(BlockDimDoorBase door, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityDimDoor) {
            TileEntityDimDoor dimTile = (TileEntityDimDoor) tile;
            IBlockState state = world.getBlockState(pos.down());
            dimTile.orientation = state.getBlock() instanceof BlockDimDoorBase
                    ? state.getValue(BlockDoor.FACING).rotateY()
                    : ModBlocks.blockDimDoor.getDefaultState().getValue(BlockDoor.FACING);
            dimTile.doorIsOpen = door.isDoorOnRift(world, pos) && door.isUpperDoorBlock(world.getBlockState(pos));
            dimTile.lockStatus = 0;
        }
    }

    @Override
    public abstract boolean isClient();
}
