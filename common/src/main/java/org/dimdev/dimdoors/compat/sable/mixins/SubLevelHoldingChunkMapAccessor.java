package org.dimdev.dimdoors.compat.sable.mixins;

import dev.ryanhcode.sable.sublevel.storage.HoldingSubLevel;
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunk;
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunkMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(value = SubLevelHoldingChunkMap.class, remap = false)
public interface SubLevelHoldingChunkMapAccessor {
    @Accessor(value = "allHoldingSubLevels", remap = false)
    Object2ObjectMap<UUID, HoldingSubLevel> dimdoors$getAllHoldingSubLevels();

    @Accessor(value = "loadedHoldingChunks", remap = false)
    Long2ObjectMap<SubLevelHoldingChunk> dimdoors$getLoadedHoldingChunks();
}


