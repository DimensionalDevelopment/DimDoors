package StevenDimDoors.mod_pocketDim.tileentities;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.ServerPacketHandler;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.util.Point4D;

public class TileEntityRift extends TileEntity
{
	private static final int MAX_SPREAD_ATTEMPTS = 3;
	private static final int MAX_SEARCH_ATTEMPTS = 50;
	private static final int MAX_ANCESTOR_LINKS = 3;
	private static final int ENDERMAN_SPAWNING_CHANCE = 1;
	private static final int MAX_ENDERMAN_SPAWNING_CHANCE = 32;
	private static final int RIFT_SPREAD_CHANCE = 1;
	private static final int MAX_RIFT_SPREAD_CHANCE = 256;
	
	private static Random random = new Random();

	private int age = 0;
	private int count = 0;
	private int count2 = 0;
	public int xOffset = 0;
	public int yOffset = 0;
	public int zOffset = 0;
	public boolean shouldClose = false;
	private boolean hasUpdated = false;
	private boolean hasGrownRifts = false;

	public DimLink nearestRiftData;
	public int spawnedEndermenID = 0;
	public HashMap<Integer, double[]> renderingCenters = new HashMap<Integer, double[]>();

	@Override
	public void updateEntity() 
	{
		//Invalidate this tile entity if it shouldn't exist
		if (!this.worldObj.isRemote && PocketManager.getLink(xCoord, yCoord, zCoord, worldObj.provider.dimensionId) == null)
		{
			this.invalidate();
			if (worldObj.getBlockId(xCoord, yCoord, zCoord) == mod_pocketDim.blockRift.blockID)
			{
				worldObj.setBlockToAir(xCoord, yCoord, zCoord);
				this.invalidate();
				return;
			}
		}

		if (worldObj.getBlockId(xCoord, yCoord, zCoord) != mod_pocketDim.blockRift.blockID)
		{
			this.invalidate();
			return;
		}

		//The code for the new rift rendering hooks in here, as well as in the ClientProxy to bind the TESR to the rift.
		//It is inactive for now.
		/**
		 if(rand.nextInt(15) == 1)
		 {
			 age = age + 1;
			 this.calculateNextRenderQuad(age, rand);	 
		 }
		this.clearBlocksOnRift();
		 **/

		//This code should execute once every 10 seconds
		count++;
		if (count > 200)
		{
			this.spawnEndermen();
			this.calculateOldParticleOffset(); //this also calculates the distance for the particle stuff.
			this.grow(mod_pocketDim.properties);
			count = 0;
		}
		
		//Determines if rift should render white closing particles and spread closing effect to other rifts nearby
		if (this.shouldClose)
		{
			closeRift();
		}
	}

	@Override
	public boolean canUpdate()
	{
		return true;
	}

	private void clearBlocksOnRift()
	{
		//clears blocks for the new rending effect
		for (double[] coord : this.renderingCenters.values())
		{
			int x = MathHelper.floor_double(coord[0] + 0.5);
			int y = MathHelper.floor_double(coord[1] + 0.5);
			int z = MathHelper.floor_double(coord[2] + 0.5);
			
			// Right side
			if (!mod_pocketDim.blockRift.isBlockImmune(worldObj, this.xCoord + x, this.yCoord + y, this.zCoord + z))
			{
				worldObj.setBlockToAir(this.xCoord + x, this.yCoord + y, this.zCoord + z);
			}
			// Left side
			if (!mod_pocketDim.blockRift.isBlockImmune(worldObj, this.xCoord - x, this.yCoord - y, this.zCoord - z))
			{
				worldObj.setBlockToAir(this.xCoord - x, this.yCoord - y, this.zCoord - z);
			}
		}
	}

	private void spawnEndermen()
	{
		if (worldObj.isRemote)
		{
			return;
		}

		NewDimData dimension = PocketManager.getDimensionData(worldObj);

		//Ensure that this rift is only spawning one enderman at a time, to prevent hordes of endermen
		Entity entity = worldObj.getEntityByID(this.spawnedEndermenID);
		if (entity != null && entity instanceof EntityEnderman)
		{
			return;
		}

		//enderman will only spawn in groups of rifts
		if (random.nextInt(MAX_ENDERMAN_SPAWNING_CHANCE) < ENDERMAN_SPAWNING_CHANCE)
		{
			if (updateNearestRift())
			{
				List<Entity> list =  worldObj.getEntitiesWithinAABB(EntityEnderman.class,
						AxisAlignedBB.getBoundingBox(xCoord - 9, yCoord - 3, zCoord - 9, xCoord + 9, yCoord + 3, zCoord + 9));

				if (list.isEmpty())
				{
					EntityEnderman enderman = new EntityEnderman(worldObj);
					enderman.setLocationAndAngles(xCoord + 0.5, yCoord - 1, zCoord + 0.5, 5, 6);
					worldObj.spawnEntityInWorld(enderman);
					EntityPlayer player = this.worldObj.getClosestPlayerToEntity(enderman, 50);
					if(player!=null)
					{
						enderman.setTarget(player);
					}
				}				
			}
		}
	}
	
	public boolean updateNearestRift()
	{
		nearestRiftData = PocketManager.getDimensionData(worldObj).findNearestRift(this.worldObj, 5, xCoord, yCoord, zCoord);
		return (nearestRiftData != null);
	}

	private void closeRift()
	{
		NewDimData dimension = PocketManager.getDimensionData(worldObj);
		if (count2 == 20)
		{			 
			ArrayList<DimLink> rifts= dimension.findRiftsInRange(worldObj, 6, xCoord, yCoord, zCoord);
			if (rifts.size()>0)
			{
				for(DimLink riftToClose : rifts)
				{
					Point4D location = riftToClose.source();
					TileEntityRift rift = (TileEntityRift) worldObj.getBlockTileEntity(location.getX(), location.getY(), location.getZ());
					if (rift != null&&rift.shouldClose!=true)
					{
						rift.shouldClose = true;
						rift.onInventoryChanged();
					}
				}

			}
		}
		if (count2 > 40)
		{
			this.invalidate();
			if (dimension.getLink(xCoord, yCoord, zCoord) != null)
			{
				if(!this.worldObj.isRemote)
				{
					dimension.deleteLink(xCoord, yCoord, zCoord);
				}
				worldObj.playSound(xCoord, yCoord, zCoord, "mods.DimDoors.sfx.riftClose", (float) .7, 1, true);
				worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			}	
		}
		count2++; 

	}

	private void calculateOldParticleOffset()
	{
		updateNearestRift();
		if (nearestRiftData != null) 
		{
			Point4D location = nearestRiftData.source();
			this.xOffset = this.xCoord - location.getX();
			this.yOffset = this.yCoord - location.getY();
			this.zOffset = this.zCoord - location.getZ();
			int distance = Math.abs(xOffset) + Math.abs(yOffset) + Math.abs(zOffset);
		}
		else
		{
			this.xOffset=0;
			this.yOffset=0;
			this.xOffset=0;
		}
		this.onInventoryChanged();
	}

	private void calculateNextRenderQuad(float age, Random rand)
	{
		int maxSize = MathHelper.floor_double((Math.log(Math.pow(age+1,2))));
		int iteration=0;
		while(iteration< maxSize)
		{
			iteration++;
			double fl =Math.log(iteration+1)/(iteration);
			double[] coords= new double[4];
			double noise = ((rand.nextGaussian())/(2+iteration/3+1));

			if(!this.renderingCenters.containsKey(iteration-1))
			{
				if (rand.nextBoolean())
				{
					coords[0] = fl*1.5;
					coords[1] = rand.nextGaussian()/5;
					coords[2] = 0;
					coords[3] = 1;
				}
				else
				{
					coords[0] = 0;
					coords[1] = rand.nextGaussian()/5;
					coords[2] = fl*1.5;
					coords[3] = 0;
				}
				this.renderingCenters.put(iteration-1,coords);
				iteration--;
			}
			else if(!this.renderingCenters.containsKey(iteration))
			{
				if(this.renderingCenters.get(iteration-1)[3]==0)
				{
					coords[0]=noise/2+this.renderingCenters.get(iteration-1)[0];
					coords[1]=noise/2+this.renderingCenters.get(iteration-1)[1];
					coords[2]= this.renderingCenters.get(iteration-1)[2]+fl;
					coords[3] = 0;
				}
				else
				{
					coords[0]=this.renderingCenters.get(iteration-1)[0]+fl;
					coords[1]=noise/2+this.renderingCenters.get(iteration-1)[1];
					coords[2]=noise/2+this.renderingCenters.get(iteration-1)[2];
					coords[3] = 1;
				}  
				this.renderingCenters.put(iteration,coords);
			}	
		}
	}

	@Override
	public boolean shouldRenderInPass(int pass)
	{
		return pass == 1;
	}

	public int countAncestorLinks(DimLink link)
	{
		if (link.parent() != null)
		{
			return countAncestorLinks(link.parent()) + 1;
		}
		else
		{
			return 0;
		}
	}

	public void grow(DDProperties properties)
	{
		if (worldObj.isRemote || hasGrownRifts || !properties.RiftSpreadEnabled
			|| random.nextInt(MAX_RIFT_SPREAD_CHANCE) < RIFT_SPREAD_CHANCE)
		{
			return;
		}

		NewDimData dimension = PocketManager.getDimensionData(worldObj);
		DimLink link = dimension.getLink(xCoord, yCoord, zCoord);
		
		if (countAncestorLinks(link) > MAX_ANCESTOR_LINKS)
		{
			return;
		}
		
		// The probability of rifts trying to spread increases if more rifts are nearby
		// Players should see rifts spread faster within clusters than at the edges of clusters
		// Also, single rifts CANNOT spread.
		int nearRifts = dimension.findRiftsInRange(this.worldObj, 5, xCoord, yCoord, zCoord).size();
		if (nearRifts == 0 || random.nextInt(nearRifts) == 0)
		{
			return;
		}

		int x, y, z;
		int spreadAttempts = 0;
		for (int searchAttempts = 0; searchAttempts < MAX_SEARCH_ATTEMPTS; searchAttempts++)
		{
			x = xCoord + MathHelper.getRandomIntegerInRange(random, -6, 6);
			y = yCoord + MathHelper.getRandomIntegerInRange(random, -4, 4);
			z = zCoord + MathHelper.getRandomIntegerInRange(random, -6, 6);
			
			if (y >= 0 && y < worldObj.getActualHeight() && worldObj.isAirBlock(x, y, z))
			{
				Vec3 position = worldObj.getWorldVec3Pool().getVecFromPool(xCoord, yCoord, zCoord);
				Vec3 spreadTarget = worldObj.getWorldVec3Pool().getVecFromPool(x, y, z);
				MovingObjectPosition hit =  worldObj.clip(position, spreadTarget, false);
				if (hit == null || !mod_pocketDim.blockRift.isBlockImmune(worldObj, hit.blockX, hit.blockY, hit.blockZ))
				{
					dimension.createChildLink(x, y, z, link);
					hasGrownRifts = true;
					break;
				}
				else
				{
					spreadAttempts++;
					if (spreadAttempts >= MAX_SPREAD_ATTEMPTS)
					{
						break;
					}
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.renderingCenters = new HashMap<Integer, double[]>();
		this.count = nbt.getInteger("count");
		this.count2 = nbt.getInteger("count2");
		this.xOffset = nbt.getInteger("xOffset");
		this.yOffset = nbt.getInteger("yOffset");
		this.zOffset = nbt.getInteger("zOffset");
		this.hasGrownRifts = nbt.getBoolean("grownRifts");
		this.age = nbt.getInteger("age");
		this.shouldClose = nbt.getBoolean("shouldClose");
		this.spawnedEndermenID = nbt.getInteger("spawnedEndermenID");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setInteger("age", this.age);
		nbt.setInteger("count", this.count);
		nbt.setInteger("count2", this.count2);
		nbt.setBoolean("grownRifts",this.hasGrownRifts);
		nbt.setInteger("xOffset", this.xOffset);
		nbt.setInteger("yOffset", this.yOffset);
		nbt.setInteger("zOffset", this.zOffset);
		nbt.setBoolean("shouldClose", this.shouldClose);
		nbt.setInteger("spawnedEndermenID", this.spawnedEndermenID);
	}

	public Packet getDescriptionPacket()
	{
		if (PocketManager.getLink(xCoord, yCoord, zCoord, worldObj) != null)
		{
			return ServerPacketHandler.createLinkPacket(PocketManager.getLink(xCoord, yCoord, zCoord, worldObj).link());
		}
		return null;
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
	{
		readFromNBT(pkt.data);
	}
}
