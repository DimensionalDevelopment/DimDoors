package StevenDimDoors.mod_pocketDim;

import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.PlayBackgroundMusicEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.world.WorldEvent;
import StevenDimDoors.mod_pocketDim.blocks.TransTrapdoor;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.core.DDTeleporter;
import StevenDimDoors.mod_pocketDim.core.DimensionType;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.items.ItemWarpDoor;
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

	@ForgeSubscribe(priority = EventPriority.LOW)
	public void onInitMapGen(InitMapGenEvent event)
	{
		// Replace the Nether fortress generator with our own only if any
		// gateways would ever generate. This allows admins to disable our
		// fortress overriding without disabling all gateways.
		/*
		 * if (properties.FortressGatewayGenerationChance > 0 &&
		 * properties.WorldRiftGenerationEnabled && event.type ==
		 * InitMapGenEvent.EventType.NETHER_BRIDGE) { event.newGen = new
		 * DDNetherFortressGenerator(); }
		 */
	}

	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onSoundLoad(SoundLoadEvent event)
	{
		event.manager.addSound(mod_pocketDim.modid + ":doorLocked.ogg");
		event.manager.addSound(mod_pocketDim.modid + ":keyLock.ogg");
		event.manager.addSound(mod_pocketDim.modid + ":keyUnlock.ogg");
		event.manager.addSound(mod_pocketDim.modid + ":monk.ogg");
		event.manager.addSound(mod_pocketDim.modid + ":crack.ogg");
		event.manager.addSound(mod_pocketDim.modid + ":tearing.ogg");
		event.manager.addSound(mod_pocketDim.modid + ":rift.ogg");
		event.manager.addSound(mod_pocketDim.modid + ":riftStart.ogg");
		event.manager.addSound(mod_pocketDim.modid + ":riftEnd.ogg");
		event.manager.addSound(mod_pocketDim.modid + ":riftClose.ogg");
		event.manager.addSound(mod_pocketDim.modid + ":riftDoor.ogg");
		event.manager.addSound(mod_pocketDim.modid + ":creepy.ogg");
	}

	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onSoundEffectResult(PlayBackgroundMusicEvent event)
	{
		if (FMLClientHandler.instance().getClient().thePlayer.worldObj.provider.dimensionId == mod_pocketDim.properties.LimboDimensionID)
		{
			this.playMusicForDim(FMLClientHandler.instance().getClient().thePlayer.worldObj);
		}
	}

	@ForgeSubscribe
	public void onPlayerEvent(PlayerInteractEvent event)
	{
		// Handle all door placement here
		if(event.action == Action.LEFT_CLICK_BLOCK)
		{
			return;
		}
		World world = event.entity.worldObj;
		ItemStack stack = event.entityPlayer.inventory.getCurrentItem();
		if (stack != null && stack.getItem() instanceof ItemDoor)
		{
			if(stack.getItem() instanceof ItemWarpDoor)
			{
				NewDimData data = PocketManager.getDimensionData(world);
				
				if(data.getDimensionType() == DimensionType.PERSONAL)
				{
					mod_pocketDim.sendChat(event.entityPlayer,("Something prevents the Warp Door from tunneling out here"));
					event.setCanceled(true);
					return;
				}
			}
			if (mod_pocketDim.itemDimensionalDoor.tryToPlaceDoor(stack, event.entityPlayer, world,
					event.x, event.y, event.z, event.face))
			{
				// Cancel the event so that we don't get two doors from vanilla doors
				event.setCanceled(true);
			}
		}
	}

	@ForgeSubscribe
	public void onWorldLoad(WorldEvent.Load event)
	{
		// We need to initialize PocketManager here because onServerAboutToStart
		// fires before we can use DimensionManager and onServerStarting fires
		// after the game tries to generate terrain. If a gateway tries to
		// generate before PocketManager has initialized, we get a crash.
		if (!PocketManager.isLoaded())
		{
			PocketManager.load();
		}

		if (event.world != null)
		{
			this.playMusicForDim(event.world);
		}
	}

	@ForgeSubscribe
	public void onPlayerFall(LivingFallEvent event)
	{
		event.setCanceled(event.entity.worldObj.provider.dimensionId == properties.LimboDimensionID);
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public boolean onDeathWithHighPriority(LivingDeathEvent event)
	{
		// Teleport the entity to Limbo if it's a player in a pocket dimension
		// and if Limbo preserves player inventories. We'll check again in a
		// low-priority event handler to give other mods a chance to save the
		// player if Limbo does _not_ preserve inventories.

		Entity entity = event.entity;

		if (properties.LimboEnabled && properties.LimboReturnsInventoryEnabled &&
				entity instanceof EntityPlayer)
		{
			if(entity.worldObj.provider instanceof PocketProvider)
			{
				EntityPlayer player = (EntityPlayer) entity;
				mod_pocketDim.deathTracker.addUsername(player.username);
				revivePlayerInLimbo(player);
				event.setCanceled(true);
				return false;
			}
			else if(entity.worldObj.provider instanceof LimboProvider && event.source == DamageSource.outOfWorld)
			{
				EntityPlayer player = (EntityPlayer) entity;
				revivePlayerInLimbo(player);
				mod_pocketDim.sendChat(player, "Search for the dark red pools which accumulate in the lower reaches of Limbo");
				event.setCanceled(true);
				return false;
			}
		}
		return true;
	}

	@ForgeSubscribe(priority = EventPriority.LOWEST)
	public boolean onDeathWithLowPriority(LivingDeathEvent event)
	{
		// This low-priority handler gives mods a chance to save a player from
		// death before we apply teleporting them to Limbo _without_ preserving
		// their inventory. We also check if the player died in a pocket
		// dimension and record it, regardless of whether the player will be
		// sent to Limbo.

		Entity entity = event.entity;

		if (entity instanceof EntityPlayer && entity.worldObj.provider instanceof PocketProvider)
		{
			EntityPlayer player = (EntityPlayer) entity;
			mod_pocketDim.deathTracker.addUsername(player.username);

			if (properties.LimboEnabled && !properties.LimboReturnsInventoryEnabled)
			{
				player.inventory.clearInventory(-1, -1);
				revivePlayerInLimbo(player);
				event.setCanceled(true);
			}
			return false;
		}
		return true;
	}

	private void revivePlayerInLimbo(EntityPlayer player)
	{
		player.extinguish();
		player.clearActivePotions();
		player.setHealth(player.getMaxHealth());
		Point4D destination = LimboProvider.getLimboSkySpawn(player, properties);
		DDTeleporter.teleportEntity(player, destination, false);
	}

	@ForgeSubscribe
	public void onWorldSave(WorldEvent.Save event)
	{
		if (event.world.provider.dimensionId == 0)
		{
			PocketManager.save(true);

			if (mod_pocketDim.deathTracker != null && mod_pocketDim.deathTracker.isModified())
			{
				mod_pocketDim.deathTracker.writeToFile();
			}
		}
	}

	public void playMusicForDim(World world)
	{
		if (world.isRemote)
		{
			SoundManager sndManager = FMLClientHandler.instance().getClient().sndManager;

			// SenseiKiwi: I've added the following check as a quick fix for a
			// reported crash. This needs to work without a hitch or we have to
			// stop trying to replace the background music...
			if (sndManager != null && sndManager.sndSystem != null)
			{
				if (world.provider instanceof LimboProvider)
				{
					sndManager.sndSystem.stop("BgMusic");
					SoundPoolEntry soundPoolEntry = sndManager.soundPoolSounds.getRandomSoundFromSoundPool(mod_pocketDim.modid + ":creepy");
					if (soundPoolEntry != null)
					{
						sndManager.sndSystem.backgroundMusic("LimboMusic", soundPoolEntry.getSoundUrl(), soundPoolEntry.getSoundName(), false);
						sndManager.sndSystem.play("LimboMusic");
					}
				}
				else if (!(world.provider instanceof LimboProvider))
				{
					sndManager.sndSystem.stop("LimboMusic");
				}
			}
		}
	}
}