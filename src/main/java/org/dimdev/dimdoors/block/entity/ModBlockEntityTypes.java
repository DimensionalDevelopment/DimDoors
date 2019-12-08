package org.dimdev.dimdoors.block.entity;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import org.dimdev.dimdoors.block.ModBlocks;

import java.util.function.Supplier;

public class ModBlockEntityTypes {
    public static final BlockEntityType<DetachedRiftBlockEntity> DETACHED_RIFT = register(
            "dimdoors:detached_rift",
            DetachedRiftBlockEntity::new,
            new Block[]{ModBlocks.DETACHED_RIFT}
    );

    public static final BlockEntityType<EntranceRiftBlockEntity> ENTRANCE_RIFT = register(
            "dimdoors:entrance_rift",
            EntranceRiftBlockEntity::new,
            new Block[]{ModBlocks.WOOD_DIMENSIONAL_DOOR, ModBlocks.IRON_DIMENSIONAL_DOOR, ModBlocks.GOLD_DIMENSIONAL_DOOR, ModBlocks.QUARTZ_DIMENSIONAL_DOOR}
    );

    private static <E extends BlockEntity> BlockEntityType<E> register(String id, Supplier<? extends E> supplier, Block[] blocks) {
        return Registry.register(Registry.BLOCK_ENTITY, id, new BlockEntityType<>(supplier, Sets.newHashSet(blocks), null));
    }
}
