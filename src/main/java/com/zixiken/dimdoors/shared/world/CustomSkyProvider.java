package com.zixiken.dimdoors.shared.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public class CustomSkyProvider extends IRenderHandler {

    int starGLCallList;
    int glSkyList;
    int glSkyList2;
    private static final ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");


    public ResourceLocation getMoonRenderPath() {
        return null;
    }

    public ResourceLocation getSunRenderPath() {
        return null;
    }


    @SideOnly(Side.CLIENT)
    @Override
    public void render(float par1, WorldClient world, Minecraft mc) {

        starGLCallList = GLAllocation.generateDisplayLists(3);
        glSkyList = this.starGLCallList + 1;
        glSkyList2 = this.starGLCallList + 2;
        GL11.glDisable(GL11.GL_FOG);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderHelper.disableStandardItemLighting();
        GL11.glDepthMask(false);

        mc.renderEngine.bindTexture((locationEndSkyPng));

        if (world.provider.isSurfaceWorld()) {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            Vec3d vec3 = world.getSkyColor(mc.getRenderViewEntity(), par1);
            float f1 = (float) vec3.xCoord;
            float f2 = (float) vec3.yCoord;
            float f3 = (float) vec3.zCoord;
            float f4;

            GL11.glColor3f(f1, f2, f3);
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer buffer = tessellator.getBuffer();

            GlStateManager.depthMask(false);
            GlStateManager.enableFog();
            GlStateManager.color(f1, f2, f3);
            GlStateManager.callList(this.glSkyList);
            GlStateManager.disableFog();
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderHelper.disableStandardItemLighting();

            float[] afloat = world.provider.calcSunriseSunsetColors(world.getCelestialAngle(par1), par1);
            float f7;
            float f8;
            float f9;
            float f10;

            if (afloat != null) {
                GlStateManager.disableTexture2D();
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
                GlStateManager.pushMatrix();
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(MathHelper.sin(world.getCelestialAngleRadians(par1)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                f4 = afloat[0];
                f7 = afloat[1];
                f8 = afloat[2];
                float f11;

                buffer.begin(6, DefaultVertexFormats.POSITION_COLOR);

                buffer.pos(0d, 100d, 0d).color(f4, f7, f8, afloat[3]).endVertex();

                byte b0 = 16;

                for (int j = 0; j <= b0; ++j) {
                    f11 = j * (float) Math.PI * 2.0F / b0;
                    float f12 = MathHelper.sin(f11);
                    float f13 = MathHelper.cos(f11);

                    buffer.pos(f12 * 120.0F, f13 * 120.0F, -f13 * 40.0F * afloat[3]).color(afloat[0], afloat[1], afloat[2], 0.0f).endVertex();
                }

                tessellator.draw();
                GlStateManager.popMatrix();
                GlStateManager.shadeModel(GL11.GL_FLAT);
            }

            GlStateManager.enableTexture2D();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            GlStateManager.pushMatrix();

            f4 = 1.0F - world.getRainStrength(par1);
            f7 = 0.0F;
            f8 = 0.0F;
            f9 = 0.0F;
            GlStateManager.color(1.0F, 1.0F, 1.0F, f4);
            GlStateManager.translate(f7, f8, f9);
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(world.getCelestialAngle(par1) * 360.0F, 1.0F, 0.0F, 0.0F);

            f10 = 30.0F;
            mc.renderEngine.bindTexture(this.getSunRenderPath());
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(-f10, 100.0D, -f10).tex(0.0D, 0.0D).endVertex();
            buffer.pos(f10, 100.0D, -f10).tex(1.0D, 0.0D).endVertex();
            buffer.pos(f10, 100.0D, f10).tex(1.0D, 1.0D).endVertex();
            buffer.pos(-f10, 100.0D, f10).tex(0.0D, 1.0D).endVertex();
            tessellator.draw();

            f10 = 20.0F;
            mc.renderEngine.bindTexture(this.getMoonRenderPath());
            int k = world.getMoonPhase();
            int l = k % 4;
            int i1 = k / 4 % 2;
            float f14 = l + 0;
            float f15 = i1 + 0;
            float f16 = l + 1;
            float f17 = i1 + 1;
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(-f10, -100.0D, f10).tex(f16, f17).endVertex();
            buffer.pos(f10, -100.0D, f10).tex(f14, f17).endVertex();
            buffer.pos(f10, -100.0D, -f10).tex(f14, f15).endVertex();
            buffer.pos(-f10, -100.0D, -f10).tex(f16, f15).endVertex();
            tessellator.draw();
            GlStateManager.disableTexture2D();
            float f18 = world.getStarBrightness(par1) * f4;

            if (f18 > 0.0F) {
                GlStateManager.color(f18, f18, f18, f18);
                GlStateManager.callList(this.starGLCallList);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableFog();
            GlStateManager.popMatrix();
            GlStateManager.disableTexture2D();
            GlStateManager.color(0.0F, 0.0F, 0.0F);
            double d0 = mc.player.getPosition().getY() - world.getHorizon();

            if (d0 < 0.0D) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0F, 12.0F, 0.0F);
                GlStateManager.callList(this.glSkyList2);
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
            GlStateManager.callList(this.glSkyList2);
            GlStateManager.popMatrix();
            GlStateManager.enableTexture2D();
            GlStateManager.depthMask(true);
        }

    }
}