package org.dimdev.dimdoors.mixin.accessor;

import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkGenerator.class)
public interface ChunkGeneratorAccessor {
}
