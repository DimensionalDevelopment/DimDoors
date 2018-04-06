package org.dimdev.dimdoors.shared.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.ddutils.Location;
import org.dimdev.dimdoors.DimDoors;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.dimdev.ddutils.RotatedLocation;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.rifts.DestinationMaker;
import org.dimdev.dimdoors.shared.sound.ModSounds;
import org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift;

import java.util.List;

public class ItemRiftSignature extends Item {
    public static final String ID = "rift_signature";

    public ItemRiftSignature() {
        setMaxStackSize(1);
        setMaxDamage(1);
        setCreativeTab(DimDoors.DIM_DOORS_CREATIVE_TAB);
        setUnlocalizedName(ID);
        setRegistryName(new ResourceLocation(DimDoors.MODID, ID));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.getTagCompound() != null && stack.getTagCompound().hasKey("destination");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        pos = world.getBlockState(pos).getBlock().isReplaceable(world, pos) ? pos : pos.offset(side);

        // Fail if the player can't place a block there TODO: spawn protection, other plugin support
        if (!player.canPlayerEdit(pos, side.getOpposite(), stack)) {
            return EnumActionResult.FAIL;
        }

        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }

        RotatedLocation target = getSource(stack);

        if (target == null) {
            // The link signature has not been used. Store its current target as the first location.
            setSource(stack, new RotatedLocation(new Location(world, pos), player.rotationYaw, 0));
            player.sendStatusMessage(new TextComponentTranslation(getUnlocalizedName() + ".stored"), true);
            world.playSound(null, player.getPosition(), ModSounds.RIFT_START, SoundCategory.BLOCKS, 0.6f, 1);
        } else {
            // Place a rift at the saved point TODO: check that the player still has permission
            if (!target.getLocation().getBlockState().getBlock().equals(ModBlocks.RIFT)) {
                if (!target.getLocation().getBlockState().getBlock().isReplaceable(world, target.getLocation().getPos())) {
                    DimDoors.sendTranslatedMessage(player, "tools.target_became_block");
                    clearSource(stack); // TODO: But is this fair? It's a rather hidden way of unbinding your signature!
                    return EnumActionResult.FAIL;
                }
                World sourceWorld = target.getLocation().getWorld();
                sourceWorld.setBlockState(target.getLocation().getPos(), ModBlocks.RIFT.getDefaultState());
                TileEntityFloatingRift rift1 = (TileEntityFloatingRift) target.getLocation().getTileEntity();
                rift1.setDestination(DestinationMaker.relativeIfPossible(target.getLocation(), new Location(world, pos)));
                rift1.setTeleportTargetRotation(target.getYaw(), 0); // setting pitch to 0 because player is always facing down to place rift
                rift1.register();
            }

            // Place a rift at the target point
            world.setBlockState(pos, ModBlocks.RIFT.getDefaultState());
            TileEntityFloatingRift rift2 = (TileEntityFloatingRift) world.getTileEntity(pos);
            rift2.setDestination(DestinationMaker.relativeIfPossible(new Location(world, pos), target.getLocation()));
            rift2.setTeleportTargetRotation(player.rotationYaw, 0);
            rift2.register();

            stack.damageItem(1, player); // TODO: calculate damage based on position?

            clearSource(stack);
            player.sendStatusMessage(new TextComponentTranslation(getUnlocalizedName() + ".created"), true);
            // null = send sound to the player too, we have to do this because this code is not run client-side
            world.playSound(null, player.getPosition(), ModSounds.RIFT_END, SoundCategory.BLOCKS, 0.6f, 1);
        }

        return EnumActionResult.SUCCESS;
    }

    public static void setSource(ItemStack itemStack, RotatedLocation destination) {
        if (!itemStack.hasTagCompound()) itemStack.setTagCompound(new NBTTagCompound());
        itemStack.getTagCompound().setTag("destination", destination.writeToNBT(new NBTTagCompound()));
    }

    public static void clearSource(ItemStack itemStack) {
        if (itemStack.hasTagCompound()) {
            itemStack.getTagCompound().removeTag("destination");
        }
    }

    public static RotatedLocation getSource(ItemStack itemStack) {
        if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("destination")) {
            RotatedLocation transform = new RotatedLocation();
            transform.readFromNBT(itemStack.getTagCompound().getCompoundTag("destination"));
            return transform;
        } else {
            return null;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        RotatedLocation transform = getSource(stack);
        if (transform != null) {
            tooltip.add(I18n.format(I18n.format(getUnlocalizedName() + ".bound.info", transform.getLocation().getX(), transform.getLocation().getY(), transform.getLocation().getZ(), transform.getLocation().getDim())));
        } else {
            tooltip.add(I18n.format(getUnlocalizedName() + ".unbound.info"));
        }
    }
}
