package StevenDimDoors.mod_pocketDimClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.ticking.MobMonolith;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
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
	
	public void doRenderLiving(EntityLiving entity, double x, double y, double z, float par8, float par9)
	{
		int noiseFactor = (int) (50-Math.log(MobMonolith.class.cast(entity).aggro));
		this.render(entity, x+entity.worldObj.rand.nextGaussian()/noiseFactor, y+entity.worldObj.rand.nextGaussian()/noiseFactor, z+entity.worldObj.rand.nextGaussian()/noiseFactor, par8, par9);
		this.func_110827_b(entity, x, y, z, par8, par9);
	}
	
	public void render(EntityLiving par1EntityLivingBase, double x, double y, double z, float par8, float par9)
	{
		 if (MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Pre(par1EntityLivingBase, this))) return;  	
		  GL11.glPushMatrix();
		  GL11.glDisable(GL11.GL_CULL_FACE);
		  GL11.glDisable(GL11.GL_LIGHTING);
		  GL11.glEnable(GL11.GL_BLEND);
		  GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		  this.mainModel.onGround = this.renderSwingProgress(par1EntityLivingBase, par9);

		  try
		  {
			  float interpolatedYaw = this.interpolateRotation(par1EntityLivingBase.prevRenderYawOffset, par1EntityLivingBase.renderYawOffset, par9);
			  float interpolatedYawHead = this.interpolateRotation(par1EntityLivingBase.prevRotationYawHead, par1EntityLivingBase.rotationYawHead, par9);
			  float rotation;
			  float pitch = par1EntityLivingBase.prevRotationPitch + (par1EntityLivingBase.rotationPitch - par1EntityLivingBase.prevRotationPitch) * par9;
			  this.renderLivingAt(par1EntityLivingBase, x, y, z);

			  rotation = this.handleRotationFloat(par1EntityLivingBase, par9);
			  this.rotateCorpse(par1EntityLivingBase, rotation, interpolatedYaw, par9);
			  float f6 = 0.0625F;
			  GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			  GL11.glScalef(-1.0F, -1.0F, 1.0F);
			  this.preRenderCallback(par1EntityLivingBase, par9);
			  GL11.glTranslatef(0.0F, -24.0F * f6 - 0.0078125F, 0.0F);

			  this.renderModel(par1EntityLivingBase, 0, 0, rotation, interpolatedYawHead - interpolatedYaw, pitch, f6);

			  OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			  GL11.glDisable(GL11.GL_TEXTURE_2D);
			  OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	            
			  GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		  }
		  catch (Exception exception)
		  {
			  exception.printStackTrace();
		  }

	        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
	        GL11.glEnable(GL11.GL_TEXTURE_2D);
	        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	        GL11.glEnable(GL11.GL_CULL_FACE);
	        GL11.glEnable(GL11.GL_LIGHTING);
	        GL11.glDisable(GL11.GL_BLEND);

	        GL11.glPopMatrix();
	        MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post(par1EntityLivingBase, this));
	}

	
	   private float interpolateRotation(float par1, float par2, float par3)
	    {
	        float f3;

	        for (f3 = par2 - par1; f3 < -180.0F; f3 += 360.0F)
	        {
	            ;
	        }

	        while (f3 >= 180.0F)
	        {
	            f3 -= 360.0F;
	        }

	        return par1 + par3 * f3;
	    }
	@Override
	protected ResourceLocation getEntityTexture(Entity entity) 
	{
		byte b0 = entity.getDataWatcher().getWatchableObjectByte(16);

		return new ResourceLocation(mod_pocketDim.modid+":textures/mobs/Monolith"+b0+".png");
	}
}