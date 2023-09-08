package org.dimdev.dimdoors.mixin;

import net.minecraft.world.level.levelgen.structure.structures.NetherFortressPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NetherFortressPieces.class)
public interface NetherFortressPiecesAccessor {
    @Accessor("BRIDGE_PIECE_WEIGHTS")
    public static NetherFortressPieces.PieceWeight[] getBridgePieceWeights() {
        throw new AssertionError("Untransformed @Accessor");
    }

    @Accessor("BRIDGE_PIECE_WEIGHTS")
    @Mutable
    public static void setBridgePieceWeights(NetherFortressPieces.PieceWeight[] pieceWeights) {
        throw new AssertionError("Untransformed @Accessor");
    }
}
