/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.items;

import com.zixiken.dimdoors.tileentities.DDTileEntityBase;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
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
    
    ItemRiftConnectionTool(float attackDamageIn, float attackSpeedIn, Item.ToolMaterial materialIn, Set<Block> effectiveBlocksIn){
        super(attackDamageIn, attackSpeedIn, materialIn, effectiveBlocksIn);
        //@todo add extra stuff
    }
    //@todo actually implement this item as a tool and stuff
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        RayTraceResult hit = ItemDoorBase.doRayTrace(worldIn, playerIn, true);
        if (hit != null) {
            BlockPos pos = hit.getBlockPos();
            if (worldIn.getTileEntity(pos) instanceof DDTileEntityBase) {
                //@todo implementation here?
                return new ActionResult(EnumActionResult.PASS, stack);
            }
        }
        return new ActionResult(EnumActionResult.FAIL, stack);
    }
}
