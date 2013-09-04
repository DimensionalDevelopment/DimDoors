package StevenDimDoors.mod_pocketDim;

import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.world.WorldEvent;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.ticking.RiftRegenerator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EventHookContainer
{
	private final DDProperties properties;
	
	public EventHookContainer(DDProperties properties)
	{
		this.properties = properties;
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
    	// We need to initialize PocketManager here because onServerAboutToStart fires before we can
    	// use DimensionManager and onServerStarting fires after the game tries to generate terrain.
    	// If a gateway tries to generate before PocketManager has initialized, we get a crash.
    	if (!PocketManager.isLoaded())
    	{
    		PocketManager.load();
    	}
    	
    	if (PocketManager.isLoaded())
    	{
    		RiftRegenerator.regenerateRiftsInAllWorlds();
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
    	if (event.world.provider.dimensionId == 0)
    	{
    		PocketManager.save();
    	}
    }
}