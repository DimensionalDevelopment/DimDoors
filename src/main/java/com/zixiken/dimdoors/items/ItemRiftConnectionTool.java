/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.items;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.RiftRegistry;
import com.zixiken.dimdoors.tileentities.DDTileEntityBase;
import java.util.Set;
import net.minecraft.block.Block;
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

    ItemRiftConnectionTool(float attackDamageIn, float attackSpeedIn, Item.ToolMaterial materialIn, Set<Block> effectiveBlocksIn) {
        super(attackDamageIn, attackSpeedIn, materialIn, effectiveBlocksIn);
        //@todo add extra stuff?
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        RayTraceResult hit = ItemDoorBase.doRayTrace(worldIn, playerIn, true);
        if (hit != null) {
            BlockPos pos = hit.getBlockPos();
            if (worldIn.getTileEntity(pos) instanceof DDTileEntityBase) {
                DDTileEntityBase rift = (DDTileEntityBase) worldIn.getTileEntity(pos);
                if (!playerIn.isSneaking()) {
                    selectPrimaryRift(stack, rift);
                } else {
                    selectSecondaryRiftAndTakeAction(stack, rift);
                }
                return new ActionResult(EnumActionResult.PASS, stack);
            }
        }
        return new ActionResult(EnumActionResult.FAIL, stack);
    }

    private void selectPrimaryRift(ItemStack stack, DDTileEntityBase rift) {
        NBTTagCompound compound = stack.getTagCompound();
        compound.setInteger("primaryRiftID", rift.riftID);
    }

    private void selectSecondaryRiftAndTakeAction(ItemStack stack, DDTileEntityBase rift) {
        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey("isInConnectMode")) {
            compound.setBoolean("isInConnectMode", true);
        }
        if (compound.getBoolean("isInConnectMode")) {
            if (compound.hasKey("primaryRiftID")) {
                int primaryRiftID = compound.getInteger("primaryRiftID");
                int secondaryRiftID = rift.riftID;
                RiftRegistry.Instance.pair(primaryRiftID, secondaryRiftID);
            } else {
                DimDoors.log(this.getClass(), "Primary Rift not selected. First select a primary rift by right-clicking without sneaking.");
            }
        } else {
            int secondaryRiftID = rift.riftID;
            RiftRegistry.Instance.unpair(secondaryRiftID);
        }
    }
}
