package StevenDimDoors.mod_pocketDimClient;

import StevenDimDoors.mod_pocketDim.ticking.MobObelisk;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
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
    
    public void renderMobObelisk(MobObelisk mobObelisk, double d, double d1, double d2,
            float f, float f1)
    {
        super.doRenderLiving( mobObelisk, d, d1, d2, f, f1);
    }

    public void doRenderLiving(EntityLiving entityliving, double d, double d1, double d2,
            float f, float f1)
    {
    	renderMobObelisk((MobObelisk)entityliving, d, d1, d2, f, f1);
    }

    public void doRender(Entity entity, double d, double d1, double d2,
            float f, float f1)
    {
    	renderMobObelisk((MobObelisk)entity, d, d1, d2, f, f1);
    }
}