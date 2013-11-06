package StevenDimDoors.mod_pocketDim;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.client.IRenderHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CloudRenderBlank extends IRenderHandler
{
    @Override
	@SideOnly(Side.CLIENT)
    public void render(float partialTicks, WorldClient world, Minecraft mc)
    {
    	
    }
}
