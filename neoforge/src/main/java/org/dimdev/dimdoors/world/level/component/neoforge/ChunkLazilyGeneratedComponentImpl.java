package org.dimdev.dimdoors.world.level.component.neoforge;

import net.minecraft.world.level.chunk.LevelChunk;
import org.dimdev.dimdoors.ModAttachmentTypes;

public class ChunkLazilyGeneratedComponentImpl {
    public static void setGenerated(LevelChunk chunk, boolean value) {
        chunk.setData(ModAttachmentTypes.HAS_BEEN_LAZY_GENNED, value);
    }

    public static boolean isGenerated(LevelChunk chunk) {
        return chunk.getExistingData(ModAttachmentTypes.HAS_BEEN_LAZY_GENNED).orElse(false);
    }
}
