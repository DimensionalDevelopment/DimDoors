package StevenDimDoors.mod_pocketDim.schematic;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class WorldBlockSetter implements IBlockSetter
{
	public final int BLOCK_UPDATES_FLAG = 1;
	public final int NOTIFY_CLIENT_FLAG = 2;
	
	private int flags;
	private boolean ignoreAir;
	
	public WorldBlockSetter(boolean doBlockUpdates, boolean notifyClients, boolean ignoreAir)
	{
		this.flags = 0;
		this.flags += doBlockUpdates ? BLOCK_UPDATES_FLAG : 0;
		this.flags += notifyClients ? NOTIFY_CLIENT_FLAG : 0;
		this.ignoreAir = ignoreAir;
	}
	
	public void setBlock(World world, int x, int y, int z, int blockID, int metadata)
	{
		if (!ignoreAir || blockID != 0)
		{
			world.setBlock(x, y, z, blockID, metadata, flags);
		}
	}
}
