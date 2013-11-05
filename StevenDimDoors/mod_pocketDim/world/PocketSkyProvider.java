package StevenDimDoors.mod_pocketDim.world;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.IRenderHandler;

public class PocketSkyProvider extends CustomSkyProvider
{
	
	public class LimboSkyProvider extends CustomSkyProvider
	{
		@Override
		public String getMoonRenderPath()
		{
			return "/mods/DimDoors/textures/other/pocketMoon.png";
		}
		
		@Override
		public String getSunRenderPath()
		{
			return "/mods/DimDoors/textures/other/pocketSun.png";
		}
	}
}