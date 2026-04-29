package org.dimdev.dimdoors.block.entity;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.block.entity.MutableBlockEntityType;
import org.dimdev.dimdoors.block.ModBlocks;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ModBlockEntityTypes {
    public static final BlockEntityType<DetachedRiftBlockEntity> DETACHED_RIFT = register(
            "detached_rift",
            DetachedRiftBlockEntity::new,
            ModBlocks.DETACHED_RIFT
    );

    public static final MutableBlockEntityType<EntranceRiftBlockEntity> ENTRANCE_RIFT = registerMutable(
            "entrance_rift",
            EntranceRiftBlockEntity::new,
            ModBlocks.DIMENSIONAL_PORTAL);

    public static final BlockEntityType<TesselatingLoomBlockEntity> TESSELATING_LOOM = register("tesselating_loom", TesselatingLoomBlockEntity::new, ModBlocks.TESSELATING_LOOM);


    private static <E extends BlockEntity> BlockEntityType<E> register(String id, BiFunction<BlockPos, BlockState, E> factory, Block... blocks) {
        return DimensionalDoors.getSided().registerBlockEntityType(id, BlockEntityType.Builder.of(factory::apply, Stream.of(blocks).toArray(Block[]::new)).build(null));
    }


    private static <E extends BlockEntity> MutableBlockEntityType<E> registerMutable(String id, MutableBlockEntityType.BlockEntityFactory<E> factory, Block... blocks) {
        return DimensionalDoors.getSided().registerBlockEntityType(id, MutableBlockEntityType.Builder.create(factory, Stream.of(blocks).toArray(Block[]::new)).build());
    }

    public static void init() {
    }
}
