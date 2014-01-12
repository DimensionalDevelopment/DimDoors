package StevenDimDoors.mod_pocketDim.world.gateways;

import java.util.ArrayList;

import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.dungeon.pack.DungeonPack;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import net.minecraft.world.World;

public class GatewayTwoPillars extends BaseGateway 
{

	private GatewayBlockFilter filter;

	public GatewayTwoPillars(DDProperties properties)
	{
		super(properties);
		super.startingPack=DungeonHelper.instance().getDungeonPack("RUINS");
		super.isBiomeSpecific=false;
		super.allowedBiomeNames=null;
		surfaceGateway=true;
		generationWeight = 0;
		schematicPath="/schematics/gateways/twoPillars.schematic";
		
	}
	@Override
	void generateRandomBits(World world, int x, int y, int z) 
	{
		
	}

}
