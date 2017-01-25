/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared.items;

import static com.zixiken.dimdoors.DimDoors.translateAndAdd;
import com.zixiken.dimdoors.shared.blocks.BlockDimDoorBase;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 *
 * @author Robijnvogel
 */
public class ItemDimDoorTransient extends ItemDoorBase {   
    
    public static final String ID = "itemDimDoorTransient";

    public ItemDimDoorTransient() {
        super(ModBlocks.blockDimDoorTransient, ModItems.itemDimDoorTransient);
        setUnlocalizedName(ID);
        setRegistryName(ID);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        translateAndAdd("info.transientDoor", tooltip);
    }

    @Override
    protected BlockDimDoorBase getDoorBlock() {
        return ModBlocks.blockDimDoorTransient;
    }
    
}
