package org.dimdev.dimdoors.forge.world;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressPieces;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.mixin.NetherFortressPiecesAccessor;
import org.dimdev.dimdoors.forge.world.structure.NetherGatewayPiece;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.function.Supplier;

public class ModStructures {
    public static Registrar<StructurePieceType> STRUCTURE_PIECE_TYPES = Registries.get(DimensionalDoors.MOD_ID).get(Registry.STRUCTURE_PIECE);
    public static final RegistrySupplier<StructurePieceType> NETHER_GATEWAY = registerNetherBridge("nether_fortress_gateway", NetherGatewayPiece.class, 5, 1);

    private static RegistrySupplier<StructurePieceType> registerNetherBridge(String name, Class<NetherGatewayPiece> netherGatewayPieceClass, int weight, int count) {
        addNetherBridgeWeight(netherGatewayPieceClass, weight, count);

        return registerContextless(name, () -> (StructurePieceType.ContextlessType) compoundTag -> {
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

    private static RegistrySupplier<StructurePieceType> registerContextless(String name, Supplier<StructurePieceType> supplier) {
        return STRUCTURE_PIECE_TYPES.register(DimensionalDoors.id(name), supplier);
    }

    public static void init() {
    }
}
