package StevenDimDoors.mod_pocketDimClient;

import net.minecraft.client.renderer.entity.RenderLiving;
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