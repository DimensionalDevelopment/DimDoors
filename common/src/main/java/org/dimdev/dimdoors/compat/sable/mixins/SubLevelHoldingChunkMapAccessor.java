package org.dimdev.dimdoors.compat.sable.mixins;

import dev.ryanhcode.sable.sublevel.storage.HoldingSubLevel;
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunk;
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunkMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

/**
 * Exposes Sable's runtime holding map so a sub-level can be found by plot coordinate.
 *
 * <p>Sable indexes stored sub-levels by UUID and by saved pointer, never by the plot they occupy,
 * and its occupancy grid records only that a plot is taken. There is therefore no public route from
 * a world position to an unloaded sub-level, which DimDoors needs for rifts that carry no tracking
 * point. This accessor exists solely to serve that migration path.</p>
 */
@Mixin(value = SubLevelHoldingChunkMap.class, remap = false)
public interface SubLevelHoldingChunkMapAccessor {
    @Accessor(value = "allHoldingSubLevels", remap = false)
    Object2ObjectMap<UUID, HoldingSubLevel> dimdoors$getAllHoldingSubLevels();

    @Accessor(value = "loadedHoldingChunks", remap = false)
    Long2ObjectMap<SubLevelHoldingChunk> dimdoors$getLoadedHoldingChunks();
}
