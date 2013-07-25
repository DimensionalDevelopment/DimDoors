package StevenDimDoors.mod_pocketDim.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLimbo extends Block
{
	public BlockLimbo(int i, int j, Material par2Material) 
	{
		super(i, Material.ground);
		this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);       
	}

	/**
	 * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side)
	{
		return this.getIcon(side, blockAccess.getBlockMetadata(x, y, z));
	}

	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		this.blockIcon = iconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2());
	}

	@Override
	public Icon getIcon(int par1, int par2)
	{
		return this.blockIcon;
	}
}
