package org.dimdev.dimdoors.particle;

import com.mojang.serialization.Codec;

import net.minecraft.particle.ParticleType;

import org.dimdev.dimdoors.particle.client.RiftParticleEffect;

public class RiftParticleType extends ParticleType<RiftParticleEffect> {
	protected RiftParticleType() {
		super(true, RiftParticleEffect.PARAMETERS_FACTORY);
	}

	@Override
	public Codec<RiftParticleEffect> getCodec() {
		return RiftParticleEffect.CODEC;
	}
}
