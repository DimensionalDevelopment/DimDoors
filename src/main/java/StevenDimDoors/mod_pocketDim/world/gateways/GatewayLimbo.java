package StevenDimDoors.mod_pocketDim.world.gateways;

import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.dungeon.pack.DungeonPack;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import StevenDimDoors.mod_pocketDim.items.BaseItemDoor;
import StevenDimDoors.mod_pocketDim.world.LimboProvider;

public class GatewayLimbo extends BaseGateway
{

	public GatewayLimbo(DDProperties properties) {
		super(properties);
		// TODO Auto-generated constructor stub
	}

	@Override
	void generateRandomBits(World world, int x, int y, int z) 
	{
		int blockID = mod_pocketDim.blockLimbo.blockID;
		//Build the gateway out of Unraveled Fabric. Since nearly all the blocks in Limbo are of
		//that type, there is no point replacing the ground.
		world.setBlock(x, y + 2, z + 1, blockID, 0, 3);
		world.setBlock(x, y + 2, z - 1, blockID, 0, 3);
		
		//Build the columns around the door
		world.setBlock(x, y + 1, z - 1, blockID, 0, 3);
		world.setBlock(x, y + 1, z + 1, blockID, 0, 3);
		world.setBlock(x, y, z - 1, blockID, 0, 3);
		world.setBlock(x, y, z + 1, blockID, 0, 3);
		
		BaseItemDoor.placeDoorBlock(world, x, y, z, 0, mod_pocketDim.transientDoor);


	}

	@Override
	public DungeonPack getStartingPack() {
		// TODO Auto-generated method stub
		return DungeonHelper.instance().getDungeonPack("RUINS");
	}

	@Override
	public String[] getBiomeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSchematicPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSurfaceGateway() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean areCoordsValid(World world, int x, int y, int z) {
		// TODO Auto-generated method stub
		return world.provider instanceof LimboProvider;
	}

}
