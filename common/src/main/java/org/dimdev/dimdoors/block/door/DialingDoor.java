package org.dimdev.dimdoors.block.door;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.block.entity.DialingDoorBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.rift.registry.DialingAddress;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.util.MathUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class DialingDoor extends DimensionalDoorBlock<DialingDoorBlockEntity> {
    private static final InteractionShape[] BUTTONS = new InteractionShape[] {
            new InteractionShape(4, 21, 0, 12, 28, 1),
            new InteractionShape(4, 14, 0, 12, 21, 1),
            new InteractionShape(4, 6, 0, 12, 14, 1)
    };

    public DialingDoor(Properties settings, BlockSetType blockSetType) {
        super(settings, blockSetType, true);
    }

    @Override
    public BlockEntityType<DialingDoorBlockEntity> getRiftBlockEnityType() {
        return ModBlockEntityTypes.DIALING_DOOR;
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        var type = getType(state, pos, hitResult);
        if(!isOpen(state) && type != null) {
            if(!world.isClientSide()) {
                getRift(world, pos, state).turnDial(type);
                world.playSound(null, pos, ModSoundEvents.KEY_UNLOCKED, SoundSource.BLOCKS, 1.0f, 1.0f);

            }

            return InteractionResult.SUCCESS;
        }

        return super.useWithoutItem(state, world, pos, player, hitResult);
    }

    private DialingAddress.DialType getType(BlockState state, BlockPos pos, HitResult hitResult) {
        var direction = state.getValue(DoorBlock.FACING);
        var lower = state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
        var upper = lower.above();

        var coordiantes = getVoxelCoord(hitResult, direction, lower, upper);

        for (int i = 0; i < BUTTONS.length; i++) {
            var button = BUTTONS[i];
             if (button.intersects(coordiantes)) {
                 return DialingAddress.DialType.values()[i];
             }
        }

        return null;
    }

    private Vec3 getVoxelCoord(HitResult hit, Direction facing, BlockPos min, BlockPos max) {
        Vec3 p = hit.getLocation().subtract(Vec3.atLowerCornerOf(min));
        Vec3 size = Vec3.atLowerCornerOf(max.subtract(min).offset(1, 1, 1));
        Vec3 c = p.subtract(size.scale(0.5));

        Vec3 front = Vec3.atLowerCornerOf(facing.getNormal());
        Vec3 right = Vec3.atLowerCornerOf(facing.getClockWise().getNormal());

        return new Vec3(
                c.dot(right) + Math.abs(size.dot(right)) * 0.5,
                p.y,
                Math.abs(-c.dot(front) + Math.abs(size.dot(front)) * 0.5 - 1)
        ).scale(16);
    }

    private Vec3 getCoord(HitResult hitResult, Direction direction, BlockPos lowerCorner, BlockPos upperCorner) {

        var size = lowerCorner.subtract(upperCorner);
        Vec3 local = hitResult.getLocation().subtract(Vec3.atLowerCornerOf(lowerCorner));

        var xCenter = size.getX() * 0.5;
        var zCenter = size.getZ() * 0.5;

        Vec3 centered = local.subtract(xCenter, 0.0, zCenter);

        Direction front = direction;
        Direction right = front.getClockWise();

        double correctedX = (centered.x * right.getStepX() + centered.z * right.getStepZ());
        double correctedY = local.y();
        double correctedZ = (size.getZ() * 16) - 1 - (centered.x * front.getStepX() + centered.z * front.getStepZ());
        correctedX += xCenter;
        correctedY += 0;
        correctedZ += zCenter;
        correctedX *= 16.0;
        correctedY *= 16.0;
        correctedZ *= 16.0;

        return new Vec3(correctedX, correctedY, correctedZ);
    }

    private BlockPos getCoord(HitResult hitResult, BlockState state, BlockPos pos) {
        BlockPos lowerPos = state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
        Vec3 local = hitResult.getLocation().subtract(Vec3.atLowerCornerOf(lowerPos));
        Vec3 centered = local.subtract(0.5, 0.0, 0.5);

        Direction front = state.getValue(DoorBlock.FACING).getOpposite();
        Direction right = front.getCounterClockWise();
        double correctedX = centered.x * right.getStepX() + centered.z * right.getStepZ();
        double correctedY = local.y();
        double correctedZ = 15 - (centered.x * front.getStepX() + centered.z * front.getStepZ());

        return new BlockPos(Mth.clamp((int) Math.floor((correctedX + 0.5) * 16.0), 0, 15), Mth.clamp((int) Math.floor(local.y * 16.0), 0, 31), 15-Mth.clamp((int) Math.floor((correctedZ + 0.5) * 16.0), 0, 15));
    }

    //Note: This is needed because of how the inheritance works.
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        ResourceKey<LootTable> resourcekey = this.getLootTable();
        if (resourcekey == BuiltInLootTables.EMPTY) {
            return Collections.emptyList();
        } else {
            LootParams lootparams = params.withParameter(LootContextParams.BLOCK_STATE, state).create(LootContextParamSets.BLOCK);
            ServerLevel serverlevel = lootparams.getLevel();
            LootTable loottable = serverlevel.getServer().reloadableRegistries().getLootTable(resourcekey);
            return loottable.getRandomItems(lootparams);
        }
    }

    public static record InteractionShape(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        boolean intersects(Vec3 point) {
            return intersects(point.x, point.y, point.z);
        }
        boolean intersects(double x, double y, double z) {
            return
                    MathUtils.betewen(x, minX, maxX) &&
                    MathUtils.betewen(y, minY, maxY) &&
                    MathUtils.betewen(z, minZ, maxZ);
        }
    }
}
