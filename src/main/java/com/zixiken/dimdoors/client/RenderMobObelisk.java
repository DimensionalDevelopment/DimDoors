package com.zixiken.dimdoors.client;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.ticking.MobMonolith;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class RenderMobObelisk extends RenderLiving<MobMonolith>
{
	protected ModelMobObelisk obeliskModel;

	public RenderMobObelisk(RenderManager manager) {
		super(manager, new ModelMobObelisk(), 0.5F);
		this.obeliskModel = (ModelMobObelisk) this.mainModel;
	}

	@Override
	public void doRender(MobMonolith entity, double x, double y, double z, float par8, float par9)
	{
		final float minScaling = 0;
		final float maxScaling = 0.1f;
		MobMonolith monolith = (MobMonolith) entity;

        float aggroScaling = 0;
        if (monolith.isDangerous()) {
            // Use linear interpolation to scale how much jitter we want for our given aggro level
            aggroScaling = minScaling + (maxScaling - minScaling) * monolith.getAggroProgress();
        }

		// Calculate jitter - include entity ID to give Monoliths individual jitters
		float time = ((Minecraft.getSystemTime() + 0xF1234568 * monolith.getEntityId()) % 200000) / 50.0F;
		// We use random constants here on purpose just to get different wave forms
		double xJitter = aggroScaling * Math.sin(1.1f * time) * Math.sin(0.8f * time);
		double yJitter = aggroScaling * Math.sin(1.2f * time) * Math.sin(0.9f * time);
		double zJitter = aggroScaling * Math.sin(1.3f * time) * Math.sin(0.7f * time);

		// Render with jitter
		this.render(entity, x + xJitter, y + yJitter, z + zJitter, par8, par9);
		this.renderLeash(entity, x, y, z, par8, par9);
	}

	public void render(MobMonolith entity, double x, double y, double z, float par8, float par9) {
		if (MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Pre(entity, this, x, y, z))) return;
		GlStateManager.pushMatrix();
        GlStateManager.disableCull();
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		try {
			float interpolatedYaw = interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, par9);
			float interpolatedYawHead = interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, par9);
			float rotation;
			float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * par9;
			this.renderLivingAt(entity, x, y, z);

			rotation = this.handleRotationFloat(entity, par9);
			this.rotateCorpse(entity, rotation, interpolatedYaw, par9);

			float f6 = 0.0625F;
			GlStateManager.enableRescaleNormal();

			GlStateManager.scale(-1.0F, -1.0F, 1.0F);
			this.preRenderCallback(entity, par9);
            GlStateManager.rotate(((MobMonolith) entity).pitchLevel, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(0.0F, 24.0F * f6 - 0.0078125F, 0.0F);


			this.renderModel(entity, 0, 0, rotation, interpolatedYaw, pitch, f6);

			OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			GlStateManager.disableTexture2D();;
			OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

			GlStateManager.disableRescaleNormal();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.enableTexture2D();
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
        GlStateManager.popMatrix();

		MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post(entity, this, x, y, z));
	}
	
	public float interpolateRotation(float par1, float par2, float par3) {
		float f3 = par2 - par1;
		while (f3 < -180.0f) {
			f3 += 360.0F;
		}

		while (f3 >= 180.0F)
		{
			f3 -= 360.0F;
		}
		return par1 + par3 * f3;
	}

	@Override
	protected ResourceLocation getEntityTexture(MobMonolith entity) {
		MobMonolith monolith = (MobMonolith) entity;
		return new ResourceLocation(DimDoors.MODID + ":textures/mobs/oldMonolith/Monolith" + monolith.getTextureState() + ".png");
	}
}