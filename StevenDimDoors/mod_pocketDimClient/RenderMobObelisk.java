package StevenDimDoors.mod_pocketDimClient;

import StevenDimDoors.mod_pocketDim.ticking.MobMonolith;
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
    
   
}