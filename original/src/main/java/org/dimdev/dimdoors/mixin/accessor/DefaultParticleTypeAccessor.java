package org.dimdev.dimdoors.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.particle.DefaultParticleType;

@Mixin(DefaultParticleType.class)
public interface DefaultParticleTypeAccessor {
	@Invoker("<init>")
	static DefaultParticleType createDefaultParticleType(boolean alwaysShow) {
		throw new UnsupportedOperationException();
	}
}
