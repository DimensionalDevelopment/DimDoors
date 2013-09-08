package StevenDimDoors.mod_pocketDim.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DDTeleporter;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkTypes;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.world.PocketProvider;

public class TransTrapdoor extends BlockTrapDoor implements IDimDoor
{

	public TransTrapdoor(int blockID, Material material) 
	{
		super(blockID, material);
		this.setCreativeTab(mod_pocketDim.dimDoorsCreativeTab);
	}

	@Override
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2());
	}

	//Teleports the player to the exit link of that dimension, assuming it is a pocket
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		enterDimDoor(world, x, y, z, entity);
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
				DDTeleporter.traverseDimDoor(world, link, entity);
			}
		}
	}	

	@Override
	public void onBlockAdded(World world, int x, int y, int z) 
	{
		this.placeDimDoor(world, x, y, z);
		//world.setBlockTileEntity(x, y, z, this.createNewTileEntity(world));
		//this.updateAttachedTile(world, x, y, z);
	}

	
	@Override
	public void placeDimDoor(World world, int x, int y, int z) 
	{
		if (!world.isRemote)
		{
			NewDimData dimension = PocketManager.getDimensionData(world);
			DimLink link = dimension.getLink(x, y, z);
			if (link == null && dimension.isPocketDimension())
			{
				dimension.createLink(x, y, z, LinkTypes.UNSAFE_EXIT);
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
}