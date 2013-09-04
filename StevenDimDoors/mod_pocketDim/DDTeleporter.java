package StevenDimDoors.mod_pocketDim;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet41EntityEffect;
import net.minecraft.network.packet.Packet43Experience;
import net.minecraft.network.packet.Packet9Respawn;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkTypes;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import StevenDimDoors.mod_pocketDim.world.PocketBuilder;
import cpw.mods.fml.common.registry.GameRegistry;

public class DDTeleporter
{
	private static final Random random = new Random();
	public static int cooldown = 0;
	
	private DDTeleporter() { }
	
	private static void placeInPortal(Entity entity, WorldServer world, Point4D destination, DDProperties properties)
	{
		int x = destination.getX();
		int y = destination.getY();
		int z = destination.getZ();

		int orientation = getDestinationOrientation(destination, properties);

		if (entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) entity;
			player.rotationYaw=(orientation*90)+90;
			if(orientation==2||orientation==6)
			{
				player.setPositionAndUpdate( x+1.5, y-1, z+.5 );
			}
			else if(orientation==3||orientation==7)
			{
				player.setPositionAndUpdate( x+.5, y-1, z+1.5 );
			}
			else if(orientation==0||orientation==4)
			{
				player.setPositionAndUpdate(x-.5, y-1, z+.5);
			}
			else if(orientation==1||orientation==5)
			{
				player.setPositionAndUpdate(x+.5, y-1, z-.5);	
			}
			else
			{
				player.setPositionAndUpdate(x, y-1, z);	
			}
		}
		else if (entity instanceof EntityMinecart)
		{
			entity.motionX=0;
			entity.motionZ=0;
			entity.motionY=0;
			entity.rotationYaw=(orientation*90)+90;

			if(orientation==2||orientation==6)
			{
				DDTeleporter.setEntityPosition(entity, x+1.5, y, z+.5 );
				entity.motionX =.39;
				entity.worldObj.updateEntityWithOptionalForce(entity, false);
			}
			else if(orientation==3||orientation==7)
			{
				DDTeleporter.setEntityPosition(entity, x+.5, y, z+1.5 );
				entity.motionZ =.39;
				entity.worldObj.updateEntityWithOptionalForce(entity, false);
			}
			else if(orientation==0||orientation==4)
			{
				DDTeleporter.setEntityPosition(entity,x-.5, y, z+.5);
				entity.motionX =-.39;
				entity.worldObj.updateEntityWithOptionalForce(entity, false);
			}
			else if(orientation==1||orientation==5)
			{
				DDTeleporter.setEntityPosition(entity,x+.5, y, z-.5);	
				entity.motionZ =-.39;
				entity.worldObj.updateEntityWithOptionalForce(entity, false);
			}
			else
			{
				DDTeleporter.setEntityPosition(entity,x, y, z);	
			}
		}
		else if (entity instanceof Entity)
		{
			entity.rotationYaw=(orientation*90)+90;
			if(orientation==2||orientation==6)
			{
				DDTeleporter.setEntityPosition(entity, x+1.5, y, z+.5 );
			}
			else if(orientation==3||orientation==7)
			{

				DDTeleporter.setEntityPosition(entity, x+.5, y, z+1.5 );
			}
			else if(orientation==0||orientation==4)
			{
				DDTeleporter.setEntityPosition(entity,x-.5, y, z+.5);
			}
			else if(orientation==1||orientation==5)
			{
				DDTeleporter.setEntityPosition(entity,x+.5, y, z-.5);	
			}
			else
			{
				DDTeleporter.setEntityPosition(entity,x, y, z);	
			}
		}
	}

	private static void setEntityPosition(Entity entity, double x, double y, double z)
	{
		entity.lastTickPosX = entity.prevPosX = entity.posX = x;
		entity.lastTickPosY = entity.prevPosY = entity.posY = y + (double)entity.yOffset;
		entity.lastTickPosZ = entity.prevPosZ = entity.posZ = z;
		entity.setPosition(x, y, z);
	}
	
	private static int getDestinationOrientation(Point4D door, DDProperties properties)
	{
		World world = DimensionManager.getWorld(door.getDimension());
		if (world == null)
		{
			throw new IllegalStateException("The destination world should be loaded!");
		}

		//Check if the block at that point is actually a door
		int blockID = world.getBlockId(door.getX(), door.getY(), door.getZ());
		if (blockID != properties.DimensionalDoorID && blockID != properties.WarpDoorID &&
			blockID != properties.TransientDoorID && blockID != properties.UnstableDoorID)
		{
			//Return the pocket's orientation instead
			return PocketManager.getDimensionData(door.getDimension()).orientation();
		}
		
		//Return the orientation portion of its metadata
		return world.getBlockMetadata(door.getX(), door.getY(), door.getZ()) & 3;
	}
	
	public static Entity teleportEntity(Entity entity, Point4D destination)
	{
		if (entity == null)
		{
			throw new IllegalArgumentException("entity cannot be null.");
		}
		if (destination == null)
		{
			throw new IllegalArgumentException("destination cannot be null.");
		}
		
		//This beautiful teleport method is based off of xCompWiz's teleport function. 

		WorldServer oldWorld = (WorldServer) entity.worldObj;
		WorldServer newWorld;
		EntityPlayerMP player = (entity instanceof EntityPlayerMP) ? (EntityPlayerMP) entity : null;
		DDProperties properties = DDProperties.instance();

		// Is something riding? Handle it first.
		if (entity.riddenByEntity != null)
		{
			return teleportEntity(entity.riddenByEntity, destination);
		}

		// Are we riding something? Dismount and tell the mount to go first.
		Entity cart = entity.ridingEntity;
		if (cart != null)
		{
			entity.mountEntity(null);
			cart = teleportEntity(cart, destination);
			// We keep track of both so we can remount them on the other side.
		}

		// Determine if our destination is in another realm.
		boolean difDest = entity.dimension == destination.getDimension();
		if (difDest)
		{
			// Destination isn't loaded? Then we need to load it.
			newWorld = DimensionManager.getWorld(destination.getDimension());
			if (newWorld == null)
			{
				DimensionManager.initDimension(destination.getDimension());
			}
			newWorld = DimensionManager.getWorld(destination.getDimension());
		}
		else
		{
			newWorld = (WorldServer) oldWorld;
		}

		// GreyMaria: What is this even accomplishing? We're doing the exact same thing at the end of this all.
		// TODO Check to see if this is actually vital.
		DDTeleporter.placeInPortal(entity, newWorld, destination, properties);

		if (difDest) // Are we moving our target to a new dimension?
		{
			if(player != null) // Are we working with a player?
			{
				// We need to do all this special stuff to move a player between dimensions.

				// Set the new dimension and inform the client that it's moving to a new world.
				player.dimension = destination.getDimension();
				player.playerNetServerHandler.sendPacketToPlayer(new Packet9Respawn(player.dimension, (byte)player.worldObj.difficultySetting, newWorld.getWorldInfo().getTerrainType(), newWorld.getHeight(), player.theItemInWorldManager.getGameType()));

				// GreyMaria: Used the safe player entity remover before.
				// This should fix an apparently unreported bug where
				// the last non-sleeping player leaves the Overworld
				// for a pocket dimension, causing all sleeping players
				// to remain asleep instead of progressing to day.
				oldWorld.removePlayerEntityDangerously(player);
				player.isDead = false;

				// Creates sanity by ensuring that we're only known to exist where we're supposed to be known to exist.
				oldWorld.getPlayerManager().removePlayer(player);
				newWorld.getPlayerManager().addPlayer(player);

				player.theItemInWorldManager.setWorld((WorldServer)newWorld);

				// Synchronize with the server so the client knows what time it is and what it's holding.
				player.mcServer.getConfigurationManager().updateTimeAndWeatherForPlayer(player, (WorldServer)newWorld);
				player.mcServer.getConfigurationManager().syncPlayerInventory(player);
				for(Object potionEffect : player.getActivePotionEffects())
				{
					PotionEffect effect = (PotionEffect)potionEffect;
					player.playerNetServerHandler.sendPacketToPlayer(new Packet41EntityEffect(player.entityId, effect));
				}

				player.playerNetServerHandler.sendPacketToPlayer(new Packet43Experience(player.experience, player.experienceTotal, player.experienceLevel));
			}  	

			// Creates sanity by removing the entity from its old location's chunk entity list, if applicable.
			int entX = entity.chunkCoordX;
			int entZ = entity.chunkCoordZ;
			if ((entity.addedToChunk) && (oldWorld.getChunkProvider().chunkExists(entX, entZ)))
			{
				oldWorld.getChunkFromChunkCoords(entX, entZ).removeEntity(entity);
				oldWorld.getChunkFromChunkCoords(entX, entZ).isModified = true;
			}
			// Memory concerns.
			oldWorld.releaseEntitySkin(entity);

			if (player == null) // Are we NOT working with a player?
			{
				NBTTagCompound entityNBT = new NBTTagCompound();
				entity.isDead = false;
				entity.addEntityID(entityNBT);
				entity.isDead = true;
				entity = EntityList.createEntityFromNBT(entityNBT, newWorld);

				if (entity == null)
				{
					// TODO FIXME IMPLEMENT NULL CHECKS THAT ACTUALLY DO SOMETHING.
					/* 
					 * shit ourselves in an organized fashion, preferably
					 * in a neat pile instead of all over our users' games
					 */
				}

			}

			// Finally, respawn the entity in its new home.
			newWorld.spawnEntityInWorld(entity);
			entity.setWorld(newWorld);
		}
		entity.worldObj.updateEntityWithOptionalForce(entity, false);

		// Hey, remember me? It's time to remount.
		if (cart != null)
		{
			// Was there a player teleported? If there was, it's important that we update shit.
			if (player != null)
			{
				entity.worldObj.updateEntityWithOptionalForce(entity, true);	
			}
			entity.mountEntity(cart);
		}

		// Did we teleport a player? Load the chunk for them.
		if (player != null)
		{
			newWorld.getChunkProvider().loadChunk(MathHelper.floor_double(entity.posX) >> 4, MathHelper.floor_double(entity.posZ) >> 4);

			// Tell Forge we're moving its players so everyone else knows.
			// Let's try doing this down here in case this is what's killing NEI.
			GameRegistry.onPlayerChangedDimension((EntityPlayer)entity);

		}
		DDTeleporter.placeInPortal(entity, newWorld, destination, properties);
		return entity;    
	}
	
	/**
	 * Primary function used to teleport the player using doors. Performs numerous null checks, and also generates the destination door/pocket if it has not done so already.
	 * Also ensures correct orientation relative to the door.
	 * @param world - world the player is currently in
	 * @param link - the link the player is using to teleport; sends the player to its destination 
	 * @param player - the instance of the player to be teleported
	 */
	public static void traverseDimDoor(World world, DimLink link, Entity entity)
	{
		if (world == null)
		{
			throw new IllegalArgumentException("world cannot be null.");
		}
		if (link == null)
		{
			throw new IllegalArgumentException("link cannot be null.");
		}
		if (entity == null)
		{
			throw new IllegalArgumentException("entity cannot be null.");
		}
		if (world.isRemote)
		{
			return;
		}
		
		if (cooldown == 0 || entity instanceof EntityPlayer)
		{
			cooldown = 2 + random.nextInt(2);
		}
		else
		{
			return;
		}

		if (!initializeDestination(link, DDProperties.instance()))
		{
			return;
		}
		
		entity = teleportEntity(entity, link.destination());	
		entity.worldObj.playSoundEffect(entity.posX, entity.posY, entity.posZ, "mob.endermen.portal", 1.0F, 1.0F);
	}

	private static boolean initializeDestination(DimLink link, DDProperties properties)
	{
		//FIXME: Change this later to support rooms that have been wiped and must be regenerated.
		if (link.hasDestination())
		{
			return true;
		}

		//Check the destination type and respond accordingly
		//FIXME: Add missing link types.
		//FIXME: Add code for restoring the destination-side door.
		switch (link.linkType())
		{
			case LinkTypes.DUNGEON:
				return PocketBuilder.generateNewDungeonPocket(link, properties);
			case LinkTypes.POCKET:
				return PocketBuilder.generateNewPocket(link, properties);
			case LinkTypes.NORMAL:
				return true;
			default:
				throw new IllegalArgumentException("link has an unrecognized link type.");
		}
	}
}
