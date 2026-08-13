package org.dimdev.dimdoors.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;
import org.dimdev.dimdoors.block.CoordinateTransformerBlock;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimdoors.block.TraversableRiftBlock;
import org.dimdev.dimdoors.util.LevelSpaceHelper;
import org.dimdev.dimdoors.pockets.DefaultDungeonDestinations;
import org.dimdev.dimdoors.rift.RiftUtils;
import org.dimdev.dimdoors.rift.targets.EscapeTarget;
import org.dimdev.dimdoors.rift.targets.LocationProvider;
import org.dimdev.dimdoors.rift.targets.Targets;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimcore.api.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EntranceRiftBlockEntity<T extends EntranceRiftBlockEntity<T>> extends RiftBlockEntity<T> {
    private static final EscapeTarget ESCAPE_TARGET = new EscapeTarget(true);
    protected BlockState doorBlockState;
    private RiftUtils.PortalPlane plane;

    public EntranceRiftBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntityTypes.ENTRANCE_RIFT, pos, state);
    }

    protected EntranceRiftBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        updateState(pos, state);
    }

    private void updateState(BlockPos pos, BlockState state) {
        var block = state.getBlock();

        doorBlockState = state;
        plane = null;

        if (block instanceof TraversableRiftBlock<?> traversableRiftBlock) {
            doorBlockState = traversableRiftBlock.getVisualBlockState(state);
            plane = traversableRiftBlock.getPortalPlane(state, pos);
        }
    }

    @Override
    public void setBlockState(@NotNull BlockState state) {
        super.setBlockState(state);

        doorBlockState = state.getBlock() instanceof TraversableRiftBlock<?> traversableRiftBlock ? traversableRiftBlock.getVisualBlockState(state) : state;
    }

    public boolean teleport(Entity entity) {
        //Sets the location where the player should be teleported back to if they are in limbo and try to escape, to be the entrance of the rift that took them into dungeons.

        boolean status = attemptTeleport(entity);

        if (this.isStateDirty() && !this.data.isAlwaysDelete()) {
            this.setChanged();
        }

        return status;
    }

    public boolean attemptTeleport(Entity entity) {
        this.setStateDirty(false);

        // Attempt a teleport
        try {
            Vec3 relativePos = new Vec3(0, 0, 0);
            Rotations relativeAngle = new Rotations(entity.getXRot(), entity.getYRot(), 0);
            Vec3 relativeVelocity = entity.getDeltaMovement();

            Target target = this.getTarget();
            var location = target instanceof LocationProvider provider ? provider.getLocation() : null;

            BlockState state = this.getLevel().getBlockState(this.getBlockPos());
            Block block = state.getBlock();
            if (block instanceof CoordinateTransformerBlock transformer) {
                var blockPos = getBlockPos();
                var sourceFrame = LevelSpaceHelper.INSTANCE.sourceTeleportFrame(
                        (ServerLevel) this.level,
                        blockPos,
                        entity,
                        entity.position(),
                        relativeAngle,
                        relativeVelocity
                );
                TransformationMatrix3d.TransformationMatrix3dBuilder transformationBuilder = transformer.transformationBuilder(state, blockPos);
                TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder = transformer.rotatorBuilder(state, blockPos);
                relativePos = transformer.transformTo(transformationBuilder, sourceFrame.pos());
                relativeAngle = transformer.rotateTo(rotatorBuilder, sourceFrame.angle());
                relativeVelocity = transformer.rotateTo(rotatorBuilder, sourceFrame.velocity());
            }

            EntityTarget entityTarget = target.as(Targets.ENTITY);
            if (entityTarget.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, location)) {
                VirtualLocation vLoc = VirtualLocation.fromLocation(Location.ofWorld((ServerLevel) entity.level(), entity.blockPosition()));
                if (DimensionalDoors.getConfig().getGeneralConfig().enableDebugMessages)
                    EntityUtils.chat(entity, Component.literal("You are at x = " + vLoc.getX() + ", y = ?, z = " + vLoc.getZ() + ", w = " + vLoc.getDepth()));
                return true;
            }
        } catch (Exception e) {
            EntityUtils.chat(entity, Component.literal("Something went wrong while trying to teleport you, please report this bug."));
            DimensionalDoors.LOGGER.error("Teleporting failed with the following exception: ", e);
        }

        return false;
    }

    @Override
    public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity, Location location) {
        if(getLevel() == null) return false;
        return receiveEntityAt((ServerLevel) this.level, this.getBlockPos(), this.getLevel().getBlockState(this.getBlockPos()), entity, relativePos, relativeAngle, relativeVelocity, location);
    }

    public static boolean receiveEntityAt(ServerLevel level, BlockPos blockPos, BlockState state, Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity, Location location) {
        Block block = state.getBlock();
        Direction direction = getOrientation(state).getOpposite();

        // compute offset once — used whether or not it's a transformer block
        double offset = DimensionalDoors.getConfig().getGeneralConfig().teleportOffset + 0.01;
        Vec3 offsetVec = Vec3.atLowerCornerOf(direction.getNormal()).scale(offset);

        Vec3 targetPos = Vec3.atCenterOf(blockPos).add(offsetVec);

        if (block instanceof CoordinateTransformerBlock transformer) {
            if (transformer.isExitFlipped()) {
                TransformationMatrix3d flipper = TransformationMatrix3d.builder().rotateY(Math.PI).build();
                relativePos = flipper.transform(relativePos);
                relativeAngle = flipper.transform(relativeAngle);
                relativeVelocity = flipper.transform(relativeVelocity);
            }

            TransformationMatrix3d.TransformationMatrix3dBuilder transformationBuilder = transformer.transformationBuilder(state, blockPos);
            TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder = transformer.rotatorBuilder(state, blockPos);

            targetPos = transformer.transformOut(transformationBuilder, relativePos).add(offsetVec); // offset reapplied here

            relativeAngle = transformer.rotateOut(rotatorBuilder, relativeAngle);
            relativeVelocity = transformer.rotateOut(rotatorBuilder, relativeVelocity);
        }

        targetPos = targetPos.add(
                direction.getNormal().getX() / 2.0,
                direction.getNormal().getY() / 2.0,
                direction.getNormal().getZ() / 2.0
        );

        LevelSpaceHelper.TeleportFrame frame = LevelSpaceHelper.INSTANCE.projectTeleportFrame(
                level,
                location,
                targetPos,
                relativeAngle,
                relativeVelocity
        );

        TeleportUtil.teleport(entity, level, frame.pos(), frame.angle(), frame.velocity());
        return true;
    }

    public Direction getOrientation() {
        //noinspection ConstantConditions

        return Optional.of(this.level.getBlockState(this.worldPosition))
                .filter(state -> state.hasProperty(HorizontalDirectionalBlock.FACING))
                .map(state -> state.getValue(HorizontalDirectionalBlock.FACING))
                .orElse(Direction.NORTH);
    }

    private static Direction getOrientation(BlockState state) {
        return state.hasProperty(HorizontalDirectionalBlock.FACING) ? state.getValue(HorizontalDirectionalBlock.FACING) : Direction.NORTH;
    }

    public boolean hasOrientation() {
        return this.level != null && this.level.getBlockState(this.worldPosition).hasProperty(HorizontalDirectionalBlock.FACING);
    }

    /**
     * Specifies if the portal should be rendered two blocks tall
     */
    public boolean isTall() {
        return ((RiftProvider<?>) this.getBlockState().getBlock()).isTall(this.getBlockState());
    }

    @Override
    public boolean isDetached() {
        return false;
    }

    public void setPortalDestination(ServerLevel world) {
        if (ModDimensions.isLimboDimension(world)) {
            this.setDestination(ESCAPE_TARGET);
        } else {
            this.setDestination(DefaultDungeonDestinations.getGateway());
            this.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
        }
    }

    @Override
    public void unregister() {
        super.unregister();

        var state = level.getBlockState(getBlockPos());
        var block = state.getBlock();

        if(block instanceof TraversableRiftBlock<?> traversableRiftBlock) {
            traversableRiftBlock.closeRift(level, getBlockPos(), state);
        }
    }

    public BlockState getRenderBlockState() {
        return doorBlockState;
    }

    public boolean hasTraversed(Level level, Vec3 previousPosition, Vec3 currentPosition) {
        return plane == null || !plane.isTraversed(level, previousPosition, currentPosition);
    }

    @Override
    public void detach() {
        if(level != null) {
            var waterlogged = getBlockState().hasProperty(BlockStateProperties.WATERLOGGED) ? getBlockState().getValue(BlockStateProperties.WATERLOGGED) : false;

            level.setBlockAndUpdate(worldPosition, ModBlocks.DETACHED_RIFT.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, waterlogged));
            level.getBlockEntity(worldPosition, ModBlockEntityTypes.DETACHED_RIFT).ifPresent(a -> a.setData(this.getData()));
        }
    }
}
