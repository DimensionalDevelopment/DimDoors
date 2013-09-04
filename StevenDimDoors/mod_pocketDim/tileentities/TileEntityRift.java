package StevenDimDoors.mod_pocketDim.tileentities;



import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.NewDimData;
import StevenDimDoors.mod_pocketDim.core.PocketManager;
import StevenDimDoors.mod_pocketDim.util.Point4D;

public class TileEntityRift extends TileEntity
{
	private static Random random = new Random();

	public int xOffset=0;
	public int yOffset=0;
	public int zOffset=0;
	public int distance=0;
	public boolean hasGrownRifts=false;
	public boolean shouldClose=false;
	public boolean isNearRift=false;
	private int count=200;
	private int count2 = 0;
	public int age = 0;

	public HashMap<Integer, double[]> renderingCenters = new HashMap<Integer, double[]>();
	public DimLink nearestRiftData;
	public int spawnedEndermenID=0;
	DataWatcher watcher = new DataWatcher();

	public void updateEntity() 
	{
		//Invalidate this tile entity if it shouldn't exist
		if (PocketManager.getLink(xCoord, yCoord, zCoord, worldObj.provider.dimensionId) == null)
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
			if (distance > 1) //only grow if rifts are nearby
			{
				this.grow(distance);
			}
			count = 0;
		}

		if (this.shouldClose) //Determines if rift should render white closing particles and spread closing effect to other rifts nearby
		{
			closeRift();
		}
	}

	public boolean canUpdate()
	{
		return true;
	}

	public void clearBlocksOnRift()
	{
		//clears blocks for the new rending effect

		for(double[] coord: this.renderingCenters.values())
		{
			int x = MathHelper.floor_double(coord[0]+.5);
			int y = MathHelper.floor_double(coord[1]+.5);
			int z = MathHelper.floor_double(coord[2]+.5);

			if (!mod_pocketDim.blockRift.isBlockImmune(worldObj,this.xCoord+x, this.yCoord+y, this.zCoord+z))//right side
			{
				worldObj.setBlockToAir(this.xCoord+x, this.yCoord+y, this.zCoord+z);
			}

			if (!mod_pocketDim.blockRift.isBlockImmune(worldObj,this.xCoord-x, this.yCoord-y, this.zCoord-z))//left side
			{
				worldObj.setBlockToAir(this.xCoord-x, this.yCoord-y, this.zCoord-z);
			}

		}
	}

	public void spawnEndermen()
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
		nearestRiftData = dimension.findNearestRift(worldObj, 5, xCoord, yCoord, zCoord);
		if (nearestRiftData != null)
		{
			if (random.nextInt(30) == 0)
			{
				@SuppressWarnings("unchecked")
				List<Entity> list =  (List<Entity>) worldObj.getEntitiesWithinAABB(EntityEnderman.class,
						AxisAlignedBB.getBoundingBox(xCoord - 9, yCoord - 3, zCoord - 9, xCoord + 9, yCoord + 3, zCoord + 9));

				if (list.isEmpty())
				{
					EntityEnderman enderman = new EntityEnderman(worldObj);
					enderman.setLocationAndAngles(xCoord + 0.5, yCoord - 1, zCoord + 0.5, 5, 6);
					worldObj.spawnEntityInWorld(enderman);
				}				
			}
		}
		else
		{
			this.isNearRift = false;
		}
	}

	public void closeRift()
	{
		NewDimData dimension = PocketManager.getDimensionData(worldObj);
		if (count2 > 20 && count2 < 22)
		{			 
			nearestRiftData = dimension.findNearestRift(worldObj, 10, xCoord, yCoord, zCoord);
			if (this.nearestRiftData != null)
			{
				Point4D location = nearestRiftData.source();
				TileEntityRift rift = (TileEntityRift) worldObj.getBlockTileEntity(location.getX(), location.getY(), location.getZ());
				if (rift != null)
				{
					rift.shouldClose = true;
				}
			}
		}
		if (count2 > 40)
		{
			this.invalidate();
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			if (dimension.getLink(xCoord, yCoord, zCoord) != null)
			{
				dimension.deleteLink(xCoord, yCoord, zCoord);
				worldObj.playSound(xCoord, yCoord, zCoord, "mods.DimDoors.sfx.riftClose", (float) .7, 1, true);
			}	
		}
		count2++; 
	}

	public void calculateOldParticleOffset()
	{
		nearestRiftData = PocketManager.getDimensionData(worldObj).findNearestRift(worldObj, 5, xCoord, yCoord, zCoord);
		if (nearestRiftData != null) 
		{
			Point4D location = nearestRiftData.source();
			this.xOffset = this.xCoord - location.getX();
			this.yOffset = this.yCoord - location.getY();
			this.zOffset = this.zCoord - location.getZ();
			this.distance = Math.abs(xOffset) + Math.abs(yOffset) + Math.abs(zOffset);
			this.isNearRift=true;

			if (!worldObj.isRemote && distance > 1)
			{
				try
				{
					grow(distance);
				}
				catch(Exception e)
				{

				}
			}
		}
	}
	
	public void grow(int distance)
	{
		if(worldObj.isRemote)
		{
			return;
		}
		int growCount=0;
		if(random.nextInt(distance*2)==0)
		{
			int x=0,y=0,z=0;
			while(growCount<100)
			{
				growCount++;
				x=this.xCoord+(1-(random.nextInt(2)*2)*random.nextInt(6));
				y=this.yCoord+(1-(random.nextInt(2)*2)*random.nextInt(4));
				z=this.zCoord+(1-(random.nextInt(2)*2)*random.nextInt(6));
				if(worldObj.isAirBlock(x, y, z))
				{
					break;
				}

			}
			if (growCount < 100)
			{
				NewDimData dimension = PocketManager.getDimensionData(worldObj);
				DimLink link = dimension.getLink(xCoord, yCoord, zCoord);
				if (link != null)
				{
					if (!this.hasGrownRifts && random.nextInt(3) == 0)
					{
						dimension.createChildLink(x, y, z, link);
						this.hasGrownRifts = true;
					}
				}
			}
		}
	}
	
	public void calculateNextRenderQuad(float age, Random rand)
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

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.renderingCenters= new HashMap<Integer, double[]>();
		this.count=nbt.getInteger("count");
		this.count2=nbt.getInteger("count2");
		this.xOffset = nbt.getInteger("xOffset");
		this.yOffset = nbt.getInteger("yOffset");
		this.zOffset = nbt.getInteger("zOffset");
		this.hasGrownRifts =nbt.getBoolean("grownRifts");
		this.age=nbt.getInteger("age");
		this.shouldClose=nbt.getBoolean("shouldClose");
		this.spawnedEndermenID = nbt.getInteger("spawnedEndermenID");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);      
		nbt.setInteger("hashMapSize", this.renderingCenters.size());
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

	@Override
	public Packet getDescriptionPacket() 
	{
		Packet132TileEntityData packet = new Packet132TileEntityData();
		packet.actionType = 0;
		packet.xPosition = xCoord;
		packet.yPosition = yCoord;
		packet.zPosition = zCoord;

		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		packet.customParam1 = nbt;
		return packet;
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) 
	{
		readFromNBT(pkt.customParam1);
	}
}
