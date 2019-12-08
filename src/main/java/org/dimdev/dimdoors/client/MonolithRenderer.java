//package org.dimdev.dimdoors.client;
//
//import com.mojang.blaze3d.systems.RenderSystem;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.render.entity.LivingEntityRenderer;
//import net.minecraft.client.renderer.RenderSystem;
//import net.minecraft.client.renderer.OpenGlHelper;
//import net.minecraft.client.renderer.entity.RenderLiving;
//import net.minecraft.client.renderer.entity.RenderManager;
//import net.minecraft.util.Identifier;
//import net.minecraftforge.client.event.RenderLivingEvent;
//import net.minecraftforge.common.MinecraftForge;
//import org.dimdev.dimdoors.DimDoors;
//import org.dimdev.dimdoors.entity.MonolithEntity;
//import org.lwjgl.opengl.GL11;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Environment(EnvType.CLIENT)
//public class MonolithRenderer extends LivingEntityRenderer<MonolithEntity, MonolithModel> {
//    protected static final List<Identifier> MONOLITH_TEXTURES = Arrays.asList(
//            new Identifier("dimdoors:textures/mobs/monolith/monolith0.png"),
//            new Identifier("dimdoors:textures/mobs/monolith/monolith1.png"),
//            new Identifier("dimdoors:textures/mobs/monolith/monolith2.png"),
//            new Identifier("dimdoors:textures/mobs/monolith/monolith3.png"),
//            new Identifier("dimdoors:textures/mobs/monolith/monolith4.png"),
//            new Identifier("dimdoors:textures/mobs/monolith/monolith5.png"),
//            new Identifier("dimdoors:textures/mobs/monolith/monolith6.png"),
//            new Identifier("dimdoors:textures/mobs/monolith/monolith7.png"),
//            new Identifier("dimdoors:textures/mobs/monolith/monolith8.png"),
//            new Identifier("dimdoors:textures/mobs/monolith/monolith9.png"),
//            new Identifier("dimdoors:textures/mobs/monolith/monolith10.png"),
//            new Identifier("dimdoors:textures/mobs/monolith/monolith11.png"),
//            new Identifier("dimdoors:textures/mobs/monolith/monolith12.png"),
//            new Identifier("dimdoors:textures/mobs/monolith/monolith13.png"),
//            new Identifier("dimdoors:textures/mobs/monolith/monolith14.png"),
//            new Identifier("dimdoors:textures/mobs/monolith/monolith15.png"),
//            new Identifier("dimdoors:textures/mobs/monolith/monolith16.png"),
//            new Identifier("dimdoors:textures/mobs/monolith/monolith17.png"),
//            new Identifier("dimdoors:textures/mobs/monolith/monolith18.png"));
//
//    public MonolithRenderer(RenderManager manager, float f) {
//        super(manager, new MonolithModel(), f);
//    }
//
//    @Override
//    public void doRender(MonolithEntity monolith, double x, double y, double z, float entityYaw, float partialTicks) {
//        final float minScaling = 0;
//        final float maxScaling = 0.1f;
//
//        float jitterScale = 0;
//        if (monolith.isDangerous()) {
//            // Use linear interpolation to scale how much jitter we want for our given aggro level
//            jitterScale = minScaling + (0.1f) * monolith.getAggroProgress();
//        }
//
//        // Calculate jitter - include entity ID to give Monoliths individual jitters
//        float time = ((Minecraft.getSystemTime() + 0xF1234568 * monolith.getEntityId()) % 200000) / 50.0F;
//
//        // We use random constants here on purpose just to get different wave forms
//        double xJitter = jitterScale * Math.sin(1.1f * time) * Math.sin(0.8f * time);
//        double yJitter = jitterScale * Math.sin(1.2f * time) * Math.sin(0.9f * time);
//        double zJitter = jitterScale * Math.sin(1.3f * time) * Math.sin(0.7f * time);
//
//        // Render with jitter
//        render(monolith, x + xJitter, y + yJitter, z + zJitter, entityYaw, partialTicks);
//    }
//
//    public void render(MonolithEntity monolith, double x, double y, double z, float entityYaw, float partialTicks) {
//        if (MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Pre<>(monolith, this, 1, x, y, z))) return;
//        RenderSystem.pushMatrix();
//        RenderSystem.disableCull();
//        RenderSystem.disableLighting();
//        RenderSystem.enableBlend();
//        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//        mainModel.swingProgress = getSwingProgress(monolith, partialTicks);
//
//        try {
//            float interpolatedYaw = interpolateRotation(monolith.prevRenderYawOffset, monolith.renderYawOffset, partialTicks);
//            float rotation;
//            float pitch = monolith.prevRotationPitch + (monolith.rotationPitch - monolith.prevRotationPitch) * partialTicks;
//            renderLivingAt(monolith, x, y, z);
//
//            rotation = handleRotationFloat(monolith, partialTicks);
//            applyRotations(monolith, rotation, interpolatedYaw, partialTicks);
//
//            float scaleFactor = 0.0625F;
//            RenderSystem.enableRescaleNormal();
//
//            RenderSystem.scale(-1.0f, -1.0f, 1.0F);
//            preRenderCallback(monolith, partialTicks);
//            RenderSystem.rotate(monolith.pitchLevel, 1.0F, 0.0F, 0.0F);
//            RenderSystem.translate(0.0F, 24.0F * scaleFactor - 0.0078125F, 0.0F);
//
//            renderModel(monolith, 0, 0, rotation, interpolatedYaw, pitch, scaleFactor);
//
//            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
//            RenderSystem.disableTexture2D();
//            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
//
//            RenderSystem.disableRescaleNormal();
//        } catch (Exception e) {
//            LOGGER.error("Couldn't render entity", e);
//        }
//
//        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
//        RenderSystem.enableTexture2D();
//        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
//        RenderSystem.enableCull();
//        RenderSystem.enableLighting();
//        RenderSystem.disableBlend();
//        RenderSystem.popMatrix();
//        MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post<>(monolith, this, 1, x, y, z));
//    }
//
//    @Override
//    protected Identifier getEntityTexture(MonolithEntity monolith) {
//        return MONOLITH_TEXTURES.get(monolith.getTextureState());
//    }
//}
