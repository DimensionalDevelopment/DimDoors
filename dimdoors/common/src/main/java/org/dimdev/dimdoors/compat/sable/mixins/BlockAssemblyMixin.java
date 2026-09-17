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
import org.dimdev.dimdoors.block.entity.Rift;
import org.dimdev.dimdoors.compat.sable.SableCompat;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(value = { DimensionalDoorBlock.class, DimensionalTrapDoorBlock.class, DimensionalPortalBlock.class, DetachedRiftBlock.class })
public abstract class BlockAssemblyMixin implements BlockSubLevelAssemblyListener {
    @Override
    public void beforeMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
        var oldLocation = Location.ofWorld(originLevel, oldPos);
        var newLocation = Location.ofWorld(resultingLevel, newPos);

        var blockEntity = originLevel.getBlockEntity(oldPos);

        if(blockEntity instanceof Rift rift) {
            if (RiftRegistry.getInstance().isRiftAt(oldLocation)) {
                    RiftRegistry.getInstance().moveRift(oldLocation, newLocation);
                    rift.setDeleteRift(false);
            }
        }
    }

    @Override
    public void afterMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
        var blockEntity = resultingLevel.getBlockEntity(newPos);

        if(blockEntity instanceof Rift rift) {
            rift.setDeleteRift(true);
            var newLocation = Location.ofWorld(resultingLevel, newPos);
            var registry = RiftRegistry.getInstance();
            if (registry.isRiftAt(newLocation)) {
                SableCompat.HELPER.updateRiftTrackingPoint(resultingLevel, registry.getRift(newLocation));
            }
        }
    }
}
