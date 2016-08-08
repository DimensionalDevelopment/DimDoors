package com.zixiken.dimdoors.world.gateways;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.blocks.DimensionalDoor;
import com.zixiken.dimdoors.blocks.TransientDoor;
import com.zixiken.dimdoors.blocks.WarpDoor;
import com.zixiken.dimdoors.schematic.Schematic;
import com.zixiken.dimdoors.schematic.SchematicFilter;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class GatewayBlockFilter extends SchematicFilter {
	private EnumFacing entranceOrientation;
	private Schematic schematic;
	private BlockPos entranceDoorLocation;

	public GatewayBlockFilter() {
		super("GatewayEntranceFinder");
		this.entranceDoorLocation = null;
		this.entranceOrientation = EnumFacing.NORTH;
		this.schematic = null;
	}
	
	public EnumFacing getEntranceOrientation() {
		return entranceOrientation;
	}

	public BlockPos getEntranceDoorLocation() {
		return entranceDoorLocation;
	}
	
	@Override
	protected boolean initialize(Schematic schematic, IBlockState[] states) {
		this.schematic = schematic;
		return true;
	}
	
	@Override
	protected boolean applyToBlock(int index, IBlockState[] states) {
		int indexBelow;
		if (states[index] == DimDoors.dimensionalDoor) {
			indexBelow = schematic.calculateIndexBelow(index);
			if (indexBelow >= 0 && states[indexBelow] == DimDoors.dimensionalDoor) {
				entranceDoorLocation = schematic.calculatePoint(index);
				entranceOrientation = states[indexBelow].getValue(DimensionalDoor.FACING);
				return true;
			}
		} if (states[index] == DimDoors.transientDoor) {
			indexBelow = schematic.calculateIndexBelow(index);
			if (indexBelow >= 0 && states[indexBelow] == DimDoors.transientDoor) {
				entranceDoorLocation = schematic.calculatePoint(index);
				entranceOrientation = states[indexBelow].getValue(TransientDoor.FACING);
				return true;
			}
		} if (states[index] == DimDoors.warpDoor) {
			indexBelow = schematic.calculateIndexBelow(index);
			if (indexBelow >= 0 && states[indexBelow] == DimDoors.warpDoor) {
				entranceDoorLocation = schematic.calculatePoint(index);
				entranceOrientation = states[indexBelow].getValue(WarpDoor.FACING);
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected boolean terminates() {
		return true;
	}
}
