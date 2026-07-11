package org.dimdev.dimdoors.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.EntityUtils;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;
import org.dimdev.dimdoors.block.*;
import org.dimdev.dimdoors.compat.sable.SableHelper;
import org.dimdev.dimdoors.item.RiftKeyItem;
import org.dimdev.dimdoors.pockets.DefaultDungeonDestinations;
import org.dimdev.dimdoors.rift.RiftUtils;
import org.dimdev.dimdoors.rift.registry.Rift;
import org.dimdev.dimdoors.rift.targets.EscapeTarget;
import org.dimdev.dimdoors.world.ModDimensions;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EntranceRiftBlockEntity extends RiftBlockEntity {
    private static final EscapeTarget ESCAPE_TARGET = new EscapeTarget(true);
    protected BlockState doorBlockState;
    private RiftUtils.PortalPlane plane;

    public EntranceRiftBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntityTypes.ENTRANCE_RIFT, pos, state);
    }

    protected EntranceRiftBlockEntity(BlockEntityType<? extends EntranceRiftBlockEntity> type, BlockPos pos, BlockState state) {
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


    @Override
    public boolean teleport(Entity entity) {
        //Sets the location where the player should be teleported back to if they are in limbo and try to escape, to be the entrance of the rift that took them into dungeons.

        boolean status = super.teleport(entity);

        if (this.riftStateChanged && !this.data.isAlwaysDelete()) {
            this.setChanged();
        }

        return status;
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

        SableHelper.TeleportFrame frame = SableHelper.INSTANCE.projectTeleportFrame(
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
    protected void onClose(Level level, BlockPos pos) {
        var state = level.getBlockState(pos);
        var block = state.getBlock();

        if(block instanceof TraversableRiftBlock<?> traversableRiftBlock) {
            traversableRiftBlock.closeRift(level, pos, state);
        }
    }

    @Override
    public boolean stablized() {
        return true;
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
            var waterlogged = getBlockState().hasProperty(BlockStateProperties.WATERLOGGED) ? getRenderBlockState().getValue(BlockStateProperties.WATERLOGGED) : false;

            level.setBlockAndUpdate(worldPosition, ModBlocks.DETACHED_RIFT.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, waterlogged));
            level.getBlockEntity(worldPosition, ModBlockEntityTypes.DETACHED_RIFT).ifPresent(a -> a.setData(this.getData()));
        }
    }
}
