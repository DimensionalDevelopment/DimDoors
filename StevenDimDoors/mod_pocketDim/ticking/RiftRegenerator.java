package StevenDimDoors.mod_pocketDim.ticking;

import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.TileEntityRift;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class RiftRegenerator implements IRegularTickReceiver {
	
	private static final int RIFT_REGENERATION_INTERVAL = 100; //Regenerate random rifts every 100 ticks

	private DDProperties properties;
	
	public RiftRegenerator(IRegularTickSender sender, DDProperties properties)
	{
		sender.registerForTicking(this, RIFT_REGENERATION_INTERVAL, false);
		this.properties = properties;
	}
	
	@Override
	public void notifyTick()
	{
		regenerate();
	}

	private void regenerate()
	{
		try
		{
			//Regenerate rifts that have been replaced (not permanently removed) by players

			int i = 0;

			while (i < 15 && FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
			{
				i++;
				LinkData link;

				//actually gets the random rift based on the size of the list
				link = (LinkData) dimHelper.instance.getRandomLinkData(true);

				if (link != null)
				{
					World world = dimHelper.getWorld(link.locDimID);

					if (world != null && !mod_pocketDim.blockRift.isBlockImmune(world, link.locXCoord, link.locYCoord, link.locZCoord))
					{
						if (dimHelper.instance.getLinkDataFromCoords(link.locXCoord, link.locYCoord, link.locZCoord, link.locDimID) != null)
						{
							world.setBlock(link.locXCoord, link.locYCoord, link.locZCoord, properties.RiftBlockID);
							TileEntityRift rift = (TileEntityRift) world.getBlockTileEntity(link.locXCoord, link.locYCoord, link.locZCoord);
							if (rift == null)
							{
								dimHelper.getWorld(link.locDimID).setBlockTileEntity(link.locXCoord, link.locYCoord, link.locZCoord, new TileEntityRift());
							}
							rift.hasGrownRifts = true;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			System.err.println("An exception occurred in RiftRegenerator.regenerate():");
			e.printStackTrace();
		}
	}
}
