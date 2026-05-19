package org.dimdev.dimdoors.compat.sable.mixins;

import dev.ryanhcode.sable.sublevel.storage.HoldingSubLevel;
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunk;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(SubLevelHoldingChunk.class)
public interface SubLevelHoldingChunkAccessor {
    @Accessor(value = "loadedHoldingSubLevels", remap = false)
    Object2ObjectMap<UUID, HoldingSubLevel> dimdoors$getLoadedHoldingSubLevels();
}