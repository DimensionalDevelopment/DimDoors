package org.dimdev.dimdoors.items;

import org.dimdev.dimdoors.blocks.BaseDimDoor;
import org.dimdev.dimdoors.DimDoors;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemGoldDimDoor extends BaseItemDoor {

    public ItemGoldDimDoor(Material material, ItemDoor door) {
        super(material, door);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        DimDoors.translateAndAdd("info.goldDimDoor", par3List);
    }

    @Override
    protected BaseDimDoor getDoorBlock() {
        return (BaseDimDoor) DimDoors.goldenDimensionalDoor;
    }
}
