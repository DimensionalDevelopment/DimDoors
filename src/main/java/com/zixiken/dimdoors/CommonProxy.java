package com.zixiken.dimdoors;

import com.zixiken.dimdoors.blocks.BlockDimDoorBase;
import com.zixiken.dimdoors.blocks.ModBlocks;
import com.zixiken.dimdoors.items.ModItems;
import com.zixiken.dimdoors.tileentities.TileEntityDimDoor;

import com.zixiken.dimdoors.tileentities.TileEntityDimDoorGold;
import com.zixiken.dimdoors.tileentities.TileEntityRift;
import com.zixiken.dimdoors.tileentities.TileEntityTransTrapdoor;
import net.minecraft.block.BlockDoor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {

    public void onPreInitialization(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new EventHookContainer());
        ModBlocks.registerBlocks();
        ModItems.registerItems();

        ModelManager.registerModelVariants();
        ModelManager.addCustomStateMappers();

        GameRegistry.registerTileEntity(TileEntityDimDoor.class, "TileEntityDimDoor");
        GameRegistry.registerTileEntity(TileEntityRift.class, "TileEntityRift");
        GameRegistry.registerTileEntity(TileEntityTransTrapdoor.class, "TileEntityDimHatch");
        GameRegistry.registerTileEntity(TileEntityDimDoorGold.class, "TileEntityDimDoorGold");
    }

    public void onInitialization(FMLInitializationEvent event) {
        CraftingManager.registerRecipes();
        ModelManager.registerModels();
    }

	public void updateDoorTE(BlockDimDoorBase door, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityDimDoor) {
			TileEntityDimDoor dimTile = (TileEntityDimDoor) tile;
			dimTile.openOrClosed = door.isDoorOnRift(world, pos) && door.isUpperDoorBlock(world.getBlockState(pos));
			dimTile.orientation = world.getBlockState(pos.down()).getValue(BlockDoor.FACING).rotateY();
            //if(state.getValue(BlockDoor.OPEN)) dimTile.orientation |= 4;
			dimTile.lockStatus = 0;
		}
	}
}