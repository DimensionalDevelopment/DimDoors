package org.dimdev.dimdoors.world.structure;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import org.dimdev.dimdoors.world.ModStructurePlacements;
import org.dimdev.dimdoors.world.ModStructures;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PocketPlacement extends StructurePlacement {
    public static final PocketPlacement INSTANCE = new PocketPlacement();

    private PocketPlacement() {
        super(Vec3i.ZERO, FrequencyReductionMethod.DEFAULT, 0, 0, Optional.empty());
    }

    @Override
    protected boolean isPlacementChunk(ChunkGeneratorStructureState chunkGeneratorStructureState, int x, int z) {
        return x == 0 && z == 0;
    }

    @Override
    public @NotNull StructurePlacementType<?> type() {
        return ModStructurePlacements.POCKET.get();
    }
}
