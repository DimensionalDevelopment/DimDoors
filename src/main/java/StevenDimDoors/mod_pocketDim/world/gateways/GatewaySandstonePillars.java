package StevenDimDoors.mod_pocketDim.world.gateways;

import java.util.ArrayList;
import java.util.Random;

import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.dungeon.pack.DungeonPack;
import StevenDimDoors.mod_pocketDim.helpers.DungeonHelper;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

public class GatewaySandstonePillars extends BaseGateway 
{

	private static final int GATEWAY_RADIUS = 4;

	public GatewaySandstonePillars(DDProperties properties)
	{
		super(properties);
		
	}
	@Override
	public boolean generate(World world, int x, int y, int z)
	{
		//simple to transform the generation location here.
		//Do you think this is the best way to do this?
		return super.generate(world, x, y+2, z);
	}
	@Override
	public void generateRandomBits(World world, int x, int y, int z) 
	{
	}
	@Override
	public DungeonPack getStartingPack() 
	{
		return DungeonHelper.instance().getDungeonPack("RUINS");
	}
	@Override
	public String[] getBiomeNames() 
	{
		return new String[]{"desert"};
	}
	@Override
	public String getSchematicPath() 
	{
		return "/schematics/gateways/sandstonePillars.schematic";
	}
	@Override
	public boolean isSurfaceGateway() 
	{
		return true;
	}
	@Override
	public boolean areCoordsValid(World world, int x, int y, int z) {
		// TODO Auto-generated method stub
		return true;
	}

}
