package org.dimdev.dimdoors.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

@Mixin(NoiseChunkGenerator.class)
public interface NoiseChunkGeneratorAccessor {
	@Accessor
	static float[] getNOISE_WEIGHT_TABLE() {
		throw new UnsupportedOperationException();
	}

	@Accessor
	static float[] getBIOME_WEIGHT_TABLE() {
		throw new UnsupportedOperationException();
	}

	@Invoker
	static double callGetNoiseWeight(int x, int y, int z) {
		throw new UnsupportedOperationException();
	}

	@Invoker
	static double callCalculateNoiseWeight(int x, int y, int z) {
		throw new UnsupportedOperationException();
	}
}
