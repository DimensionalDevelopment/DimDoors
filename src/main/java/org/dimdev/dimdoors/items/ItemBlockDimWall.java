package org.dimdev.dimdoors.items;

import org.dimdev.dimdoors.DimDoors;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockDimWall extends ItemBlock {
    private final static String[] subNames = {"tile.blockDimWall", "tile.blockAncientWall", "tile.blockAlteredWall"};

    public ItemBlockDimWall(Block block) {
        super(block);
        this.setCreativeTab(DimDoors.dimDoorsCreativeTab);
        setHasSubtypes(true);
    }

    @Override
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon(DimDoors.modid + ":" + this.getUnlocalizedName().replace("tile.", ""));
    }

    @Override
    public int getMetadata(int damageValue) {
        return damageValue;
    }

    public String getUnlocalizedName(ItemStack par1ItemStack) {
        return subNames[this.getDamage(par1ItemStack)];
    }
}