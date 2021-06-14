package org.dimdev.dimdoors.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import org.dimdev.dimdoors.listener.pocket.PocketListenerUtil;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.type.addon.SkyAddon;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

@Mixin(WorldRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class WorldRendererMixin {
	@Unique
	private static final Identifier MOON_RENDER_PATH = new Identifier("dimdoors:textures/other/limbo_moon.png");
	@Unique
	private static final Identifier SUN_RENDER_PATH = new Identifier("dimdoors:textures/other/limbo_sun.png");

	@Shadow
	private ClientWorld world;

	@Shadow
	private MinecraftClient client;

	@Shadow
	@Final
	private static Identifier END_SKY;

	@Shadow
	protected abstract void renderEndSky(MatrixStack matrices);

	@Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
	public void beforeRenderSky(MatrixStack matrices, Matrix4f matrix4f, float f, Runnable runnable, CallbackInfo ci) {
		List<SkyAddon> skyAddons = PocketListenerUtil.applicableAddonsClient(SkyAddon.class, this.world, this.client.gameRenderer.getCamera().getBlockPos());
		SkyAddon skyAddon = null;
		if (skyAddons.size() > 0) {
			// There should really only be one of these.
			// If anyone needs to use multiple SkyAddons then go ahead and change this.
			skyAddon = skyAddons.get(0);
		}

		if (skyAddon != null) {
			RegistryKey<World> world = skyAddon.getWorld();

			if (world.equals(World.END)) {
				this.renderEndSky(matrices);
				ci.cancel();
				return;
			} else if (world.equals(ModDimensions.LIMBO)) {
				this.renderLimboSky(matrices);
				ci.cancel();
				return;
			}
		}
		if (ModDimensions.isLimboDimension(this.world)) {
			renderLimboSky(matrices);
			ci.cancel();
		} else if (ModDimensions.isPrivatePocketDimension(this.world)) {
			this.renderPocketSky(matrices, 255, 255, 255);
			ci.cancel();
		} else if (ModDimensions.isPocketDimension(this.world)) {
			this.renderPocketSky(matrices, 0, 0, 0);
			ci.cancel();
		}
	}

	@Inject(method = "renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Matrix4f;FDDD)V", at = @At("HEAD"), cancellable = true)
	public void beforeRendererCloud(MatrixStack matrices, Matrix4f matrix4f, float f, double d, double e, double g, CallbackInfo ci) {
		if (ModDimensions.isLimboDimension(this.world) || ModDimensions.isPrivatePocketDimension(this.world) || ModDimensions.isPocketDimension(this.world)) {
			ci.cancel();
		}
	}

	@Unique
	private void renderLimboSky(MatrixStack matrices) {
		Matrix4f matrix4f = matrices.peek().getModel();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(false);
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderColor(1, 1, 1, 1);

		for (int i = 0; i < 6; ++i) {
			matrices.push();
			if (i == 1) {
				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
			}

			if (i == 2) {
				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
			}

			if (i == 3) {
				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180.0F));
			}

			if (i == 4) {
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
			}

			if (i == 5) {
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-90.0F));
			}

			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).color(40, 40, 40, 255).next();
			bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).color(40, 40, 40, 255).next();
			bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).color(40, 40, 40, 255).next();
			bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).color(40, 40, 40, 255).next();
			tessellator.draw();
			matrices.pop();
		}

		float s = 30.0F;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, SUN_RENDER_PATH);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(matrix4f, -s, 100.0F, -s).texture(0.0F, 0.0F).next();
		bufferBuilder.vertex(matrix4f, s, 100.0F, -s).texture(1.0F, 0.0F).next();
		bufferBuilder.vertex(matrix4f, s, 100.0F, s).texture(1.0F, 1.0F).next();
		bufferBuilder.vertex(matrix4f, -s, 100.0F, s).texture(0.0F, 1.0F).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		RenderSystem.setShaderTexture(0, MOON_RENDER_PATH);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(matrix4f, -s, -100.0F, -s).texture(0.0F, 0.0F).next();
		bufferBuilder.vertex(matrix4f, s, -100.0F, -s).texture(1.0F, 0.0F).next();
		bufferBuilder.vertex(matrix4f, s, -100.0F, s).texture(1.0F, 1.0F).next();
		bufferBuilder.vertex(matrix4f, -s, -100.0F, s).texture(0.0F, 1.0F).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);

		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	private void renderPocketSky(MatrixStack matrices, int r, int g, int b) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(false);
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		for (int i = 0; i < 6; ++i) {
			matrices.push();
			if (i == 1) {
				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
			}

			if (i == 2) {
				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
			}

			if (i == 3) {
				matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180.0F));
			}

			if (i == 4) {
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
			}

			if (i == 5) {
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-90.0F));
			}

			Matrix4f matrix4f = matrices.peek().getModel();
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).color(r, g, b, 255).next();
			bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).color(r, g, b, 255).next();
			bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).color(r, g, b, 255).next();
			bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).color(r, g, b, 255).next();
			tessellator.draw();
			matrices.pop();
		}

		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}
}
