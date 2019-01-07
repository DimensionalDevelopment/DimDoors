package org.dimdev.dimdoors.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.dimdev.ddutils.RGBA;
import org.dimdev.dimdoors.DimDoors;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

public final class DimensionalPortalRenderer {

    private static final FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
    private static final ResourceLocation warpPath = new ResourceLocation(DimDoors.MODID + ":textures/other/warp.png");

    /**
     * Renders a dimensional portal, for use in various situations. Code is mostly based
     * on vanilla's TileEntityEndGatewayRenderer.
     *
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
    public static void renderDimensionalPortal(double x, double y, double z, EnumFacing orientation, double width, double height, RGBA[] colors) { // TODO: Make this work at any angle
        GlStateManager.disableLighting();
        GlStateManager.disableCull();

        for (int pass = 0; pass < 16; pass++) {
            GlStateManager.pushMatrix();

            float translationScale = 16 - pass;
            float scale = 0.2625F;
            float colorMultiplier = 1.0F / (translationScale + .80F);

            Minecraft.getMinecraft().getTextureManager().bindTexture(warpPath);
            GlStateManager.enableBlend();

            if (pass == 0) {
                colorMultiplier = 0.1F;
                translationScale = 25.0F;
                scale = 0.125F;

                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            }

            if (pass == 1) {
                scale = 0.5F;
                GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
            }

            double offset = Minecraft.getSystemTime() % 200000L / 200000.0F;
            GlStateManager.translate(offset, offset, offset);

            GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_LINEAR);
            GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_LINEAR);
            GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_LINEAR);

            if (orientation == EnumFacing.UP || orientation == EnumFacing.DOWN) {
                GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_EYE_LINEAR);
            } else {
                GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_OBJECT_LINEAR);
            }

            switch (orientation) { // TODO: Why 0.15F? Is that a door's thickness? If yes, don't hardcode that.
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
                    GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_EYE_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    break;
            }

            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.S);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.T);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.R);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.Q);

            GlStateManager.popMatrix();

            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, offset * translationScale, 0.0F);
            GlStateManager.scale(scale, scale, scale);
            GlStateManager.translate(0.5F, 0.5F, 0.5F);
            GlStateManager.rotate((pass * pass * 4321 + pass * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.5F, 0.5F, 0.5F);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder worldRenderer = tessellator.getBuffer();
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

            RGBA color = colors[pass];
            GlStateManager.color(color.getRed() * colorMultiplier, color.getGreen() * colorMultiplier, color.getBlue() * colorMultiplier, color.getAlpha());

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
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
    }

    private static FloatBuffer getBuffer(float f1, float f2, float f3, float f4) {
        buffer.clear();
        buffer.put(f1).put(f2).put(f3).put(f4);
        buffer.flip();
        return buffer;
    }
}
