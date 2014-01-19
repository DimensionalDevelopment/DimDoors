package StevenDimDoors.mod_pocketDim.world.gateways;

import java.util.ArrayList;

import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.schematic.Schematic;
import StevenDimDoors.mod_pocketDim.schematic.SchematicFilter;

public class GatewayBlockFilter extends SchematicFilter {

	private short dimensionalDoorID;
	private int transientDoorID;
	private int warpDoorID;
	private int entranceOrientation;
	private Schematic schematic;
	private Point3D entranceDoorLocation;

	public GatewayBlockFilter(short dimensionalDoorID, short transientDoorID, short warpDoorID)
	{
		super("GatewayEntranceFinder");
		this.entranceDoorLocation = null;
		this.entranceOrientation = 0;
		this.schematic = null;
		this.dimensionalDoorID = dimensionalDoorID;
		this.transientDoorID = transientDoorID;
		this.warpDoorID = warpDoorID;
	}
	
	public int getEntranceOrientation() {
		return entranceOrientation;
	}

	public Point3D getEntranceDoorLocation() {
		return entranceDoorLocation;
	}
	
	@Override
	protected boolean initialize(Schematic schematic, short[] blocks, byte[] metadata)
	{
		this.schematic = schematic;
		return true;
	}
	
	@Override
	protected boolean applyToBlock(int index, short[] blocks, byte[] metadata)
	{
		int indexBelow;
		int indexDoubleBelow;
		if (blocks[index] == dimensionalDoorID)
		{
			indexBelow = schematic.calculateIndexBelow(index);
			if (indexBelow >= 0 && blocks[indexBelow] == dimensionalDoorID)
			{
				entranceDoorLocation = schematic.calculatePoint(index);
				entranceOrientation = (metadata[indexBelow] & 3);
				return true;
			}
		}
		if (blocks[index] == transientDoorID)
		{
			indexBelow = schematic.calculateIndexBelow(index);
			if (indexBelow >= 0 && blocks[indexBelow] == transientDoorID)
			{
				entranceDoorLocation = schematic.calculatePoint(index);
				entranceOrientation = (metadata[indexBelow] & 3);
				return true;
			}
		}
		if (blocks[index] == warpDoorID)
		{
			indexBelow = schematic.calculateIndexBelow(index);
			if (indexBelow >= 0 && blocks[indexBelow] == warpDoorID)
			{
				entranceDoorLocation = schematic.calculatePoint(index);
				entranceOrientation = (metadata[indexBelow] & 3);
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected boolean terminates()
	{
		return true;
	}
}
