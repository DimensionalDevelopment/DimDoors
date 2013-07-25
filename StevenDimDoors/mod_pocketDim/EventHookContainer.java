package StevenDimDoors.mod_pocketDim;

import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.world.WorldEvent;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EventHookContainer
{
	private static DDProperties properties = null;
	
	public EventHookContainer()
	{
		if (properties == null)
			properties = DDProperties.instance();
	}
	
	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onSoundLoad(SoundLoadEvent event) 
	{
		event.manager.soundPoolSounds.addSound("mods/DimDoors/sfx/monk.ogg", (mod_pocketDim.class.getResource("/mods/DimDoors/sfx/monk.ogg")));
		event.manager.soundPoolSounds.addSound("mods/DimDoors/sfx/crack.ogg", (mod_pocketDim.class.getResource("/mods/DimDoors/sfx/crack.ogg")));
		event.manager.soundPoolSounds.addSound("mods/DimDoors/sfx/tearing.ogg", (mod_pocketDim.class.getResource("/mods/DimDoors/sfx/tearing.ogg")));
		event.manager.soundPoolSounds.addSound("mods/DimDoors/sfx/rift.ogg", (mod_pocketDim.class.getResource("/mods/DimDoors/sfx/rift.ogg")));
		event.manager.soundPoolSounds.addSound("mods/DimDoors/sfx/riftStart.ogg", (mod_pocketDim.class.getResource("/mods/DimDoors/sfx/riftStart.ogg")));
		event.manager.soundPoolSounds.addSound("mods/DimDoors/sfx/riftEnd.ogg", (mod_pocketDim.class.getResource("/mods/DimDoors/sfx/riftEnd.ogg")));
		event.manager.soundPoolSounds.addSound("mods/DimDoors/sfx/riftClose.ogg", (mod_pocketDim.class.getResource("/mods/DimDoors/sfx/riftClose.ogg")));
		event.manager.soundPoolSounds.addSound("mods/DimDoors/sfx/riftDoor.ogg", (mod_pocketDim.class.getResource("/mods/DimDoors/sfx/riftDoor.ogg")));
	}

    @ForgeSubscribe
    public void onWorldLoad(WorldEvent.Load event)
    {
    	if (!mod_pocketDim.hasInitDims && event.world.provider.dimensionId == 0 && !event.world.isRemote)
    	{
    		System.out.println("Registering Pocket Dims");
    		mod_pocketDim.hasInitDims = true;
    		dimHelper.instance.unregsisterDims();
        	dimHelper.dimList.clear();
        	dimHelper.instance.interDimLinkList.clear();
        	dimHelper.instance.initPockets();
    	}
    	for (Integer ids : dimHelper.getIDs())
    	{
    		World world = dimHelper.getWorld(ids);
    		int linkCount = 0;
    		
    		if (dimHelper.dimList.containsKey(world.provider.dimensionId))
    		{
    			//TODO added temporary Try/catch block to prevent a crash here, getLinksInDim needs to be looked at
    			try
    			{    			
	    			for (LinkData link:dimHelper.dimList.get(world.provider.dimensionId).getLinksInDim())
	    			{
	    				if(linkCount>100) //TODO: Wtf? wouldn't this cause some links to not load on servers with several links? Not sure what's going on here. ~SenseiKiwi
	    				{
	    					break;
	    				}
	    				linkCount++;
	    				int blocktoReplace = world.getBlockId(link.locXCoord, link.locYCoord, link.locZCoord);
	    				if (!mod_pocketDim.blocksImmuneToRift.contains(blocktoReplace))
	    				{
	        				dimHelper.getWorld(link.locDimID).setBlock(link.locXCoord, link.locYCoord, link.locZCoord, properties.RiftBlockID);
	    				}
	    			}
    			}
    			catch(Exception e)
    			{
    				e.printStackTrace();
    			}
    		}
    	}   
    }
    
    @ForgeSubscribe
    public void onPlayerFall(LivingFallEvent event)
    {
    	event.setCanceled(event.entity.worldObj.provider.dimensionId == properties.LimboDimensionID);
    }
   
    @ForgeSubscribe
    public void onPlayerDrops(PlayerDropsEvent event)
    {
    	//TODO: I have some doubts. Is this triggered even if you die outside Limbo? And do you still drop items that others could pick up? We don't cancel the event. ~SenseiKiwi
    	mod_pocketDim.limboSpawnInventory.put(event.entityPlayer.username, event.drops);
    }

    @ForgeSubscribe
    public void onWorldsave(WorldEvent.Save event)
    {
    
    	if (mod_pocketDim.hasInitDims && event.world.provider.dimensionId == 0)
    	{
    		dimHelper.instance.save();
    	}
    }
}