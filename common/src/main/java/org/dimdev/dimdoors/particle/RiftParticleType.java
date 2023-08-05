package org.dimdev.dimdoors.particle;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import org.dimdev.dimdoors.particle.client.RiftParticleOptions;

public class RiftParticleType extends ParticleType<RiftParticleOptions> {
	protected RiftParticleType() {
		super(true, RiftParticleOptions.PARAMETERS_FACTORY);
	}

	@Override
	public Codec<RiftParticleOptions> codec() {
		return RiftParticleOptions.CODEC;
	}
}
