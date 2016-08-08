package com.zixiken.dimdoors;

import com.zixiken.dimdoors.config.DDProperties;
import com.zixiken.dimdoors.config.DDWorldProperties;
import com.zixiken.dimdoors.core.*;
import com.zixiken.dimdoors.items.BaseItemDoor;
import com.zixiken.dimdoors.items.ItemWarpDoor;
import com.zixiken.dimdoors.ticking.RiftRegenerator;
import com.zixiken.dimdoors.util.Point4D;
import com.zixiken.dimdoors.world.LimboProvider;
import com.zixiken.dimdoors.world.PocketProvider;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import com.zixiken.dimdoors.core.DimData;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EventHookContainer
{
	private static final int MAX_FOOD_LEVEL = 20;
	
	private final DDProperties properties;
	private DDWorldProperties worldProperties;
	private RiftRegenerator regenerator;

	public EventHookContainer(DDProperties properties)
	{
		this.properties = properties;
	}
	
	public void setSessionFields(DDWorldProperties worldProperties, RiftRegenerator regenerator)
	{
		// SenseiKiwi:
		// Why have a setter rather than accessing DimDoors directly?
		// I want to make this dependency explicit in our code.
		this.worldProperties = worldProperties;
		this.regenerator = regenerator;
	}

    @SubscribeEvent(priority = EventPriority.LOW)
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

	@SubscribeEvent
	public void onPlayerEvent(PlayerInteractEvent event)
	{
		// Handle all door placement here
		if (event.action == Action.LEFT_CLICK_BLOCK)
		{
			return;
		}
		World world = event.entity.worldObj;
		ItemStack stack = event.entityPlayer.inventory.getCurrentItem();
		if (stack != null)
		{
			if(stack.getItem() instanceof ItemWarpDoor)
			{
				DimData data = PocketManager.getDimensionData(world);
				
				if(data.type() == DimensionType.PERSONAL)
				{
					DimDoors.sendChat(event.entityPlayer,("Something prevents the Warp Door from tunneling out here"));
					event.setCanceled(true);
					return;
				}
			}
			if (BaseItemDoor.tryToPlaceDoor(stack, event.entityPlayer, world,
					event.x, event.y, event.z, event.face))
			{
				// Cancel the event so that we don't get two doors from vanilla doors
				event.setCanceled(true);
			}
		}
		
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event)
	{
		// We need to initialize PocketManager here because onServerAboutToStart
		// fires before we can use DimensionManager and onServerStarting fires
		// after the game tries to generate terrain. If a gateway tries to
		// generate before PocketManager has initialized, we get a crash.
		if (!event.world.isRemote && !PocketManager.isLoaded())
		{
			PocketManager.load();
		}
	}

	@SubscribeEvent
	public void onPlayerFall(LivingFallEvent event)
	{
		event.setCanceled(event.entity.worldObj.provider.dimensionId == properties.LimboDimensionID);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public boolean onDeathWithHighPriority(LivingDeathEvent event)
	{
		// Teleport the entity to Limbo if it's a player in a pocket dimension
		// and if Limbo preserves player inventories. We'll check again in a
		// low-priority event handler to give other mods a chance to save the
		// player if Limbo does _not_ preserve inventories.

		Entity entity = event.entity;

		if (properties.LimboEnabled && properties.LimboReturnsInventoryEnabled &&
				entity instanceof EntityPlayer && isValidSourceForLimbo(entity.worldObj.provider))
		{
			if(entity.worldObj.provider instanceof PocketProvider)
			{
				EntityPlayer player = (EntityPlayer) entity;
				DimDoors.deathTracker.addUsername(player.getGameProfile().getName());
				revivePlayerInLimbo(player);
				event.setCanceled(true);
				return false;
			}
			else if(entity.worldObj.provider instanceof LimboProvider && event.source == DamageSource.outOfWorld)
			{
				EntityPlayer player = (EntityPlayer) entity;
				revivePlayerInLimbo(player);
				DimDoors.sendChat(player, "Search for the dark red pools which accumulate in the lower reaches of Limbo");
				event.setCanceled(true);
				return false;
			}
		}
		return true;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public boolean onDeathWithLowPriority(LivingDeathEvent event)
	{
		// This low-priority handler gives mods a chance to save a player from
		// death before we apply teleporting them to Limbo _without_ preserving
		// their inventory. We also check if the player died in a pocket
		// dimension and record it, regardless of whether the player will be
		// sent to Limbo.

		Entity entity = event.entity;

		if (entity instanceof EntityPlayer && isValidSourceForLimbo(entity.worldObj.provider))
		{
			EntityPlayer player = (EntityPlayer) entity;
			DimDoors.deathTracker.addUsername(player.getGameProfile().getName());

			if (properties.LimboEnabled && !properties.LimboReturnsInventoryEnabled)
			{
				player.inventory.clearInventory(null, -1);
				revivePlayerInLimbo(player);
				event.setCanceled(true);
			}
			return false;
		}
		return true;
	}
	
	private boolean isValidSourceForLimbo(WorldProvider provider)
	{
		// Returns whether a given world is a valid place for sending a player
		// to Limbo. We can send someone to Limbo from a certain dimension if
		// Universal Limbo is enabled and the source dimension is not Limbo, or
		// if the source dimension is a pocket dimension.
		
		return (worldProperties.UniversalLimboEnabled && provider.dimensionId != properties.LimboDimensionID) ||
				(provider instanceof PocketProvider);
	}

	private void revivePlayerInLimbo(EntityPlayer player)
	{
		player.extinguish();
		player.clearActivePotions();
		player.setHealth(player.getMaxHealth());
		player.getFoodStats().addStats(MAX_FOOD_LEVEL, 0);
		Point4D destination = LimboProvider.getLimboSkySpawn(player, properties);
		DDTeleporter.teleportEntity(player, destination, false);
	}

	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save event)
	{
		if (event.world.provider.dimensionId == 0)
		{
			PocketManager.save(true);

			if (DimDoors.deathTracker != null && DimDoors.deathTracker.isModified())
			{
				DimDoors.deathTracker.writeToFile();
			}
		}
	}
	
	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load event)
	{
		// Schedule rift regeneration for any links located in this chunk.
		// This event runs on both the client and server. Allow server only.
		// Also, check that PocketManager is loaded, because onChunkLoad() can
		// fire while chunks are being initialized in a new world, before
		// onWorldLoad() fires.
		Chunk chunk = event.getChunk();
		if (!chunk.worldObj.isRemote && PocketManager.isLoaded())
		{
			DimData dimension = PocketManager.createDimensionData(chunk.worldObj);
			for (DimLink link : dimension.getChunkLinks(chunk.xPosition, chunk.zPosition))
			{
				regenerator.scheduleSlowRegeneration(link);
			}
		}
	}
}