/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared.items;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.RayTraceHelper;
import com.zixiken.dimdoors.shared.RiftRegistry;
import com.zixiken.dimdoors.shared.tileentities.DDTileEntityBase;
import java.util.HashSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/**
 *
 * @author Robijnvogel
 */
public class ItemRiftConnectionTool extends ItemTool {

    public static final String ID = "itemRiftConnectionTool";

    ItemRiftConnectionTool() {
        super(1.0F, -2.8F, ToolMaterial.WOOD, new HashSet());
        //@todo add extra stuff?
        this.setMaxDamage(16);
        setCreativeTab(DimDoors.dimDoorsCreativeTab);
        setUnlocalizedName(ID);
        setRegistryName(ID);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if (worldIn.isRemote) {
            return new ActionResult(EnumActionResult.FAIL, stack);
        }
        if (!stack.hasTagCompound()) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setBoolean("isInConnectMode", true);
            stack.setTagCompound(compound);
        }

        RayTraceResult hit = rayTrace(worldIn, playerIn, true);
        if (RayTraceHelper.isAbstractRift(hit, worldIn)) {
            DDTileEntityBase rift = (DDTileEntityBase) worldIn.getTileEntity(hit.getBlockPos());
            if (playerIn.isSneaking()) {
                return selectRift(stack, worldIn, rift, playerIn); //new ActionResult(EnumActionResult.PASS, stack));
            }
        } else {
            return changeMode(stack, playerIn);
        }

        return new ActionResult(EnumActionResult.FAIL, stack);
    }

    private ActionResult<ItemStack> selectRift(ItemStack stack, World worldIn, DDTileEntityBase rift, EntityPlayer playerIn) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound.getBoolean("isInConnectMode")) {
            if (compound.hasKey("RiftID")) {
                int primaryRiftID = compound.getInteger("RiftID");
                int secondaryRiftID = rift.getRiftID();
                DimDoors.chat(playerIn, "Pairing rift " + primaryRiftID
                        + " with rift " + secondaryRiftID + ".");
                RiftRegistry.Instance.pair(primaryRiftID, secondaryRiftID);
                compound.removeTag("RiftID");
                stack.damageItem(1, playerIn);
            } else {
                int riftID = rift.getRiftID();
                compound.setInteger("RiftID", riftID);
                DimDoors.chat(playerIn, "Rift " + riftID + " stored for connecting.");
            }
        } else {
            if (!worldIn.isRemote) {
                int riftID = rift.getRiftID();
                RiftRegistry.Instance.unpair(riftID);
                DimDoors.chat(playerIn, "Rift " + riftID + " and its paired rift are now disconnected.");
            }
            stack.damageItem(1, playerIn);
        }
        return new ActionResult(EnumActionResult.SUCCESS, stack);
    }

    private ActionResult<ItemStack> changeMode(ItemStack stack, EntityPlayer player) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound.getBoolean("isInConnectMode")) {
            compound.setBoolean("isInConnectMode", false);
            if (compound.hasKey("RiftID")) {
                compound.removeTag("RiftID");
            }
        } else {
            compound.setBoolean("isInConnectMode", true);
        }
        DimDoors.chat(player, "Connection tool mode set to: "
                + (compound.getBoolean("isInConnectMode") ? "Connect" : "Disconnect"));
        return new ActionResult(EnumActionResult.SUCCESS, stack);
    }
}
