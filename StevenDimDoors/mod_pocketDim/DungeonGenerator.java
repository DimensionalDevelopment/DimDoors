package StevenDimDoors.mod_pocketDim;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import StevenDimDoors.mod_pocketDim.dungeon.pack.DungeonPack;
import StevenDimDoors.mod_pocketDim.dungeon.pack.DungeonType;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;

public class DungeonGenerator implements Serializable
{
	//This static field is hax so that I don't have to add an instance field to DungeonGenerator to support DungeonType.
	//Otherwise it would have to be serializable and all sorts of problems would arise.
	private static final HashMap<DungeonGenerator, DungeonType> dungeonTypes = new HashMap<DungeonGenerator, DungeonType>();
	
	public int weight;
	public String schematicPath;
	public ArrayList<HashMap> sideRifts = new ArrayList<HashMap>();
	public LinkData exitLink;
	public boolean isOpen;
	
	public int sideDoorsSoFar=0;
	public int exitDoorsSoFar=0;
	public int deadEndsSoFar=0;
	
	public DungeonGenerator(int weight, String schematicPath, boolean isOpen, DungeonType dungeonType)
	{
		this.weight = weight;
		this.schematicPath = schematicPath;
		this.isOpen = isOpen;
		
		dungeonTypes.put(this, dungeonType); //Hax...
	}

	public DungeonType getDungeonType()
	{
		DungeonType type = dungeonTypes.get(this);
		if (type == null)
		{
			//Infer the dungeon's type from its file name
			//There is minimal risk of us applying this to untagged dungeons and this'll be phased out
			//when we get the new save format.
			try
			{
				File file = new File(schematicPath);
				String typeName = file.getName().split("_")[0];
				String packName = file.getParentFile().getName();
				DungeonPack pack = DungeonHelper.instance().getDungeonPack(packName);
				if (pack == null)
				{
					pack = DungeonHelper.instance().getDungeonPack("ruins");
				}
				type = pack.getType(typeName);
			}
			catch (Exception e) { }
			if (type == null)
			{
				type = DungeonType.UNKNOWN_TYPE;
			}
			dungeonTypes.put(this, type);
		}
		return type;
	}
	
	@Override
	public int hashCode()
	{
		return (schematicPath != null) ? schematicPath.hashCode() : 0;
	}
	
	@Override
	public boolean equals(Object other)
	{
		return equals((DungeonGenerator) other);
	}
	
	public boolean equals(DungeonGenerator other)
	{
		return ((this.schematicPath != null && this.schematicPath.equals(other.schematicPath)) ||
				(this.schematicPath == other.schematicPath));
	}
}