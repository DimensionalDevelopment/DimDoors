package org.dimdev.dimdoors.rift.targets;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.rift.target.*;
import org.dimdev.dimdoors.api.util.EntityUtils;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.block.CoordinateTransformerBlock;
import org.dimdev.dimdoors.block.RiftVariantProvider;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.compat.sable.SableHelper;

import java.util.Optional;

// A list of the default targets provided by dimcore. Add your own in ModTargets
public final class Targets {
    public static final Class<EntityTarget> ENTITY = EntityTarget.class;
    public static final Class<ItemTarget> ITEM = ItemTarget.class;
    public static final Class<FluidTarget> FLUID = FluidTarget.class;
    public static final Class<RedstoneTarget> REDSTONE = RedstoneTarget.class;

    public static void registerDefaultTargets() {

        DefaultTargets.registerDefaultTarget(ENTITY, (entity, relativePos, relativeRotation, relativeVelocity, location) -> {
            if (location != null) {
                var targetLevel = location.getWorld();
                if (targetLevel == null) {
                    return false;
                }

                SableHelper.INSTANCE.ensureSableSubLevelLoaded(targetLevel, location.pos);

                EntityTarget target = resolveEntityTarget(targetLevel, location.pos);
                if (target != null) {
                    return target.receiveEntity(entity, relativePos, relativeRotation, relativeVelocity, location);
                }

                var localTargetPos = Vec3.upFromBottomCenterOf(location.pos, 0.0);
                var frame = SableHelper.INSTANCE.projectTeleportFrame(targetLevel, location, localTargetPos, relativeRotation, relativeVelocity);

                TeleportUtil.teleport(entity, targetLevel, frame.pos(), frame.angle(), frame.velocity());
                return true;
            }

            EntityUtils.chat(entity, Component.translatable("rifts.unlinked2"));
            return false;
        });
        DefaultTargets.registerDefaultTarget(ITEM, stack -> false);

        DefaultTargets.registerDefaultTarget(FLUID, new FluidTarget() {
            @Override
            public boolean addFluidFlow(Direction relativeFacing, Fluid fluid, int level) {
                return false;
            }

            @Override
            public void subtractFluidFlow(Direction relativeFacing, Fluid fluid, int level) {
                throw new RuntimeException("Subtracted fluid flow that was never accepted");
            }
        });

        DefaultTargets.registerDefaultTarget(REDSTONE, new RedstoneTarget() {
            @Override
            public boolean addRedstonePower(Direction relativeFacing, int strength) {
                return false;
            }

            @Override
            public void subtractRedstonePower(Direction relativeFacing, int strength) {
                throw new RuntimeException("Subtracted redstone that was never accepted");
            }
        });
    }

    private static EntityTarget resolveEntityTarget(ServerLevel level, BlockPos pos) {
        EntityTarget target = resolveEntityTargetAt(level, pos);
        if (target != null) {
            return target;
        }

        target = resolveEntityTargetAt(level, pos.below());
        if (target != null) {
            return target;
        }

        return resolveEntityTargetAt(level, pos.above());
    }

    private static EntityTarget resolveEntityTargetAt(ServerLevel level, BlockPos pos) {
        if (SableHelper.INSTANCE.getBlockEntity(level, pos) instanceof EntityTarget target) {
            return target;
        }

        return resolveBlockStateEntityTarget(level, pos);
    }

    static EntityTarget resolveBlockStateEntityTarget(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        BlockPos targetPos = pos;

        if (state.hasProperty(DoorBlock.HALF) && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
            targetPos = pos.below();
            state = level.getBlockState(targetPos);
            block = state.getBlock();
        }

        if (!(block instanceof CoordinateTransformerBlock)) {
            if (!(block instanceof RiftVariantProvider provider)) {
                return null;
            }

            Optional<BlockState> providerState = provider.getRiftProviderState(state);
            if (providerState.isEmpty()) {
                return null;
            }

            state = providerState.get();
            block = state.getBlock();
            if (!(block instanceof CoordinateTransformerBlock)) {
                return null;
            }
        }

        BlockState targetState = state;
        BlockPos finalTargetPos = targetPos;
        return (entity, relativePos, relativeAngle, relativeVelocity, location) ->
                EntranceRiftBlockEntity.receiveEntityAt(level, finalTargetPos, targetState, entity, relativePos, relativeAngle, relativeVelocity, location);
    }
}
