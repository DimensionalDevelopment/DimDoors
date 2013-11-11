package StevenDimDoors.mod_pocketDim.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;

public class BlockDoorGold extends BlockDoor
{

	private Icon blockIconBottom;
	@SuppressWarnings("unused") // ??
	private DDProperties properties;

	public BlockDoorGold(int par1, Material par2Material,DDProperties properties) 
	{
		super(par1, par2Material);
		this.properties=properties;

	}
	
	@Override
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName()+"_top");
		this.blockIconBottom = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName()+"_bottom");
	} 
	
	public int idDropped(int par1, Random par2Random, int par3)
	{
		return mod_pocketDim.itemGoldDoor.itemID;
	}
	  
	@Override
	public Icon getIcon(int par1, int par2)
	{
		return this.blockIcon;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		if(par1IBlockAccess.getBlockId(par2, par3-1, par4) == this.blockID)
		{
			return this.blockIcon;
		}
		else
		{
			return blockIconBottom;
		}
	}
}
