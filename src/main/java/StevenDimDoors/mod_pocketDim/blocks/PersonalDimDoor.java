package StevenDimDoors.mod_pocketDim.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkTypes;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;

public class PersonalDimDoor extends BaseDimDoor
{

	public PersonalDimDoor(int blockID, Material material, DDProperties properties)
	{
		super(blockID, material, properties);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void placeLink(World world, int x, int y, int z)
	{
		if (!world.isRemote && world.getBlockId(x, y - 1, z) == this.blockID)
		{
			NewDimData dimension = PocketManager.getDimensionData(world);
			DimLink link = dimension.getLink(x, y, z);
			if (link == null)
			{
				dimension.createLink(x, y, z, LinkTypes.PERSONAL, world.getBlockMetadata(x, y - 1, z));
			}
		}
	}

	@Override
	public int getDrops()
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
