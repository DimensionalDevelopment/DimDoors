/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.items;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.RiftRegistry;
import com.zixiken.dimdoors.tileentities.DDTileEntityBase;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
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
        setUnlocalizedName(ID);
        setRegistryName(ID);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if (!stack.hasTagCompound()) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setBoolean("isInConnectMode", true);
            stack.setTagCompound(compound);
        }

        RayTraceResult hit = ItemDoorBase.doRayTrace(worldIn, playerIn, true);
        if (hit != null && worldIn.getTileEntity(hit.getBlockPos()) instanceof DDTileEntityBase) {
            DDTileEntityBase rift = (DDTileEntityBase) worldIn.getTileEntity(hit.getBlockPos());
            if (playerIn.isSneaking()) {
                return selectRift(stack, worldIn, rift, playerIn); //new ActionResult(EnumActionResult.PASS, stack));
            }
        } else {
            return changeMode(stack);
        }

        return new ActionResult(EnumActionResult.FAIL, stack);
    }

    private ActionResult<ItemStack> selectRift(ItemStack stack, World worldIn, DDTileEntityBase rift, EntityPlayer playerIn) {
        DimDoors.log(this.getClass(), "Selecting rift with ID: " + rift.riftID);
        NBTTagCompound compound = stack.getTagCompound();
        if (compound.getBoolean("isInConnectMode")) {
            if (compound.hasKey("RiftID")) {
                int primaryRiftID = compound.getInteger("RiftID");
                int secondaryRiftID = rift.riftID;
                if (!worldIn.isRemote) {
                    DimDoors.log(this.getClass(), "Pairing rifts with IDs: " + primaryRiftID + " and " + secondaryRiftID);
                    RiftRegistry.Instance.pair(primaryRiftID, secondaryRiftID);
                }
                compound.removeTag("RiftID");
                stack.damageItem(1, playerIn);
            } else {
                compound.setInteger("RiftID", rift.riftID);
            }
        } else {
            if (!worldIn.isRemote) {
                RiftRegistry.Instance.unpair(rift.riftID);
            }
            stack.damageItem(1, playerIn);
        }
        return new ActionResult(EnumActionResult.SUCCESS, stack);
    }

    private ActionResult<ItemStack> changeMode(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound.getBoolean("isInConnectMode")) {
            compound.setBoolean("isInConnectMode", false);
            if (compound.hasKey("RiftID")) {
                compound.removeTag("RiftID");
            }
        } else {
            compound.setBoolean("isInConnectMode", true);
        }
        DimDoors.log(this.getClass(), "isInConnectMode set to: " + compound.getBoolean("isInConnectMode"));
        return new ActionResult(EnumActionResult.SUCCESS, stack);
    }
}
