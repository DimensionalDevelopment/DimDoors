package StevenDimDoors.mod_pocketDim.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DDTeleporter;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.schematic.BlockRotator;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityDimDoor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class BaseDimDoor extends BlockDoor implements IDimDoor, ITileEntityProvider
{
	protected final DDProperties properties;
	private Icon blockIconBottom;
	
	public BaseDimDoor(int blockID, Material material, DDProperties properties) 
	{
		super(blockID, material);

		this.properties = properties;
	}

	public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2()+"_top");
		this.blockIconBottom = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2()+"_bottom");
	}
	
    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(int par1, int par2)
    {
        return this.blockIcon;
    }
    
    

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) 
	{
		this.enterDimDoor(world, x, y, z, entity);
	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{

		boolean shouldOpen=true;

		//System.out.println(String.valueOf(par1World.getBlockMetadata(par2, par3, par4)));
		if(player.inventory.getCurrentItem()!=null)
		{
			if(player.inventory.getCurrentItem().getItem() == mod_pocketDim.itemRiftBlade)
			{
				shouldOpen = false;
				if (!world.isRemote && world.getBlockId(x, y-1, z) == this.blockID)
				{
					int var12 = (int) (MathHelper.floor_double((double)((player.rotationYaw+90) * 4.0F / 360.0F) + 0.5D) & 3);

					if (world.getBlockMetadata(x, y-1, z) == var12)
					{
						var12 = BlockRotator.transformMetadata(var12, 1, this.blockID);
					}
					world.setBlockMetadataWithNotify(x, y-1, z, var12, 2);
				}
				if (!world.isRemote && world.getBlockId(x, y+1, z) == this.blockID)
				{
					int var12 = (int) (MathHelper.floor_double((double)((player.rotationYaw+90) * 4.0F / 360.0F) + 0.5D) & 3);
					if(world.getBlockMetadata(x, y, z)==var12)
					{ 
						var12 = BlockRotator.transformMetadata(var12, 1, this.blockID);
					}
					world.setBlockMetadataWithNotify(x, y, z, var12, 2);
				}
				world.playAuxSFXAtEntity(player, 1001, x, y, z, 0);

				if (!shouldOpen && !world.isRemote)
				{
					player.inventory.getCurrentItem().damageItem(5, player);
				}
			}
		}

		if(shouldOpen)
		{
			int var10 = this.getFullMetadata(world, x, y, z);
			int var11 = var10 & 7;
			var11 ^= 4;

			if ((var10 & 8) == 0)
			{
				world.setBlockMetadataWithNotify(x, y, z, var11,2);
				world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
			}
			else
			{
				world.setBlockMetadataWithNotify(x, y - 1, z, var11,2);
				world.markBlockRangeForRenderUpdate(x, y - 1, z, x, y, z);
			}

			world.playAuxSFXAtEntity(player, 1003, x, y, z, 0);
			return true;
		}
		else 
		{
			return false;
		}

	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) 
	{
		//FIXME: We need to set door generation flags on the tile entities. Ignoring that for now. ~SenseiKiwi
		
		this.placeLink(world, x, y, z);
		world.setBlockTileEntity(x, y, z, this.createNewTileEntity(world));
		this.updateAttachedTile(world, x, y, z);
	}


	/**
	 * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
	 */
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

	//Called to update the render information on the tile entity. Could probably implement a data watcher,
	//but this works fine and is more versatile I think. 
	public BaseDimDoor updateAttachedTile(World world, int x, int y, int z)
	{
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile instanceof TileEntityDimDoor)
		{
			TileEntityDimDoor dimTile = (TileEntityDimDoor) tile;
			dimTile.openOrClosed = PocketManager.getLink(x, y, z, world.provider.dimensionId) != null;
			dimTile.orientation = this.getFullMetadata(world, x, y, z) & 7;
		}
		return this;
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random) 
	{
		TileEntityDimDoor tile = (TileEntityDimDoor) par1World.getBlockTileEntity(par2, par3, par4);
		tile.openOrClosed = this.isDoorOpen( par1World,  par2,  par3,  par4);  	
		tile.orientation = this.getFullMetadata(par1World, par2, par3, par4) & 7;
	}
	
	private void setDoorRotation(int par1)
	{
		float var2 = 0.1875F;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
		int var3 = par1 & 3;
		boolean var4 = (par1 & 4) != 0;
		boolean var5 = (par1 & 16) != 0;

		if (var3 == 0)
		{
			if (var4)
			{
				if (!var5)
				{
					this.setBlockBounds(0.001F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
				}
				else
				{
					this.setBlockBounds(0.001F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
				}
			}
			else
			{
				this.setBlockBounds(0.0F, 0.0F, 0.0F, var2, 1.0F, 1.0F);
			}
		}
		else if (var3 == 1)
		{
			if (var4)
			{
				if (!var5)
				{
					this.setBlockBounds(1.0F - var2, 0.0F, 0.001F, 1.0F, 1.0F, 1.0F);
				}
				else
				{
					this.setBlockBounds(0.0F, 0.0F, 0.001F, var2, 1.0F, 1.0F);
				}
			}
			else
			{
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
			}
		}
		else if (var3 == 2)
		{
			if (var4)
			{
				if (!var5)
				{
					this.setBlockBounds(0.0F, 0.0F, 1.0F - var2, .99F, 1.0F, 1.0F);
				}
				else
				{
					this.setBlockBounds(0.0F, 0.0F, 0.0F, .99F, 1.0F, var2);
				}
			}
			else
			{
				this.setBlockBounds(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			}
		}
		else if (var3 == 3)
		{
			if (var4)
			{
				if (!var5)
				{
					this.setBlockBounds(0.0F, 0.0F, 0.0F, var2, 1.0F, 0.99F);
				}
				else
				{
					this.setBlockBounds(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 0.99F);
				}
			}
			else
			{
				this.setBlockBounds(0.0F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
			}
		}
	}


	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor blockID
	 */
	@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
	{
		int var6 = par1World.getBlockMetadata(par2, par3, par4);

		if ((var6 & 8) == 0)
		{
			boolean var7 = false;

			if (par1World.getBlockId(par2, par3 + 1, par4) != this.blockID)
			{
				par1World.setBlock(par2, par3, par4, 0);
				var7 = true;
			}

			/**
            if (!par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4))
            {
                par1World.setBlockWithNotify(par2, par3, par4, 0);
                var7 = true;

                if (par1World.getBlockId(par2, par3 + 1, par4) == this.blockID)
                {
                    par1World.setBlockWithNotify(par2, par3 + 1, par4, 0);
                }
            }
			 **/

			if (var7)
			{
				if (!par1World.isRemote)
				{
					this.dropBlockAsItem(par1World, par2, par3, par4, properties.DimensionalDoorID, 0);
				}
			}
			else
			{
				boolean var8 = par1World.isBlockIndirectlyGettingPowered(par2, par3, par4) || par1World.isBlockIndirectlyGettingPowered(par2, par3 + 1, par4);

				if ((var8 || par5 > 0 && Block.blocksList[par5].canProvidePower()) && par5 != this.blockID)
				{
					this.onPoweredBlockChange(par1World, par2, par3, par4, var8);
				}
			}
		}
		else
		{
			if (par1World.getBlockId(par2, par3 - 1, par4) != this.blockID)
			{
				par1World.setBlock(par2, par3, par4, 0);
			}

			if (par5 > 0 && par5 != this.blockID)
			{
				this.onNeighborBlockChange(par1World, par2, par3 - 1, par4, par5);
			}
		}
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public int idPicked(World par1World, int par2, int par3, int par4)
	{
		return this.getDrops();
	}

	@Override
	public int idDropped(int par1, Random par2Random, int par3)
	{
		return (par1 & 8) != 0 ? 0 : (getDrops());
	}

	/**
	 * Called when the block is attempted to be harvested
	 */
	@Override
	public void onBlockHarvested(World par1World, int par2, int par3, int par4, int par5, EntityPlayer par6EntityPlayer)
	{
		if (par6EntityPlayer.capabilities.isCreativeMode && (par5 & 8) != 0 && par1World.getBlockId(par2, par3 - 1, par4) == this.blockID)
		{
			par1World.setBlock(par2, par3 - 1, par4, 0);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TileEntityDimDoor();
	}

	@Override
	public void enterDimDoor(World world, int x, int y, int z, Entity entity) 
	{
		// We need to ignore particle entities
		if (world.isRemote || entity instanceof EntityFX)
		{
			return;
		}
		
		// Check that this is the top block of the door
		if (world.getBlockId(x, y - 1, z) == this.blockID)
		{
			int metadata = world.getBlockMetadata(x, y - 1, z);
			boolean canUse = isDoorOpen(metadata);
			if (canUse && entity instanceof EntityLiving)
			{
				// Don't check for non-living entities since it might not work right
				canUse = isEntityFacingDoor(metadata, (EntityLiving) entity);
			}
			if (canUse)
			{
				// Teleport the entity through the link, if it exists
				DimLink link = PocketManager.getLink(x, y, z, world.provider.dimensionId);
				if (link != null)
				{
					DDTeleporter.traverseDimDoor(world, link, entity);
				}
				// Close the door only after the entity goes through
				// so players don't have it slam in their faces.
				this.onPoweredBlockChange(world, x, y, z, false);
			}
		}
		else if (world.getBlockId(x, y + 1, z) == this.blockID)
		{
			enterDimDoor(world, x, y + 1, z, entity);
		}
	}
	
	@Override
	public int getDrops()
	{
		return this.blockID;
	}
	
	protected static boolean isDoorOpen(int metadata)
	{
		return (metadata & 4) != 0;
	}
	
	protected static boolean isEntityFacingDoor(int metadata, EntityLiving entity)
	{
		// Although any entity has the proper fields for this check,
		// we should only apply it to living entities since things
		// like Minecarts might come in backwards.
		int direction = (int) (MathHelper.floor_double((double) ((entity.rotationYaw + 90) * 4.0F / 360.0F) + 0.5D) & 3);
		return ((metadata & 3) == direction);
	}
}