package org.dimdev.dimdoors.mixin;

import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressPieces;
import org.dimdev.dimdoors.world.structure.NetherGatewayPiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NetherFortressPieces.class)
public class NetherFortressPiecesMixin {
    @Inject(method = "findAndCreateBridgePieceFactory", at = @At("HEAD"), cancellable = true)
    private static void findAndCreateBridgePieceFactory(NetherFortressPieces.PieceWeight weight, StructurePieceAccessor pieces, RandomSource random, int x, int y, int z, Direction orientation, int genDepth, CallbackInfoReturnable<NetherFortressPieces.NetherBridgePiece> cir) {
        if (weight.pieceClass == NetherGatewayPiece.class) {
            cir.setReturnValue(NetherGatewayPiece.createPieces(pieces, x, y, z, genDepth, orientation));
        }
    }
}
