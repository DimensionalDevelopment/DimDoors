package org.dimdev.dimdoors.shared.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

public class CustomSkyProvider extends IRenderHandler {

    private static final ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");
    int starGLCallList;
    int glSkyList;
    int glSkyList2;

    public ResourceLocation getMoonRenderPath() {
        return null;
    }

    public ResourceLocation getSunRenderPath() {
        return null;
    }


    @SideOnly(Side.CLIENT)
    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {

        starGLCallList = GLAllocation.generateDisplayLists(3);
        glSkyList = starGLCallList + 1;
        glSkyList2 = starGLCallList + 2;
        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.depthMask(false);

        mc.renderEngine.bindTexture(locationEndSkyPng);

        if (mc.world.provider.isSurfaceWorld() && Objects.nonNull(mc.getRenderViewEntity())) {
            GlStateManager.disableTexture2D();
            final Vec3d vec3 = world.getSkyColor(mc.getRenderViewEntity(), partialTicks);
            float f1 = (float) vec3.x;
            float f2 = (float) vec3.y;
            float f3 = (float) vec3.z;
            float f4;

            if (mc.gameSettings.anaglyph) {
                float f5 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
                float f6 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
                f4 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
                f1 = f5;
                f2 = f6;
                f3 = f4;
            }

            GlStateManager.color(f1, f2, f3);
            final Tessellator tessellator = Tessellator.getInstance();
            final BufferBuilder buffer = tessellator.getBuffer();
            GlStateManager.depthMask(false);
            GlStateManager.enableFog();
            GlStateManager.color(f1, f2, f3);
            GlStateManager.callList(glSkyList);
            GlStateManager.disableFog();
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderHelper.disableStandardItemLighting();
            float[] afloat = world.provider.calcSunriseSunsetColors(world.getCelestialAngle(partialTicks), partialTicks);
            float f7;
            float f8;
            float f9;
            float f10;

            if (afloat != null) {
                GlStateManager.disableTexture2D();
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
                GlStateManager.pushMatrix();
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(MathHelper.sin(world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                f4 = afloat[0];
                f7 = afloat[1];
                f8 = afloat[2];
                float f11;

                if (mc.gameSettings.anaglyph) {
                    f9 = (f4 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
                    f10 = (f4 * 30.0F + f7 * 70.0F) / 100.0F;
                    f11 = (f4 * 30.0F + f8 * 70.0F) / 100.0F;
                    f4 = f9;
                    f7 = f10;
                    f8 = f11;
                }

                buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
                buffer.color(f4, f7, f8, afloat[3]).pos(0.0, 100D, 0).endVertex();
                byte b0 = 16;

                for (int j = 0; j <= b0; ++j) {
                    f11 = j * (float) Math.PI * 2.0F / b0;
                    float f12 = MathHelper.sin(f11);
                    float f13 = MathHelper.cos(f11);
                    buffer.color(afloat[0], afloat[1], afloat[2], 0.0F).pos(f12 * 120.0F, f13 * 120.0F, -f13 * 40.0F * afloat[3]).endVertex();
                }

                tessellator.draw();
                GlStateManager.popMatrix();
                GlStateManager.shadeModel(GL11.GL_FLAT);
            }

            GlStateManager.enableTexture2D();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            GlStateManager.pushMatrix();
            f4 = 1.0F - world.getRainStrength(partialTicks);
            f7 = 0.0F;
            f8 = 0.0F;
            f9 = 0.0F;
            GlStateManager.color(1.0F, 1.0F, 1.0F, f4);
            GlStateManager.translate(f7, f8, f9);
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
            f10 = 30.0F;
            mc.renderEngine.bindTexture(getSunRenderPath());
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(-f10, 100, -f10).tex(0, 0).endVertex();
            buffer.pos(f10, 100, -f10).tex(1, 0).endVertex();
            buffer.pos(f10, 100, f10).tex(1, 1).endVertex();
            buffer.pos(-f10, 100, f10).tex(0, 1).endVertex();
            tessellator.draw();

            f10 = 20.0F;
            mc.renderEngine.bindTexture(getMoonRenderPath());
            int k = world.getMoonPhase();
            int i = k % 4;
            int i1 = k / 4 % 2;
            float f16 = i + 1;
            float f17 = i1 + 1;
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(-f10, -100, f10).tex(f16, f17).endVertex();
            buffer.pos(f10, -100, f10).tex((float) i, f17).endVertex();
            buffer.pos(f10, -100, -f10).tex((float) i, (float) i1).endVertex();
            buffer.pos(-f10, -100, -f10).tex(f16, (float) i1).endVertex();
            tessellator.draw();
            GlStateManager.disableTexture2D();
            float f18 = world.getStarBrightness(partialTicks) * f4;

            if (f18 > 0.0F) {
                GlStateManager.color(f18, f18, f18, f18);
                GlStateManager.callList(starGLCallList);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableFog();
            GlStateManager.popMatrix();
            GlStateManager.disableTexture2D();
            GlStateManager.color(0.0F, 0.0F, 0.0F);
            double d0 = mc.player.getLook(partialTicks).y - world.getHorizon();

            if (d0 < 0.0D) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0F, 12.0F, 0.0F);
                GlStateManager.callList(glSkyList2);
                GlStateManager.popMatrix();
                f8 = 1.0F;
                f9 = -((float) (d0 + 65.0D));
                f10 = -f8;

                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                buffer.pos(-f8, f9, f8).color(0,0,0,1).endVertex();
                buffer.pos(f8, f9, f8).color(0,0,0,1).endVertex();
                buffer.pos(f8, f10, f8).color(0,0,0,1).endVertex();
                buffer.pos(-f8, f10, f8).color(0,0,0,1).endVertex();
                buffer.pos(-f8, f10, -f8).color(0,0,0,1).endVertex();
                buffer.pos(f8, f10, -f8).color(0,0,0,1).endVertex();
                buffer.pos(f8, f9, -f8).color(0,0,0,1).endVertex();
                buffer.pos(-f8, f9, -f8).color(0,0,0,1).endVertex();
                buffer.pos(f8, f10, -f8).color(0,0,0,1).endVertex();
                buffer.pos(f8, f10, f8).color(0,0,0,1).endVertex();
                buffer.pos(f8, f9, f8).color(0,0,0,1).endVertex();
                buffer.pos(f8, f9, -f8).color(0,0,0,1).endVertex();
                buffer.pos(-f8, f9, -f8).color(0,0,0,1).endVertex();
                buffer.pos(-f8, f9, f8).color(0,0,0,1).endVertex();
                buffer.pos(-f8, f10, f8).color(0,0,0,1).endVertex();
                buffer.pos(-f8, f10, -f8).color(0,0,0,1).endVertex();
                buffer.pos(-f8, f10, -f8).color(0,0,0,1).endVertex();
                buffer.pos(-f8, f10, f8).color(0,0,0,1).endVertex();
                buffer.pos(f8, f10, f8).color(0,0,0,1).endVertex();
                buffer.pos(f8, f10, -f8).color(0,0,0,1).endVertex();
                tessellator.draw();
            }

            if (world.provider.isSkyColored()) {
                GlStateManager.color(f1 * 0.2F + 0.04F, f2 * 0.2F + 0.04F, f3 * 0.6F + 0.1F);
            } else {
                GlStateManager.color(f1, f2, f3);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, -((float) (d0 - 16.0D)), 0.0F);
            GlStateManager.callList(glSkyList2);
            GlStateManager.popMatrix();
            GlStateManager.enableTexture2D();
            GlStateManager.depthMask(true);
        }
    }
}
