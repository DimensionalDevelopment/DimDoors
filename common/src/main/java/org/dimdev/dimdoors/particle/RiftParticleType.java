package org.dimdev.dimdoors.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.dimdev.dimdoors.particle.client.RiftParticleOptions;
import org.jetbrains.annotations.NotNull;

public class RiftParticleType extends ParticleType<RiftParticleOptions> {
	protected RiftParticleType() {
		super(true);
	}

	@Override
	public @NotNull MapCodec<RiftParticleOptions> codec() {
		return RiftParticleOptions.CODEC;
	}

	@Override
	public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, RiftParticleOptions> streamCodec() {
		return RiftParticleOptions.STREAM_CODEC;
	}
}
