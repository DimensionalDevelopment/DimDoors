package StevenDimDoors.mod_pocketDim.dungeon;

import java.util.ArrayList;

import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.schematic.Schematic;
import StevenDimDoors.mod_pocketDim.schematic.SchematicFilter;

public class SpecialBlockFinder extends SchematicFilter {

	private short warpDoorID;
	private short dimensionalDoorID;
	private short monolithSpawnMarkerID;
	private short exitMarkerID;
	private int entranceOrientation;
	private Schematic schematic;
	private Point3D entranceDoorLocation;
	private ArrayList<Point3D> exitDoorLocations;
	private ArrayList<Point3D> dimensionalDoorLocations;
	private ArrayList<Point3D> monolithSpawnLocations;
	
	public SpecialBlockFinder(short warpDoorID, short dimensionalDoorID, short monolithSpawnMarkerID, short exitMarkerID)
	{
		super("SpecialBlockFinder");
		this.warpDoorID = warpDoorID;
		this.dimensionalDoorID = dimensionalDoorID;
		this.monolithSpawnMarkerID = monolithSpawnMarkerID;
		this.exitMarkerID = exitMarkerID;
		this.entranceDoorLocation = null;
		this.entranceOrientation = 0;
		this.exitDoorLocations = new ArrayList<Point3D>();
		this.dimensionalDoorLocations = new ArrayList<Point3D>();
		this.monolithSpawnLocations = new ArrayList<Point3D>();
		this.schematic = null;
	}
	
	public int getEntranceOrientation() {
		return entranceOrientation;
	}

	public Point3D getEntranceDoorLocation() {
		return entranceDoorLocation;
	}

	public ArrayList<Point3D> getExitDoorLocations() {
		return exitDoorLocations;
	}

	public ArrayList<Point3D> getDimensionalDoorLocations() {
		return dimensionalDoorLocations;
	}

	public ArrayList<Point3D> getMonolithSpawnLocations() {
		return monolithSpawnLocations;
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
		
		if (blocks[index] == monolithSpawnMarkerID)
		{
			monolithSpawnLocations.add(schematic.calculatePoint(index));
			return true;
		}
		if (blocks[index] == dimensionalDoorID)
		{
			indexBelow = schematic.calculateIndexBelow(index);
			if (indexBelow >= 0 && blocks[indexBelow] == dimensionalDoorID)
			{
				dimensionalDoorLocations.add(schematic.calculatePoint(index));
				return true;
			}
			else
			{
				return false;
			}
		}
		if (blocks[index] == warpDoorID)
		{
			indexBelow = schematic.calculateIndexBelow(index);
			if (indexBelow >= 0 && blocks[indexBelow] == warpDoorID)
			{
				indexDoubleBelow = schematic.calculateIndexBelow(indexBelow);
				if (indexDoubleBelow >= 0 && blocks[indexDoubleBelow] == exitMarkerID)
				{
					exitDoorLocations.add(schematic.calculatePoint(index));
					return true;
				}
				else if (entranceDoorLocation == null)
				{
					entranceDoorLocation = schematic.calculatePoint(index);
					entranceOrientation = (metadata[indexBelow] & 3);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	protected boolean terminates()
	{
		return false;
	}
}
