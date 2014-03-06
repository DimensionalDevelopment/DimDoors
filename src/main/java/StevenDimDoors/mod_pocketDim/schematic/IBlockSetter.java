package StevenDimDoors.mod_pocketDim.schematic;

import net.minecraft.world.World;

public interface IBlockSetter
{
	public void setBlock(World world, int x, int y, int z, int blockID, int metadata);
}
