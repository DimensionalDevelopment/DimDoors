package org.dimdev.dimdoors.api.rift.target;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.dimdev.dimdoors.api.util.BlockPosUtil;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.block.CoordinateTransformerBlock;
import org.dimdev.dimdoors.block.RiftVariantProvider;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.util.LevelSpaceHelper;

import java.util.function.BiFunction;

public final class TargetResolver {
    private TargetResolver() {}

    public static <T extends Target> T target(Location location, Class<T> targetClazz) {
        return location == null ? null : target(location.getWorld(), location.pos, targetClazz, null);
    }

    public static <T extends Target> T target(ServerLevel level, BlockPos pos, Class<T> targetClazz) {
        return target(level, pos, targetClazz, null);
    }

    public static <T extends Target> T target(ServerLevel level, BlockPos pos, Class<T> targetClazz, BiFunction<ServerLevel, BlockPos, T> function) {
        if (level == null) {
            return null;
        }

        return BlockPosUtil.nearbyVertical(pos, p -> {
            var target = castOrNull(level, p, LevelSpaceHelper.INSTANCE::getBlockEntity, targetClazz);
            if(target == null) target = castOrNull(level, p, (level1, pos1) -> LevelSpaceHelper.INSTANCE.getBlockState(level1, pos1).getBlock(), targetClazz);
            if(target == null) target = castOrNull(level, p, function, targetClazz);

            return target;
        });
    }

    public static Target target(Location location) {
        return location == null ? null : target(location.getWorld(), location.pos);
    }

    public static Target target(ServerLevel level, BlockPos pos) {
        return target(level, pos, Target.class, null);
    }

    public static EntityTarget entity(Location location) {
        return location == null ? null : entity(location.getWorld(), location.pos);
    }

    public static EntityTarget entity(ServerLevel level, BlockPos pos) {
        return target(level, pos, EntityTarget.class, TargetResolver::blockStateEntity);
    }

    public static <T, V> T castOrNull(ServerLevel level, BlockPos pos, BiFunction<ServerLevel, BlockPos, V> function, Class<T> tClass) {
        return function != null ? castOrNull(function.apply(level, pos), tClass) : null;
    }

    public static <T> T castOrNull(Object obj, Class<T> tClass) {
        return tClass.isInstance(obj) ? tClass.cast(obj) : null;
    }

    public static EntityTarget blockStateEntity(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);

        if (state.hasProperty(DoorBlock.HALF) && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
            pos = pos.below();
            state = level.getBlockState(pos);
        }

        var block = state.getBlock();
        BlockState targetState = block instanceof CoordinateTransformerBlock
                ? state
                : block instanceof RiftVariantProvider provider
                        ? provider.getRiftProviderState(state)
                                .filter(s -> s.getBlock() instanceof CoordinateTransformerBlock)
                                .orElse(null)
                        : null;

        if (targetState == null) return null;

        BlockPos targetPos = pos;
        return (entity, relPos, relAngle, relVel, location) ->
                EntranceRiftBlockEntity.receiveEntityAt(level, targetPos, targetState, entity, relPos, relAngle, relVel, location);
    }
}