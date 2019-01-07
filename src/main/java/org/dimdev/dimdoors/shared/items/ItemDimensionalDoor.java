package org.dimdev.dimdoors.shared.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.client.TileEntityFloatingRiftRenderer;
import org.dimdev.dimdoors.shared.ModConfig;
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

// TODO: All wood types, Biome O' Plenty support
public abstract class ItemDimensionalDoor extends ItemDoor {

    public <T extends Block & IRiftProvider<TileEntityEntranceRift>> ItemDimensionalDoor(T block) {
        super(block);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        BlockPos originalPos = pos; // super.onItemUse needs the actual position
        if (!world.getBlockState(pos).getBlock().isReplaceable(world, pos)) pos = pos.offset(facing);

        boolean placedOnRift = world.getBlockState(pos).getBlock() == ModBlocks.RIFT;

        if (!placedOnRift && !player.isSneaking() && isRiftNear(world, pos)) {
            // Allowing on second right click would require cancelling client-side, which
            // is impossible (see https://github.com/MinecraftForge/MinecraftForge/issues/3272)
            // without sending custom packets.
            if (world.isRemote) {
                DimDoors.chat(player, "rifts.entrances.rift_too_close");
                TileEntityFloatingRiftRenderer.showRiftCoreUntil = System.currentTimeMillis() + ModConfig.graphics.highlightRiftCoreFor;
            }
            return EnumActionResult.FAIL;
        }

        if (world.isRemote) {
            return super.onItemUse(player, world, originalPos, hand, facing, hitX, hitY, hitZ);
        }

        // Store the rift entity if there's a rift block there that may be broken
        TileEntityFloatingRift rift = null;
        if (placedOnRift) {
            if (canBePlacedOnRift()) {
                rift = (TileEntityFloatingRift) world.getTileEntity(pos);
                rift.setUnregisterDisabled(true);
            } else {
                DimDoors.sendTranslatedMessage(player, "rifts.entrances.cannot_be_placed_on_rift");
            }
        }

        EnumActionResult result = super.onItemUse(player, world, originalPos, hand, facing, hitX, hitY, hitZ);
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

    public static boolean isRiftNear(World world, BlockPos pos) {
        // TODO: This is called every right click server-side! Is this efficient enough? Maybe use rift registry?
        for (int x = pos.getX() - 5; x < pos.getX() + 5; x++) {
            for (int y = pos.getY() - 5; y < pos.getY() + 5; y++) {
                for (int z = pos.getZ() - 5; z < pos.getZ() + 5; z++) {
                    BlockPos searchPos = new BlockPos(x, y, z);
                    if (world.getBlockState(searchPos).getBlock() == ModBlocks.RIFT) {
                        TileEntityFloatingRift rift = (TileEntityFloatingRift) world.getTileEntity(searchPos);
                        if (Math.sqrt(pos.distanceSq(searchPos)) < rift.size / 150) return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (I18n.hasKey(getRegistryName() + ".info")) {
            tooltip.add(I18n.format(getRegistryName() + ".info"));
        }
    }

    public abstract void setupRift(TileEntityEntranceRift entranceRift); // TODO: NBT-based, or maybe lambda function-based?
    public abstract boolean canBePlacedOnRift(); // TODO: NBT-based, true when no NBT is present
}
