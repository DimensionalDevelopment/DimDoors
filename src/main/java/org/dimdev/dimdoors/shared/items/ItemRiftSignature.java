package org.dimdev.dimdoors.shared.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.ddutils.I18nUtils;
import org.dimdev.ddutils.Location;
import org.dimdev.dimdoors.DimDoors;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.dimdev.ddutils.RotatedLocation;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.rifts.destinations.GlobalDestination;
import org.dimdev.dimdoors.shared.rifts.TileEntityRift;
import org.dimdev.dimdoors.shared.sound.ModSounds;

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
        // Return false on the client side to pass this request to the server
        if (world.isRemote) {
            return EnumActionResult.FAIL;
        }

        // Fail if the player can't place a block there TODO: spawn protection, other plugin support
        if (!player.canPlayerEdit(pos, side.getOpposite(), stack)) {
            return EnumActionResult.PASS;
        }
        pos = pos.offset(side);

        RotatedLocation target = getSource(stack);

        if (target != null) {
            // Place a rift at the saved point TODO: check that the player still has permission
            if (!target.getLocation().getBlockState().getBlock().equals(ModBlocks.RIFT)) {
                if (!target.getLocation().getBlockState().getBlock().equals(Blocks.AIR)) {
                    return EnumActionResult.FAIL; // TODO: send a message
                }
                World sourceWorld = target.getLocation().getWorld();
                sourceWorld.setBlockState(target.getLocation().getPos(), ModBlocks.RIFT.getDefaultState());
                TileEntityRift rift1 = (TileEntityRift) target.getLocation().getTileEntity();
                rift1.setSingleDestination(new GlobalDestination(new Location(world, pos)));
                rift1.register();
                rift1.setRotation(target.getYaw(), 0);
            }

            // Place a rift at the target point
            world.setBlockState(pos, ModBlocks.RIFT.getDefaultState());
            TileEntityRift rift2 = (TileEntityRift) world.getTileEntity(pos);
            rift2.setSingleDestination(new GlobalDestination(target.getLocation()));
            rift2.setRotation(player.rotationYaw, 0);
            rift2.register();

            stack.damageItem(1, player); // TODO: calculate damage based on position?

            clearSource(stack);
            DimDoors.chat(player, "Rift Created");
            // null = send sound to the player too, we have to do this because this code is not run client-side
            world.playSound(null, player.getPosition(), ModSounds.RIFT_END, SoundCategory.BLOCKS, 0.6f, 1);
        } else {
            // The link signature has not been used. Store its current target as the first location.
            setSource(stack, new RotatedLocation(new Location(world, pos), player.rotationYaw, 0));
            DimDoors.chat(player, "Location Stored in Rift Signature");
            world.playSound(null, player.getPosition(), ModSounds.RIFT_START, SoundCategory.BLOCKS, 0.6f, 1);
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
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
        RotatedLocation transform = getSource(stack);
        if (transform != null) {
            tooltip.add(I18n.format("info.rift_signature.bound", transform.getLocation().getX(), transform.getLocation().getY(), transform.getLocation().getZ(), transform.getLocation().getDim()));
        } else {
            tooltip.addAll(I18nUtils.translateMultiline("info.rift_signature.unbound"));
        }
    }
}
