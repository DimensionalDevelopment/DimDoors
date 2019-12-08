package org.dimdev.dimdoors.client;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.Direction;

public final class DimensionalPortalRenderer { // TODO
//    private static final FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
//    private static final Identifier warpPath = new Identifier("dimdoors" + ":textures/other/warp.png");

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
    public static void renderDimensionalPortal(VertexConsumer vc, double x, double y, double z, Direction orientation, double width, double height, float[][] colors) { // TODO: Make this work at any angle
//        RenderSystem.disableLighting();
//        RenderSystem.disableCull();
//
//        for (int pass = 0; pass < 16; pass++) {
//            RenderSystem.pushMatrix();
//
//            float translationScale = 16 - pass;
//            float scale = 0.2625F;
//            float colorMultiplier = 1.0F / (translationScale + .80F);
//
//            MinecraftClient.getInstance().getTextureManager().bindTexture(warpPath);
//            RenderSystem.enableBlend();
//
//            if (pass == 0) {
//                colorMultiplier = 0.1F;
//                translationScale = 25.0F;
//                scale = 0.125F;
//
//                RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//            }
//
//            if (pass == 1) {
//                scale = 0.5F;
//                RenderSystem.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
//            }
//
//            double offset = Minecraft.getSystemTime() % 200000L / 200000.0F;
//            RenderSystem.translatef(offset, offset, offset);
//
//            RenderSystem.texGen(RenderSystem.TexGen.S, GL11.GL_OBJECT_LINEAR);
//            RenderSystem.texGen(RenderSystem.TexGen.T, GL11.GL_OBJECT_LINEAR);
//            RenderSystem.texGen(RenderSystem.TexGen.R, GL11.GL_OBJECT_LINEAR);
//
//            if (orientation == Direction.UP || orientation == Direction.DOWN) {
//                RenderSystem.texGen(RenderSystem.TexGen.Q, GL11.GL_EYE_LINEAR);
//            } else {
//                RenderSystem.texGen(RenderSystem.TexGen.Q, GL11.GL_OBJECT_LINEAR);
//            }
//
//            switch (orientation) { // TODO: Why 0.15F? Is that a door's thickness? If yes, don't hardcode that.
//                case SOUTH:
//                    RenderSystem.texGen(RenderSystem.TexGen.S, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
//                    RenderSystem.texGen(RenderSystem.TexGen.T, GL11.GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
//                    RenderSystem.texGen(RenderSystem.TexGen.R, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
//                    RenderSystem.texGen(RenderSystem.TexGen.Q, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, -0.15F));
//                    break;
//                case WEST:
//                    RenderSystem.texGen(RenderSystem.TexGen.S, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
//                    RenderSystem.texGen(RenderSystem.TexGen.T, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
//                    RenderSystem.texGen(RenderSystem.TexGen.R, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
//                    RenderSystem.texGen(RenderSystem.TexGen.Q, GL11.GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.15F));
//                    break;
//                case NORTH:
//                    RenderSystem.texGen(RenderSystem.TexGen.S, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
//                    RenderSystem.texGen(RenderSystem.TexGen.T, GL11.GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
//                    RenderSystem.texGen(RenderSystem.TexGen.R, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
//                    RenderSystem.texGen(RenderSystem.TexGen.Q, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.15F));
//                    break;
//                case EAST:
//                    RenderSystem.texGen(RenderSystem.TexGen.S, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
//                    RenderSystem.texGen(RenderSystem.TexGen.T, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
//                    RenderSystem.texGen(RenderSystem.TexGen.R, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
//                    RenderSystem.texGen(RenderSystem.TexGen.Q, GL11.GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, -0.15F));
//                    break;
//                case UP:
//                    RenderSystem.texGen(RenderSystem.TexGen.S, GL11.GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
//                    RenderSystem.texGen(RenderSystem.TexGen.T, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
//                    RenderSystem.texGen(RenderSystem.TexGen.R, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
//                    RenderSystem.texGen(RenderSystem.TexGen.Q, GL11.GL_EYE_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
//                    break;
//                case DOWN:
//                    RenderSystem.texGen(RenderSystem.TexGen.S, GL11.GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
//                    RenderSystem.texGen(RenderSystem.TexGen.T, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
//                    RenderSystem.texGen(RenderSystem.TexGen.R, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
//                    RenderSystem.texGen(RenderSystem.TexGen.Q, GL11.GL_EYE_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
//                    break;
//            }
//
//            RenderSystem.enableTexGenCoord(RenderSystem.TexGen.S);
//            RenderSystem.enableTexGenCoord(RenderSystem.TexGen.T);
//            RenderSystem.enableTexGenCoord(RenderSystem.TexGen.R);
//            RenderSystem.enableTexGenCoord(RenderSystem.TexGen.Q);
//
//            RenderSystem.popMatrix();
//
//            RenderSystem.matrixMode(GL11.GL_TEXTURE);
//            RenderSystem.pushMatrix();
//            RenderSystem.loadIdentity();
//            RenderSystem.translatef(0.0F, offset * translationScale, 0.0F);
//            RenderSystem.scaled(scale, scale, scale);
//            RenderSystem.translatef(0.5F, 0.5F, 0.5F);
//            RenderSystem.rotatef((pass * pass * 4321 + pass * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
//            RenderSystem.translatef(0.5F, 0.5F, 0.5F);
//
//            Tessellator tessellator = Tessellator.getInstance();
//            BufferBuilder worldRenderer = tessellator.getBuffer();
//            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
//
//            RGBA color = colors[pass];
//            RenderSystem.color(color.getRed() * colorMultiplier, color.getGreen() * colorMultiplier, color.getBlue() * colorMultiplier, color.getAlpha());
//
//            switch (orientation) {
//                case NORTH:
//                    worldRenderer.pos(x, y, z).endVertex();
//                    worldRenderer.pos(x, y + height, z).endVertex();
//                    worldRenderer.pos(x + width, y + height, z).endVertex();
//                    worldRenderer.pos(x + width, y, z).endVertex();
//                    break;
//                case SOUTH:
//                    worldRenderer.pos(x, y, z).endVertex();
//                    worldRenderer.pos(x + width, y, z).endVertex();
//                    worldRenderer.pos(x + width, y + height, z).endVertex();
//                    worldRenderer.pos(x, y + height, z).endVertex();
//                    break;
//                case WEST:
//                    worldRenderer.pos(x, y, z).endVertex();
//                    worldRenderer.pos(x, y, z + width).endVertex();
//                    worldRenderer.pos(x, y + height, z + width).endVertex();
//                    worldRenderer.pos(x, y + height, z).endVertex();
//                    break;
//                case EAST:
//                    worldRenderer.pos(x, y, z).endVertex();
//                    worldRenderer.pos(x, y + height, z).endVertex();
//                    worldRenderer.pos(x, y + height, z + width).endVertex();
//                    worldRenderer.pos(x, y, z + width).endVertex();
//                    break;
//                case UP:
//                    worldRenderer.pos(x, y, z).endVertex();
//                    worldRenderer.pos(x, y, z + width).endVertex();
//                    worldRenderer.pos(x + width, y, z + width).endVertex();
//                    worldRenderer.pos(x + width, y, z).endVertex();
//                    break;
//                case DOWN:
//                    worldRenderer.pos(x, y, z).endVertex();
//                    worldRenderer.pos(x + width, y, z).endVertex();
//                    worldRenderer.pos(x + width, y, z + width).endVertex();
//                    worldRenderer.pos(x, y, z + width).endVertex();
//                    break;
//            }
//
//            tessellator.draw();
//
//            RenderSystem.popMatrix();
//            RenderSystem.matrixMode(GL11.GL_MODELVIEW);
//        }
//
//        RenderSystem.disableBlend();
//        RenderSystem.disableTexGenCoord(RenderSystem.TexGen.S);
//        RenderSystem.disableTexGenCoord(RenderSystem.TexGen.T);
//        RenderSystem.disableTexGenCoord(RenderSystem.TexGen.R);
//        RenderSystem.disableTexGenCoord(RenderSystem.TexGen.Q);
//        RenderSystem.enableCull();
//        RenderSystem.enableLighting();
    }
//
//    private static FloatBuffer getBuffer(float f1, float f2, float f3, float f4) {
//        buffer.clear();
//        buffer.put(f1).put(f2).put(f3).put(f4);
//        buffer.flip();
//        return buffer;
//    }
}
