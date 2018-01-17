package org.dimdev.dimdoors.client;

import net.minecraft.client.renderer.GlStateManager;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.entities.EntityMonolith;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class RenderMonolith extends RenderLiving<EntityMonolith> {

    protected ModelMonolith monolithModel;

    protected static final List<ResourceLocation> monolith_textures = Arrays.asList(
            new ResourceLocation(DimDoors.MODID + ":textures/mobs/monolith/monolith0.png"),
            new ResourceLocation(DimDoors.MODID + ":textures/mobs/monolith/monolith1.png"),
            new ResourceLocation(DimDoors.MODID + ":textures/mobs/monolith/monolith2.png"),
            new ResourceLocation(DimDoors.MODID + ":textures/mobs/monolith/monolith3.png"),
            new ResourceLocation(DimDoors.MODID + ":textures/mobs/monolith/monolith4.png"),
            new ResourceLocation(DimDoors.MODID + ":textures/mobs/monolith/monolith5.png"),
            new ResourceLocation(DimDoors.MODID + ":textures/mobs/monolith/monolith6.png"),
            new ResourceLocation(DimDoors.MODID + ":textures/mobs/monolith/monolith7.png"),
            new ResourceLocation(DimDoors.MODID + ":textures/mobs/monolith/monolith8.png"),
            new ResourceLocation(DimDoors.MODID + ":textures/mobs/monolith/monolith9.png"),
            new ResourceLocation(DimDoors.MODID + ":textures/mobs/monolith/monolith10.png"),
            new ResourceLocation(DimDoors.MODID + ":textures/mobs/monolith/monolith11.png"),
            new ResourceLocation(DimDoors.MODID + ":textures/mobs/monolith/monolith12.png"),
            new ResourceLocation(DimDoors.MODID + ":textures/mobs/monolith/monolith13.png"),
            new ResourceLocation(DimDoors.MODID + ":textures/mobs/monolith/monolith14.png"),
            new ResourceLocation(DimDoors.MODID + ":textures/mobs/monolith/monolith15.png"),
            new ResourceLocation(DimDoors.MODID + ":textures/mobs/monolith/monolith16.png"),
            new ResourceLocation(DimDoors.MODID + ":textures/mobs/monolith/monolith17.png"),
            new ResourceLocation(DimDoors.MODID + ":textures/mobs/monolith/monolith18.png"));

    public RenderMonolith(RenderManager manager, float f) {
        super(manager, new ModelMonolith(), f);
        monolithModel = (ModelMonolith) mainModel;
    }

    @Override
    public void doRender(EntityMonolith monolith, double x, double y, double z, float entityYaw, float partialTicks) {
        final float minScaling = 0;
        final float maxScaling = 0.1f;

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
        render(monolith, x + xJitter, y + yJitter, z + zJitter, entityYaw, partialTicks);
        //this.renderLeash(entity, x, y, z, par8, par9);
    }

    public void render(EntityMonolith par1EntityLivingBase, double x, double y, double z, float par8, float par9) {
        if (MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Pre<>(par1EntityLivingBase, this, 1, x, y, z))) return;
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        mainModel.swingProgress = getSwingProgress(par1EntityLivingBase, par9);

        try {
            float interpolatedYaw = interpolateRotation(par1EntityLivingBase.prevRenderYawOffset, par1EntityLivingBase.renderYawOffset, par9);
            float rotation;
            float pitch = par1EntityLivingBase.prevRotationPitch + (par1EntityLivingBase.rotationPitch - par1EntityLivingBase.prevRotationPitch) * par9;
            renderLivingAt(par1EntityLivingBase, x, y, z);

            rotation = handleRotationFloat(par1EntityLivingBase, par9);
            applyRotations(par1EntityLivingBase, rotation, interpolatedYaw, par9);

            float f6 = 0.0625F;
            GlStateManager.enableRescaleNormal();

            GlStateManager.scale(-1.0F, -1.0F, 1.0F);
            preRenderCallback(par1EntityLivingBase, par9);
            GlStateManager.rotate(par1EntityLivingBase.pitchLevel, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(0.0F, 24.0F * f6 - 0.0078125F, 0.0F);

            renderModel(par1EntityLivingBase, 0, 0, rotation, interpolatedYaw, pitch, f6);

            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.disableTexture2D();
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

            GlStateManager.disableRescaleNormal();
        } catch (Exception e) {
            DimDoors.log.error(e);
        }

        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post<>(par1EntityLivingBase, this, 1, x, y, z));
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMonolith monolith) {
        return monolith_textures.get(monolith.getTextureState());
        //return new ResourceLocation(DimDoors.MODID + ":textures/mobs/monolith/monolith" + monolith.getTextureState() + ".png");
    }
}
