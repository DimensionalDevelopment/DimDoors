package StevenDimDoors.mod_pocketDim.world.gateways;

import java.util.ArrayList;

import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.dungeon.pack.DungeonPack;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import StevenDimDoors.mod_pocketDim.world.LimboProvider;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class GatewayTwoPillars extends BaseGateway 
{

	private static final int GATEWAY_RADIUS = 4;

	public GatewayTwoPillars(DDProperties properties)
	{
		super(properties);
	}
	@Override
	void generateRandomBits(World world, int x, int y, int z) 
	{
		final int blockID = Block.stoneBrick.blockID;
		
		//Replace some of the ground around the gateway with bricks
		for (int xc = -GATEWAY_RADIUS; xc <= GATEWAY_RADIUS; xc++)
		{
			for (int zc= -GATEWAY_RADIUS; zc <= GATEWAY_RADIUS; zc++)
			{
				//Check that the block is supported by an opaque block.
				//This prevents us from building over a cliff, on the peak of a mountain,
				//or the surface of the ocean or a frozen lake.
				if (world.isBlockOpaqueCube(x + xc, y - 2, z + zc))
				{
					//Randomly choose whether to place bricks or not. The math is designed so that the
					//chances of placing a block decrease as we get farther from the gateway's center.
					if (Math.abs(xc) + Math.abs(zc) < world.rand.nextInt(2) + 3)
					{
						//Place Stone Bricks
						world.setBlock(x + xc, y - 1, z + zc, blockID, 0, 3);
					}
					else if (Math.abs(xc) + Math.abs(zc) < world.rand.nextInt(3) + 3)
					{
						//Place Cracked Stone Bricks
						world.setBlock(x + xc, y - 1, z + zc, blockID, 2, 3);
					}
				}
			}
		}
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
		return "/schematics/gateways/twoPillars.schematic";
	}
	@Override
	public boolean isSurfaceGateway() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public boolean areCoordsValid(World world, int x, int y, int z) {
		// TODO Auto-generated method stub
		return !(world.provider instanceof LimboProvider);
	}

}
