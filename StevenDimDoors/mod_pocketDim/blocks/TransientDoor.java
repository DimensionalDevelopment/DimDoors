package StevenDimDoors.mod_pocketDim.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.core.DDTeleporter;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkTypes;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;

public class TransientDoor extends BaseDimDoor
{
	public TransientDoor(int blockID, Material material, DDProperties properties) 
	{
		super(blockID, material, properties);
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
			boolean canUse = true;
			int metadata = world.getBlockMetadata(x, y - 1, z);
			if (canUse && entity instanceof EntityLiving)
			{
				// Don't check for non-living entities since it might not work right
				canUse = BaseDimDoor.isEntityFacingDoor(metadata, (EntityLiving) entity);
			}
			if (canUse)
			{
				// Teleport the entity through the link, if it exists
				DimLink link = PocketManager.getLink(x, y, z, world.provider.dimensionId);
				if (link != null)
				{
					DDTeleporter.traverseDimDoor(world, link, entity);
					// Turn the door into a rift AFTER teleporting the player.
					// The door's orientation may be necessary for the teleport.
					world.setBlock(x, y, z, properties.RiftBlockID);
					world.setBlockToAir(x, y - 1, z);
				}
			}
		}
		else if (world.getBlockId(x, y + 1, z) == this.blockID)
		{
			enterDimDoor(world, x, y + 1, z, entity);
		}
	}	

	@Override
	public void placeLink(World world, int x, int y, int z) 
	{
		if (!world.isRemote && world.getBlockId(x, y - 1, z) == this.blockID)
		{
			NewDimData dimension = PocketManager.getDimensionData(world);
			DimLink link = dimension.getLink(x, y, z);
			if (link == null && dimension.isPocketDimension())
			{
				dimension.createLink(x, y, z, LinkTypes.SAFE_EXIT);
			}
		}
	}

	@Override
	public int getDrops()
	{
		return 0;
	}

	@Override
	public boolean isCollidable()
	{
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
	{
		return null;
	}

	@Override
	public int getRenderType()
	{
		return 8;
	}
}