package com.zixiken.dimdoors.world.gateways;

import com.zixiken.dimdoors.config.DDProperties;

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
