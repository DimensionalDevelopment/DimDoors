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
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
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

	@Shadow
	private ClientWorld world;

	@Shadow
	private MinecraftClient client;

	@Shadow
	@Final
	private BufferBuilderStorage bufferBuilders;

	@Shadow
	private int ticks;

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
				VertexConsumer vertexConsumer2 = new OverlayVertexConsumer(this.bufferBuilders.getEffectVertexConsumers().getBuffer((RenderLayer) ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.get(stage)), entry3.getPositionMatrix(), entry3.getNormalMatrix());
				this.client.getBlockRenderManager().renderDamage(this.world.getBlockState(pos), pos, this.world, matrices, vertexConsumer2);
				matrices.pop();
			}
		}
	}

	@ModifyConstant(
			method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Matrix4f;FLjava/lang/Runnable;)V",
			constant = @Constant(doubleValue = 0.0, ordinal = 0),
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/client/world/ClientWorld$Properties;getSkyDarknessHeight(Lnet/minecraft/world/HeightLimitView;)D"
					)
			)
	)
	private double modifyVoidBackgroundCondition(double zero) {
		if(ModDimensions.isPrivatePocketDimension(world)) {
			zero = Double.NEGATIVE_INFINITY;
		}
		return zero;
	}
}
