package org.dimdev.dimdoors.world.structure;

import net.minecraft.world.level.levelgen.structure.SinglePieceStructure;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class GatewayStructure extends SinglePieceStructure {
    protected GatewayStructure(PieceConstructor constructor, int width, int depth, StructureSettings settings) {
        super(constructor, width, depth, settings);
    }

    @Override
    public StructureType<?> type() {
        return null;
    }
}
