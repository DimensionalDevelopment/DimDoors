package org.dimdev.dimdoors.compat.create;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.block.entity.MutableBlockEntityType;

import java.util.stream.Stream;

public final class CreateCompatBlockEntityTypes {
    public static final MutableBlockEntityType<SlidingEntranceRiftBlockEntity> SLIDING_ENTRANCE_RIFT = registerMutable(
            "sliding_entrance_rift",
            SlidingEntranceRiftBlockEntity::new
    );

    private CreateCompatBlockEntityTypes() {
    }

    private static <E extends BlockEntity> MutableBlockEntityType<E> registerMutable(String id, MutableBlockEntityType.BlockEntityFactory<E> factory, Block... blocks) {
        return DimensionalDoors.getSided().registerBlockEntityType(id, MutableBlockEntityType.Builder.create(factory, Stream.of(blocks).toArray(Block[]::new)).build());
    }

    public static void init() {
    }
}
