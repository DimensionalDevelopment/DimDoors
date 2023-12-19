package org.dimdev.dimdoors.items;

import org.dimdev.dimdoors.DimDoors;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

public class ItemWorldThread extends Item {
    public ItemWorldThread() {
        super();
        this.setCreativeTab(DimDoors.dimDoorsCreativeTab);
    }

    @Override
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon(DimDoors.modid + ":" + this.getUnlocalizedName().replace("item.", ""));
    }
}
