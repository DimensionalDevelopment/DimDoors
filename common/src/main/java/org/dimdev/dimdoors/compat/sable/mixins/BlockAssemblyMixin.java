package org.dimdev.dimdoors.compat.sable.mixins;

import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.block.DetachedRiftBlock;
import org.dimdev.dimdoors.block.DimensionalPortalBlock;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.dimdev.dimdoors.block.door.DimensionalTrapDoorBlock;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = { DimensionalDoorBlock.class, DimensionalTrapDoorBlock.class, DimensionalPortalBlock.class, DetachedRiftBlock.class })
public abstract class BlockAssemblyMixin implements BlockSubLevelAssemblyListener {
    @Override
    public void beforeMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
        var oldLocation = new Location(originLevel, oldPos);
        var newLocation = new Location(resultingLevel, newPos);

        var blockEntity = originLevel.getBlockEntity(oldPos);

        if(blockEntity instanceof RiftBlockEntity rift) {
            if (DimensionalRegistry.getRiftRegistry().isRiftAt(oldLocation)) {
                    DimensionalRegistry.getRiftRegistry().moveRift(oldLocation, newLocation);
                    rift.setDeleteRift(false);
            }
        }
    }

    @Override
    public void afterMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
        var blockEntity = resultingLevel.getBlockEntity(newPos);

        if(blockEntity instanceof RiftBlockEntity rift) {
            rift.setDeleteRift(false);
        }
    }
}
