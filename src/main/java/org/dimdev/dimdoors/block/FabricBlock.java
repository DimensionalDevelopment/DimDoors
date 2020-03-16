package org.dimdev.dimdoors.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.dimdoors.world.ModDimensions;

public class FabricBlock extends Block {
    public FabricBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    @SuppressWarnings({"deprecation"})
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack heldStack = hand == Hand.MAIN_HAND ? player.getMainHandStack() : player.getOffHandStack();
        Block heldBlock = Block.getBlockFromItem(heldStack.getItem());

        if (world.canPlayerModifyAt(player, pos) &&
            player.canPlaceOn(pos, hit.getSide(), heldStack) &&
            heldBlock.getDefaultState().isFullCube(world, pos) &&
            !heldBlock.hasBlockEntity() &&
            heldBlock != this &&
            !player.isSneaking() &&
            ModDimensions.isDimDoorsPocketDimension(world)) {

            if (!player.isCreative()) {
                heldStack.decrement(1);
            }

            world.setBlockState(pos, heldBlock.getPlacementState(new ItemPlacementContext(new ItemUsageContext(player, hand, hit))));
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }
}
