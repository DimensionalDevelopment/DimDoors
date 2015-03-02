package StevenDimDoors.mod_pocketDim.world.gateways;

import StevenDimDoors.mod_pocketDim.config.DDProperties;

public class GatewaySandstonePillars extends BaseSchematicGateway 
{
	public GatewaySandstonePillars(DDProperties properties)
	{
		super(properties);
	}
	
	@Override
	public String[] getBiomeKeywords() 
	{
		return new String[] { "desert" };
	}
	
	@Override
	public String getSchematicPath() 
	{
		return "/schematics/gateways/sandstonePillars.schematic";
	}
}
