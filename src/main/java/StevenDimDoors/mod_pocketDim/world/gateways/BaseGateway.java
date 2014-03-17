package StevenDimDoors.mod_pocketDim.world.gateways;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import StevenDimDoors.mod_pocketDim.config.DDProperties;

public abstract class BaseGateway 
{
	protected DDProperties properties;
	
	public BaseGateway(DDProperties properties)
	{
		this.properties = properties;
	}
	
	/**
	 * Generates the gateway centered on the given coordinates
	 * @param world - the world in which to generate the gateway
	 * @param x - the x-coordinate at which to center the gateway; usually where the door is placed
	 * @param y - the y-coordinate of the block on which the gateway may be built
	 * @param z - the z-coordinate at which to center the gateway; usually where the door is placed
	 */
	public abstract boolean generate(World world, int x, int y, int z);
	
	/**
	 * Determines whether the specified biome is a valid biome in which to generate this gateway
	 * @param biome - the biome to be checked
	 * @return <code>true</code> true if the specified biome is a valid for generating this gateway, otherwise <code>false</code>
	 */
	protected boolean isBiomeValid(BiomeGenBase biome)
	{
		String biomeName = biome.biomeName.toLowerCase();
		String[] keywords = this.getBiomeKeywords();
		if (keywords != null)
		{
			for (String keyword : keywords)
			{
				if (biomeName.contains(keyword))
				{
					return true;
				}
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Determines whether the specified world and coordinates are a valid location for generating this gateway
	 * @param world - the world in which to generate the gateway
	 * @param x - the x-coordinate at which to center the gateway; usually where the door is placed
	 * @param y - the y-coordinate of the block on which the gateway may be built
	 * @param z - the z-coordinate at which to center the gateway; usually where the door is placed
	 * @return <code>true</code> if the location is valid, otherwise <code>false</code>
	 */
	public boolean isLocationValid(World world, int x, int y, int z)
	{
		return isBiomeValid(world.getBiomeGenForCoords(x, z));
	}

	/**
	 * Gets the dungeon pack associated with this gateway
	 * @return the dungeon pack to use for this gateway
	 */
	/*protected DungeonPack getDungeonPack()
	{
		return DungeonHelper.instance().getDungeonPack("RUINS");
	}*/
	
	/**
	 * Gets the lowercase keywords to be used in checking whether a given biome is a valid location for this gateway
	 * @return an array of biome keywords to match against
	 */
	public String[] getBiomeKeywords()
	{
		return new String[] { "" };
	}
}
