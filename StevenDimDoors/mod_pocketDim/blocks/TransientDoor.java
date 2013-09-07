package StevenDimDoors.mod_pocketDim.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
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
		//TODO: Would it kill us to use REASONABLE variable names? <_< ~SenseiKiwi
		int var12 = (int) (MathHelper.floor_double((double) ((entity.rotationYaw + 90) * 4.0F / 360.0F) + 0.5D) & 3);

		int orientation = world.getBlockMetadata(x, y - 1, z);
		if (!world.isRemote && orientation == var12 && world.getBlockId(x, y - 1, z) == this.blockID)
		{
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

	@Override
	public void placeDimDoor(World world, int x, int y, int z) 
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