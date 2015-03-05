package StevenDimDoors.mod_pocketDim.world.gateways;

import java.util.ArrayList;

import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.schematic.Schematic;
import StevenDimDoors.mod_pocketDim.schematic.SchematicFilter;
import net.minecraft.block.Block;

public class GatewayBlockFilter extends SchematicFilter {

	private static final short STANDARD_WARP_DOOR_ID = 1975;
	private static final short STANDARD_DIMENSIONAL_DOOR_ID = 1970;
	private static final short STANDARD_TRANSIENT_DOOR_ID = 1979;

	private int entranceOrientation;
	private Schematic schematic;
	private Point3D entranceDoorLocation;

	public GatewayBlockFilter()
	{
		super("GatewayEntranceFinder");
		this.entranceDoorLocation = null;
		this.entranceOrientation = 0;
		this.schematic = null;
	}
	
	public int getEntranceOrientation() {
		return entranceOrientation;
	}

	public Point3D getEntranceDoorLocation() {
		return entranceDoorLocation;
	}
	
	@Override
	protected boolean initialize(Schematic schematic, Block[] blocks, byte[] metadata)
	{
		this.schematic = schematic;
		return true;
	}
	
	@Override
	protected boolean applyToBlock(int index, Block[] blocks, byte[] metadata)
	{
		int indexBelow;
		int indexDoubleBelow;
		if (blocks[index] == mod_pocketDim.dimensionalDoor)
		{
			indexBelow = schematic.calculateIndexBelow(index);
			if (indexBelow >= 0 && blocks[indexBelow] == mod_pocketDim.dimensionalDoor)
			{
				entranceDoorLocation = schematic.calculatePoint(index);
				entranceOrientation = (metadata[indexBelow] & 3);
				return true;
			}
		}
		if (blocks[index] == mod_pocketDim.transientDoor)
		{
			indexBelow = schematic.calculateIndexBelow(index);
			if (indexBelow >= 0 && blocks[indexBelow] == mod_pocketDim.transientDoor)
			{
				entranceDoorLocation = schematic.calculatePoint(index);
				entranceOrientation = (metadata[indexBelow] & 3);
				return true;
			}
		}
		if (blocks[index] == mod_pocketDim.warpDoor)
		{
			indexBelow = schematic.calculateIndexBelow(index);
			if (indexBelow >= 0 && blocks[indexBelow] == mod_pocketDim.warpDoor)
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
