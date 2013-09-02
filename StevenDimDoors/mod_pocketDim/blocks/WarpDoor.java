package StevenDimDoors.mod_pocketDim.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.IDimLink;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WarpDoor extends DimensionalDoor
{
	private Icon blockIconBottom;

	public WarpDoor(int blockID, Material material) 
	{
		super(blockID, material);
	}

	public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2()+"_top");
		this.blockIconBottom = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2()+"_bottom");
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) 
	{
		//FIXME: We need to set door generation flags on the tile entities. Ignoring that for now. ~SenseiKiwi

		if (!world.isRemote && world.getBlockId(x, y - 1, z) == this.blockID)
		{
			NewDimData dimension = PocketManager.getDimensionData(world);
			IDimLink link = dimension.getLink(x, y, z);
			if (link == null)
			{
				dimension.createLink(x, y, z, IDimLink.TYPE_SAFE_EXIT);
			}
		}
		world.setBlockTileEntity(x, y, z, this.createNewTileEntity(world));
	}

	@SideOnly(Side.CLIENT)
	/**
	 * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
	 */
	public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		if(par1IBlockAccess.getBlockId(par2, par3-1, par4)==this.blockID)
		{
			return this.blockIcon;
		}
		else
		{
			return this.blockIconBottom;
		}
	}

	public int idPicked(World par1World, int par2, int par3, int par4)
	{
		return Item.doorWood.itemID;
	}

	public int idDropped(int par1, Random par2Random, int par3)
	{
		return (par1 & 8) != 0 ? 0 : (Item.doorWood.itemID);
	}
}