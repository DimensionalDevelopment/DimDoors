package com.zixiken.dimdoors.world.gateways;

import com.zixiken.dimdoors.Point3D;
import com.zixiken.dimdoors.config.DDProperties;
import com.zixiken.dimdoors.core.LinkType;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.dungeon.DungeonSchematic;
import com.zixiken.dimdoors.schematic.InvalidSchematicException;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;

public abstract class BaseSchematicGateway extends BaseGateway {
	public BaseSchematicGateway(DDProperties properties) {
		super(properties);
	}
	
	@Override
	public boolean generate(World world, BlockPos pos) {
		DungeonSchematic schematic;
		
		try {
			schematic = DungeonSchematic.readFromResource(this.getSchematicPath());
		} catch (InvalidSchematicException e) {
			System.err.println("Could not load the schematic for a gateway. The following exception occurred:");
			e.printStackTrace();
			return false;
		}
		
		// Apply filters - the order is important!
		GatewayBlockFilter gatewayFilter = new GatewayBlockFilter();
		schematic.applyFilter(gatewayFilter);
		schematic.applyImportFilters(properties);
		
		BlockPos doorLocation = gatewayFilter.getEntranceDoorLocation();
		EnumFacing orientation = gatewayFilter.getEntranceOrientation();

		// Build the gateway into the world
		schematic.copyToWorld(world, pos.subtract(new Vec3i(doorLocation.getX(), 0, doorLocation.getZ())), true, true);
		this.generateRandomBits(world, pos);
		
		// Generate a dungeon link in the door
		PocketManager.getDimensionData(world).createLink(pos.add(0, doorLocation.getY(), 0), LinkType.DUNGEON, orientation);

		return true;
	}
	
	/**
	 * Generates randomized portions of the gateway structure (e.g. rubble, foliage)
	 * @param world - the world in which to generate the gateway
	 * @param pos - the coordinate at which to center the gateway; usually where the door is placed
	 */
	protected void generateRandomBits(World world, BlockPos pos) {
	}
	
	/**
	 * Gets the path for the schematic file to be used for this gateway. Subsequent calls to this method may return other schematic paths.
	 * @return the path to the schematic file for this gateway
	 */
	protected abstract String getSchematicPath();
}
