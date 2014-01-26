package StevenDimDoors.mod_pocketDim.ticking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.util.ChunkLocation;
import StevenDimDoors.mod_pocketDim.util.Point4D;

public class FastRiftRegenerator implements IRegularTickReceiver {
	
	private static final int RIFT_REGENERATION_INTERVAL = 10; //Regenerate random rifts every 200 ticks
	private ArrayList<Point4D> locationsToRegen=new ArrayList<Point4D>();
	
	public FastRiftRegenerator(IRegularTickSender sender)
	{
		sender.registerForTicking(this, RIFT_REGENERATION_INTERVAL, false);
	}
	
	@Override
	public void notifyTick()
	{
		regenerateRiftsInAllWorlds();
	}
	
	public void regenerateRiftsInAllWorlds()
	{
		if(this.locationsToRegen.isEmpty())
		{
			return;
		}
		List<Integer> loadedWorlds = (List<Integer>)Arrays.asList(DimensionManager.getIDs());
		
		for(Point4D point: this.locationsToRegen)
		{
			if(loadedWorlds.contains(point.getDimension())&&PocketManager.getLink(point)!=null)
			{
	    		World world = DimensionManager.getWorld(point.getDimension());
	    	
	    		if(point!=null)
	    		{
	    			if (!mod_pocketDim.blockRift.isBlockImmune(world, point.getX(), point.getY(), point.getZ())&& world.getChunkProvider().chunkExists(point.getX() >> 4, point.getZ() >> 4))
					{
						world.setBlock(point.getX(), point.getY(), point.getZ(), mod_pocketDim.blockRift.blockID);
					}
	    		}
	    		
			}
		}
		this.locationsToRegen.clear();
	}
	public void registerRiftForRegen(int x, int y, int z, int dimID)
	{
		this.locationsToRegen.add(new Point4D(x,y,z,dimID));
	}
	
}
