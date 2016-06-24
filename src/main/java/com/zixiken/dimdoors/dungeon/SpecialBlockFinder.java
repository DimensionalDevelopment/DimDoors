package com.zixiken.dimdoors.dungeon;

import java.util.ArrayList;

import com.zixiken.dimdoors.schematic.Schematic;
import com.zixiken.dimdoors.schematic.SchematicFilter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class SpecialBlockFinder extends SchematicFilter {

	private Block warpDoor;
    private Block dimensionalDoor;
    private Block monolithSpawnMarker;
    private Block exitMarker;
	private EnumFacing entranceOrientation;
	private Schematic schematic;
	private BlockPos entranceDoorLocation;
	private ArrayList<BlockPos> exitDoorLocations;
	private ArrayList<BlockPos> dimensionalDoorLocations;
	private ArrayList<BlockPos> monolithSpawnLocations;
	
	public SpecialBlockFinder(Block warpDoor, Block dimensionalDoor, Block monolithSpawn, Block exitDoor) {
		super("SpecialBlockFinder");
		this.warpDoor = warpDoor;
        this.dimensionalDoor = dimensionalDoor;
        this.monolithSpawnMarker = monolithSpawn;
        this.exitMarker = exitDoor;
		this.entranceDoorLocation = null;
		this.entranceOrientation = EnumFacing.SOUTH;
		this.exitDoorLocations = new ArrayList<BlockPos>();
		this.dimensionalDoorLocations = new ArrayList<BlockPos>();
		this.monolithSpawnLocations = new ArrayList<BlockPos>();
		this.schematic = null;
	}
	
	public EnumFacing getEntranceOrientation() {
		return entranceOrientation;
	}

	public BlockPos getEntranceDoorLocation() {
		return entranceDoorLocation;
	}

	public ArrayList<BlockPos> getExitDoorLocations() {
		return exitDoorLocations;
	}

	public ArrayList<BlockPos> getDimensionalDoorLocations() {
		return dimensionalDoorLocations;
	}

	public ArrayList<BlockPos> getMonolithSpawnLocations() {
		return monolithSpawnLocations;
	}
	
	@Override
	protected boolean initialize(Schematic schematic, IBlockState[] state) {
		this.schematic = schematic;
		return true;
	}
	
	@Override
	protected boolean applyToBlock(int index, IBlockState[] state) {
		int indexBelow;
		int indexDoubleBelow;
		
		if (state[index] == monolithSpawnMarker) {
			monolithSpawnLocations.add(schematic.calculatePoint(index));
			return true;
		} if (state[index] == dimensionalDoor) {
			indexBelow = schematic.calculateIndexBelow(index);
			if (indexBelow >= 0 && state[indexBelow] == dimensionalDoor) {
				dimensionalDoorLocations.add(schematic.calculatePoint(index));
				return true;
			} else {
				return false;
			}
		} if (state[index] == warpDoor) {
			indexBelow = schematic.calculateIndexBelow(index);
			if (indexBelow >= 0 && state[indexBelow] == warpDoor) {
				indexDoubleBelow = schematic.calculateIndexBelow(indexBelow);
				if (indexDoubleBelow >= 0 && state[indexDoubleBelow] == exitMarker) {
					exitDoorLocations.add(schematic.calculatePoint(index));
					return true;
				} else if (entranceDoorLocation == null) {
					entranceDoorLocation = schematic.calculatePoint(index);
					entranceOrientation = state[indexBelow].getValue(BlockDoor.FACING);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	protected boolean terminates() {
		return false;
	}
}
