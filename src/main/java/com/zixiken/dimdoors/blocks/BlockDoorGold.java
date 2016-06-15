package com.zixiken.dimdoors.blocks;

import java.util.Random;

import com.zixiken.dimdoors.mod_pocketDim;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
        return mod_pocketDim.modid + ":" + this.getUnlocalizedName();
    }
	
	@Override
	public Item getItemDropped(int par1, Random par2Random, int par3)
    {
        return (par1 & 8) != 0 ? null : mod_pocketDim.itemGoldenDoor;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z) {
        return mod_pocketDim.itemGoldenDoor;
    }
}
