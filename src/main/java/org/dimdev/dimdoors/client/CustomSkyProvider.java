package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.waterpicker.openworlds.renderer.SkyRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3i;

public class CustomSkyProvider implements SkyRenderer {
    private final Identifier moon;
    private final Identifier sun;
    private final Vec3i color;

    public CustomSkyProvider(Identifier moon, Identifier sun, Vec3i color) {
        this.moon = moon;
        this.sun = sun;
        this.color = color;
    }

    @Override
    public void render(MinecraftClient client, MatrixStack matrices, float tickDelta) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);

        matrices.push();

        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
        Matrix4f matrix4f2 = matrices.peek().getModel();

        float s = 30.0F;
        client.getTextureManager().bindTexture(sun);
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f2, -s, 100.0F, -s).texture(0.0F, 0.0F).next();
        bufferBuilder.vertex(matrix4f2, s, 100.0F, -s).texture(1.0F, 0.0F).next();
        bufferBuilder.vertex(matrix4f2, s, 100.0F, s).texture(1.0F, 1.0F).next();
        bufferBuilder.vertex(matrix4f2, -s, 100.0F, s).texture(0.0F, 1.0F).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);

        client.getTextureManager().bindTexture(moon);
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f2, -s, -100.0F, s).texture(0f, 0f).next();
        bufferBuilder.vertex(matrix4f2, s, -100.0F, s).texture(1f, 0f).next();
        bufferBuilder.vertex(matrix4f2, s, -100.0F, -s).texture(1f, 1f).next();
        bufferBuilder.vertex(matrix4f2, -s, -100.0F, -s).texture(0f, 1f).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);

        matrices.pop();

        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();

        renderSkyBox(matrices);
    }

    private void renderSkyBox(MatrixStack matrices) {
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        for(int i = 0; i < 6; ++i) {
            matrices.push();
            if (i == 1) {
                matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F));
            }

            if (i == 2) {
                matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
            }

            if (i == 3) {
                matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0F));
            }

            if (i == 4) {
                matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
            }

            if (i == 5) {
                matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(-90.0F));
            }

            Matrix4f matrix4f = matrices.peek().getModel();
            bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).color(color.getX(), color.getY(), color.getZ(), 255).next();
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).color(color.getX(), color.getY(), color.getZ(), 255).next();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).color(color.getX(), color.getY(), color.getZ(), 255).next();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).color(color.getX(), color.getY(), color.getZ(), 255).next();
            tessellator.draw();
            matrices.pop();
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }

}
