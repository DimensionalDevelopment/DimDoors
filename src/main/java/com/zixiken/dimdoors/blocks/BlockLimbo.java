package com.zixiken.dimdoors.blocks;

import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.config.DDProperties;
import com.zixiken.dimdoors.world.LimboDecay;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLimbo extends Block {
	public static final String ID = "blockLimbo";

	private final int limboDimensionID;
	private final LimboDecay decay;
	
	public BlockLimbo(LimboDecay decay) {
		super(Material.ground);
		limboDimensionID = DDProperties.instance().LimboDimensionID;
		this.decay = decay;
		setTickRandomly(true);
		setCreativeTab(DimDoors.dimDoorsCreativeTab);
        setHardness(.2F);
        setUnlocalizedName(ID);
        setLightLevel(.0F);
	}

	/**
	 * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side)
	{
		return this.getIcon(side, blockAccess.getBlockMetadata(x, y, z));
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		this.blockIcon = iconRegister.registerIcon(DimDoors.modid + ":" + this.getUnlocalizedName());
	}

	@Override
	public IIcon getIcon(int par1, int par2)
	{
		return this.blockIcon;
	}
	
	/**
	 * If the block is in Limbo, attempt to decay surrounding blocks upon receiving a random update tick.
	 */
    @Override
    public void updateTick(World world, int x, int y, int z, Random random)
    {
    	//Make sure this block is in Limbo
    	if (world.provider.dimensionId == limboDimensionID)
    	{
    		decay.applySpreadDecay(world, x, y, z);
    	}
    }
}
