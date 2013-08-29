package StevenDimDoors.mod_pocketDim;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.blocks.ExitDoor;
import StevenDimDoors.mod_pocketDim.core.IDimLink;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TransientDoor extends ExitDoor
{

	protected TransientDoor(int par1, Material material) 
	{
		super(par1, Material.grass);
		//	this.blockIndexInTexture = 18;

		if (properties == null)
			properties = DDProperties.instance();
	}

	private static DDProperties properties = null;

	public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2()+"_top");

	}
	@SideOnly(Side.CLIENT)

	/**
	 * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
	 */
	public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{

		return this.blockIcon;


	}
	public boolean isCollidable()
	{
		return false;
	}

	public void onBlockAdded(World par1World, int par2, int par3, int par4) 
	{
		super.onBlockAdded(par1World, par2, par3, par4);
		this.updateAttachedTile(par1World, par2, par3, par4);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
	{
		return null;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) 
	{
		//TODO: Would it kill us to use REASONABLE variable names? <_< ~SenseiKiwi
		int var12 = (int) (MathHelper.floor_double((double) ((entity.rotationYaw + 90) * 4.0F / 360.0F) + 0.5D) & 3);

		int orientation = world.getBlockMetadata(x, y - 1, z);
		if (!world.isRemote && (orientation >= 4 && orientation <= 7) && (orientation - 4) == var12 &&
				world.getBlockId(x, y - 1, z) == this.blockID)
		{
			this.onPoweredBlockChange(world, x, y, z, false);

			IDimLink link = PocketManager.getLink(x, y, z, world.provider.dimensionId);
			if (link != null)
			{
				//Turn the transient door into a rift before teleporting the entity
				world.setBlock(x, y, z, properties.RiftBlockID);
				world.setBlockToAir(x, y - 1, z);
				PocketManager.traverseDimDoor(world, link, entity);
			}
		}
	}
	
	public int idPicked(World par1World, int par2, int par3, int par4)
	{
		return 0;
	}

	public int idDropped(int par1, Random par2Random, int par3)
	{
		return 0;
	}

	public int getRenderType()
	{
		return 8;
	}
}