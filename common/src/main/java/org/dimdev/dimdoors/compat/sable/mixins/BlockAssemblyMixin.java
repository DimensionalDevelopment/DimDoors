package org.dimdev.dimdoors.compat.sable.mixins;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.block.DimensionalPortalBlock;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.dimdev.dimdoors.block.door.DimensionalTrapDoorBlock;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Consumer;

@Mixin(value = { DimensionalDoorBlock.class, DimensionalTrapDoorBlock.class, DimensionalPortalBlock.class })
public abstract class BlockAssemblyMixin implements BlockSubLevelAssemblyListener {
    @Override
    public void beforeMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
        var oldLocation = new Location(originLevel, oldPos);
        var newLocation = new Location(resultingLevel, newPos);

        originLevel.getBlockEntity(oldPos, ModBlockEntityTypes.ENTRANCE_RIFT).ifPresent(blockEntity -> {
            DimensionalRegistry.getRiftRegistry().moveRift(oldLocation, newLocation);
            blockEntity.setDeleteRift(false);
        });
    }

    @Override
    public void afterMove(ServerLevel originLevel, ServerLevel resultingLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {

    }
}
