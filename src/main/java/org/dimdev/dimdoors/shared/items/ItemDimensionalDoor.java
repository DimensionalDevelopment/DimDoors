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
import net.minecraft.item.ItemDoor;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;
import org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift;

import java.util.List;

public abstract class ItemDimensionalDoor extends ItemDoor { // TODO: Biomes O' Plenty doors

    public <T extends Block & IRiftProvider<TileEntityEntranceRift>> ItemDimensionalDoor(T block) {
        super(block);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return EnumActionResult.FAIL;
        boolean replaceable = world.getBlockState(pos).getBlock().isReplaceable(world, pos);

        // Store the rift entity if there's a rift block there that may be broken
        TileEntityFloatingRift rift = null;
        if (world.getBlockState(replaceable ? pos : pos.offset(facing)).getBlock().equals(ModBlocks.RIFT)) {
            rift = (TileEntityFloatingRift) world.getTileEntity(replaceable ? pos : pos.offset(facing));
            rift.setUnregisterDisabled(true);
        }

        EnumActionResult result = super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        if (!replaceable) pos = pos.offset(facing);
        if (result == EnumActionResult.SUCCESS) {
            IBlockState state = world.getBlockState(pos);
            if (rift == null) {
                // Get the rift entity (not hard coded, works with any door size)
                @SuppressWarnings("unchecked") // Guaranteed to be IRiftProvider<TileEntityEntranceRift> because of constructor
                TileEntityEntranceRift entranceRift = ((IRiftProvider<TileEntityEntranceRift>) state.getBlock()).getRift(world, pos, state);

                // Configure the rift to its default functionality
                setupRift(entranceRift);

                // Register the rift in the registry
                entranceRift.markDirty();
                entranceRift.register();
            } else {
                // Copy from the old rift
                TileEntityEntranceRift newRift = (TileEntityEntranceRift) world.getTileEntity(pos);
                newRift.copyFrom(rift);
                newRift.updateType();
            }
        } else if (rift != null) {
            rift.setUnregisterDisabled(false);
        }
        return result;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (I18n.hasKey(getUnlocalizedName() + ".info")) {
            tooltip.add(I18n.format(getUnlocalizedName() + ".info"));
        }
    }

    public abstract void setupRift(TileEntityEntranceRift entranceRift); // TODO: NBT-based, or maybe lambda function-based?
    public abstract boolean canBePlacedOnRift(); // TODO: NBT-based, true when no NBT is present
}
