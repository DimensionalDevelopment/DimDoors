package StevenDimDoors.mod_pocketDim.dungeon;

import java.io.FileNotFoundException;
import java.io.Serializable;

import StevenDimDoors.mod_pocketDim.dungeon.pack.DungeonType;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import StevenDimDoors.mod_pocketDim.schematic.InvalidSchematicException;

public class DungeonData implements Serializable
{
	private static final long serialVersionUID = -5624866366474710161L;

	private final int weight;
	private final boolean isOpen;
	private final boolean isInternal;
	private final String schematicPath;
	private final String schematicName;
	private final DungeonType dungeonType;
	
	public DungeonData(String schematicPath, boolean isInternal, DungeonType dungeonType, boolean isOpen, int weight)
	{
		this.schematicPath = schematicPath;
		this.schematicName = getSchematicName(schematicPath);
		this.dungeonType = dungeonType;
		this.isInternal = isInternal;
		this.isOpen = isOpen;
		this.weight = weight;
	}
	
	private static String getSchematicName(String schematicPath)
	{
		int indexA = schematicPath.lastIndexOf('\\');
		int indexB = schematicPath.lastIndexOf('/');
		indexA = Math.max(indexA, indexB) + 1;
		
		return schematicPath.substring(indexA, schematicPath.length() - DungeonHelper.SCHEMATIC_FILE_EXTENSION.length() - indexA);
	}
	
	public int weight()
	{
		return weight;
	}
	
	public boolean isOpen()
	{
		return isOpen;
	}
	
	public String schematicPath()
	{
		return schematicPath;
	}
	
	public DungeonType dungeonType()
	{
		return dungeonType;
	}
	
	public String schematicName()
	{
		return schematicName;
	}
	
	public DungeonSchematic loadSchematic() throws InvalidSchematicException, FileNotFoundException
	{
		if (isInternal)
		{
			return DungeonSchematic.readFromResource(schematicPath);
		}
		else
		{
			return DungeonSchematic.readFromFile(schematicPath);
		}
	}
}