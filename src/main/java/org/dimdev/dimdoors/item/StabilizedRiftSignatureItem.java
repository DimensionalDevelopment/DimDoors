package org.dimdev.dimdoors.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.rift.targets.RiftReference;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.util.Location;
import org.dimdev.util.RotatedLocation;

import java.util.List;

public class StabilizedRiftSignatureItem extends Item { // TODO: common superclass with rift signature
    public static final String ID = "stabilized_rift_signature";

    public StabilizedRiftSignatureItem(Settings settings) {
        setMaxStackSize(1);
        setMaxDamage(20);
        setCreativeTab(ModItemGroups.DIMENSIONAL_DOORS);
        setTranslationKey(ID);
        setRegistryName(new Identifier("dimdoors", ID));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.getTagCompound() != null && stack.getTagCompound().hasKey("destination");
    }

    @Override
    public ActionResult onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        pos = world.getBlockState(pos).getBlock().isReplaceable(world, pos) ? pos : pos.offset(side);
        // Fail if the player can't place a block there
        if (!player.canPlayerEdit(pos, side.getOpposite(), stack)) {
            return ActionResult.FAIL;
        }

        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        RotatedLocation target = getTarget(stack);

        if (target == null) {
            // The link signature has not been used. Store its current target as the first location.
            setSource(stack, new RotatedLocation(world, pos, player.rotationYaw, 0));
            player.sendStatusMessage(new TextComponentTranslation(getRegistryName() + ".stored"), true);
            world.playSound(null, player.getPosition(), ModSoundEvents.RIFT_START, SoundCategory.BLOCKS, 0.6f, 1);
        } else {
            // Place a rift at the target point
            if (target.getBlockState().getBlock() != ModBlocks.DETACHED_RIFT) {
                if (!target.getBlockState().getBlock().isReplaceable(world, ((Location) target).getPos())) {
                    DimDoors.sendTranslatedMessage(player, "tools.target_became_block");
                    // Don't clear source, stabilized signatures always stay bound
                    return ActionResult.FAIL;
                }
                World targetWorld = ((Location) target).getWorld();
                targetWorld.setBlockState(((Location) target).getPos(), ModBlocks.DETACHED_RIFT.getDefaultState());
                DetachedRiftBlockEntity rift1 = (DetachedRiftBlockEntity) target.getBlockEntity();
                rift1.setTeleportTargetRotation(target.yaw, 0);
                rift1.register();
            }

            // Place a rift at the source point
            world.setBlockState(pos, ModBlocks.DETACHED_RIFT.getDefaultState());
            DetachedRiftBlockEntity rift2 = (DetachedRiftBlockEntity) world.getBlockEntity(pos);
            rift2.setDestination(RiftReference.tryMakeRelative(new Location(world, pos), target));
            rift2.setTeleportTargetRotation(player.rotationYaw, 0);
            rift2.register();

            stack.damageItem(1, player);

            player.sendStatusMessage(new TextComponentTranslation(getRegistryName() + ".created"), true);
            world.playSound(null, player.getPosition(), ModSoundEvents.RIFT_END, SoundCategory.BLOCKS, 0.6f, 1);
        }

        return ActionResult.SUCCESS;
    }

    public static void setSource(ItemStack itemStack, RotatedLocation destination) {
        if (!itemStack.hasTagCompound()) itemStack.setTagCompound(new CompoundTag());
        itemStack.getTagCompound().put("destination", destination.serialize());
    }

    public static void clearSource(ItemStack itemStack) {
        if (itemStack.hasTagCompound()) {
            itemStack.getTagCompound().removeTag("destination");
        }
    }

    public static RotatedLocation getTarget(ItemStack itemStack) {
        if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("destination")) {
            RotatedLocation transform = RotatedLocation.deserialize(itemStack.getTagCompound().getCompoundTag("destination"));
            return transform;
        } else {
            return null;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        RotatedLocation transform = getTarget(stack);
        if (transform != null) {
            tooltip.add(I18n.format(getRegistryName() + ".bound.info", transform.getX(), transform.getY(), transform.getZ(), ((Location) transform).dim));
        } else {
            tooltip.add(I18n.format(getRegistryName() + ".unbound.info"));
        }
    }
}
