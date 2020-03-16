package org.dimdev.dimdoors.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

public class DimensionalDoorBlock extends DoorBlock implements RiftProvider<EntranceRiftBlockEntity> {
    public DimensionalDoorBlock(Settings settings) {
        super(settings);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient) {
            return;
        }

        BlockState doorState = world.getBlockState(state.get(HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos);

        if (doorState.getBlock() == this && doorState.get(DoorBlock.OPEN)) { // '== this' to check if not half-broken
            getRift(world, pos, state).teleport(entity);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        state = state.cycle(OPEN);
        world.setBlockState(pos, state, 10);
        world.playLevelEvent(player, state.get(OPEN) ? material == Material.METAL ? 1005 : 1006 : material == Material.METAL ? 1011 : 1012, pos, 0);
        return ActionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return super.canReplace(state, context) || state.getBlock() == ModBlocks.DETACHED_RIFT;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new EntranceRiftBlockEntity();
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState blockState, BlockEntity entity, ItemStack stack) {
        if (entity instanceof EntranceRiftBlockEntity) {
            world.setBlockState(pos, ModBlocks.DETACHED_RIFT.getDefaultState());
            ((DetachedRiftBlockEntity) world.getBlockEntity(pos)).load(((EntranceRiftBlockEntity) entity).serialize());
        }
    }

    @Override
    public EntranceRiftBlockEntity getRift(World world, BlockPos pos, BlockState state) {
        BlockEntity bottomEntity;
        BlockEntity topEntity;

        if (state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
            bottomEntity = world.getBlockEntity(pos);
            topEntity = world.getBlockEntity(pos.up());
        } else {
            bottomEntity = world.getBlockEntity(pos.down());
            topEntity = world.getBlockEntity(pos);
        }

        // TODO: Also notify player in case of error, don't crash
        if (bottomEntity instanceof EntranceRiftBlockEntity && topEntity instanceof EntranceRiftBlockEntity) {
            LOGGER.error("Dimensional door at " + pos + " in world " + world + " contained two rifts, please report this. Defaulting to bottom.");
            return (EntranceRiftBlockEntity) bottomEntity;
        } else if (bottomEntity instanceof EntranceRiftBlockEntity) {
            return (EntranceRiftBlockEntity) bottomEntity;
        } else if (topEntity instanceof EntranceRiftBlockEntity) {
            return (EntranceRiftBlockEntity) topEntity;
        } else {
            throw new RuntimeException("Dimensional door at " + pos + " in world " + world + " contained no rift.");
        }
    }
}
