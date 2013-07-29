package StevenDimDoors.mod_pocketDim.dungeon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.schematic.CompoundFilter;
import StevenDimDoors.mod_pocketDim.schematic.InvalidSchematicException;
import StevenDimDoors.mod_pocketDim.schematic.ReplacementFilter;
import StevenDimDoors.mod_pocketDim.schematic.Schematic;

public class DungeonSchematic extends Schematic {

	private static final short MAX_VANILLA_BLOCK_ID = 158;
	private static final short STANDARD_FABRIC_OF_REALITY_ID = 1973;
	private static final short STANDARD_ETERNAL_FABRIC_ID = 220;
	private static final short[] MOD_BLOCK_FILTER_EXCEPTIONS = new short[] {
		STANDARD_FABRIC_OF_REALITY_ID,
		STANDARD_ETERNAL_FABRIC_ID
	};
	
	private DungeonSchematic(Schematic source)
	{
		super(source);
	}
	
	private DungeonSchematic()
	{
		//Used to create a dummy instance for readFromResource()
		super((short) 0, (short) 0, (short) 0, null, null, null);
	}

	public static DungeonSchematic readFromFile(String schematicPath) throws FileNotFoundException, InvalidSchematicException
	{
		return readFromFile(new File(schematicPath));
	}

	public static DungeonSchematic readFromFile(File schematicFile) throws FileNotFoundException, InvalidSchematicException
	{
		return readFromStream(new FileInputStream(schematicFile));
	}

	public static DungeonSchematic readFromResource(String resourcePath) throws InvalidSchematicException
	{
		//We need an instance of a class in the mod to retrieve a resource
		DungeonSchematic empty = new DungeonSchematic();
		InputStream schematicStream = empty.getClass().getResourceAsStream(resourcePath);
		return readFromStream(schematicStream);
	}

	public static DungeonSchematic readFromStream(InputStream schematicStream) throws InvalidSchematicException
	{
		return new DungeonSchematic(Schematic.readFromStream(schematicStream));
	}
	
	public void ApplyImportFilters(DDProperties properties)
	{
		//Filter out mod blocks except some of our own
		CompoundFilter standardizer = new CompoundFilter();
		standardizer.addFilter(new ModBlockFilter(MAX_VANILLA_BLOCK_ID, MOD_BLOCK_FILTER_EXCEPTIONS,
				(short) properties.FabricBlockID, (byte) 0));
		
		//Also convert standard DD block IDs to local versions
		Map<Short, Short> mapping = getAssignedToStandardIDMapping(properties);
		
		for (Entry<Short, Short> entry : mapping.entrySet())
		{
			if (entry.getKey() != entry.getValue())
			{
				standardizer.addFilter(new ReplacementFilter(entry.getValue(), entry.getKey()));
			}
		}
		standardizer.apply(this, this.blocks, this.metadata);
	}
	
	public void ApplyExportFilters(DDProperties properties)
	{
		//Check if some block IDs assigned by Forge differ from our standard IDs
		//If so, change the IDs to standard values
		CompoundFilter standardizer = new CompoundFilter();
		Map<Short, Short> mapping = getAssignedToStandardIDMapping(properties);
		
		for (Entry<Short, Short> entry : mapping.entrySet())
		{
			if (entry.getKey() != entry.getValue())
			{
				standardizer.addFilter(new ReplacementFilter(entry.getKey(), entry.getValue()));
			}
		}
		standardizer.apply(this, this.blocks, this.metadata);
	}
	
	private Map<Short, Short> getAssignedToStandardIDMapping(DDProperties properties)
	{
		//If we ever need this broadly or support other mods, this should be moved to a separate class
		TreeMap<Short, Short> mapping = new TreeMap<Short, Short>();
		mapping.put((short) properties.FabricBlockID, STANDARD_FABRIC_OF_REALITY_ID);
		mapping.put((short) properties.PermaFabricBlockID, STANDARD_ETERNAL_FABRIC_ID);
		return mapping;
	}
	
	public static DungeonSchematic copyFromWorld(World world, int x, int y, int z, short width, short height, short length, boolean doCompactBounds)
	{
		return new DungeonSchematic(Schematic.copyFromWorld(world, x, y, z, width, height, length, doCompactBounds));
	}
}
