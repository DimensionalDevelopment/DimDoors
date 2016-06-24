package com.zixiken.dimdoors.world.gateways;

import com.zixiken.dimdoors.Point3D;
import com.zixiken.dimdoors.config.DDProperties;
import com.zixiken.dimdoors.core.LinkType;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.dungeon.DungeonSchematic;
import com.zixiken.dimdoors.schematic.InvalidSchematicException;
import net.minecraft.world.World;

public abstract class BaseSchematicGateway extends BaseGateway 
{
	public BaseSchematicGateway(DDProperties properties)
	{
		super(properties);
	}
	
	@Override
	public boolean generate(World world, int x, int y, int z)
	{
		DungeonSchematic schematic;
		
		try
		{
			schematic = DungeonSchematic.readFromResource(this.getSchematicPath());
		}
		catch (InvalidSchematicException e)
		{
			System.err.println("Could not load the schematic for a gateway. The following exception occurred:");
			e.printStackTrace();
			return false;
		}
		
		// Apply filters - the order is important!
		GatewayBlockFilter gatewayFilter = new GatewayBlockFilter();
		schematic.applyFilter(gatewayFilter);
		schematic.applyImportFilters(properties);
		
		BlockPos doorLocation = gatewayFilter.getEntranceDoorLocation();
		int orientation = gatewayFilter.getEntranceOrientation();

		// Build the gateway into the world
		schematic.copyToWorld(world, x - doorLocation.getX(), y, z - doorLocation.getZ(), true, true);
		this.generateRandomBits(world, x, y, z);
		
		// Generate a dungeon link in the door
		PocketManager.getDimensionData(world).createLink(x, y + doorLocation.getY(), z, LinkType.DUNGEON, orientation);

		return true;
	}
	
	/**
	 * Generates randomized portions of the gateway structure (e.g. rubble, foliage)
	 * @param world - the world in which to generate the gateway
	 * @param x - the x-coordinate at which to center the gateway; usually where the door is placed
	 * @param y - the y-coordinate of the block on which the gateway may be built
	 * @param z - the z-coordinate at which to center the gateway; usually where the door is placed
	 */
	protected void generateRandomBits(World world, int x, int y, int z) { }
	
	/**
	 * Gets the path for the schematic file to be used for this gateway. Subsequent calls to this method may return other schematic paths.
	 * @return the path to the schematic file for this gateway
	 */
	protected abstract String getSchematicPath();
}
