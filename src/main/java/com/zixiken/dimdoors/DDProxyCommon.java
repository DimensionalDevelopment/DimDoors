package com.zixiken.dimdoors;

import com.zixiken.dimdoors.blocks.BlockDimDoorBase;
import com.zixiken.dimdoors.blocks.ModBlocks;
import com.zixiken.dimdoors.items.ModItems;
import com.zixiken.dimdoors.tileentities.TileEntityDimDoor;

import com.zixiken.dimdoors.tileentities.TileEntityDimDoorGold;
import com.zixiken.dimdoors.tileentities.TileEntityRift;
import com.zixiken.dimdoors.tileentities.TileEntityTransTrapdoor;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public abstract class DDProxyCommon implements IDDProxy {

    @Override
    public void onPreInitialization(FMLPreInitializationEvent event) {
        ModBlocks.registerBlocks();
        ModItems.registerItems();

        ModelManager.registerModelVariants();
        ModelManager.addCustomStateMappers();

        GameRegistry.registerTileEntity(TileEntityDimDoor.class, "TileEntityDimDoor");
        GameRegistry.registerTileEntity(TileEntityRift.class, "TileEntityRift");
        GameRegistry.registerTileEntity(TileEntityTransTrapdoor.class, "TileEntityDimHatch");
        GameRegistry.registerTileEntity(TileEntityDimDoorGold.class, "TileEntityDimDoorGold");
    }

    @Override
    public void onInitialization(FMLInitializationEvent event) {
        CraftingManager.registerRecipes();
        ModelManager.registerModels();
    }

    public void updateDoorTE(BlockDimDoorBase door, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityDimDoor) {
            TileEntityDimDoor dimTile = (TileEntityDimDoor) tile;
            IBlockState state = world.getBlockState(pos.down());
            dimTile.orientation = state.getBlock() instanceof BlockDimDoorBase
                    ? state.getValue(BlockDoor.FACING).rotateY()
                    : ModBlocks.blockDimDoor.getDefaultState().getValue(BlockDoor.FACING);
            dimTile.openOrClosed = door.isDoorOnRift(world, pos) && door.isUpperDoorBlock(world.getBlockState(pos));
            dimTile.lockStatus = 0;
        }
    }

    @Override
    public abstract boolean isClient();
}
