package StevenDimDoors.mod_pocketDim.ticking;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.util.Point4D;

public class RiftRegenerator implements IRegularTickReceiver {
	
	private static final int RIFT_REGENERATION_INTERVAL = 200; //Regenerate random rifts every 200 ticks
	private static final int RIFTS_REGENERATED_PER_DIMENSION = 5;
	private static Random random = new Random();
	
	public RiftRegenerator(IRegularTickSender sender)
	{
		sender.registerForTicking(this, RIFT_REGENERATION_INTERVAL, false);
	}
	
	@Override
	public void notifyTick()
	{
		regenerateRiftsInLoadedWorlds();
	}
	
	private static void regenerateRiftsInLoadedWorlds()
	{
		// Regenerate rifts that have been replaced (not permanently removed) by players
		// Only do this in dimensions that are currently loaded
		List<Integer> loadedWorlds = Arrays.asList(DimensionManager.getIDs());
		for (Integer dimensionID : loadedWorlds)
    	{
			NewDimData dimension = PocketManager.getDimensionData(dimensionID);
			if (dimension.linkCount() > 0)
			{
	    		World world = DimensionManager.getWorld(dimension.id());
	    		
	    		if (world != null)
	    		{
	    			for (int count = 0; count < RIFTS_REGENERATED_PER_DIMENSION; count++)
	    			{
						DimLink link = dimension.getRandomLink();
	    				Point4D source = link.source();
	    				mod_pocketDim.blockRift.regenerateRift(world, source.getX(), source.getY(), source.getZ(), random);
	    			}
	    		}
			}
    	}
	}
}
