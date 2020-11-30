package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

@SuppressWarnings("ConstantConditions")
public class LimboSkyProvider extends CustomSkyProvider {
    private static final Identifier MOON_RENDER_PATH = new Identifier("dimdoors:textures/other/limbo_moon.png");
    private static final Identifier SUN_RENDER_PATH = new Identifier("dimdoors:textures/other/limbo_sun.png");
    private static final Identifier GREY_TEX = new Identifier("dimdoors:textures/other/grey.png");

    public LimboSkyProvider() {
        super(MOON_RENDER_PATH, SUN_RENDER_PATH, new Vec3i(0, 0, 0));
    }

    @Override
    protected void renderSkyBox(MatrixStack matrices, float tickDelta) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        Vec3d color = MinecraftClient.getInstance().world.method_23777(MinecraftClient.getInstance().gameRenderer.getCamera().getBlockPos(), tickDelta);
        BackgroundRenderer.setFogBlack();
        RenderSystem.depthMask(false);
        RenderSystem.enableFog();
        RenderSystem.color3f((float) color.x, (float) color.y,   (float) color.z);
        RenderSystem.disableFog();
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        MinecraftClient.getInstance().getTextureManager().bindTexture(GREY_TEX);
        for (int i = 0; i < 6; ++i) {
            matrices.push();
            this.multiply(matrices, i);
            Matrix4f matrix4f = matrices.peek().getModel();
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).texture(0.0F, 0.0F).color(40, 40, 40, 255).next();
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).texture(0.0F, 16.0F).color(40, 40, 40, 255).next();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).texture(16.0F, 16.0F).color(40, 40, 40, 255).next();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).texture(16.0F, 0.0F).color(40, 40, 40, 255).next();
            tessellator.draw();
            matrices.pop();
        }
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }
}
