package StevenDimDoors.mod_pocketDim.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DDTeleporter;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkTypes;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.items.ItemDDKey;
import StevenDimDoors.mod_pocketDim.tileentities.TileEntityTransTrapdoor;

public class TransTrapdoor extends BlockTrapDoor implements IDimDoor, ITileEntityProvider
{

	public TransTrapdoor(int blockID, Material material) 
	{
		super(blockID, material);
		this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
	}

	@Override
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName());
	}

	//Teleports the player to the exit link of that dimension, assuming it is a pocket
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		enterDimDoor(world, x, y, z, entity);
	}

	public boolean checkCanOpen(World world, int x, int y, int z)
	{
		return this.checkCanOpen(world, x, y, z, null);
	}
	
	public boolean checkCanOpen(World world, int x, int y, int z, EntityPlayer player)
	{
		DimLink link = PocketManager.getLink( x, y,z, world);
		if(link==null||player==null)
		{
			return link==null;
		}
		if(!link.isLocked())
		{
			return true;
		}
		
		for(ItemStack item : player.inventory.mainInventory)
		{
			if(item != null)
			{
				if(item.getItem() instanceof ItemDDKey)
				{
					if(link.open(item))
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
		if(this.checkCanOpen(par1World, par3, par3, par4, par5EntityPlayer))
		{
			return super.onBlockActivated(par1World, par2, par3, par4, par5EntityPlayer, par6, par7, par8, par9);
		}
		return false;
    }

    public void onPoweredBlockChange(World par1World, int par2, int par3, int par4, boolean par5)
    {
    	if(this.checkCanOpen(par1World, par2, par3, par4))
    	{
    		super.onPoweredBlockChange(par1World, par2, par3, par4, par5);
    	}
    }
	@Override
	public void enterDimDoor(World world, int x, int y, int z, Entity entity) 
	{
		if (!world.isRemote && isTrapdoorOpen(world.getBlockMetadata(x, y, z)))
		{
			this.onPoweredBlockChange(world, x, y, z, false);
			
			DimLink link = PocketManager.getLink(x, y, z, world);
			if (link != null)
			{
				DDTeleporter.traverseDimDoor(world, link, entity,this);
			}
		}
	}	

	@Override
	public void onBlockAdded(World world, int x, int y, int z) 
	{
		this.placeLink(world, x, y, z);
		world.setBlockTileEntity(x, y, z, this.createNewTileEntity(world));
		this.updateAttachedTile(world, x, y, z);
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random random) 
	{
		TileEntityTransTrapdoor tile = (TileEntityTransTrapdoor) world.getBlockTileEntity(x, y, z);
		tile.hasRift = PocketManager.getLink(x, y, z, world) != null;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) 
	{
		return new TileEntityTransTrapdoor();
	}

	public void updateAttachedTile(World world, int x, int y, int z)
	{
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile instanceof TileEntityTransTrapdoor)
		{
			TileEntityTransTrapdoor trapdoorTile = (TileEntityTransTrapdoor) tile;
			trapdoorTile.hasRift = (PocketManager.getLink(x, y, z, world) != null);
		}
	}
	
	@Override
	public void placeLink(World world, int x, int y, int z) 
	{
		if (!world.isRemote)
		{
			NewDimData dimension = PocketManager.getDimensionData(world);
			DimLink link = dimension.getLink(x, y, z);
			if (link == null && dimension.isPocketDimension())
			{
				dimension.createLink(x, y, z, LinkTypes.UNSAFE_EXIT,0);
			}
		}
	}

	
	@Override
	public int idDropped(int metadata, Random random, int fortuneLevel)
    {
        return getDrops();
    }

	@Override
	public int getDrops()
	{
		return  Block.trapdoor.blockID;
	}	
	
	public static boolean isTrapdoorSetLow(int metadata)
	{
		return (metadata & 8) == 0;
	}
	
	@Override
	public TileEntity initDoorTE(World world, int x, int y, int z)
	{
		TileEntity te = this.createNewTileEntity(world);
		world.setBlockTileEntity(x, y, z, te);
		return te;
	}

	@Override
	public boolean isDoorOnRift(World world, int x, int y, int z)
	{
		return PocketManager.getLink(x, y, z, world)!=null;
	}
}