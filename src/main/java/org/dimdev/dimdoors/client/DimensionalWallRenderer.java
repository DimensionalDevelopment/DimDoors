package org.dimdev.dimdoors.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.dimdev.ddutils.RGBA;
import org.dimdev.dimdoors.DimDoors;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

public final class DimensionalWallRenderer {

    private static final FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
    private static final ResourceLocation warpPath = new ResourceLocation(DimDoors.MODID + ":textures/other/warp.png");

    // TODO: any angle
    /**
     * @param x           The x coordinate of the wall's center.
     * @param y           The y coordinate of the wall's center.
     * @param z           The z coordinate of the wall's center.
     *                    //@param yaw    The yaw of the normal vector of the wall in degrees, relative to __.
     *                    //@param pitch  The pitch of the normal vector of the wall, relative to the xz plane.
     * @param orientation The orientation of the wall.
     * @param width       The width of the wall.
     * @param height      The height of the wall.
     * @param colors      An array containing the color to use on each pass. Its length determines the number of passes to do.
     */
    public static void renderDimensionalWall(double x, double y, double z, EnumFacing orientation, double width, double height, RGBA[] colors) {
        GlStateManager.disableLighting();

        for (int pass = 0; pass < 16; pass++) {
            GlStateManager.pushMatrix();

            float var15 = 16 - pass;
            float var16 = 0.2625F;
            float var17 = 1.0F / (var15 + .80F);

            Minecraft.getMinecraft().getTextureManager().bindTexture(warpPath);
            GlStateManager.enableBlend();

            if (pass == 0) {
                var17 = 0.1F;
                var15 = 25.0F;
                var16 = 0.125F;

                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            }

            if (pass == 1) {
                var16 = 0.5F;
                GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
            }

            GlStateManager.translate(Minecraft.getSystemTime() % 200000L / 200000.0F, 0, 0.0F);
            GlStateManager.translate(0, Minecraft.getSystemTime() % 200000L / 200000.0F, 0.0F);
            GlStateManager.translate(0, 0, Minecraft.getSystemTime() % 200000L / 200000.0F);

            GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_LINEAR);
            GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_LINEAR);
            GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_LINEAR);
            GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_OBJECT_LINEAR);


            switch (orientation) {
                case SOUTH:
                    GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, -0.15F));
                    break;
                case WEST:
                    GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.15F));
                    break;
                case NORTH:
                    GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.15F));
                    break;
                case EAST:
                    GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, -0.15F));
                    break;
                case UP:
                    GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_EYE_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    break;
                case DOWN:
                    // TODO: logic for DOWN
            }

            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.S);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.T);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.R);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.Q);

            GlStateManager.popMatrix();

            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, Minecraft.getSystemTime() % 200000L / 200000.0F * var15, 0.0F);
            GlStateManager.scale(var16, var16, var16);
            GlStateManager.translate(0.5F, 0.5F, 0.5F);
            GlStateManager.rotate((pass * pass * 4321 + pass * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.5F, 0.5F, 0.5F);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder worldRenderer = tessellator.getBuffer();
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

            RGBA color = colors[pass];
            GlStateManager.color(color.getRed() * var17, color.getGreen() * var17, color.getBlue() * var17, color.getAlpha());

            switch (orientation) {
                case NORTH:
                    worldRenderer.pos(x, y, z).endVertex();
                    worldRenderer.pos(x, y + height, z).endVertex();
                    worldRenderer.pos(x + width, y + height, z).endVertex();
                    worldRenderer.pos(x + width, y, z).endVertex();
                    break;
                case SOUTH:
                    worldRenderer.pos(x, y, z).endVertex();
                    worldRenderer.pos(x + width, y, z).endVertex();
                    worldRenderer.pos(x + width, y + height, z).endVertex();
                    worldRenderer.pos(x, y + height, z).endVertex();
                    break;
                case WEST:
                    worldRenderer.pos(x, y, z).endVertex();
                    worldRenderer.pos(x, y, z + width).endVertex();
                    worldRenderer.pos(x, y + height, z + width).endVertex();
                    worldRenderer.pos(x, y + height, z).endVertex();
                    break;
                case EAST:
                    worldRenderer.pos(x, y, z).endVertex();
                    worldRenderer.pos(x, y + height, z).endVertex();
                    worldRenderer.pos(x, y + height, z + width).endVertex();
                    worldRenderer.pos(x, y, z + width).endVertex();
                    break;
                case UP:
                    worldRenderer.pos(x, y, z).endVertex();
                    worldRenderer.pos(x, y, z + width).endVertex();
                    worldRenderer.pos(x + width, y, z + width).endVertex();
                    worldRenderer.pos(x + width, y, z).endVertex();
                    break;
                case DOWN:
                    worldRenderer.pos(x, y, z).endVertex();
                    worldRenderer.pos(x + width, y, z).endVertex();
                    worldRenderer.pos(x + width, y, z + width).endVertex();
                    worldRenderer.pos(x, y, z + width).endVertex();
                    break;
            }

            tessellator.draw();

            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }

        GlStateManager.disableBlend();
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.Q);
        GlStateManager.enableLighting();
    }

    private static FloatBuffer getBuffer(float par1, float par2, float par3, float par4) {
        buffer.clear();
        buffer.put(par1).put(par2).put(par3).put(par4);
        buffer.flip();
        return buffer;
    }
}
