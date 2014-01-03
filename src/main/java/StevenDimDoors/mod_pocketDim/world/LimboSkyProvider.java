package StevenDimDoors.mod_pocketDim.world;

import net.minecraft.util.ResourceLocation;

public class LimboSkyProvider extends CustomSkyProvider
{
	@Override
	public ResourceLocation getMoonRenderPath()
	{
		return new ResourceLocation("DimDoors:textures/other/limboMoon.png");
	}
	
	@Override
	public ResourceLocation getSunRenderPath()
	{
		return new ResourceLocation("DimDoors:textures/other/limboSun.png");
	}
}