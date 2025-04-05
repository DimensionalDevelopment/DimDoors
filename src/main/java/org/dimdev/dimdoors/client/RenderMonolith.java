package org.dimdev.dimdoors.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent.Post;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.entities.EntityMonolith;

import java.util.Arrays;
import java.util.List;

import static net.minecraft.client.renderer.OpenGlHelper.defaultTexUnit;
import static net.minecraft.client.renderer.OpenGlHelper.lightmapTexUnit;
import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static org.dimdev.dimdoors.DimDoors.log;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;

@SideOnly(CLIENT)
public class RenderMonolith extends RenderLiving<EntityMonolith> {

    protected static final List<ResourceLocation> MONOLITH_TEXTURES = getMonolithTextures();
    
    private static List<ResourceLocation> getMonolithTextures() {
        ResourceLocation[] textures = new ResourceLocation[19];
        for(int i=0;i<19;i++)
            textures[i] = DimDoors.getResource("textures/mobs/monolith/monolith"+i+".png");
        return Arrays.asList(textures);
    }

    public RenderMonolith(RenderManager manager, float f) {
        super(manager, new ModelMonolith(), f);
    }

    @Override public void doRender(EntityMonolith monolith, double x, double y, double z, float yaw, float partialTicks) {
        final float minScaling = 0;
        final float maxScaling = 0.1f;
        float jitterScale = 0;
        if(monolith.isDangerous())
            // Use linear interpolation to scale how much jitter we want for our given aggro level
            jitterScale = minScaling+(maxScaling-minScaling)*monolith.getAggroProgress();
        // Calculate jitter - include entity ID to give Monoliths individual jitters
        float time = ((Minecraft.getSystemTime()+0xF1234568*monolith.getEntityId())%200000)/50f;
        // We use random constants here on purpose just to get different wave forms
        double xJitter = jitterScale*Math.sin(1.1f*time)*Math.sin(0.8f*time);
        double yJitter = jitterScale*Math.sin(1.2f*time)*Math.sin(0.9f*time);
        double zJitter = jitterScale*Math.sin(1.3f*time)*Math.sin(0.7f*time);
        // Render with jitter
        render(monolith,x+xJitter,y+yJitter,z+zJitter,yaw,partialTicks);
    }

    public void render(EntityMonolith monolith, double x, double y, double z, float entityYaw, float partialTicks) {
        if(EVENT_BUS.post(new Pre<>(monolith,this,1,x,y,z))) return;
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
        mainModel.swingProgress = getSwingProgress(monolith,partialTicks);
        try {
            float interpolatedYaw = interpolateRotation(monolith.prevRenderYawOffset,monolith.renderYawOffset,partialTicks);
            float rotation;
            float pitch = monolith.prevRotationPitch+(monolith.rotationPitch-monolith.prevRotationPitch)*partialTicks;
            renderLivingAt(monolith,x,y,z);
            rotation = handleRotationFloat(monolith, partialTicks);
            applyRotations(monolith,rotation, interpolatedYaw, partialTicks);
            float scaleFactor = 0.0625f;
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(-1f,-1f,1f);
            preRenderCallback(monolith,partialTicks);
            GlStateManager.rotate(monolith.pitchLevel,1f,0f,0f);
            GlStateManager.translate(0f,24f*scaleFactor-0.0078125f,0f);
            renderModel(monolith,0,0,rotation,interpolatedYaw,pitch,scaleFactor);
            OpenGlHelper.setActiveTexture(lightmapTexUnit);
            GlStateManager.disableTexture2D();
            OpenGlHelper.setActiveTexture(defaultTexUnit);
            GlStateManager.disableRescaleNormal();
        } catch(Exception ex) {
            log.error("Couldn't render entity",ex);
        }
        OpenGlHelper.setActiveTexture(lightmapTexUnit);
        GlStateManager.enableTexture2D();
        OpenGlHelper.setActiveTexture(defaultTexUnit);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        EVENT_BUS.post(new Post<>(monolith,this,1,x,y,z));
    }

    @Override protected ResourceLocation getEntityTexture(EntityMonolith monolith) {
        return MONOLITH_TEXTURES.get(monolith.getTextureState());
    }
}