package org.dimdev.dimdoors.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.dimdev.dimdoors.client.CustomBreakBlockHandler;
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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;
import java.util.Map;

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
	private BufferBuilderStorage bufferBuilders;

	@Shadow
	private int ticks;

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

	@Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/util/math/Matrix4f;)V",
	at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/WorldRenderer;blockBreakingProgressions:Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;", ordinal = 1)) // bytecode order is flipped from java code order, notice the ordinal
	public void renderCustomBreakBlockAnimation(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
		Vec3d vec3d = camera.getPos();
		double d = vec3d.getX();
		double e = vec3d.getY();
		double f = vec3d.getZ();

		Map<BlockPos, CustomBreakBlockHandler.BreakBlockInfo> breakBlocks = CustomBreakBlockHandler.getCustomBreakBlockMap(this.ticks);

		// stolen from WorldRenderer#render
		for (Map.Entry<BlockPos, CustomBreakBlockHandler.BreakBlockInfo> entry : breakBlocks.entrySet()) {
			BlockPos pos = entry.getKey();
			double h = (double) pos.getX() - d;
			double x = (double) pos.getY() - e;
			double y = (double) pos.getZ() - f;
			if (!(h * h + x * x + y * y > 1024.0D)) {
				int stage = entry.getValue().getStage();
				matrices.push();
				matrices.translate((double) pos.getX() - d, (double) pos.getY() - e, (double) pos.getZ() - f);
				MatrixStack.Entry entry3 = matrices.peek();
				VertexConsumer vertexConsumer2 = new OverlayVertexConsumer(this.bufferBuilders.getEffectVertexConsumers().getBuffer((RenderLayer) ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.get(stage)), entry3.getModel(), entry3.getNormal());
				this.client.getBlockRenderManager().renderDamage(this.world.getBlockState(pos), pos, this.world, matrices, vertexConsumer2);
				matrices.pop();
			}
		}
	}
}
