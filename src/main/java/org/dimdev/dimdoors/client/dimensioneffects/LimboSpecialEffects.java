package org.dimdev.dimdoors.client.dimensioneffects;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

import org.dimdev.dimdoors.DimensionalDoors;

public class LimboSpecialEffects extends BaseDimensonSpecialEffects {
	private static final ResourceLocation MOON_RENDER_PATH = DimensionalDoors.resource("textures/other/limbo_moon.png");
	private static final ResourceLocation SUN_RENDER_PATH = DimensionalDoors.resource("textures/other/limbo_sun.png");

	public LimboSpecialEffects() {
		super(true);
	}

	@Override
	public boolean renderSky(@NotNull ClientLevel level, int ticks, float partialTick, PoseStack poseStack, @NotNull Camera camera, @NotNull Matrix4f projectionMatrix, boolean isFoggy, @NotNull Runnable setupFog) {
		Matrix4f matrix4f = poseStack.last().pose();
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuilder();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(false);
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderColor(1, 1, 1, 1);

		float s = 30.0F;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, SUN_RENDER_PATH);
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferBuilder.vertex(matrix4f, -s, 100.0F, -s).uv(0.0F, 0.0F).endVertex();
		bufferBuilder.vertex(matrix4f, s, 100.0F, -s).uv(1.0F, 0.0F).endVertex();
		bufferBuilder.vertex(matrix4f, s, 100.0F, s).uv(1.0F, 1.0F).endVertex();
		bufferBuilder.vertex(matrix4f, -s, 100.0F, s).uv(0.0F, 1.0F).endVertex();
		tessellator.end();
//        BufferRenderer.draw(bufferBuilder);
		RenderSystem.setShaderTexture(0, MOON_RENDER_PATH);
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferBuilder.vertex(matrix4f, -s, -100.0F, -s).uv(0.0F, 0.0F).endVertex();
		bufferBuilder.vertex(matrix4f, s, -100.0F, -s).uv(1.0F, 0.0F).endVertex();
		bufferBuilder.vertex(matrix4f, s, -100.0F, s).uv(1.0F, 1.0F).endVertex();
		bufferBuilder.vertex(matrix4f, -s, -100.0F, s).uv(0.0F, 1.0F).endVertex();
		tessellator.end();
//        BufferRenderer.draw(bufferBuilder);

		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();

		return true;
	}
}
