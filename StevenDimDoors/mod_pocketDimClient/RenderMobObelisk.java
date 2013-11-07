package StevenDimDoors.mod_pocketDimClient;

import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMobObelisk extends RenderLiving
{
	protected ModelMobObelisk obeliskModel;

	public RenderMobObelisk(float f)
	{
		super(new ModelMobObelisk(), f);
		this.obeliskModel = (ModelMobObelisk)this.mainModel;
	}
	


	@Override
	protected ResourceLocation getEntityTexture(Entity entity) 
	{
		byte b0 = entity.getDataWatcher().getWatchableObjectByte(16);

		return new ResourceLocation(mod_pocketDim.modid+":/textures/mobs/Monolith"+b0+".png");
	}
}