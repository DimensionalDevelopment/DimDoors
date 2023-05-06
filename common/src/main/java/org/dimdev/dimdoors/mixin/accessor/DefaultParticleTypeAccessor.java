package org.dimdev.dimdoors.mixin.accessor;

import net.minecraft.core.particles.SimpleParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SimpleParticleType.class)
public interface DefaultParticleTypeAccessor {
	@Invoker("<init>")
	static SimpleParticleType createDefaultParticleType(boolean alwaysShow) {
		throw new UnsupportedOperationException();
	}
}
