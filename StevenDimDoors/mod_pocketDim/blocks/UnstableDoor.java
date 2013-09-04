package StevenDimDoors.mod_pocketDim.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.LinkTypes;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class UnstableDoor extends DimensionalDoor
{
	private Icon blockIconBottom;
	private static DDProperties properties = null;

	public UnstableDoor(int par1, Material material) 
	{
		super(par1, material);
		if (properties == null)
			properties = DDProperties.instance();
	}

	public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2()+"_top");
		this.blockIconBottom = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2()+"_bottom");
	}

	@SideOnly(Side.CLIENT)

	/**
	 * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
	 */
	public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		if (par1IBlockAccess.getBlockId(par2, par3 - 1, par4) == this.blockID)
		{
			return this.blockIcon;
		}
		else
		{
			return this.blockIconBottom;
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) 
	{
		if (!world.isRemote && world.getBlockId(x, y - 1, z) == this.blockID)
		{
			NewDimData dimension = PocketManager.getDimensionData(world);
			dimension.createLink(x, y, z, LinkTypes.RANDOM);
		}
	}
}