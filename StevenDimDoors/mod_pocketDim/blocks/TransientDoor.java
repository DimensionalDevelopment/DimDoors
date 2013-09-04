package StevenDimDoors.mod_pocketDim.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DDTeleporter;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TransientDoor extends WarpDoor
{
	public TransientDoor(int blockID, Material material) 
	{
		super(blockID, material);
	}

	public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2()+"_top");
	}

	/**
	 * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
	 */
	@SideOnly(Side.CLIENT)
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

			DimLink link = PocketManager.getLink(x, y, z, world.provider.dimensionId);
			if (link != null)
			{
				DDTeleporter.traverseDimDoor(world, link, entity);
				//Turn the transient door into a rift AFTER teleporting the entity.
				//The door's orientation may be needed for generating a room at the link's destination.
				world.setBlock(x, y, z, properties.RiftBlockID);
				world.setBlockToAir(x, y - 1, z);
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