package StevenDimDoors.mod_pocketDim.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
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
	
	@SideOnly(Side.CLIENT)
    private Icon[] upperTextures;
    @SideOnly(Side.CLIENT)
    private Icon[] lowerTextures;
	
	public BaseDimDoor(int blockID, Material material, DDProperties properties) 
	{
		super(blockID, material);
		
		this.properties = properties;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister)
	{
		upperTextures = new Icon[2];
        lowerTextures = new Icon[2];
        upperTextures[0] = iconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName() + "_upper");
        lowerTextures[0] = iconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName() + "_lower");
        upperTextures[1] = new IconFlipped(upperTextures[0], true, false);
        lowerTextures[1] = new IconFlipped(lowerTextures[0], true, false);
	}
	
    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int metadata)
    {
        return this.upperTextures[0];
    }
    
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) 
	{
		this.enterDimDoor(world, x, y, z, entity);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		final int MAGIC_CONSTANT = 1003;
		
		int metadata = this.getFullMetadata(world, x, y, z);
		int lowMeta = metadata & 7;
		lowMeta ^= 4;

		if (isUpperDoorBlock(metadata))
		{
			world.setBlockMetadataWithNotify(x, y - 1, z, lowMeta, 2);
			world.markBlockRangeForRenderUpdate(x, y - 1, z, x, y, z);			
		}
		else
		{
			world.setBlockMetadataWithNotify(x, y, z, lowMeta, 2);
			world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
		}

		world.playAuxSFXAtEntity(player, MAGIC_CONSTANT, x, y, z, 0);
		return true;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) 
	{
		this.placeLink(world, x, y, z);
		world.setBlockTileEntity(x, y, z, this.createNewTileEntity(world));
		this.updateAttachedTile(world, x, y, z);
	}

	/**
     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
     */
	@Override
    @SideOnly(Side.CLIENT)
    public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side)
    {
        if (side != 1 && side != 0)
        {
            int fullMetadata = this.getFullMetadata(blockAccess, x, y, z);
            int orientation = fullMetadata & 3;
            boolean reversed = false;

            if (isDoorOpen(fullMetadata))
            {
                if (orientation == 0 && side == 2)
                {
                    reversed = !reversed;
                }
                else if (orientation == 1 && side == 5)
                {
                    reversed = !reversed;
                }
                else if (orientation == 2 && side == 3)
                {
                    reversed = !reversed;
                }
                else if (orientation == 3 && side == 4)
                {
                    reversed = !reversed;
                }
            }
            else
            {
                if (orientation == 0 && side == 5)
                {
                    reversed = !reversed;
                }
                else if (orientation == 1 && side == 3)
                {
                    reversed = !reversed;
                }
                else if (orientation == 2 && side == 4)
                {
                    reversed = !reversed;
                }
                else if (orientation == 3 && side == 2)
                {
                    reversed = !reversed;
                }

                if ((fullMetadata & 16) != 0)
                {
                    reversed = !reversed;
                }
            }

            if (isUpperDoorBlock(fullMetadata))
            	return this.upperTextures[reversed ? 1 : 0];
            else
            	return this.lowerTextures[reversed ? 1 : 0];
        }
        else
        {
            return this.lowerTextures[0];
        }
    }

	//Called to update the render information on the tile entity. Could probably implement a data watcher,
	//but this works fine and is more versatile I think. 
	public BaseDimDoor updateAttachedTile(World world, int x, int y, int z)
	{
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile instanceof TileEntityDimDoor)
		{
			int metadata = world.getBlockMetadata(x, y, z);
			TileEntityDimDoor dimTile = (TileEntityDimDoor) tile;
			dimTile.openOrClosed = this.isDoorOnRift(world, x, y, z)&&this.isUpperDoorBlock(metadata);
			dimTile.orientation = this.getFullMetadata(world, x, y, z) & 7;
		}
		return this;
	}
	
	public boolean isDoorOnRift(World world, int x, int y, int z)
	{
		if(this.isUpperDoorBlock( world.getBlockMetadata(x, y, z)))
		{
			if(PocketManager.getLink(x, y, z, world.provider.dimensionId) != null||PocketManager.getLink(x, y-1, z, world.provider.dimensionId) != null)
			{
				return true;
			}
		}
		else
		{
			if(PocketManager.getLink(x, y, z, world.provider.dimensionId) != null||PocketManager.getLink(x, y+1, z, world.provider.dimensionId) != null)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random) 
	{
		this.updateAttachedTile(par1World, par2, par3, par4);
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
	{
		this.setDoorRotation(this.getFullMetadata(par1IBlockAccess, par2, par3, par4));
	}
	
	
	public void setDoorRotation(int par1)
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
	public void onNeighborBlockChange(World world, int x, int y, int z, int neighborID)
	{
		int metadata = world.getBlockMetadata(x, y, z);
		if (isUpperDoorBlock(metadata))
		{
			if (world.getBlockId(x, y - 1, z) != this.blockID)
			{
				world.setBlock(x, y, z, 0);
			}
			
			if (neighborID > 0 && neighborID != this.blockID)
			{
				this.onNeighborBlockChange(world, x, y - 1, z, neighborID);
			}
		}
		else
		{
			if (world.getBlockId(x, y + 1, z) != this.blockID)
			{
				world.setBlock(x, y, z, 0);
				if (!world.isRemote)
				{
					this.dropBlockAsItem(world, x, y, z, metadata, 0);
				}
			}
			else
			{
				boolean powered = world.isBlockIndirectlyGettingPowered(x, y, z) || world.isBlockIndirectlyGettingPowered(x, y + 1, z);
				if ((powered || neighborID > 0 && Block.blocksList[neighborID].canProvidePower()) && neighborID != this.blockID)
				{
					this.onPoweredBlockChange(world, x, y, z, powered);
				}
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

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int metadata, Random random, int fortune)
    {
        return isUpperDoorBlock(metadata) ? 0 : this.getDrops();
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
		// FX entities dont exist on the server
		if (world.isRemote)
		{
			return;
		}
		
		// Check that this is the top block of the door
		if (world.getBlockId(x, y - 1, z) == this.blockID)
		{
			int metadata = world.getBlockMetadata(x, y - 1, z);
			boolean canUse = isDoorOpen(metadata);
			if (canUse && entity instanceof EntityPlayer)
			{
				// Dont check for non-player entites
				canUse = isEntityFacingDoor(metadata, (EntityLivingBase) entity);
			}
			if (canUse)
			{
				// Teleport the entity through the link, if it exists
				DimLink link = PocketManager.getLink(x, y, z, world.provider.dimensionId);
				if (link != null)
				{
					try
					{
						DDTeleporter.traverseDimDoor(world, link, entity, this);
					}
					catch (Exception e)
					{
						System.err.println("Something went wrong teleporting to a dimension:");
						e.printStackTrace();
					}
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
	
	public static boolean isUpperDoorBlock(int metadata)
	{
		return (metadata & 8) != 0;
	}
	
	public static boolean isDoorOpen(int metadata)
	{
		return (metadata & 4) != 0;
	}
	
	protected static boolean isEntityFacingDoor(int metadata, EntityLivingBase entity)
	{
		// Although any entity has the proper fields for this check,
		// we should only apply it to living entities since things
		// like Minecarts might come in backwards.
		int direction = MathHelper.floor_double((entity.rotationYaw + 90) * 4.0F / 360.0F + 0.5D) & 3;
		return ((metadata & 3) == direction);
	}
	
	@Override
	public TileEntity initDoorTE(World world, int x, int y, int z)
	{
		TileEntity te = this.createNewTileEntity(world);
		world.setBlockTileEntity(x, y, z, te);
		return te;
	}
}