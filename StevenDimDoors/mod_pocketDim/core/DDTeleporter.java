package StevenDimDoors.mod_pocketDim.core;

import java.util.ArrayList;
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
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.helpers.yCoordHelper;
import StevenDimDoors.mod_pocketDim.items.ItemDimensionalDoor;
import StevenDimDoors.mod_pocketDim.schematic.BlockRotator;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import StevenDimDoors.mod_pocketDim.world.PocketBuilder;
import cpw.mods.fml.common.registry.GameRegistry;

public class DDTeleporter
{
	private static final Random random = new Random();
	private static final int END_DIMENSION_ID = 1;
	private static final int MAX_ROOT_SHIFT_CHANCE = 100;
	private static final int START_ROOT_SHIFT_CHANCE = 0;
	private static final int ROOT_SHIFT_CHANCE_PER_LEVEL = 5;
	
	public static int cooldown = 0;
	
	private DDTeleporter() { }
	
	private static void placeInPortal(Entity entity, WorldServer world, Point4D destination, DDProperties properties, boolean checkOrientation)
	{
		int x = destination.getX();
		int y = destination.getY();
		int z = destination.getZ();

		int orientation;
		if (checkOrientation)
		{
			orientation = getDestinationOrientation(destination, properties);
			entity.rotationYaw = (orientation * 90) + 90;
		}
		else
		{
			//Teleport the entity to the precise destination point
			orientation = -1;
		}
		
		if (entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) entity;
			switch (orientation)
			{
				case 0:
					player.setPositionAndUpdate(x - 0.5, y - 1, z + 0.5);
					break;
				case 1:
					player.setPositionAndUpdate(x + 0.5, y - 1, z - 0.5);
					break;
				case 2:
					player.setPositionAndUpdate(x + 1.5, y - 1, z + 0.5);
					break;
				case 3:
					player.setPositionAndUpdate(x + 0.5, y - 1, z + 1.5);
					break;
				default:
					player.setPositionAndUpdate(x, y - 1, z);	
					break;
			}
		}
		else if (entity instanceof EntityMinecart)
		{
			entity.motionX = 0;
			entity.motionZ = 0;
			entity.motionY = 0;

			switch (orientation)
			{
				case 0:
					DDTeleporter.setEntityPosition(entity, x - 0.5, y, z + 0.5);
					entity.motionX = -0.39;
					entity.worldObj.updateEntityWithOptionalForce(entity, false);
					break;
				case 1:
					DDTeleporter.setEntityPosition(entity, x + 0.5, y, z - 0.5);
					entity.motionZ = -0.39;
					entity.worldObj.updateEntityWithOptionalForce(entity, false);
					break;
				case 2:
					DDTeleporter.setEntityPosition(entity, x + 1.5, y, z + 0.5);
					entity.motionX = 0.39;
					entity.worldObj.updateEntityWithOptionalForce(entity, false);
					break;
				case 3:
					DDTeleporter.setEntityPosition(entity, x + 0.5, y, z + 1.5 );
					entity.motionZ = 0.39;
					entity.worldObj.updateEntityWithOptionalForce(entity, false);
					break;
				default:
					DDTeleporter.setEntityPosition(entity, x, y, z);	
					break;
			}
		}
		else
		{
			switch (orientation)
			{
				case 0:
					setEntityPosition(entity, x - 0.5, y, z + 0.5);
					break;
				case 1:
					setEntityPosition(entity, x + 0.5, y, z - 0.5);
					break;
				case 2:
					setEntityPosition(entity, x + 1.5, y, z + 0.5);
					break;
				case 3:
					setEntityPosition(entity, x + 0.5, y, z + 1.5);
					break;
				default:
					setEntityPosition(entity, x, y, z);	
					break;
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
		
		//Check if the block below that point is actually a door
		int blockID = world.getBlockId(door.getX(), door.getY() - 1, door.getZ());
		if (blockID != properties.DimensionalDoorID && blockID != properties.WarpDoorID &&
			blockID != properties.TransientDoorID && blockID != properties.UnstableDoorID)
		{
			//Return the pocket's orientation instead
			return PocketManager.getDimensionData(door.getDimension()).orientation();
		}
		
		//Return the orientation portion of its metadata
		return world.getBlockMetadata(door.getX(), door.getY() - 1, door.getZ()) & 3;
	}
	
	public static Entity teleportEntity(Entity entity, Point4D destination, boolean checkOrientation)
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
			return teleportEntity(entity.riddenByEntity, destination, checkOrientation);
		}

		// Are we riding something? Dismount and tell the mount to go first.
		Entity cart = entity.ridingEntity;
		if (cart != null)
		{
			entity.mountEntity(null);
			cart = teleportEntity(cart, destination, checkOrientation);
			// We keep track of both so we can remount them on the other side.
		}

		// Determine if our destination is in another realm.
		boolean difDest = entity.dimension != destination.getDimension();
		if (difDest)
		{
			// Destination isn't loaded? Then we need to load it.
			newWorld = PocketManager.loadDimension(destination.getDimension());
		}
		else
		{
			newWorld = (WorldServer) oldWorld;
		}

		// GreyMaria: What is this even accomplishing? We're doing the exact same thing at the end of this all.
		// TODO Check to see if this is actually vital.
		DDTeleporter.placeInPortal(entity, newWorld, destination, properties, checkOrientation);

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

				player.theItemInWorldManager.setWorld(newWorld);

				// Synchronize with the server so the client knows what time it is and what it's holding.
				player.mcServer.getConfigurationManager().updateTimeAndWeatherForPlayer(player, newWorld);
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
		DDTeleporter.placeInPortal(entity, newWorld, destination, properties, checkOrientation);
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
		
		if (link.linkType() == LinkTypes.RANDOM)
		{
			Point4D randomDestination = getRandomDestination();
			if (randomDestination != null)
			{
				entity = teleportEntity(entity, randomDestination, true);
				entity.worldObj.playSoundEffect(entity.posX, entity.posY, entity.posZ, "mob.endermen.portal", 1.0F, 1.0F);
			}
		}
		else
		{
			entity = teleportEntity(entity, link.destination(), link.linkType() != LinkTypes.UNSAFE_EXIT);
			entity.worldObj.playSoundEffect(entity.posX, entity.posY, entity.posZ, "mob.endermen.portal", 1.0F, 1.0F);
		}
	}

	private static boolean initializeDestination(DimLink link, DDProperties properties)
	{
		// FIXME: Change this later to support rooms that have been wiped and must be regenerated.
		// FIXME: Add code for restoring the destination-side door.
		// We might need to implement regeneration for REVERSE links as well.
		
		if (link.hasDestination())
		{
			return true;
		}

		// Check the destination type and respond accordingly
		switch (link.linkType())
		{
			case LinkTypes.DUNGEON:
				return PocketBuilder.generateNewDungeonPocket(link, properties);
			case LinkTypes.POCKET:
				return PocketBuilder.generateNewPocket(link, properties);
			case LinkTypes.SAFE_EXIT:
				return generateSafeExit(link, properties);
			case LinkTypes.DUNGEON_EXIT:
				return generateDungeonExit(link, properties);
			case LinkTypes.UNSAFE_EXIT:
				return generateUnsafeExit(link);
			case LinkTypes.NORMAL:
			case LinkTypes.REVERSE:
			case LinkTypes.RANDOM:
				return true;
			default:
				throw new IllegalArgumentException("link has an unrecognized link type.");
		}
	}

	private static Point4D getRandomDestination()
	{
		// Our aim is to return a random link's source point
		// so that a link of type RANDOM can teleport a player there.
		
		// Restrictions:
		// 1. Ignore links with their source inside a pocket dimension.
		// 2. Ignore links with link type RANDOM.
		
		// Iterate over the root dimensions. Pocket dimensions cannot be roots.
		// Don't just pick a random root and a random link within that root
		// because we want to have unbiased selection among all links.
		ArrayList<Point4D> matches = new ArrayList<Point4D>();
		for (NewDimData dimension : PocketManager.getRootDimensions())
		{
			for (DimLink link : dimension.getAllLinks())
			{
				if (link.linkType() != LinkTypes.RANDOM)
				{
					matches.add(link.source());
				}
			}
		}
		
		// Pick a random point, if any is available
		if (!matches.isEmpty())
		{
			return matches.get( random.nextInt(matches.size()) );
		}
		else
		{
			return null;
		}
	}
	
	private static boolean generateUnsafeExit(DimLink link)
	{
		// An unsafe exit teleports the user to the first available air space
		// in the pocket's root dimension. X and Z are kept roughly the same
		// as the source location, but Y is set by searching down. We don't
		// place a platform at the destination. We also don't place a reverse
		// link at the destination, so it's a one-way trip. Good luck!
		
		// To avoid loops, don't generate a destination if the player is
		// already in a non-pocket dimension.
		
		NewDimData current = PocketManager.getDimensionData(link.source.getDimension());
		if (current.isPocketDimension())
		{
			Point4D source = link.source();
			World world = PocketManager.loadDimension(current.root().id());
			if (world == null)
			{
				return false;
			}
			
			Point3D destination = yCoordHelper.findDropPoint(world, source.getX(), source.getY(), source.getZ());
			if (destination != null)
			{
				current.root().setDestination(link, source.getX(), source.getY(), source.getZ());
				return true;				
			}
		}
		return false;
	}

	private static boolean generateSafeExit(DimLink link, DDProperties properties)
	{
		NewDimData current = PocketManager.getDimensionData(link.source.getDimension());
		return generateSafeExit(current.root(), link, properties);
	}
	
	private static boolean generateDungeonExit(DimLink link, DDProperties properties)
	{
		// A dungeon exit acts the same as a safe exit, but has the chance of
		// taking the user to any non-pocket dimension, excluding Limbo and The End.
		
		NewDimData current = PocketManager.getDimensionData(link.source.getDimension());
		ArrayList<NewDimData> roots = PocketManager.getRootDimensions();
		int shiftChance = START_ROOT_SHIFT_CHANCE + ROOT_SHIFT_CHANCE_PER_LEVEL * (current.packDepth() - 1);

		if (random.nextInt(MAX_ROOT_SHIFT_CHANCE) < shiftChance)
		{
			for (int attempts = 0; attempts < 10; attempts++)
			{
				NewDimData selection = roots.get( random.nextInt(roots.size()) );
				if (selection.id() != END_DIMENSION_ID &&
					selection.id() != properties.LimboDimensionID &&
					selection != current.root())
				{
					return generateSafeExit(selection, link, properties);
				}
			}
		}
		
		// Yes, this could lead you back into Limbo. That's intentional.
		return generateSafeExit(current.root(), link, properties);
	}
	
	private static boolean generateSafeExit(NewDimData destinationDim, DimLink link, DDProperties properties)
	{
		// A safe exit attempts to place a Warp Door in a dimension with
		// some precautions to protect the player. The X and Z coordinates
		// are fixed to match the source (mostly - may be shifted a little),
		// but the Y coordinate is chosen by searching for the nearest
		// a safe location to place the door.
		
		Point4D source = link.source();
		World world = PocketManager.loadDimension(destinationDim.id());
		if (world == null)
		{
			return false;
		}
		
		int startY = source.getY() - 2;
		Point3D destination;
		Point3D locationUp = yCoordHelper.findSafeCubeUp(world, source.getX(), startY, source.getZ());
		Point3D locationDown = yCoordHelper.findSafeCubeDown(world, source.getX(), startY, source.getZ());
		
		if (locationUp == null)
		{
			destination = locationDown;
		}
		else if (locationDown == null)
		{
			destination = locationUp;
		}
		else if (locationUp.getY() - startY <= startY - locationDown.getY())
		{
			destination = locationUp;
		}
		else
		{
			destination = locationDown;
		}
		if (destination != null)
		{
			// Set up a 3x3 platform at the destination
			int x = destination.getX();
			int y = destination.getY();
			int z = destination.getZ();
			for (int dx = -1; dx <= 1; dx++)
			{
				for (int dz = -1; dz <= 1; dz++)
				{
					world.setBlock(x + dx, y, z + dz, properties.FabricBlockID);
				}
			}
			
			// Create a reverse link for returning
			NewDimData sourceDim = PocketManager.getDimensionData(link.source().getDimension());
			DimLink reverse = destinationDim.createLink(x, y + 2, z, LinkTypes.REVERSE);
			sourceDim.setDestination(reverse, source.getX(), source.getY(), source.getZ());
			
			// Set up the warp door at the destination
			int orientation = getDestinationOrientation(source, properties);
			orientation = BlockRotator.transformMetadata(orientation, 2, properties.WarpDoorID);
			ItemDimensionalDoor.placeDoorBlock(world, x, y + 1, z, orientation, mod_pocketDim.warpDoor);
			
			// Complete the link to the destination
			// This comes last so the destination isn't set unless everything else works first
			destinationDim.setDestination(link, x, y + 2, z);
		}
		
		return (destination != null);
	}
}
