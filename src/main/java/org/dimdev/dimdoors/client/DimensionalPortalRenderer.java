package org.dimdev.dimdoors.client;

import java.nio.FloatBuffer;
import java.util.Arrays;

import com.flowpowered.math.vector.VectorNi;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.dimdev.dimdoors.mixin.DirectionAccessor;
import org.dimdev.dimdoors.util.RGBA;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.enums.DoorHinge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import static org.lwjgl.opengl.GL11.*;

@Environment(EnvType.CLIENT)
public final class DimensionalPortalRenderer {
    private static final FloatBuffer BUFFER = GlAllocationUtils.allocateFloatBuffer(16);
    private static final Identifier WARP_PATH = new Identifier("dimdoors:textures/other/warp.png");
    private static final BooleanProperty OPEN_PROPERTY = BooleanProperty.of("open");
    private static final EnumProperty<DoorHinge> HINGE_PROPERTY = EnumProperty.of("hinge", DoorHinge.class);
    private static final DirectionProperty FACING_PROPERTY = DirectionProperty.of("facing", Arrays.asList(DirectionAccessor.getHorizontal()));

    private static final TextureManager TEXTURE_MANAGER = MinecraftClient.getInstance().getTextureManager();
    private static final BlockModels BLOCK_MODELS = MinecraftClient.getInstance().getBlockRenderManager().getModels();
    private static final BakedModelManager MODEL_MANAGER = BLOCK_MODELS.getModelManager();

    private static final VectorNi COLORLESS = new VectorNi(255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255);

    public static Identifier getWarpPath() {
        return WARP_PATH;
    }

    // TODO: any render angle

    /**
     * Renders a dimensional portal, for use in various situations. Code is mostly based
     * on vanilla's EndGatewayBlockEntityRenderer.
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
    @Deprecated
    public static void renderDimensionalPortal(double x, double y, double z, Direction orientation, double width, double height, RGBA[] colors, MatrixStack matrices, VertexConsumer consumer) { // TODO: Make this work at any angle
        RenderSystem.disableLighting();
        RenderSystem.disableCull();

        for (int pass = 0; pass < 4; pass++) {
            RenderSystem.pushMatrix();

            float translationScale = 16 - pass;
            float scale = 0.2625F;
            float colorMultiplier = 1.0F / (translationScale + .80F);

            MinecraftClient.getInstance().getTextureManager().bindTexture(WARP_PATH);
            RenderSystem.enableBlend();

            if (pass == 0) {
                colorMultiplier = 0.1F;
                translationScale = 25.0F;
                scale = 0.125F;

                RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            }

            if (pass == 1) {
                scale = 0.5F;
                RenderSystem.blendFunc(GL_ONE, GL_ONE);
            }

            double offset = Util.getMeasuringTimeNano() % 200000L / 200000.0F;
            RenderSystem.translated(offset, offset, offset);

            GlStateManager.texGenMode(GlStateManager.TexCoord.S, GL_OBJECT_LINEAR);
            GlStateManager.texGenMode(GlStateManager.TexCoord.T, GL_OBJECT_LINEAR);
            GlStateManager.texGenMode(GlStateManager.TexCoord.R, GL_OBJECT_LINEAR);
            GlStateManager.texGenMode(GlStateManager.TexCoord.Q, GL_OBJECT_LINEAR);

            if (orientation == Direction.UP || orientation == Direction.DOWN) {
                GlStateManager.texGenMode(GlStateManager.TexCoord.Q, GL11.GL_EYE_LINEAR);
            } else {
                GlStateManager.texGenMode(GlStateManager.TexCoord.Q, GL11.GL_OBJECT_LINEAR);
            }

            switch (orientation) { // TODO: Why 0.15F? Is that a door's thickness? If yes, don't hardcode that.
                case SOUTH:
                    GlStateManager.texGenParam(GlStateManager.TexCoord.S, GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.T, GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.R, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.Q, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, -0.15F));
                    break;
                case WEST:
                    GlStateManager.texGenParam(GlStateManager.TexCoord.S, GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.T, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.R, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.Q, GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.15F));
                    break;
                case NORTH:
                    GlStateManager.texGenParam(GlStateManager.TexCoord.S, GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.T, GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.R, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.Q, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.15F));
                    break;
                case EAST:
                    GlStateManager.texGenParam(GlStateManager.TexCoord.S, GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.T, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.R, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.Q, GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, -0.15F));
                    break;
                case UP:
                    GlStateManager.texGenParam(GlStateManager.TexCoord.S, GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.T, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.R, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.Q, GL_EYE_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    break;
                case DOWN:
                    GlStateManager.texGenParam(GlStateManager.TexCoord.S, GL11.GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.T, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.R, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.Q, GL11.GL_EYE_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    break;
            }

            GlStateManager.enableTexGen(GlStateManager.TexCoord.S);
            GlStateManager.enableTexGen(GlStateManager.TexCoord.T);
            GlStateManager.enableTexGen(GlStateManager.TexCoord.R);
            GlStateManager.enableTexGen(GlStateManager.TexCoord.Q);

            RenderSystem.popMatrix();

            RenderSystem.matrixMode(GL_TEXTURE);
            RenderSystem.pushMatrix();
            RenderSystem.loadIdentity();
            RenderSystem.translated(0.0F, offset * translationScale, 0.0F);
            RenderSystem.scalef(scale, scale, scale);
            RenderSystem.translatef(0.5F, 0.5F, 0.5F);
            RenderSystem.rotatef((pass * pass * 4321 + pass) * 9 * 2.0F, 0.0F, 0.0F, 1.0F);
            RenderSystem.translated(0.5F, 0.5F, 0.5F);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder worldRenderer = tessellator.getBuffer();
            worldRenderer.begin(GL_QUADS, VertexFormats.POSITION);

            RGBA color = colors[pass];
            consumer.color(color.getRed() * colorMultiplier, color.getGreen() * colorMultiplier, color.getBlue() * colorMultiplier, color.getAlpha());

            switch (orientation) {
                case NORTH:
                    worldRenderer.vertex(x, y, z);
                    worldRenderer.vertex(x, y + height, z);
                    worldRenderer.vertex(x + width, y + height, z);
                    worldRenderer.vertex(x + width, y, z);
                    break;
                case SOUTH:
                    worldRenderer.vertex(x, y, z);
                    worldRenderer.vertex(x + width, y, z);
                    worldRenderer.vertex(x + width, y + height, z);
                    worldRenderer.vertex(x, y + height, z);
                    break;
                case WEST:
                    worldRenderer.vertex(x, y, z);
                    worldRenderer.vertex(x, y, z + width);
                    worldRenderer.vertex(x, y + height, z + width);
                    worldRenderer.vertex(x, y + height, z);
                    break;
                case EAST:
                    worldRenderer.vertex(x, y, z);
                    worldRenderer.vertex(x, y + height, z);
                    worldRenderer.vertex(x, y + height, z + width);
                    worldRenderer.vertex(x, y, z + width);
                    break;
                case UP:
                    worldRenderer.vertex(x, y, z);
                    worldRenderer.vertex(x, y, z + width);
                    worldRenderer.vertex(x + width, y, z + width);
                    worldRenderer.vertex(x + width, y, z);
                    break;
                case DOWN:
                    worldRenderer.vertex(x, y, z);
                    worldRenderer.vertex(x + width, y, z);
                    worldRenderer.vertex(x + width, y, z + width);
                    worldRenderer.vertex(x, y, z + width);
                    break;
            }

            tessellator.draw();

            RenderSystem.popMatrix();
            RenderSystem.matrixMode(GL_MODELVIEW);
        }

        RenderSystem.disableBlend();
        GlStateManager.disableTexGen(GlStateManager.TexCoord.S);
        GlStateManager.disableTexGen(GlStateManager.TexCoord.T);
        GlStateManager.disableTexGen(GlStateManager.TexCoord.R);
        GlStateManager.disableTexGen(GlStateManager.TexCoord.Q);
        RenderSystem.enableCull();
        RenderSystem.enableLighting();
    }

    static FloatBuffer getBuffer(float f1, float f2, float f3, float f4) {
        BUFFER.clear();
        BUFFER.put(f1).put(f2).put(f3).put(f4);
        BUFFER.flip();
        return BUFFER;
    }
}