package org.dimdev.dimdoors.shared.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;

import java.util.List;

public abstract class ItemDimensionalTrapdoor extends ItemBlock {

    public <T extends Block & IRiftProvider<TileEntityEntranceRift>>ItemDimensionalTrapdoor(T block) {
        super(block);
    }

    // TODO: placing trapdoors on rifts, merge this code with the dimdoors code/common interface
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        boolean replaceable = world.getBlockState(pos).getBlock().isReplaceable(world, pos); // Check this before calling super, since that changes the block
        EnumActionResult result = super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        if (result == EnumActionResult.SUCCESS) {
            if (!replaceable) pos = pos.offset(facing);
            IBlockState state = world.getBlockState(pos);
            // Get the rift entity (not hard coded, works with any door size)
            @SuppressWarnings("unchecked") // Guaranteed to be IRiftProvider<TileEntityEntranceRift> because of constructor
            TileEntityEntranceRift entranceRift = ((IRiftProvider<TileEntityEntranceRift>) state.getBlock()).getRift(world, pos, state);
            // Configure the rift to its default functionality
            setupRift(entranceRift);
            // Register the rift in the registry
            entranceRift.markDirty();
            entranceRift.register();
        }
        return result;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (I18n.hasKey(getRegistryName() + ".info")) tooltip.add(I18n.format(getRegistryName() + ".info"));
    }

    protected abstract void setupRift(TileEntityEntranceRift entranceRift); // TODO: NBT-based
}
