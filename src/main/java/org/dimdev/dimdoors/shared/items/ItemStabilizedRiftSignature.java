package org.dimdev.dimdoors.shared.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.ddutils.Location;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.ddutils.RotatedLocation;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.rifts.targets.RiftReference;
import org.dimdev.dimdoors.shared.sound.ModSounds;
import org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift;

import java.util.List;
import java.util.Objects;

public class ItemStabilizedRiftSignature extends Item { // TODO: common superclass with rift signature
    public static final String ID = "stabilized_rift_signature";

    public ItemStabilizedRiftSignature() {
        setMaxStackSize(1);
        setMaxDamage(20);
        setCreativeTab(ModCreativeTabs.DIMENSIONAL_DOORS_CREATIVE_TAB);
        setTranslationKey(ID);
        setRegistryName(DimDoors.getResource(ID));
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
        // Fail if the player can't place a block there
        if (!player.canPlayerEdit(pos, side.getOpposite(), stack)) return EnumActionResult.FAIL;
        if (world.isRemote) return EnumActionResult.SUCCESS;
        RotatedLocation target = getTarget(stack);
        if (Objects.isNull(target)) {
            // The link signature has not been used. Store its current target as the first location.
            setSource(stack, new RotatedLocation(new Location(world, pos), player.rotationYaw, 0));
            player.sendStatusMessage(new TextComponentTranslation(getTranslationKey() + ".stored"), true);
            world.playSound(null, player.getPosition(), ModSounds.RIFT_START, SoundCategory.BLOCKS, 0.6f, 1);
        } else {
            // Place a rift at the target point
            if (target.getLocation().getBlockState().getBlock() != ModBlocks.RIFT) {
                if (!target.getLocation().getBlockState().getBlock().isReplaceable(world, target.getLocation().getPos())) {
                    DimDoors.sendTranslatedMessage(player, "tools.target_became_block");
                    // Don't clear source, stabilized signatures always stay bound
                    return EnumActionResult.FAIL;
                }
                World targetWorld = target.getLocation().getWorld();
                targetWorld.setBlockState(target.getLocation().getPos(), ModBlocks.RIFT.getDefaultState());
                TileEntityFloatingRift rift1 = (TileEntityFloatingRift) target.getLocation().getTileEntity();
                rift1.setTeleportTargetRotation(target.getYaw(), 0);
                rift1.register();
            }
            // Place a rift at the source point
            world.setBlockState(pos, ModBlocks.RIFT.getDefaultState());
            TileEntityFloatingRift rift2 = (TileEntityFloatingRift) world.getTileEntity(pos);
            rift2.setDestination(RiftReference.tryMakeRelative(new Location(world, pos), target.getLocation()));
            rift2.setTeleportTargetRotation(player.rotationYaw, 0);
            rift2.register();
            stack.damageItem(1, player);
            player.sendStatusMessage(new TextComponentTranslation(getTranslationKey() + ".created"), true);
            world.playSound(null, player.getPosition(), ModSounds.RIFT_END, SoundCategory.BLOCKS, 0.6F, 1.0F);
        }
        return EnumActionResult.SUCCESS;
    }

    public static void setSource(ItemStack itemStack, RotatedLocation destination) {
        if (!itemStack.hasTagCompound()) itemStack.setTagCompound(new NBTTagCompound());
        itemStack.getTagCompound().setTag("destination", destination.writeToNBT(new NBTTagCompound()));
    }

    public static void clearSource(ItemStack itemStack) {
        if (itemStack.hasTagCompound()) itemStack.getTagCompound().removeTag("destination");
    }

    public static RotatedLocation getTarget(ItemStack itemStack) {
        if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("destination")) {
            RotatedLocation transform = new RotatedLocation();
            transform.readFromNBT(itemStack.getTagCompound().getCompoundTag("destination"));
            return transform;
        } return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        RotatedLocation transform = getTarget(stack);
        if (Objects.nonNull(transform))
            tooltip.add(I18n.format(getTranslationKey() + ".bound.info", transform.getLocation().getX(),
                    transform.getLocation().getY(), transform.getLocation().getZ(), transform.getLocation().dim));
        else tooltip.add(I18n.format(getTranslationKey() + ".unbound.info"));
    }
}
