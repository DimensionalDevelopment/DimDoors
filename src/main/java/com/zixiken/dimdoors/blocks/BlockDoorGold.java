package com.zixiken.dimdoors.blocks;

import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class BlockDoorGold extends BlockDoor {
    public static final String ID = "doorGold";

	public BlockDoorGold() {
		super(Material.iron);
        setHardness(0.1F);
        setUnlocalizedName(ID);
	}

    @SideOnly(Side.CLIENT)
    protected String getTextureName()
    {
        return DimDoors.modid + ":" + this.getUnlocalizedName();
    }
	
	@Override
	public Item getItemDropped(int par1, Random par2Random, int par3)
    {
        return (par1 & 8) != 0 ? null : DimDoors.itemGoldenDoor;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z) {
        return DimDoors.itemGoldenDoor;
    }
}
