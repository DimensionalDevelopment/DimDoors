package StevenDimDoors.mod_pocketDim.ticking;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet34EntityTeleport;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.dimHelper;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.pocketProvider;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MobObelisk extends EntityFlying implements IMob
{

	float soundTime = 0;
	int aggro = 0;
	byte textureState = 0;
	boolean hasJumped= false;
	
	int destX=0;
	int destY=0;
	int destZ=0;
	public MobObelisk(World par1World) 
	{
		super(par1World);
		this.texture="/mods/DimensionalDoors/textures/mobs/Monolith0.png";
		this.setSize(2F, 8.0F);
		this.noClip=true;
		
		
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getMaxHealth() 
	{
		// TODO Auto-generated method stub
		return 20;
	}
	
	 public void setEntityPosition(Entity entity, double x, double y, double z)
	 {
		 entity.lastTickPosX = entity.prevPosX = entity.posX = x;
		 entity.lastTickPosY = entity.prevPosY = entity.posY = y + (double)entity.yOffset;
		 entity.lastTickPosZ = entity.prevPosZ = entity.posZ = z;
		 entity.setPosition(x, y, z);
	 }
	 
	  protected void entityInit()
	  {
		  super.entityInit();
	      this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
	      
	    
	  }
	  
	  @SideOnly(Side.CLIENT)

	    /**
	     * Returns render size modifier
	     */
	   
	  
	@Override
	public void onEntityUpdate()
	{
		 byte b0 = this.dataWatcher.getWatchableObjectByte(16);
		

	     	this.texture="/mods/DimensionalDoors/textures/mobs/Monolith"+b0+".png";
	     	  if(!this.hasJumped&&!this.worldObj.isRemote)
			  {
	     		 int sanity=0;
	     		 double jumpHeight=0;
	     		  do
	     		  {
	     			 jumpHeight = this.posY+rand.nextInt(25);
	     			 sanity++;
	     		  }
	     		  while(!this.worldObj.isAirBlock((int)this.posX,(int)jumpHeight+6 , (int)this.posZ)&&sanity<20);
				  this.hasJumped=true;
				  
				  this.setLocationAndAngles(this.posX,jumpHeight , this.posZ, this.rotationPitch, this.rotationYaw);
				  PacketDispatcher.sendPacketToAllInDimension(new Packet34EntityTeleport(this), this.worldObj.provider.dimensionId);
				  this.worldObj.updateEntity(this);
			  }
	        
		super.onEntityUpdate();
		
		 if (this.isEntityAlive() && this.isEntityInsideOpaqueBlock())
	        {
	        	this.pushOutOfBlocks(this.posX - (double)this.width * 0.35D, this.boundingBox.minY + 0.5D, this.posZ + (double)this.width * 0.35D);
	            this.pushOutOfBlocks(this.posX - (double)this.width * 0.35D, this.boundingBox.minY + 0.5D, this.posZ - (double)this.width * 0.35D);
	            this.pushOutOfBlocks(this.posX + (double)this.width * 0.35D, this.boundingBox.minY + 0.5D, this.posZ - (double)this.width * 0.35D);
	            this.pushOutOfBlocks(this.posX + (double)this.width * 0.35D, this.boundingBox.minY + 0.5D, this.posZ + (double)this.width * 0.35D);
	        }
		 

		
		  
		EntityPlayer entityPlayer = this.worldObj.getClosestPlayerToEntity(this, 25);

		if(entityPlayer != null)		
		{
			
			
				
			
			this.faceEntity(entityPlayer, 1, 1);
			
			if(shouldAttackPlayer(entityPlayer))
			{
				if(soundTime<=0)
				{	
					if(this.worldObj.isRemote)
					{
						FMLClientHandler.instance().getClient().sndManager.playEntitySound("mods.DimensionalDoors.sounds.Monolith", this, 1, 1, false);
					}
					soundTime=100;
				}
				if(aggro<470)
				{
					if(rand.nextBoolean())
					{
						aggro++;
					}
					else if (rand.nextBoolean())
					{
						aggro++;
						aggro++;
					}
					else if (rand.nextBoolean())
					{
						aggro++;
					}
					
					if(this.worldObj.provider instanceof pocketProvider)
					{
						aggro++;
						if(aggro==455)
						{
							FMLClientHandler.instance().getClient().sndManager.playSoundFX("mods.DimensionalDoors.sounds.wylkermaxcrack", 12, 1);

						}
						aggro++;
						if(aggro==455)
						{
							FMLClientHandler.instance().getClient().sndManager.playSoundFX("mods.DimensionalDoors.sounds.wylkermaxcrack", 12, 1);

						}
					}
					if(aggro>455&&aggro<460)
					{
						FMLClientHandler.instance().getClient().sndManager.playSoundFX("mods.DimensionalDoors.sounds.wylkermaxcrack", 12, 1);

					}
					
					
				}
				else
				{
					if(this.worldObj.isRemote)
					{
						FMLClientHandler.instance().getClient().sndManager.stopEntitySound(this);
					}
					LinkData link = new LinkData(this.worldObj.provider.dimensionId, mod_pocketDim.limboDimID, (int)this.posX, (int)this.posY, (int)this.posZ, (int)this.posX+rand.nextInt(500)-250, (int)this.posY+500, (int)this.posZ+rand.nextInt(500)-250, false,0);
					dimHelper.instance.teleportToPocket(worldObj, link, entityPlayer);
					
					
				}
				
			}
			
			
		}
		else 
		{
			if(this.worldObj.isRemote)
			{
				FMLClientHandler.instance().getClient().sndManager.stopEntitySound(this);
			}
			if(aggro>0)
			{
				aggro--;
			}
		}
		if(soundTime>0)
		{
			soundTime--;
		}
		
		if(this.prevPosX==this.posX||this.prevPosY==this.posY||this.prevPosZ==this.posZ)
		{
			do
			{
				destX= rand.nextInt(40)-20;
				destY= rand.nextInt(40)-20;
				destZ= rand.nextInt(40)-20;
				
				
			}
			while(!this.isCourseTraversable(destX, destY, destZ, 1));
		}
		
		
		if(Math.abs(this.posX)-Math.abs(this.destX)+Math.abs(this.posY)-Math.abs(this.destY)+Math.abs(this.posZ)-Math.abs(this.destZ)<5)
		{
			do
			{
				destX= rand.nextInt(40)-20;
				destY= rand.nextInt(40)-20;
				destZ= rand.nextInt(40)-20;
				
				
			}
			while(!this.isCourseTraversable(destX, destY, destZ, 1));
		}
		
		{
			
		
		
		//	this.moveEntity(this.posX=this.posX+(posX-destX)/200, 	this.posY=this.posY+(posY+destY)/200, 	this.posZ=this.posZ+(posZ+destZ)/200);

		}
		
		
		this.textureState= (byte) (this.aggro/25);
		 if(!this.worldObj.isRemote)
		 {

			 this.dataWatcher.updateObject(16, Byte.valueOf(this.textureState));
		 }
		
	
	
		
		

	}
	  public boolean getCanSpawnHere()
	    {
		  	List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this,AxisAlignedBB.getBoundingBox( this.posX-15, posY-4, this.posZ-15, this.posX+15, this.posY+15, this.posZ+15));
	      
		  	if(list.size()>2)
		  	{
		  		return false;
		  	}
		  	return this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox);
	    }
	
	 private boolean shouldAttackPlayer(EntityPlayer par1EntityPlayer)
	    {
		 return par1EntityPlayer.canEntityBeSeen(this);
	        
	    }
	 
	 
	
	  private boolean isCourseTraversable(double par1, double par3, double par5, double par7)
	    {
	        double d4 = (par1 - this.posX) / par7;
	        double d5 = (par3 - this.posY) / par7;
	        double d6 = (par5 - this.posZ) / par7;
	        AxisAlignedBB axisalignedbb = this.boundingBox.copy();

	        for (int i = 1; (double)i < par7; ++i)
	        {
	            axisalignedbb.offset(d4, d5, d6);

	            if (!this.worldObj.getCollidingBoundingBoxes(this, axisalignedbb).isEmpty())
	            {
	                return false;
	            }
	        }

	        return true;
	    }
	  public void faceEntity(Entity par1Entity, float par2, float par3)
	    {
	        double d0 = par1Entity.posX - this.posX;
	        double d1 = par1Entity.posZ - this.posZ;
	        double d2;

	        if (par1Entity instanceof EntityLiving)
	        {
	            EntityLiving entityliving = (EntityLiving)par1Entity;
	            d2 = entityliving.posY + (double)entityliving.getEyeHeight() - (this.posY + (double)this.getEyeHeight());
	        }
	        else
	        {
	            d2 = (par1Entity.boundingBox.minY + par1Entity.boundingBox.maxY)  - (this.posY + (double)this.getEyeHeight());
	        }

	        double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d1 * d1);
	        float f2 = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
	        float f3 = (float)(-(Math.atan2(d2, d3) * 180.0D / Math.PI));
	        this.rotationYaw =  f2;
	        this.rotationYawHead=f2;
			this.renderYawOffset=this.rotationYaw;

	    }
	  
	  private float updateRotation(float par1, float par2, float par3)
	    {
	        float f3 = MathHelper.wrapAngleTo180_float(par2 - par1);

	        if (f3 > par3)
	        {
	            f3 = par3;
	        }

	        if (f3 < -par3)
	        {
	            f3 = -par3;
	        }

	        return par1 + f3;
	    }
	  
	  public float getRotationYawHead()
	    {
	        return 0.0F;
	    }

	
	
}