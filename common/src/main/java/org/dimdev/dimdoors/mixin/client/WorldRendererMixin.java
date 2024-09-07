package org.dimdev.dimdoors.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.client.CustomBreakBlockHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LevelRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class WorldRendererMixin {

	@Shadow
	private ClientLevel level;

	@Shadow
	private Minecraft minecraft;

	@Shadow
	@Final
	private RenderBuffers renderBuffers;

	@Shadow
	private int ticks;

	@Inject(method = "renderLevel",
	at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer;destructionProgress:Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;", ordinal = 1)) // bytecode order is flipped from java code order, notice the ordinal
	public void renderCustomBreakBlockAnimation(PoseStack matrices, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
		Vec3 vec3d = camera.getPosition();
		double d = vec3d.x();
		double e = vec3d.y();
		double f = vec3d.z();

		Map<BlockPos, CustomBreakBlockHandler.BreakBlockInfo> breakBlocks = CustomBreakBlockHandler.getCustomBreakBlockMap(this.ticks);

		// stolen from WorldRenderer#render
		for (Map.Entry<BlockPos, CustomBreakBlockHandler.BreakBlockInfo> entry : breakBlocks.entrySet()) {
			BlockPos pos = entry.getKey();
			double h = (double) pos.getX() - d;
			double x = (double) pos.getY() - e;
			double y = (double) pos.getZ() - f;
			if (!(h * h + x * x + y * y > 1024.0D)) {
				int stage = entry.getValue().getStage();
				matrices.pushPose();
				matrices.translate((double) pos.getX() - d, (double) pos.getY() - e, (double) pos.getZ() - f);
				PoseStack.Pose entry3 = matrices.last();
				VertexConsumer vertexConsumer2 = new SheetedDecalTextureGenerator(this.renderBuffers.crumblingBufferSource().getBuffer((RenderType) ModelBakery.DESTROY_TYPES.get(stage)), entry3.pose(), entry3.normal());
				this.minecraft.getBlockRenderer().renderBreakingTexture(this.level.getBlockState(pos), pos, this.level, matrices, vertexConsumer2);
				matrices.popPose();
			}
		}
	}

//	TODO:Fix or find out alternative.
//	@ModifyConstant(
//			method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Matrix4f;FLjava/lang/Runnable;)V",
//			constant = @Constant(doubleValue = 0.0, ordinal = 0),
//			slice = @Slice(
//					from = @At(
//							value = "INVOKE",
//							target = "Lnet/minecraft/client/world/ClientWorld$Properties;getSkyDarknessHeight(Lnet/minecraft/world/HeightLimitView;)D"
//					)
//			)
//	)
//	private double modifyVoidBackgroundCondition(double zero) {
//		if(ModDimensions.isPrivatePocketDimension(world)) {
//			zero = Double.NEGATIVE_INFINITY;
//		}
//		return zero;
//	}
}
