package org.dimdev.dimdoors.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.dimdev.dimdoors.particle.client.RiftParticleOptions;

public class RiftParticleType extends ParticleType<RiftParticleOptions> {
	protected RiftParticleType() {
		super(true);
	}

	@Override
	public MapCodec<RiftParticleOptions> codec() {
		return RiftParticleOptions.CODEC;
	}

	@Override
	public StreamCodec<? super RegistryFriendlyByteBuf, RiftParticleOptions> streamCodec() {
		return RiftParticleOptions.PARAMETERS_FACTORY;
	}
}
