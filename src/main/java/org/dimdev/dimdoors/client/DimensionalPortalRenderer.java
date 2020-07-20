package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import static com.mojang.blaze3d.platform.GlStateManager.TexCoord.*;
import static com.mojang.blaze3d.platform.GlStateManager.enableTexGen;

@Environment(EnvType.CLIENT)
public final class DimensionalPortalRenderer { // TODO
    private static final FloatBuffer BUFFER = GlAllocationUtils.allocateFloatBuffer(16);
    private static final Identifier WARP_TEX = new Identifier("dimdoors", "textures/other/warp.png");

    /**
     * Renders a dimensional portal, for use in various situations. Code is mostly based
     * on vanilla's BlockEntityEndGatewayRenderer.
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
    public static void renderDimensionalPortal(VertexConsumerProvider vc, double x, double y, double z, Direction orientation, double width, double height, float[][] colors) { // TODO: Make this work at any angle
        RenderSystem.disableLighting();
        RenderSystem.disableCull();

        for (int pass = 0; pass < 16; pass++) {
            RenderSystem.pushMatrix();

            float translationScale = 16 - pass;
            float scale = 0.2625F;
            float colorMultiplier = 1.0F / (translationScale + .80F);

            MinecraftClient.getInstance().getTextureManager().bindTexture(WARP_TEX);
            RenderSystem.enableBlend();

            if (pass == 0) {
                colorMultiplier = 0.1F;
                translationScale = 25.0F;
                scale = 0.125F;

                RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            }

            if (pass == 1) {
                scale = 0.5F;
                RenderSystem.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
            }

            double offset = Util.getMeasuringTimeNano() % 200000L / 200000.0F;
            RenderSystem.translated(offset, offset, offset);

            GlStateManager.texGenMode(S, GL11.GL_OBJECT_LINEAR);
            GlStateManager.texGenMode(T, GL11.GL_OBJECT_LINEAR);
            GlStateManager.texGenMode(R, GL11.GL_OBJECT_LINEAR);

            if (orientation == Direction.UP || orientation == Direction.DOWN) {
                GlStateManager.texGenMode(Q, GL11.GL_EYE_LINEAR);
            } else {
                GlStateManager.texGenMode(Q, GL11.GL_OBJECT_LINEAR);
            }

            switch (orientation) { // TODO: Why 0.15F? Is that a door's thickness? If yes, don't hardcode that.
                case SOUTH:
                    GlStateManager.texGenParam(S, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGenParam(T, GL11.GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GlStateManager.texGenParam(R, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGenParam(Q, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, -0.15F));
                    break;
                case WEST:
                    GlStateManager.texGenParam(S, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGenParam(T, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GlStateManager.texGenParam(R, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGenParam(Q, GL11.GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.15F));
                    break;
                case NORTH:
                    GlStateManager.texGenParam(S, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGenParam(T, GL11.GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GlStateManager.texGenParam(R, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGenParam(Q, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.15F));
                    break;
                case EAST:
                    GlStateManager.texGenParam(S, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGenParam(T, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GlStateManager.texGenParam(R, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGenParam(Q, GL11.GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, -0.15F));
                    break;
                case UP:
                case DOWN:
                    GlStateManager.texGenParam(S, GL11.GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GlStateManager.texGenParam(T, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GlStateManager.texGenParam(R, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGenParam(Q, GL11.GL_EYE_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    break;
            }

            enableTexGen(S);
            enableTexGen(T);
            enableTexGen(R);
            enableTexGen(Q);

            RenderSystem.popMatrix();

            RenderSystem.matrixMode(GL11.GL_TEXTURE);
            RenderSystem.pushMatrix();
            RenderSystem.loadIdentity();
            RenderSystem.translatef(0.0F, (float) (offset * translationScale), 0.0F);
            RenderSystem.scaled(scale, scale, scale);
            RenderSystem.translatef(0.5F, 0.5F, 0.5F);
            RenderSystem.rotatef((pass * pass * 4321 + pass * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
            RenderSystem.translatef(0.5F, 0.5F, 0.5F);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder worldRenderer = tessellator.getBuffer();
            worldRenderer.begin(GL11.GL_QUADS, VertexFormats.POSITION);

            float[] color = colors[pass];
            RenderSystem.color4f(color[0] * colorMultiplier, color[1] * colorMultiplier, color[2] * colorMultiplier, color[3]);

            switch (orientation) {
                case NORTH:
                    worldRenderer.vertex(x, y, z).next();
                    worldRenderer.vertex(x, y + height, z).next();
                    worldRenderer.vertex(x + width, y + height, z).next();
                    worldRenderer.vertex(x + width, y, z).next();
                    break;
                case SOUTH:
                    worldRenderer.vertex(x, y, z).next();
                    worldRenderer.vertex(x + width, y, z).next();
                    worldRenderer.vertex(x + width, y + height, z).next();
                    worldRenderer.vertex(x, y + height, z).next();
                    break;
                case WEST:
                    worldRenderer.vertex(x, y, z).next();
                    worldRenderer.vertex(x, y, z + width).next();
                    worldRenderer.vertex(x, y + height, z + width).next();
                    worldRenderer.vertex(x, y + height, z).next();
                    break;
                case EAST:
                    worldRenderer.vertex(x, y, z).next();
                    worldRenderer.vertex(x, y + height, z).next();
                    worldRenderer.vertex(x, y + height, z + width).next();
                    worldRenderer.vertex(x, y, z + width).next();
                    break;
                case UP:
                    worldRenderer.vertex(x, y, z).next();
                    worldRenderer.vertex(x, y, z + width).next();
                    worldRenderer.vertex(x + width, y, z + width).next();
                    worldRenderer.vertex(x + width, y, z).next();
                    break;
                case DOWN:
                    worldRenderer.vertex(x, y, z).next();
                    worldRenderer.vertex(x + width, y, z).next();
                    worldRenderer.vertex(x + width, y, z + width).next();
                    worldRenderer.vertex(x, y, z + width).next();
                    break;
            }

            tessellator.draw();

            RenderSystem.popMatrix();
            RenderSystem.matrixMode(GL11.GL_MODELVIEW);
        }

        RenderSystem.disableBlend();
        GlStateManager.disableTexGen(S);
        GlStateManager.disableTexGen(T);
        GlStateManager.disableTexGen(R);
        GlStateManager.disableTexGen(Q);
        RenderSystem.enableCull();
        RenderSystem.enableLighting();
    }

    private static FloatBuffer getBuffer(float f1, float f2, float f3, float f4) {
        BUFFER.clear();
        BUFFER.put(f1).put(f2).put(f3).put(f4);
        BUFFER.flip();
        return BUFFER;
    }
}
