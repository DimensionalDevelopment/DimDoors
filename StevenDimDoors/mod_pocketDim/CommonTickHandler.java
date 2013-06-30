package StevenDimDoors.mod_pocketDim;

import java.util.EnumSet;
import java.util.Random;

import StevenDimDoors.mod_pocketDim.helpers.dimHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;

public class CommonTickHandler implements ITickHandler
{
	private Random rand = new Random();
	public int tickCount=0;
	public int tickCount2=0;
	private static DDProperties properties = null;
	
	public CommonTickHandler()
	{
		if (properties == null)
			properties = DDProperties.instance();
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) 
	{
		if (type.equals(EnumSet.of(TickType.SERVER)))
		{
			onTickInGame();
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		if (type.equals(EnumSet.of(TickType.SERVER)))
		{
		}
	}

	public EnumSet ticks()
	{
		return EnumSet.of(TickType.SERVER);
	}

	public String getLabel()
	{
		return null;
	}

	//replaces rifts in game that have been destroyed/have blocks placed over them. 
	private void onTickInGame()
	{

		try
		{

			if(tickCount>100)
			{
				tickCount=0;
				int i=0;


				while (i<15&&FMLCommonHandler.instance().getEffectiveSide()==Side.SERVER)
				{
					i++;
					LinkData link;

					//actually gets the random rift based on the size of the list
					link = (LinkData) dimHelper.instance.getRandomLinkData(true);



					if(link!=null)
					{

						if(dimHelper.getWorld(link.locDimID)!=null)
						{
							World world=dimHelper.getWorld(link.locDimID);

							int blocktoReplace = world.getBlockId(link.locXCoord, link.locYCoord, link.locZCoord);

							if(!mod_pocketDim.blocksImmuneToRift.contains(blocktoReplace))//makes sure the rift doesn't replace a door or something
							{
								if(dimHelper.instance.getLinkDataFromCoords(link.locXCoord, link.locYCoord, link.locZCoord, link.locDimID)==null)
								{
								}
								else
								{
									dimHelper.getWorld(link.locDimID).setBlock(link.locXCoord, link.locYCoord, link.locZCoord, properties.RiftBlockID);
									TileEntityRift.class.cast(dimHelper.getWorld(link.locDimID).getBlockTileEntity(link.locXCoord, link.locYCoord, link.locZCoord)).hasGrownRifts=true;
								}
							}
						}
					}
				}
			}


		}
		catch (Exception e)
		{
			tickCount++;
			System.out.println("something on tick went wrong: " + e);
			e.printStackTrace();
		}
		tickCount++;

		//this section regulates decay in Limbo- it records any blocks placed by the player and later progresss them through the decay cycle
		if(tickCount2>10&&dimHelper.blocksToDecay!=null)
		{
			tickCount2=0;
			if(!dimHelper.blocksToDecay.isEmpty()&&dimHelper.getWorld(properties.LimboDimensionID)!=null)
			{


				if(dimHelper.blocksToDecay.size()>rand.nextInt(400))
				{
					int index = rand.nextInt(dimHelper.blocksToDecay.size());
					Point3D point = (Point3D) dimHelper.blocksToDecay.get(index);

					int blockID = dimHelper.getWorld(properties.LimboDimensionID).getBlockId(point.getX(), point.getY(), point.getZ());
					int idToSet=Block.stone.blockID;

					if(blockID==0||blockID==properties.LimboBlockID)
					{
						dimHelper.blocksToDecay.remove(index);
					}
					else
					{
						if(Block.blocksList[idToSet] instanceof BlockContainer)
						{
							idToSet=-1;
							dimHelper.blocksToDecay.remove(index); 
						}



						if(blockID==Block.cobblestone.blockID)
						{
							idToSet=Block.gravel.blockID;
						}
						if(blockID==Block.stone.blockID)
						{
							idToSet=Block.cobblestone.blockID;

						}
						if(blockID==Block.gravel.blockID&&!dimHelper.getWorld(properties.LimboDimensionID).isAirBlock(point.getX(), point.getY()-1, point.getZ()))
						{
							idToSet=properties.LimboBlockID;
							dimHelper.getWorld(properties.LimboDimensionID).scheduleBlockUpdate(point.getX(), point.getY(), point.getZ(),10, idToSet);

						}
						else if(blockID==Block.gravel.blockID)
						{
							dimHelper.blocksToDecay.remove(index);
							idToSet=-1;

						}

						if(idToSet!=-1)
						{

							dimHelper.getWorld(properties.LimboDimensionID).setBlock(point.getX(), point.getY(), point.getZ(), idToSet);

						}    		
					}   		    		
				}   		
			}
		}

		tickCount2++;

		if(mod_pocketDim.teleTimer>0)
		{
			mod_pocketDim.teleTimer--;
		}






	}

}
