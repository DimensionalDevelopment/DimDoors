package org.dimdev.dimdoors.world

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressPieces
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided
import org.dimdev.dimdoors.mixin.NetherFortressPiecesAccessor
import org.dimdev.dimdoors.world.structure.NetherGatewayPiece

object ModStructuresPieces : PlatformRegistry<StructurePieceType>(Registries.STRUCTURE_PIECE, BuiltInRegistries.STRUCTURE_PIECE, getSided() ){
    @JvmField val NETHER_GATEWAY: StructurePieceType = registerNetherBridge("nether_fortress_gateway", NetherGatewayPiece::class.java, 5, 1, StructurePieceType.ContextlessType { NetherGatewayPiece(it) })

    private fun registerNetherBridge(name: String, pieceClass: Class<out NetherFortressPieces.NetherBridgePiece>, weight: Int, maxPlaceCount: Int, type: StructurePieceType): StructurePieceType {
        NetherFortressPiecesAccessor.setBridgePieceWeights(NetherFortressPiecesAccessor.getBridgePieceWeights() + NetherFortressPieces.PieceWeight(pieceClass, weight, maxPlaceCount))

        return create(name, { type} )
    }

}
