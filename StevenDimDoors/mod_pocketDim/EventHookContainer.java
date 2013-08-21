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
    	
    	//TODO: In the future, we should iterate over DimHelper's dimension list. We ignore other dimensions anyway.
    	for (int dimensionID : dimHelper.getIDs())
    	{    		
    		World world = dimHelper.getWorld(dimensionID);
    		int linkCount = 0;
    		
    		if (dimHelper.dimList.containsKey(dimensionID))
    		{
    			for (LinkData link : dimHelper.instance.getDimData(dimensionID).getLinksInDim())
    			{
    				if (!mod_pocketDim.blockRift.isBlockImmune(world, link.locXCoord, link.locYCoord, link.locZCoord))
    				{
        				world.setBlock(link.locXCoord, link.locYCoord, link.locZCoord, properties.RiftBlockID);
    				}
    				linkCount++;
    				if (linkCount >= 100)
    				{
    					break;
    				}
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