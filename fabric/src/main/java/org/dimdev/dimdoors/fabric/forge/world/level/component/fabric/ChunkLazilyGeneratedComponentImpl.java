package org.dimdev.dimdoors.fabric.forge.world.level.component.fabric;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.world.level.chunk.LevelChunk;
import org.dimdev.dimdoors.fabric.DimensionalDoorsComponents;
import org.dimdev.dimdoors.forge.world.level.component.ChunkLazilyGeneratedComponent;

public class ChunkLazilyGeneratedComponentImpl extends ChunkLazilyGeneratedComponent implements Component {
    public static ChunkLazilyGeneratedComponent get(LevelChunk chunk) {
        return DimensionalDoorsComponents.CHUNK_LAZILY_GENERATED_COMPONENT_KEY.get(chunk);
    }
}
