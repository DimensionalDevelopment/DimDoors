package StevenDimDoors.mod_pocketDimClient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.ticking.MobMonolith;
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
	public void doRender(EntityLiving entity, double x, double y, double z, float par8, float par9)
	{
		final float minScaling = 0;
		final float maxScaling = 0.1f;
		MobMonolith monolith = ((MobMonolith) entity);

		// Use linear interpolation to scale how much jitter we want for our given aggro level
		float aggroScaling = minScaling + (maxScaling - minScaling) * monolith.getAggroProgress();

		// Calculate jitter - include entity ID to give Monoliths individual jitters
		float time = ((Minecraft.getSystemTime() + 0xF1234568 * monolith.getEntityId()) % 200000) / 50.0F;
		// We use random constants here on purpose just to get different wave forms
		double xJitter = aggroScaling * Math.sin(1.1f * time) * Math.sin(0.8f * time);
		double yJitter = aggroScaling * Math.sin(1.2f * time) * Math.sin(0.9f * time);
		double zJitter = aggroScaling * Math.sin(1.3f * time) * Math.sin(0.7f * time);

		// Render with jitter
		this.render(entity, x + xJitter, y + yJitter, z + zJitter, par8, par9);
		this.func_110827_b(entity, x, y, z, par8, par9);
	}

	public void render(EntityLiving par1EntityLivingBase, double x, double y, double z, float par8, float par9)
	{
		if (MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Pre(par1EntityLivingBase, this, x, y, z))) return;
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		this.mainModel.onGround = this.renderSwingProgress(par1EntityLivingBase, par9);

		try
		{
			float interpolatedYaw = interpolateRotation(par1EntityLivingBase.prevRenderYawOffset, par1EntityLivingBase.renderYawOffset, par9);
			float interpolatedYawHead = interpolateRotation(par1EntityLivingBase.prevRotationYawHead, par1EntityLivingBase.rotationYawHead, par9);
			float rotation;
			float pitch = par1EntityLivingBase.prevRotationPitch + (par1EntityLivingBase.rotationPitch - par1EntityLivingBase.prevRotationPitch) * par9;
			this.renderLivingAt(par1EntityLivingBase, x, y, z);

			rotation = this.handleRotationFloat(par1EntityLivingBase, par9);
			this.rotateCorpse(par1EntityLivingBase, rotation, interpolatedYaw, par9);

			float f6 = 0.0625F;
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);

			GL11.glScalef(-1.0F, -1.0F, 1.0F);
			this.preRenderCallback(par1EntityLivingBase, par9);
            GL11.glRotatef(((MobMonolith)par1EntityLivingBase).pitchLevel , 1.0F, 0.0F, 0.0F);
    		GL11.glTranslatef(0.0F, 24.0F * f6 - 0.0078125F, 0.0F);


			this.renderModel(par1EntityLivingBase, 0, 0, rotation, interpolatedYaw, pitch, f6);

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
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glPopMatrix();
		MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post(par1EntityLivingBase, this, x, y, z));
	}
	
	private static float interpolateRotation(float par1, float par2, float par3)
	{
		float f3 = par2 - par1;
		while (f3 < -180.0f)
		{
			f3 += 360.0F;
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
		MobMonolith monolith = (MobMonolith) entity;
		return new ResourceLocation(mod_pocketDim.modid + ":textures/mobs/Monolith" + monolith.getTextureState() + ".png");
	}
}