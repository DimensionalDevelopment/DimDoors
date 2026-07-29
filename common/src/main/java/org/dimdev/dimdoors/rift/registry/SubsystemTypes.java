package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.ModRegistryKeys;
import org.dimdev.dimdoors.world.pocket.PrivateRegistry;

import java.util.function.Supplier;

public class SubsystemTypes {
    public static final SubSystem.Type<RiftGraph> GRAPH = register("rift_graph", RiftGraph::new, RiftGraph.CODEC);
    public static final SubSystem.Type<RiftRegistry> RIFT = register("rift_registry", RiftRegistry::new, RiftRegistry.CODEC);
    public static final SubSystem.Type<PrivateRegistry> PRIVATE = register("private_registry", PrivateRegistry::new, PrivateRegistry.CODEC);
    public static final SubSystem.Type<PocketRegistry> POCKET = register("pocket_registry", PocketRegistry::new, PocketRegistry.CODEC);

    public static void register() {
    }

    private static <T extends SubSystem<T>> SubSystem.Type<T> register(String name, Supplier<T> supplier, MapCodec<T> codec) {
        return DimensionalDoors.getSided().register(ModRegistryKeys.SUBSYSTEM_TYPE, name, new SubSystem.Type<>(name, supplier, codec));
    }
}
