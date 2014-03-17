package StevenDimDoors.mod_pocketDim.world.gateways;

import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.items.BaseItemDoor;
import StevenDimDoors.mod_pocketDim.world.LimboProvider;

public class GatewayLimbo extends BaseGateway
{
	public GatewayLimbo(DDProperties properties)
	{
		super(properties);
	}

	@Override
	public boolean generate(World world, int x, int y, int z) 
	{
		int blockID = mod_pocketDim.blockLimbo.blockID;
		// Build the gateway out of Unraveled Fabric. Since nearly all the blocks in Limbo are of
		// that type, there is no point replacing the ground.
		world.setBlock(x, y + 2, z + 1, blockID, 0, 3);
		world.setBlock(x, y + 2, z - 1, blockID, 0, 3);
		
		// Build the columns around the door
		world.setBlock(x, y + 1, z - 1, blockID, 0, 3);
		world.setBlock(x, y + 1, z + 1, blockID, 0, 3);
		world.setBlock(x, y, z - 1, blockID, 0, 3);
		world.setBlock(x, y, z + 1, blockID, 0, 3);
		
		BaseItemDoor.placeDoorBlock(world, x, y, z, 0, mod_pocketDim.transientDoor);
		return true;
	}

	@Override
	public boolean isLocationValid(World world, int x, int y, int z) {
		return (world.provider instanceof LimboProvider);
	}
}
