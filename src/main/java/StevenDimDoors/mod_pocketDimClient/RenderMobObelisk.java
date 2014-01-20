package StevenDimDoors.mod_pocketDimClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import StevenDimDoors.mod_pocketDim.mod_pocketDim;
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
	
	public void doRenderLiving(EntityLiving par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9)
	{
		  if (MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Pre(par1EntityLivingBase, this))) return;
	        GL11.glPushMatrix();
	        GL11.glDisable(GL11.GL_CULL_FACE);
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	        this.mainModel.onGround = this.renderSwingProgress(par1EntityLivingBase, par9);

	        if (this.renderPassModel != null)
	        {
	            this.renderPassModel.onGround = this.mainModel.onGround;
	        }

	        this.mainModel.isRiding = par1EntityLivingBase.isRiding();

	        if (this.renderPassModel != null)
	        {
	            this.renderPassModel.isRiding = this.mainModel.isRiding;
	        }

	        this.mainModel.isChild = par1EntityLivingBase.isChild();

	        if (this.renderPassModel != null)
	        {
	            this.renderPassModel.isChild = this.mainModel.isChild;
	        }

	        try
	        {
	            float f2 = this.interpolateRotation(par1EntityLivingBase.prevRenderYawOffset, par1EntityLivingBase.renderYawOffset, par9);
	            float f3 = this.interpolateRotation(par1EntityLivingBase.prevRotationYawHead, par1EntityLivingBase.rotationYawHead, par9);
	            float f4;

	            if (par1EntityLivingBase.isRiding() && par1EntityLivingBase.ridingEntity instanceof EntityLivingBase)
	            {
	                EntityLivingBase entitylivingbase1 = (EntityLivingBase)par1EntityLivingBase.ridingEntity;
	                f2 = this.interpolateRotation(entitylivingbase1.prevRenderYawOffset, entitylivingbase1.renderYawOffset, par9);
	                f4 = MathHelper.wrapAngleTo180_float(f3 - f2);

	                if (f4 < -85.0F)
	                {
	                    f4 = -85.0F;
	                }

	                if (f4 >= 85.0F)
	                {
	                    f4 = 85.0F;
	                }

	                f2 = f3 - f4;

	                if (f4 * f4 > 2500.0F)
	                {
	                    f2 += f4 * 0.2F;
	                }
	            }

	            float f5 = par1EntityLivingBase.prevRotationPitch + (par1EntityLivingBase.rotationPitch - par1EntityLivingBase.prevRotationPitch) * par9;
	            this.renderLivingAt(par1EntityLivingBase, par2, par4, par6);
	            f4 = this.handleRotationFloat(par1EntityLivingBase, par9);
	            this.rotateCorpse(par1EntityLivingBase, f4, f2, par9);
	            float f6 = 0.0625F;
	            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	            GL11.glScalef(-1.0F, -1.0F, 1.0F);
	            this.preRenderCallback(par1EntityLivingBase, par9);
	            GL11.glTranslatef(0.0F, -24.0F * f6 - 0.0078125F, 0.0F);
	            float f7 = par1EntityLivingBase.prevLimbSwingAmount + (par1EntityLivingBase.limbSwingAmount - par1EntityLivingBase.prevLimbSwingAmount) * par9;
	            float f8 = par1EntityLivingBase.limbSwing - par1EntityLivingBase.limbSwingAmount * (1.0F - par9);

	            if (par1EntityLivingBase.isChild())
	            {
	                f8 *= 3.0F;
	            }

	            if (f7 > 1.0F)
	            {
	                f7 = 1.0F;
	            }

	            GL11.glEnable(GL11.GL_ALPHA_TEST);
	            this.mainModel.setLivingAnimations(par1EntityLivingBase, f8, f7, par9);
	            this.renderModel(par1EntityLivingBase, f8, f7, f4, f3 - f2, f5, f6);
	            float f9;
	            int i;
	            float f10;
	            float f11;

	            for (int j = 0; j < 4; ++j)
	            {
	                i = this.shouldRenderPass(par1EntityLivingBase, j, par9);

	                if (i > 0)
	                {
	                    this.renderPassModel.setLivingAnimations(par1EntityLivingBase, f8, f7, par9);
	                    this.renderPassModel.render(par1EntityLivingBase, f8, f7, f4, f3 - f2, f5, f6);

	                    if ((i & 240) == 16)
	                    {
	                        this.func_82408_c(par1EntityLivingBase, j, par9);
	                        this.renderPassModel.render(par1EntityLivingBase, f8, f7, f4, f3 - f2, f5, f6);
	                    }

	                    if ((i & 15) == 15)
	                    {
	                        f9 = (float)par1EntityLivingBase.ticksExisted + par9;
	                        GL11.glEnable(GL11.GL_BLEND);
	                        f10 = 0.5F;
	                        GL11.glColor4f(f10, f10, f10, 1.0F);
	                        GL11.glDepthFunc(GL11.GL_EQUAL);
	                        GL11.glDepthMask(false);

	                        for (int k = 0; k < 2; ++k)
	                        {
	                            GL11.glDisable(GL11.GL_LIGHTING);
	                            f11 = 0.76F;
	                            GL11.glColor4f(0.5F * f11, 0.25F * f11, 0.8F * f11, 1.0F);
	                            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
	                            GL11.glMatrixMode(GL11.GL_TEXTURE);
	                            GL11.glLoadIdentity();
	                            float f12 = f9 * (0.001F + (float)k * 0.003F) * 20.0F;
	                            float f13 = 0.33333334F;
	                            GL11.glScalef(f13, f13, f13);
	                            GL11.glRotatef(30.0F - (float)k * 60.0F, 0.0F, 0.0F, 1.0F);
	                            GL11.glTranslatef(0.0F, f12, 0.0F);
	                            GL11.glMatrixMode(GL11.GL_MODELVIEW);
	                            this.renderPassModel.render(par1EntityLivingBase, f8, f7, f4, f3 - f2, f5, f6);
	                        }

	                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	                        GL11.glMatrixMode(GL11.GL_TEXTURE);
	                        GL11.glDepthMask(true);
	                        GL11.glLoadIdentity();
	                        GL11.glMatrixMode(GL11.GL_MODELVIEW);
	                        GL11.glEnable(GL11.GL_LIGHTING);
	                        GL11.glDisable(GL11.GL_BLEND);
	                        GL11.glDepthFunc(GL11.GL_LEQUAL);
	                    }

	                    GL11.glDisable(GL11.GL_BLEND);
	                    GL11.glEnable(GL11.GL_ALPHA_TEST);
	                }
	            }

	            GL11.glDepthMask(true);
	            this.renderEquippedItems(par1EntityLivingBase, par9);
	            float f14 = par1EntityLivingBase.getBrightness(par9);
	            i = this.getColorMultiplier(par1EntityLivingBase, f14, par9);
	            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
	            GL11.glDisable(GL11.GL_TEXTURE_2D);
	            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

	            if ((i >> 24 & 255) > 0 || par1EntityLivingBase.hurtTime > 0 || par1EntityLivingBase.deathTime > 0)
	            {
	                GL11.glDisable(GL11.GL_TEXTURE_2D);
	                GL11.glDisable(GL11.GL_ALPHA_TEST);
	                GL11.glEnable(GL11.GL_BLEND);
	                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	                GL11.glDepthFunc(GL11.GL_EQUAL);

	                if (par1EntityLivingBase.hurtTime > 0 || par1EntityLivingBase.deathTime > 0)
	                {
	                    GL11.glColor4f(f14, 0.0F, 0.0F, 0.4F);
	                    this.mainModel.render(par1EntityLivingBase, f8, f7, f4, f3 - f2, f5, f6);

	                    for (int l = 0; l < 4; ++l)
	                    {
	                        if (this.inheritRenderPass(par1EntityLivingBase, l, par9) >= 0)
	                        {
	                            GL11.glColor4f(f14, 0.0F, 0.0F, 0.4F);
	                            this.renderPassModel.render(par1EntityLivingBase, f8, f7, f4, f3 - f2, f5, f6);
	                        }
	                    }
	                }

	                if ((i >> 24 & 255) > 0)
	                {
	                    f9 = (float)(i >> 16 & 255) / 255.0F;
	                    f10 = (float)(i >> 8 & 255) / 255.0F;
	                    float f15 = (float)(i & 255) / 255.0F;
	                    f11 = (float)(i >> 24 & 255) / 255.0F;
	                    GL11.glColor4f(f9, f10, f15, f11);
	                    this.mainModel.render(par1EntityLivingBase, f8, f7, f4, f3 - f2, f5, f6);

	                    for (int i1 = 0; i1 < 4; ++i1)
	                    {
	                        if (this.inheritRenderPass(par1EntityLivingBase, i1, par9) >= 0)
	                        {
	                            GL11.glColor4f(f9, f10, f15, f11);
	                            this.renderPassModel.render(par1EntityLivingBase, f8, f7, f4, f3 - f2, f5, f6);
	                        }
	                    }
	                }

	                GL11.glDepthFunc(GL11.GL_LEQUAL);
	                GL11.glDisable(GL11.GL_BLEND);
	                GL11.glEnable(GL11.GL_ALPHA_TEST);
	                GL11.glEnable(GL11.GL_TEXTURE_2D);
	            }

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
	        GL11.glPopMatrix();
	        this.passSpecialRender(par1EntityLivingBase, par2, par4, par6);
	        MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post(par1EntityLivingBase, this));
	    
		this.func_110827_b(par1EntityLivingBase, par2, par4, par6, par8, par9);
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