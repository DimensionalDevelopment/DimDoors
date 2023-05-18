package org.dimdev.dimdoors.api.block.entity.fabric;

import net.fabricmc.fabric.mixin.lookup.BlockEntityTypeAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

public class MutableBlockEntityTypeImpl {
    public static Set<Block> getBlocks(BlockEntityType<?> type) {
        return ((BlockEntityTypeAccessor) type).getBlocks();
    }
}
