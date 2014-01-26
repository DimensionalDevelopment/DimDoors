package StevenDimDoors.mod_pocketDim;

import paulscode.sound.SoundSystem;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPool;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.PlayBackgroundMusicEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.world.WorldEvent;
import StevenDimDoors.mod_pocketDim.core.DDTeleporter;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.ticking.RiftRegenerator;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import StevenDimDoors.mod_pocketDim.world.LimboProvider;
import StevenDimDoors.mod_pocketDim.world.PocketProvider;
import cpw.mods.fml.client.FMLClientHandler;
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
		event.manager.addSound(mod_pocketDim.modid+":monk.ogg");
		event.manager.addSound(mod_pocketDim.modid+":crack.ogg");
		event.manager.addSound(mod_pocketDim.modid+":tearing.ogg");
		event.manager.addSound(mod_pocketDim.modid+":rift.ogg");
		event.manager.addSound(mod_pocketDim.modid+":riftStart.ogg");
		event.manager.addSound(mod_pocketDim.modid+":riftEnd.ogg");
		event.manager.addSound(mod_pocketDim.modid+":riftClose.ogg");
		event.manager.addSound(mod_pocketDim.modid+":riftDoor.ogg");
		event.manager.addSound(mod_pocketDim.modid+":creepy.ogg");

	}
	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onSoundEffectResult(PlayBackgroundMusicEvent event) 
	{
        if (FMLClientHandler.instance().getClient().thePlayer.worldObj.provider.dimensionId==mod_pocketDim.properties.LimboDimensionID); 
        {
        	this.playMusicForDim(FMLClientHandler.instance().getClient().thePlayer.worldObj);
        }
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
    	
    	if(event.world!=null)
    	{
    		this.playMusicForDim(event.world);
    	}
    }
    
    @ForgeSubscribe
    public void onPlayerFall(LivingFallEvent event)
    {
    	event.setCanceled(event.entity.worldObj.provider.dimensionId == properties.LimboDimensionID);
    }
    @ForgeSubscribe(priority=EventPriority.HIGHEST)
    public boolean LivingDeathEvent(LivingDeathEvent event)
    {
    	Entity entity = event.entity;
    	
    	if (entity instanceof EntityPlayer && properties.LimboEnabled &&
    		entity.worldObj.provider instanceof PocketProvider)
    	{
    		EntityPlayer player = (EntityPlayer) entity;
    		if (!properties.LimboReturnsInventoryEnabled)
    		{
    			player.inventory.clearInventory(-1, -1);
    		}
    		player.extinguish();
    		player.clearActivePotions();
    		ChunkCoordinates coords = LimboProvider.getLimboSkySpawn(player.worldObj.rand);
    		Point4D destination = new Point4D((int) (coords.posX+entity.posX), coords.posY, (int) (coords.posZ+entity.posZ ), mod_pocketDim.properties.LimboDimensionID);
    		DDTeleporter.teleportEntity(player, destination, false);
    		player.setHealth(player.getMaxHealth());
    		event.setCanceled(true);
    		
    		return false;
    	}
    	return true;
    }

    @ForgeSubscribe
    public void onWorldSave(WorldEvent.Save event)
    {
    	if (event.world.provider.dimensionId == 0)
    	{
    		PocketManager.save();
    	}
    }
    
    public void playMusicForDim(World world)
    {
    	if(world.isRemote)
    	{
    		SoundManager sndManager =  FMLClientHandler.instance().getClient().sndManager;

	    	if(world.provider instanceof LimboProvider)
	    	{
	    		sndManager.sndSystem.stop("BgMusic");
	    		SoundPoolEntry soundPoolEntry = sndManager.soundPoolSounds.getRandomSoundFromSoundPool(mod_pocketDim.modid+":creepy");
	    		if(soundPoolEntry!=null) 
	    		{
	    			sndManager.sndSystem.backgroundMusic("LimboMusic", soundPoolEntry.getSoundUrl(), soundPoolEntry.getSoundName(), false);
	    			sndManager.sndSystem.play("LimboMusic");
	    			sndManager.sndSystem.setVolume("BgMusic", sndManager.sndSystem.getMasterVolume()+5);

	    		}
	    	}
	    	else if(!(world.provider instanceof LimboProvider))
	    	{
	    		sndManager.sndSystem.stop("LimboMusic");
	    	}
    	}
    }
}