package org.dimdev.dimdoors.compat.sable;

import dev.ryanhcode.sable.sublevel.storage.HoldingSubLevel;
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunk;
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunkMap;
import org.dimdev.dimdoors.compat.sable.mixins.SubLevelHoldingChunkMapAccessor;

import java.util.Collection;

final class SableHoldingChunks {
    private SableHoldingChunks() {
    }

    static Collection<SubLevelHoldingChunk> loadedHoldingChunks(SubLevelHoldingChunkMap map) {
        return ((SubLevelHoldingChunkMapAccessor) map).dimdoors$getLoadedHoldingChunks().values();
    }

    static Collection<HoldingSubLevel> allHoldingSubLevels(SubLevelHoldingChunkMap map) {
        return ((SubLevelHoldingChunkMapAccessor) map).dimdoors$getAllHoldingSubLevels().values();
    }
}
