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
	
	public RiftRegenerator(IRegularTickSender sender)
	{
		sender.registerForTicking(this, RIFT_REGENERATION_INTERVAL, false);
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

				if(link!=null)
				{

					if (dimHelper.getWorld(link.locDimID)!=null)
					{
						World world = dimHelper.getWorld(link.locDimID);

						int blocktoReplace = world.getBlockId(link.locXCoord, link.locYCoord, link.locZCoord);

						if(!mod_pocketDim.blocksImmuneToRift.contains(blocktoReplace))//makes sure the rift doesn't replace a door or something
						{
							if(dimHelper.instance.getLinkDataFromCoords(link.locXCoord, link.locYCoord, link.locZCoord, link.locDimID) != null)
							{
								dimHelper.getWorld(link.locDimID).setBlock(link.locXCoord, link.locYCoord, link.locZCoord, properties.RiftBlockID);
								TileEntityRift.class.cast(dimHelper.getWorld(link.locDimID).getBlockTileEntity(link.locXCoord, link.locYCoord, link.locZCoord)).hasGrownRifts=true;
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("An exception occurred in RiftRegenerator.regenerate():");
			e.printStackTrace();
		}
	}
}
