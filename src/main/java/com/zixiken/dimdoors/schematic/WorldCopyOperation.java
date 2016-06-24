package com.zixiken.dimdoors.schematic;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldCopyOperation extends WorldOperation {
	private BlockPos origin;
	private int index;
	private IBlockState[] state;
	private NBTTagList tileEntities;
	
	public WorldCopyOperation() {
		super("WorldCopyOperation");
		state = null;
		tileEntities = null;
	}
	
	@Override
	protected boolean initialize(World world, BlockPos pos, BlockPos volume) {
		index = 0;
		origin = pos;
		state = new IBlockState[volume.getX() * volume.getY() * volume.getZ()];
		tileEntities = new NBTTagList();
		return true;
	}

	@Override
	protected boolean applyToBlock(World world, BlockPos pos) {
		state[index] = world.getBlockState(pos);
		
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity != null) {
			//Extract tile entity data
			NBTTagCompound tileTag = new NBTTagCompound();
			tileEntity.writeToNBT(tileTag);
			//Translate the tile entity's position from the world's coordinate system
			//to the schematic's coordinate system.
			tileTag.setInteger("x", pos.getX() - origin.getX());
			tileTag.setInteger("y", pos.getY() - origin.getY());
			tileTag.setInteger("z", pos.getZ() - origin.getZ());
			tileEntities.appendTag(tileTag);
		}
		index++; //This works assuming the loops in WorldOperation are done in YZX order
		return true;
	}
	
	public IBlockState[] getBlockState() {
		return state;
	}
	
	public NBTTagList getTileEntities() {
		return tileEntities;
	}
}
