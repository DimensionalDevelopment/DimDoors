package org.dimdev.dimdoors.mixin;

import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;
import org.dimdev.dimdoors.world.feature.gateway.NetherGatwayPiece;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.structure.NetherFortressGenerator;
import net.minecraft.structure.StructurePiecesHolder;
import net.minecraft.util.math.Direction;

@Mixin(NetherFortressGenerator.class)
public class NetherPieceMixin {
    @Shadow @Final @Mutable
    static NetherFortressGenerator.PieceData[] ALL_CORRIDOR_PIECES;

    @Inject(method = "createPiece(Lnet/minecraft/structure/NetherFortressGenerator$PieceData;Lnet/minecraft/structure/StructurePiecesHolder;Ljava/util/Random;IIILnet/minecraft/util/math/Direction;I)Lnet/minecraft/structure/NetherFortressGenerator$Piece;", at = @At("TAIL"), cancellable = true)
    private static void createPiece(NetherFortressGenerator.PieceData pieceData, StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength, CallbackInfoReturnable<NetherFortressGenerator.Piece> cir) {
        if(pieceData.pieceType == NetherGatwayPiece.class) {
            cir.setReturnValue(NetherGatwayPiece.create(holder, x, y, z, chainLength, orientation));
        }
    }

    static {
        ArrayUtils.addAll(ALL_CORRIDOR_PIECES, new NetherFortressGenerator.PieceData(NetherGatwayPiece.class, 5, 1));
    }
}
