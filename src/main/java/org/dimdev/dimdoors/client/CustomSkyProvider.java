package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.waterpicker.openworlds.renderer.SkyRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

public class CustomSkyProvider implements SkyRenderer {

    private static final Identifier locationEndSkyPng = new Identifier("textures/environment/end_sky.png");

    public Identifier getMoonRenderPath() {
        return null;
    }

    public Identifier getSunRenderPath() {
        return null;
    }

    @Override
    public void render(MinecraftClient client, MatrixStack matrices, float tickDelta) {
        RenderSystem.disableTexture();

        Vec3d vec3d = client.world.method_23777(client.gameRenderer.getCamera().getBlockPos(), tickDelta);
        float f = (float)vec3d.x;
        float g = (float)vec3d.y;
        float h = (float)vec3d.z;
        BackgroundRenderer.setFogBlack();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        float r;
        float s;

        RenderSystem.enableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        matrices.push();
        r = 1.0F - client.world.getRainGradient(tickDelta);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, r);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(client.world.getSkyAngle(tickDelta) * 360.0F));
        Matrix4f matrix4f2 = matrices.peek().getModel();
        s = 30.0F;
        client.getTextureManager().bindTexture(getSunRenderPath());
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f2, -s, 100.0F, -s).texture(0.0F, 0.0F).next();
        bufferBuilder.vertex(matrix4f2, s, 100.0F, -s).texture(1.0F, 0.0F).next();
        bufferBuilder.vertex(matrix4f2, s, 100.0F, s).texture(1.0F, 1.0F).next();
        bufferBuilder.vertex(matrix4f2, -s, 100.0F, s).texture(0.0F, 1.0F).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        s = 20.0F;
        client.getTextureManager().bindTexture(getMoonRenderPath());
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f2, -s, -100.0F, s).texture(0f, 0f).next();
        bufferBuilder.vertex(matrix4f2, s, -100.0F, s).texture(1f, 0f).next();
        bufferBuilder.vertex(matrix4f2, s, -100.0F, -s).texture(1f, 1f).next();
        bufferBuilder.vertex(matrix4f2, -s, -100.0F, -s).texture(0f, 1f).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.disableTexture();

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableFog();
        matrices.pop();
        RenderSystem.disableTexture();
        RenderSystem.color3f(0.0F, 0.0F, 0.0F);

        if (client.world.getSkyProperties().isAlternateSkyColor()) {
            RenderSystem.color3f(f * 0.2F + 0.04F, g * 0.2F + 0.04F, h * 0.6F + 0.1F);
        } else {
            RenderSystem.color3f(f, g, h);
        }

        RenderSystem.enableTexture();
        RenderSystem.depthMask(true);
        RenderSystem.disableFog();

    }
}