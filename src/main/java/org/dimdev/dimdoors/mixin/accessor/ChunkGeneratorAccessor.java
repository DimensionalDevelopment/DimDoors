package org.dimdev.dimdoors.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.gen.chunk.ChunkGenerator;

@Mixin(ChunkGenerator.class)
public interface ChunkGeneratorAccessor {
	@Accessor
	long getWorldSeed();
}
