package StevenDimDoors.mod_pocketDim.world.gateways;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.Map.Entry;

import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkTypes;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.dungeon.DungeonData;
import StevenDimDoors.mod_pocketDim.dungeon.DungeonSchematic;
import StevenDimDoors.mod_pocketDim.dungeon.ModBlockFilter;
import StevenDimDoors.mod_pocketDim.dungeon.SpecialBlockFinder;
import StevenDimDoors.mod_pocketDim.dungeon.pack.DungeonPack;
import StevenDimDoors.mod_pocketDim.schematic.BlockRotator;
import StevenDimDoors.mod_pocketDim.schematic.CompoundFilter;
import StevenDimDoors.mod_pocketDim.schematic.InvalidSchematicException;
import StevenDimDoors.mod_pocketDim.schematic.ReplacementFilter;
import StevenDimDoors.mod_pocketDim.schematic.Schematic;
import StevenDimDoors.mod_pocketDim.schematic.SchematicFilter;
import StevenDimDoors.mod_pocketDim.world.PocketBuilder;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public abstract class BaseGateway 
{
	DDProperties properties;
	
	public BaseGateway(DDProperties properties)
	{
		this.properties=properties;
	}
	
	/**
	 * Generates the gateway centered on the given coords
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	public boolean generate(World world, int x, int y, int z)
	{
		int orientation = 0;
		
		if (this.getSchematicPath()!=null)
		{
			//Get the correct filters
			GatewayBlockFilter filter = new GatewayBlockFilter();
			DungeonSchematic schematic = this.getSchematicToBuild(world, x, y, z);
			
			//apply filters
			schematic.applyFilter(filter);	
			schematic.applyImportFilters(properties);
			
			Point3D doorLocation = filter.getEntranceDoorLocation();
			orientation = filter.getEntranceOrientation();
			
			// I suspect that the location used below is wrong. Gateways should be placed vertically based on
			// the Y position of the surface where they belong. I'm pretty sure including doorLocation.getY()
			// messes up the calculation. ~SenseiKiwi

			//schematic.copyToWorld(world, x - doorLocation.getX(), y, z - doorLocation.getZ());
			schematic.copyToWorld(world, x - doorLocation.getX(), y + 1 - doorLocation.getY(), z - doorLocation.getZ(), true);
		}
			
		this.generateRandomBits(world, x, y, z);
		
		DimLink link = PocketManager.getDimensionData(world).createLink(x, y + 1, z, LinkTypes.DUNGEON, orientation);
		DungeonData dungeon = this.getStartingDungeon(PocketManager.getDimensionData(world), world.rand);
		if (dungeon != null)
		{
			PocketBuilder.generateSelectedDungeonPocket(link, mod_pocketDim.properties, dungeon);
		}
		else
		{
			System.err.println("Warning: Dimensional Doors was unable to assign a dungeon to a Rift Gateway.");
		}

		return true;
	}
	
	/**
	 * Gets a .schematic to generate for this gateway
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public DungeonSchematic getSchematicToBuild(World world, int x, int y, int z)
	{
		//TODO- refine selection criteria here, this is the default case
		try 
		{
			return DungeonSchematic.readFromResource(this.getSchematicPath());
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.err.println("Could not load schematic for gateway");
			return null;
		}
	}
	
	/**
	 * returns a dungeon from the assigned pack to start with
	 * @return
	 */
	public DungeonData getStartingDungeon(NewDimData dimension, Random random)
	{
		return getStartingPack().getNextDungeon(dimension, random);
	}
	
	/**
	 * determines if a given location is valid for the gateway to be generated, based on height, biome, and world.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param biome
	 * @return
	 */
	public boolean isLocationValid(World world, int x, int y, int z, BiomeGenBase biome)
	{
		return this.isBiomeValid(biome)&&areCoordsValid(world, x, y, z);
	}
	
	public boolean isBiomeValid(BiomeGenBase biome)
	{
		if(this.getBiomeNames()!=null)
		{
			for(String biomeName : this.getBiomeNames())
			{
				if(biome.biomeName.contains(biomeName))
				{
					return true;
				}
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Use this function to generate randomized bits of the structure. 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	abstract void generateRandomBits(World world, int x, int y, int z);
	
	/**
	 * Decides if the given coords/world are valid
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public abstract boolean areCoordsValid(World world, int x, int y, int z);

	/**
	 * @return the pack the dungeon initially generates into from this gateway.
	 */
	public abstract DungeonPack getStartingPack();
	
	/**
	 * Is by default a whitelist, but the isBiomeValid method 
	 * can be overriden for specific gateways. For example, any biome containing 'forest' would be valid if we added 'forest',
	 * even from other mods.
	 * @return List of biome names that we check against. 
	 */
	public abstract String[] getBiomeNames();
	
	/**
	 * @return List containing all the .schematics attached to this gateway. Selection is random by default
	 */
	public abstract String getSchematicPath();
	
	//TODO not yet implemented
	public abstract boolean isSurfaceGateway();
	
}
