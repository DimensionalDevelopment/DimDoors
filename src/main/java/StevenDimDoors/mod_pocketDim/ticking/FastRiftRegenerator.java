package StevenDimDoors.mod_pocketDim.ticking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.util.Point4D;

public class FastRiftRegenerator implements IRegularTickReceiver {
	
	private static final int RIFT_REGENERATION_INTERVAL = 10; //Regenerate scheduled rifts every 10 ticks
	private static Random random = new Random();

	private ArrayList<Point4D> locationsToRegen = new ArrayList<Point4D>();
	
	public FastRiftRegenerator(IRegularTickSender sender)
	{
		sender.registerReceiver(this, RIFT_REGENERATION_INTERVAL, false);
	}
	
	@Override
	public void notifyTick()
	{
		regenerateScheduledRifts();
	}
	
	public void regenerateScheduledRifts()
	{
		if (!locationsToRegen.isEmpty())
		{
			@SuppressWarnings("cast")
			List<Integer> loadedWorlds = (List<Integer>) Arrays.asList(DimensionManager.getIDs());
			for (Point4D point: locationsToRegen)
			{
				if (loadedWorlds.contains(point.getDimension()) && PocketManager.getLink(point) != null)
				{
		    		World world = DimensionManager.getWorld(point.getDimension());
	    			mod_pocketDim.blockRift.regenerateRift(world, point.getX(), point.getY(), point.getZ(), random);
				}
			}
			locationsToRegen.clear();
		}
	}
	
	public void registerRiftForRegen(int x, int y, int z, int dimID)
	{
		this.locationsToRegen.add(new Point4D(x, y, z, dimID));
	}
}
