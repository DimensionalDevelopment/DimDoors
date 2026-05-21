package org.dimdev.dimdoors.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.EntityUtils;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;
import org.dimdev.dimdoors.block.CoordinateTransformerBlock;
import org.dimdev.dimdoors.block.DimensionalPortalBlock;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.RiftProvider;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;
import org.dimdev.dimdoors.block.door.DimensionalTrapDoorBlock;
import org.dimdev.dimdoors.compat.sable.SableHelper;
import org.dimdev.dimdoors.item.RiftKeyItem;
import org.dimdev.dimdoors.pockets.DefaultDungeonDestinations;
import org.dimdev.dimdoors.rift.RiftUtils;
import org.dimdev.dimdoors.rift.registry.Rift;
import org.dimdev.dimdoors.rift.targets.EscapeTarget;
import org.dimdev.dimdoors.world.ModDimensions;

import java.util.Optional;

import static net.minecraft.world.level.block.DoorBlock.*;
import static org.dimdev.dimdoors.block.door.WaterLoggableDoorBlock.WATERLOGGED;

public class EntranceRiftBlockEntity extends RiftBlockEntity {
    private static final EscapeTarget ESCAPE_TARGET = new EscapeTarget(true);
    private static final Logger LOGGER = LogManager.getLogger();
    private BlockState doorBlockState;
    private boolean locked;
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

        switch (block) {
            case DimensionalDoorBlock dimDoor -> {
                if (dimDoor instanceof DimensionalDoorBlockRegistrar.AutoGenDimensionalDoorBlock autoDimDoor)
                    doorBlockState = autoDimDoor.getOriginalBlock().defaultBlockState().setValue(FACING, state.getValue(FACING)).setValue(OPEN, state.getValue(OPEN)).setValue(HINGE, state.getValue(HINGE)).setValue(POWERED, state.getValue(POWERED)).setValue(HALF, state.getValue(HALF));
                plane = RiftUtils.PortalPlane.ofDoor(state, pos);
            }
            case DimensionalTrapDoorBlock dimTrapDoor -> {
                if (dimTrapDoor instanceof DimensionalDoorBlockRegistrar.AutoGenDimensionalTrapdoorBlock autoDimTrapDoor)
                    doorBlockState = autoDimTrapDoor.getOriginalBlock().defaultBlockState().setValue(FACING, state.getValue(FACING)).setValue(OPEN, state.getValue(OPEN)).setValue(POWERED, state.getValue(POWERED)).setValue(TrapDoorBlock.HALF, state.getValue(TrapDoorBlock.HALF));
                plane = RiftUtils.PortalPlane.ofTrapdoor(state, pos);
            }
            case DimensionalPortalBlock dimensionalPortalBlock -> plane = RiftUtils.PortalPlane.ofDoor(state, pos);
            default -> {
            }
        }
    }

    @Override
    public void setBlockState(BlockState state) {
        super.setBlockState(state);
        updateState(this.worldPosition, state);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.loadAdditional(nbt, provider);
        locked = nbt.getBoolean("locked");
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putBoolean("locked", locked);
        super.saveAdditional(nbt, provider);
    }

    @Override
    public boolean teleport(Entity entity) {
        return teleport(entity, entity.position());
    }

    public boolean teleport(Entity entity, Vec3 sourcePosition) {
        //Sets the location where the player should be teleported back to if they are in limbo and try to escape, to be the entrance of the rift that took them into dungeons.

        if (this.isLocked()) {
            if (entity instanceof LivingEntity) {
                ItemStack stack = ((LivingEntity) entity).getItemInHand(((LivingEntity) entity).getUsedItemHand());
                Rift rift = this.asRift();

                if (RiftKeyItem.has(stack, rift.getId())) {
                    return innerTeleport(entity, sourcePosition);
                }

                EntityUtils.chat(entity, Component.translatable("rifts.isLocked"));
            }
            return false;
        }

        return innerTeleport(entity, sourcePosition);
    }

    private boolean innerTeleport(Entity entity, Vec3 sourcePosition) {
        boolean status = super.teleport(entity, sourcePosition);

        if (this.riftStateChanged && !this.data.isAlwaysDelete()) {
            this.setChanged();
        }

        return status;
    }

    @Override
    public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity, Location location) {
        return receiveEntityAt((ServerLevel) this.level, this.getBlockPos(), this.getLevel().getBlockState(this.getBlockPos()), entity, relativePos, relativeAngle, relativeVelocity, location);
    }

    public InteractionResult tryTeleportOnTraversal(Level level, Entity entity, AABB previousBox, AABB currentBox, Vec3 previousPos, Vec3 currentPos) {
        boolean traversed = previousBox != null && currentBox != null
                ? hasTraversed(level, previousBox, currentBox, previousPos, currentPos)
                : hasTraversed(level, entity, previousPos, currentPos);

        if (!traversed) {
            return InteractionResult.PASS;
        }

        if (entity.isOnPortalCooldown()) {
            entity.setPortalCooldown();
            return InteractionResult.PASS;
        }

        entity.setPortalCooldown();
        teleport(entity, getTeleportSourcePosition(previousPos, currentPos));
        return InteractionResult.SUCCESS;
    }

    public static boolean receiveEntityAt(ServerLevel level, BlockPos blockPos, BlockState state, Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity, Location location) {
        Block block = state.getBlock();
        Vec3 exitNormal = getExitNormal(state);

        // compute offset once — used whether or not it's a transformer block
        double offset = DimensionalDoors.getConfig().getGeneralConfig().teleportOffset + 0.01;
        Vec3 targetPos = Vec3.atCenterOf(blockPos);

        if (block instanceof CoordinateTransformerBlock transformer) {
            TransformationMatrix3d.TransformationMatrix3dBuilder transformationBuilder = transformer.transformationBuilder(state, blockPos);
            TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder = transformer.rotatorBuilder(state, blockPos);
            Vec3 localExitNormal = transformer.rotateTo(rotatorBuilder, exitNormal);

            if (transformer.isExitFlipped()) {
                TransformationMatrix3d flipper = TransformationMatrix3d.builder().rotateY(Math.PI).build();
                relativePos = flipper.transform(relativePos);
                relativeAngle = flipper.transform(relativeAngle);
                relativeVelocity = flipper.transform(relativeVelocity);
            }

            targetPos = transformer.transformOut(transformationBuilder, relativePos);

            relativeAngle = transformer.rotateOut(rotatorBuilder, relativeAngle);
            relativeVelocity = transformer.rotateOut(rotatorBuilder, relativeVelocity);
            exitNormal = normalize(transformer.rotateOut(rotatorBuilder, localExitNormal));
        }

        targetPos = targetPos.add(exitNormal.scale(offset + 0.5D));

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

    private static Vec3 getExitNormal(BlockState state) {
        if (state.getBlock() instanceof DimensionalTrapDoorBlock && state.hasProperty(TrapDoorBlock.HALF)) {
            return state.getValue(TrapDoorBlock.HALF) == Half.TOP ? new Vec3(0.0D, -1.0D, 0.0D) : new Vec3(0.0D, 1.0D, 0.0D);
        }

        return Vec3.atLowerCornerOf(getOrientation(state).getOpposite().getNormal());
    }

    private static Vec3 normalize(Vec3 vector) {
        return vector.lengthSqr() > 1.0E-12D ? vector.normalize() : Vec3.ZERO;
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

    @Override
    public boolean isLocked() {
        return locked;
    }

    @Override
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setPortalDestination(ServerLevel world) {
        if (ModDimensions.isLimboDimension(world)) {
            this.setDestination(ESCAPE_TARGET);
        } else {
            this.setDestination(DefaultDungeonDestinations.getGateway());
            this.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
        }
    }

    public void generateDetached(Level world) {
        var blockState = getBlockState();
        var pos = getBlockPos();
        world.setBlockAndUpdate(pos, ModBlocks.DETACHED_RIFT.defaultBlockState().setValue(WATERLOGGED, blockState.getValue(WATERLOGGED)));
        ((DetachedRiftBlockEntity) world.getBlockEntity(pos)).setData(getData());
    }

    @Override
    protected void onClose(Level level, BlockPos pos) {
        var state = level.getBlockState(pos);
        var block = state.getBlock();

        if (block instanceof DimensionalDoorBlock dimensionalDoorBlock) {
            var base = dimensionalDoorBlock.baseBlock();

            if (base instanceof DoorBlock doorBlock) {
                var newState = doorBlock.defaultBlockState()
                        .setValue(FACING, state.getValue(FACING))
                        .setValue(OPEN, state.getValue(OPEN))
                        .setValue(HINGE, state.getValue(HINGE))
                        .setValue(POWERED, state.getValue(POWERED))
                        .setValue(HALF, DoubleBlockHalf.LOWER);

                level.removeBlock(pos, false);
                level.setBlockAndUpdate(pos, newState);
                level.setBlockAndUpdate(pos.above(), newState.setValue(HALF, DoubleBlockHalf.UPPER));
            }
        } else if (block instanceof DimensionalTrapDoorBlock dimensionalDoorBlock) {
            var base = dimensionalDoorBlock.baseBlock();

            if (base instanceof TrapDoorBlock doorBlock) {
                var newState = doorBlock.defaultBlockState()
                        .setValue(FACING, state.getValue(FACING))
                        .setValue(OPEN, state.getValue(OPEN))
                        .setValue(POWERED, state.getValue(POWERED))
                        .setValue(TrapDoorBlock.HALF, state.getValue(TrapDoorBlock.HALF));

                level.removeBlock(pos, false);
                level.setBlockAndUpdate(pos, newState);
            }
        } else if (block instanceof DimensionalPortalBlock) {
            level.removeBlock(pos, false);
        }
    }

    @Override
    public boolean stablized() {
        return true;
    }

    public BlockState getRenderBlockState() {
        return doorBlockState;
    }

    public boolean hasTraversed(Level level, Entity entity, Vec3 previousPosition, Vec3 currentPosition) {
        return plane != null && plane.isTraversed(level, entity, previousPosition, currentPosition);
    }

    public boolean hasTraversed(Level level, AABB previousBox, AABB currentBox, Vec3 previousPosition, Vec3 currentPosition) {
        return plane != null && plane.isTraversed(level, previousBox, currentBox, previousPosition, currentPosition);
    }

    public boolean hasTraversed(Level level, Vec3 previousPosition, Vec3 currentPosition) {
        return plane != null && plane.isTraversed(level, previousPosition, currentPosition);
    }

    public Vec3 getTeleportSourcePosition(Vec3 previousPosition, Vec3 currentPosition) {
        return plane == null ? currentPosition : plane.intersectionOrProjection(previousPosition, currentPosition);
    }
}
