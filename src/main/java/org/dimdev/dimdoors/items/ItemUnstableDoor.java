package org.dimdev.dimdoors.items;

import org.dimdev.dimdoors.blocks.BaseDimDoor;
import org.dimdev.dimdoors.DimDoors;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.util.List;

public class ItemUnstableDoor extends BaseItemDoor {
    public ItemUnstableDoor(Material material, ItemDoor door) {
        super(material, door);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        par3List.add(StatCollector.translateToLocal("info.chaosDoor"));
    }

    @Override
    protected BaseDimDoor getDoorBlock() {
        return (BaseDimDoor) DimDoors.unstableDoor;
    }
}