package org.dimdev.dimdoors.particle.client;

import org.dimdev.dimdoors.client.MonolithModel;
import org.dimdev.dimdoors.client.MonolithRenderer;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MonolithParticle extends Particle {

	public MonolithParticle(ClientWorld world, double x, double y, double z) {
		super(world, x, y, z);
		this.maxAge = 30;
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		float delta = ((float)this.age + tickDelta) / (float)this.maxAge;
		MatrixStack matrices = new MatrixStack();
		matrices.multiply(camera.getRotation());
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(150.0F * delta - 60.0F));
		matrices.scale(-1.0F, -1.0F, 1.0F);
		matrices.translate(0.0D, -1.1009999513626099D, 1.5D);
		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		VertexConsumer vertexConsumer2 = immediate.getBuffer(MonolithRenderer.getInstance().getLayer(new Identifier("dimdoors:textures/mob/monolith/monolith14.png")));
		MonolithRenderer.getInstance().render(matrices, vertexConsumer2, 0xf000f0, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
		immediate.draw();
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.CUSTOM;
	}

	public static class Factory implements ParticleFactory<DefaultParticleType> {
		@Nullable
		@Override
		public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
			return new MonolithParticle(world, x, y, z);
		}
	}
}
