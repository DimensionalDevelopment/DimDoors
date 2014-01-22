package StevenDimDoors.mod_pocketDim.world.gateways;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.Map.Entry;

import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkTypes;
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
	//This pack is what the dungeon initially generates into from this gateway.
	protected DungeonPack startingPack;
	
	/**Flag that determines if this gateway is tied to a specific biome. 
	 *For compatabilities sake, we are just using string comparison to check.
	 **/
	protected boolean isBiomeSpecific;
	
	/**
	 * List of biome names that we check against. Is by default a whitelist, but the isBiomeValid method 
	 * can be overriden for specific gateways. For example, any biome containing 'forest' would be valid if we added 'forest',
	 * even from other mods.
	 */
	protected ArrayList<String> biomeNames = new ArrayList<String>();
	
	/**
	 * List containing all the .schematics attached to this gateway. Selection is random by default, 
	 * but can be overriden for specific gateways in getSchematicToBuild
	 */
	protected ArrayList<String> schematicPaths= new ArrayList<String>();
	
	//TODO not yet implemented
	protected boolean surfaceGateway;
	
	//TODO not yet implemented
	protected int generationWeight;
	
	private DDProperties properties;

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
		
		if (this.hasSchematic())
		{
			//Get the correct filters
			GatewayBlockFilter filter = new GatewayBlockFilter();
			DungeonSchematic schematic = this.getSchematicToBuild(world, x, y, z);
			
			//apply filters
			schematic.applyImportFilters(properties);
			schematic.applyFilter(filter);	
			
			Point3D doorLocation = filter.getEntranceDoorLocation();
			orientation = filter.getEntranceOrientation();
			
			// I suspect that the location used below is wrong. Gateways should be placed vertically based on
			// the Y position of the surface where they belong. I'm pretty sure including doorLocation.getY()
			// messes up the calculation. ~SenseiKiwi

			//schematic.copyToWorld(world, x - doorLocation.getX(), y, z - doorLocation.getZ());
			schematic.copyToWorld(world, x - doorLocation.getX(), y + 1 - doorLocation.getY(), z - doorLocation.getZ());
		}
			
		this.generateRandomBits(world, x, y, z);
		
		DimLink link = PocketManager.getDimensionData(world).createLink(x, y + 1, z, LinkTypes.DUNGEON, orientation);
		PocketBuilder.generateSelectedDungeonPocket(link, mod_pocketDim.properties, this.getStartingDungeon(world.rand));

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
			return DungeonSchematic.readFromResource(schematicPaths.get(world.rand.nextInt(schematicPaths.size())));
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.err.println("Could not load schematic for gateway");
			return null;
		}
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
	 * returns a dungeon from the assigned pack to start with
	 * @return
	 */
	public DungeonData getStartingDungeon(Random random)
	{
		return startingPack.getRandomDungeon(random);
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
		//TODO- refine condition here as warranted
		return this.isBiomeValid(biome);
	}
	
	public boolean shouldGenUnderground()
	{
		return !surfaceGateway;
	}
	
	public boolean isBiomeValid(BiomeGenBase biome)
	{
		return !this.isBiomeSpecific || this.biomeNames.contains(biome.biomeName.toLowerCase());
	}

	public boolean hasSchematic()
	{
		return this.schematicPaths != null && !this.schematicPaths.isEmpty();
	}
}
