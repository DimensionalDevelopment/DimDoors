package StevenDimDoors.mod_pocketDim;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
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
import StevenDimDoors.mod_pocketDim.core.IDimLink;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.util.Point4D;

public class DDTeleporter
{
	private DDTeleporter() { }

	/**
	 * Create a new portal near an entity.
	 */
	public static void placeInPortal(Entity par1Entity, WorldServer world, IDimLink link)
	{
		Point4D destination = link.destination();
		int x = destination.getX();
		int y = destination.getY();
		int z = destination.getZ();

		//TODO Temporary workaround for mismatched door/rift metadata cases. Gives priority to the door. 
		int orientation = PocketManager.getDestinationOrientation(link);
		int receivingDoorMeta = world.getBlockMetadata(x, y - 1, z);
		int receivingDoorID = world.getBlockId(x, y, z);
		if (receivingDoorMeta != orientation)
		{
			if (receivingDoorID == mod_pocketDim.dimDoor.blockID || receivingDoorID == mod_pocketDim.ExitDoor.blockID)
			{
				orientation = receivingDoorMeta;
			}
		}

		if (par1Entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) par1Entity;
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
		else if (par1Entity instanceof EntityMinecart)
		{
			par1Entity.motionX=0;
			par1Entity.motionZ=0;
			par1Entity.motionY=0;
			par1Entity.rotationYaw=(orientation*90)+90;

			if(orientation==2||orientation==6)
			{
				DDTeleporter.setEntityPosition(par1Entity, x+1.5, y, z+.5 );
				par1Entity.motionX =.39;
				par1Entity.worldObj.updateEntityWithOptionalForce(par1Entity, false);
			}
			else if(orientation==3||orientation==7)
			{
				DDTeleporter.setEntityPosition(par1Entity, x+.5, y, z+1.5 );
				par1Entity.motionZ =.39;
				par1Entity.worldObj.updateEntityWithOptionalForce(par1Entity, false);
			}
			else if(orientation==0||orientation==4)
			{
				DDTeleporter.setEntityPosition(par1Entity,x-.5, y, z+.5);
				par1Entity.motionX =-.39;
				par1Entity.worldObj.updateEntityWithOptionalForce(par1Entity, false);
			}
			else if(orientation==1||orientation==5)
			{
				DDTeleporter.setEntityPosition(par1Entity,x+.5, y, z-.5);	
				par1Entity.motionZ =-.39;
				par1Entity.worldObj.updateEntityWithOptionalForce(par1Entity, false);
			}
			else
			{
				DDTeleporter.setEntityPosition(par1Entity,x, y, z);	
			}
		}
		else if (par1Entity instanceof Entity)
		{
			par1Entity.rotationYaw=(orientation*90)+90;
			if(orientation==2||orientation==6)
			{
				DDTeleporter.setEntityPosition(par1Entity, x+1.5, y, z+.5 );
			}
			else if(orientation==3||orientation==7)
			{

				DDTeleporter.setEntityPosition(par1Entity, x+.5, y, z+1.5 );
			}
			else if(orientation==0||orientation==4)
			{
				DDTeleporter.setEntityPosition(par1Entity,x-.5, y, z+.5);
			}
			else if(orientation==1||orientation==5)
			{
				DDTeleporter.setEntityPosition(par1Entity,x+.5, y, z-.5);	
			}
			else
			{
				DDTeleporter.setEntityPosition(par1Entity,x, y, z);	
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
	
	public static Entity teleportEntity(World world, Entity entity, IDimLink link)
	{	
		//This beautiful teleport method is based off of xCompWiz's teleport function. 

		WorldServer oldWorld = (WorldServer)world;
		WorldServer newWorld;
		EntityPlayerMP player = (entity instanceof EntityPlayerMP) ? (EntityPlayerMP)entity : null;

		// Is something riding? Handle it first.
		if(entity.riddenByEntity != null)
		{
			return teleportEntity(oldWorld, entity.riddenByEntity, link);
		}

		// Are we riding something? Dismount and tell the mount to go first.
		Entity cart = entity.ridingEntity;
		if (cart != null)
		{
			entity.mountEntity(null);
			cart = teleportEntity(oldWorld, cart, link);
			// We keep track of both so we can remount them on the other side.
		}

		// Destination doesn't exist? We need to make it.
		if (DimensionManager.getWorld(link.destination().getDimension()) == null)
		{
			//FIXME: I think this is where I need to add initialization code for pockets!!! REALLY IMPORTANT!!!
			DimensionManager.initDimension(link.destination().getDimension());
		}

		// Determine if our destination's in another realm.
		boolean difDest = link.source().getDimension() != link.destination().getDimension();
		if (difDest)
		{
			newWorld = DimensionManager.getWorld(link.destination().getDimension());
		}
		else
		{
			newWorld = (WorldServer) oldWorld;
		}

		// GreyMaria: What is this even accomplishing? We're doing the exact same thing at the end of this all.
		// TODO Check to see if this is actually vital.
		DDTeleporter.placeInPortal(entity, newWorld, link);

		if (difDest) // Are we moving our target to a new dimension?
		{
			if(player != null) // Are we working with a player?
			{
				// We need to do all this special stuff to move a player between dimensions.

				// Set the new dimension and inform the client that it's moving to a new world.
				player.dimension = link.destination().getDimension();
				player.playerNetServerHandler.sendPacketToPlayer(new Packet9Respawn(player.dimension, (byte)player.worldObj.difficultySetting, newWorld.getWorldInfo().getTerrainType(), newWorld.getHeight(), player.theItemInWorldManager.getGameType()));

				// GreyMaria: Used the safe player entity remover before.
				// This should fix an apparently unreported bug where
				// the last non-sleeping player leaves the Overworld
				// for a pocket dimension, causing all sleeping players
				// to remain asleep instead of progressing to day.
				oldWorld.removePlayerEntityDangerously(player);
				player.isDead=false;

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
				{   // TODO FIXME IMPLEMENT NULL CHECKS THAT ACTUALLY DO SOMETHING.
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
		if(player != null)
		{
			WorldServer.class.cast(newWorld).getChunkProvider().loadChunk(MathHelper.floor_double(entity.posX) >> 4, MathHelper.floor_double(entity.posZ) >> 4);

			// Tell Forge we're moving its players so everyone else knows.
			// Let's try doing this down here in case this is what's killing NEI.
			GameRegistry.onPlayerChangedDimension((EntityPlayer)entity);

		}
		DDTeleporter.placeInPortal(entity, newWorld, link);
		return entity;    
	}
	
	/**
	 * Primary function used to teleport the player using doors. Performs numerous null checks, and also generates the destination door/pocket if it has not done so already.
	 * Also ensures correct orientation relative to the door using DDTeleporter.
	 * @param world- world the player is currently in
	 * @param linkData- the link the player is using to teleport, sends the player to its dest information. 
	 * @param player- the instance of the player to be teleported
	 * @param orientation- the orientation of the door used to teleport, determines player orientation and door placement on arrival
	 * @Return
	 */
	public static void traverseDimDoor(World world, IDimLink linkData, Entity entity)
	{
		DDProperties properties = DDProperties.instance();

		if (world.isRemote)
		{
			return;
		}
		if (linkData != null)
		{
			int destinationID = link.destination().getDimension();

			if(PocketManager.dimList.containsKey(destinationID) && PocketManager.dimList.containsKey(world.provider.dimensionId))
			{
				this.generatePocket(linkData);

				if(mod_pocketDim.teleTimer==0||entity instanceof EntityPlayer)
				{
					mod_pocketDim.teleTimer=2+rand.nextInt(2);
				}
				else
				{
					return;
				}			
				if(!world.isRemote)
				{	
					entity = this.teleportEntity(world, entity, linkData);		
				}	
				entity.worldObj.playSoundEffect(entity.posX, entity.posY, entity.posZ, "mob.endermen.portal", 1.0F, 1.0F);

				int playerXCoord=MathHelper.floor_double(entity.posX);
				int playerYCoord=MathHelper.floor_double(entity.posY);
				int playerZCoord=MathHelper.floor_double(entity.posZ);

				if(!entity.worldObj.isBlockOpaqueCube(playerXCoord, playerYCoord-1,playerZCoord )&&PocketManager.instance.getDimData(linkData.locDimID).isDimRandomRift&&!linkData.hasGennedDoor)
				{						
					for(int count=0;count<20;count++)
					{
						if(!entity.worldObj.isAirBlock(playerXCoord, playerYCoord-2-count,playerZCoord))
						{
							if(Block.blocksList[entity.worldObj.getBlockId(playerXCoord, playerYCoord-2-count,playerZCoord)].blockMaterial.isLiquid())
							{
								entity.worldObj.setBlock(playerXCoord, playerYCoord-1, playerZCoord, properties.FabricBlockID);
								break;
							}
						}

						if(entity.worldObj.isBlockOpaqueCube(playerXCoord, playerYCoord-1-count,playerZCoord))
						{
							break;
						}
						if(count==19)
						{
							entity.worldObj.setBlock(playerXCoord, playerYCoord-1, playerZCoord, properties.FabricBlockID);
						}
					}	    																	
				}						

				this.generateDoor(world,linkData);


				if(!entity.worldObj.isAirBlock(playerXCoord,playerYCoord+1,playerZCoord))
				{
					if(Block.blocksList[entity.worldObj.getBlockId(playerXCoord,playerYCoord+1,playerZCoord)].isOpaqueCube() &&
							!mod_pocketDim.blockRift.isBlockImmune(entity.worldObj, playerXCoord+1,playerYCoord,playerZCoord))
					{
						entity.worldObj.setBlock(playerXCoord,playerYCoord+1,playerZCoord,0);
					}
				}
				if (!entity.worldObj.isAirBlock(playerXCoord,playerYCoord,playerZCoord))
				{
					if(Block.blocksList[entity.worldObj.getBlockId(playerXCoord,playerYCoord,playerZCoord)].isOpaqueCube() &&
							!mod_pocketDim.blockRift.isBlockImmune(entity.worldObj, playerXCoord,playerYCoord,playerZCoord))
					{
						entity.worldObj.setBlock(playerXCoord,playerYCoord,playerZCoord,0);
					}
				}
			}
		}
		return;
	}
}
