package StevenDimDoors.mod_pocketDim.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.world.LimboDecay;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLimbo extends Block
{
	private final int limboDimensionID;
	private final LimboDecay decay;
	
	public BlockLimbo(int j, Material par2Material, int limboDimensionID, LimboDecay decay)
	{
		super(Material.ground);
		this.limboDimensionID = limboDimensionID;
		this.decay = decay;
		this.setTickRandomly(true);
		this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
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
		this.blockIcon = iconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName());
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
