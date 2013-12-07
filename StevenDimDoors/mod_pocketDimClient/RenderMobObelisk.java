package StevenDimDoors.mod_pocketDimClient;

import net.minecraft.client.renderer.entity.RenderLiving;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

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
	public ResourceLocation getEntityTexture(Entity entity) {
		int watchByte = entity.getDataWatcher().getWatchableObjectByte(16);

		return new ResourceLocation("/mods/DimDoors/textures/mobs/Monolith"+watchByte+".png");
	}
}