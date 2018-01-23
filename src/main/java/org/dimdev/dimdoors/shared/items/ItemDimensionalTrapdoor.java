package org.dimdev.dimdoors.shared.items;

import org.dimdev.dimdoors.shared.blocks.IRiftProvider;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public abstract class ItemDimensionalTrapdoor extends ItemBlock {

    public <T extends Block & IRiftProvider<?>>ItemDimensionalTrapdoor(T block) {
        super(block);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        boolean replaceable = world.getBlockState(pos).getBlock().isReplaceable(world, pos); // Check this before calling super, since that changes the block
        EnumActionResult result = super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        if (result == EnumActionResult.SUCCESS) {
            if (!replaceable) pos = pos.offset(facing);
            IBlockState state = world.getBlockState(pos);
            ((IRiftProvider<?>) state.getBlock()).handleRiftSetup(world, pos, state);
        }
        return result;
    }
}
