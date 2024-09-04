package org.dimdev.dimdoors.particle.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleOptions;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.forge.client.MonolithRenderer;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class MonolithParticle extends Particle {

	public MonolithParticle(ClientLevel world, double x, double y, double z) {
		super(world, x, y, z);
		this.age = 30;
	}

	@Override
	public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		float delta = ((float)this.age + tickDelta) / (float) this.age;
		PoseStack matrices = new PoseStack();
		matrices.mulPose(camera.rotation());
		matrices.mulPose(Vector3f.XP.rotationDegrees(150.0F * delta - 60.0F));
		matrices.scale(-1.0F, -1.0F, 1.0F);
		matrices.translate(0.0D, -1.1009999513626099D, 1.5D);
		MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
		VertexConsumer vertexConsumer2 = immediate.getBuffer(MonolithRenderer.getInstance().renderType(DimensionalDoors.id("textures/mob/monolith/solid/monolith_14.png")));
		MonolithRenderer.getInstance().renderToBuffer(matrices, vertexConsumer2, 0xf000f0, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		immediate.endBatch();
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.CUSTOM;
	}

	public static class Factory implements ParticleProvider<ParticleOptions> {
		@Nullable
		@Override
		public Particle createParticle(ParticleOptions particleOptions, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
			return new MonolithParticle(world, x, y, z);
		}
	}
}
