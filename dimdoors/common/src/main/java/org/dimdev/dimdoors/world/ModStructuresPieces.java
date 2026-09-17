package org.dimdev.dimdoors.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressPieces;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.mixin.NetherFortressPiecesAccessor;
import org.dimdev.dimdoors.world.structure.NetherGatewayPiece;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ModStructuresPieces {
    public static final StructurePieceType NETHER_GATEWAY = registerNetherBridge("nether_fortress_gateway", NetherGatewayPiece.class, 5, 1);

    private static StructurePieceType registerNetherBridge(String name, Class<NetherGatewayPiece> netherGatewayPieceClass, int weight, int count) {
        addNetherBridgeWeight(netherGatewayPieceClass, weight, count);

        return registerContextless(name, (StructurePieceType.ContextlessType) compoundTag -> {
            try {
                return netherGatewayPieceClass.getConstructor(CompoundTag.class).newInstance(compoundTag);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void addNetherBridgeWeight(Class<NetherGatewayPiece> netherGatewayPieceClass, int weight, int count) {
        var array = Arrays.copyOf(NetherFortressPiecesAccessor.getBridgePieceWeights(), NetherFortressPiecesAccessor.getBridgePieceWeights().length + 1);
        array[array.length - 1] = new NetherFortressPieces.PieceWeight(netherGatewayPieceClass, weight, count);
        NetherFortressPiecesAccessor.setBridgePieceWeights(array);
    }

    private static StructurePieceType registerContextless(String name, StructurePieceType supplier) {
        return DimensionalDoors.getSided().register(Registries.STRUCTURE_PIECE, name, supplier);
    }

    public static void init() {
    }
}
