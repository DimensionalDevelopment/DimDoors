package com.zixiken.dimdoors.world.gateways;

import net.minecraft.block.Block;
import net.minecraft.item.ItemDoor;
import net.minecraft.world.World;
import com.zixiken.dimdoors.mod_pocketDim;
import com.zixiken.dimdoors.config.DDProperties;
import com.zixiken.dimdoors.core.LinkType;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.world.LimboProvider;

public class GatewayLimbo extends BaseGateway
{
	public GatewayLimbo(DDProperties properties)
	{
		super(properties);
	}

	@Override
	public boolean generate(World world, int x, int y, int z) 
	{
	    Block block = mod_pocketDim.blockLimbo;
		// Build the gateway out of Unraveled Fabric. Since nearly all the blocks in Limbo are of
		// that type, there is no point replacing the ground.
		world.setBlock(x, y + 3, z + 1, block, 0, 3);
		world.setBlock(x, y + 3, z - 1, block, 0, 3);
		
		// Build the columns around the door
		world.setBlock(x, y + 2, z - 1, block, 0, 3);
		world.setBlock(x, y + 2, z + 1, block, 0, 3);
		world.setBlock(x, y + 1, z - 1, block, 0, 3);
		world.setBlock(x, y + 1, z + 1, block, 0, 3);

		PocketManager.getDimensionData(world).createLink(x, y + 2, z, LinkType.DUNGEON, 0);

		ItemDoor.placeDoorBlock(world, x, y + 1, z, 0, mod_pocketDim.transientDoor);
		return true;
	}

	@Override
	public boolean isLocationValid(World world, int x, int y, int z) {
		return (world.provider instanceof LimboProvider);
	}
}
