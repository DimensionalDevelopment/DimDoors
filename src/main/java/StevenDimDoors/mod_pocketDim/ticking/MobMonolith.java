package StevenDimDoors.mod_pocketDim.ticking;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.core.DDTeleporter;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import StevenDimDoors.mod_pocketDim.world.LimboProvider;
import StevenDimDoors.mod_pocketDim.world.PocketProvider;

public class MobMonolith extends EntityFlying implements IMob
{
	public static final int MAX_AGGRO_RANGE = 35;
	public static final int MAX_SOUND_COOLDOWN = 200;
	public static final float MAX_AGGRO = 100;
	public static final int TEXTURE_STATES = 18;
	public float pitchLevel;
	
	public float aggro = 0;
	private float soundTime = 0;
	private byte textureState = 0;
	
	private int aggroMax;

	private static DDProperties properties = null;

	public MobMonolith(World par1World) 
	{
		super(par1World);
		this.setSize(3F, 3F);
		this.noClip=true;
		this.aggroMax = rand.nextInt(245)+200;
		if (properties == null)
			properties = DDProperties.instance();
	}

	@Override
	protected void damageEntity(DamageSource par1DamageSource, float par2)
	{
		return;
	}

	@Override
	public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
	{
		if (par1DamageSource == DamageSource.inWall)
		{
			this.posY = posY + 1;
		}
		else
		{
			this.aggro = this.aggroMax;
		}
		return false;
	}
	@Override
	public AxisAlignedBB getBoundingBox()
	{
		return null;
	}
	
	@Override
	public AxisAlignedBB getCollisionBox(Entity par1Entity)
	{
		return null;
	}

	@Override
	public boolean canDespawn()
	{
		return false;
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.maxHealth).setAttribute(10);
	}

	@Override
	public boolean canBePushed()
	{
		return false;
	}

	public void setEntityPosition(Entity entity, double x, double y, double z)
	{
		entity.lastTickPosX = entity.prevPosX = entity.posX = x;
		entity.lastTickPosY = entity.prevPosY = entity.posY = y + entity.yOffset;
		entity.lastTickPosZ = entity.prevPosZ = entity.posZ = z;
		entity.setPosition(x, y, z);
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
	}

	@Override
	public boolean isEntityAlive()
	{
		return false;
	}

	@Override
	public void onEntityUpdate()
	{
		if (!(this.worldObj.provider instanceof LimboProvider || this.worldObj.provider instanceof PocketProvider))
		{
			this.setDead();
		}

		super.onEntityUpdate();

		EntityPlayer entityPlayer = this.worldObj.getClosestPlayerToEntity(this,MAX_AGGRO_RANGE);
		
		//need to always manage aggro level, even if player is out of range. 
		this.setAggroLevel(entityPlayer);

		//these things only matter if the player is in range. 
		if (entityPlayer != null)
		{
			this.faceEntity(entityPlayer, 1, 1);
			this.playSounds(entityPlayer);
			//teleport the player if the conditions are met
			if (aggro >= MAX_AGGRO && !this.worldObj.isRemote && properties.MonolithTeleportationEnabled && !entityPlayer.capabilities.isCreativeMode)
			{
				Point4D destination = LimboProvider.getLimboSkySpawn(entityPlayer, properties);
				DDTeleporter.teleportEntity(entityPlayer, destination, false);
				this.aggro = 0;
				entityPlayer.worldObj.playSoundAtEntity(entityPlayer,mod_pocketDim.modid+":crack",13, 1);
			}
		}
	}

	private void setAggroLevel(EntityPlayer player)
	{
		//aggro constantly decreases at a rate that varies with the current amount of aggro. 
		if(aggro > 0)
		{
			this.aggro = this.aggro -(this.aggro/25);
		}
		if(player != null)
		{
			//monoliths increase aggro slightly if the player is near, but slowly and to a cap. 
			float distance = this.getDistanceToEntity(player);
			aggro+= 1.5-(distance/this.MAX_AGGRO_RANGE);
			
			//rapidly increase aggro if the monolith has line of sight to the player.
			if(player.canEntityBeSeen(this))
			{				
				//prevent monoliths from teleporting the player in limbo
				if(this.worldObj.provider instanceof LimboProvider)
				{
					aggro+=1.5;
				}
				else
				{
					this.spawnParticles(player);
					aggro+=3;
				}
			}
		}
		
		//convert the aggro counter to one of the texture states, and set it.
		this.textureState = (byte) ((this.TEXTURE_STATES/this.MAX_AGGRO)*this.aggro);
		if(this.textureState>TEXTURE_STATES)
		{
			textureState = TEXTURE_STATES;
		}
		if (!this.worldObj.isRemote)
		{
			this.dataWatcher.updateObject(16, Byte.valueOf(this.textureState));
		}
	}
	
	/**
	 * Plays sounds at different levels of aggro, using soundTime to prevent too many sounds at once. 
	 * @param entityPlayer
	 */
	private void playSounds(EntityPlayer entityPlayer)
	{
		float aggroPercent = (aggro/MAX_AGGRO);
		if(this.soundTime<=0)
		{
			this.playSound(mod_pocketDim.modid+":monk",  1F, 1F);
			this.soundTime=100;
		}
		if ((aggroPercent>.80)&&this.soundTime<100)
		{
			this.worldObj.playSoundEffect(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ,mod_pocketDim.modid+":tearing",2, 1F);
			this.soundTime=200;
		}
		if ((aggroPercent>.95)&&this.soundTime<200)
		{
			this.worldObj.playSoundEffect(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ,mod_pocketDim.modid+":tearing",5, 1F);
			this.soundTime=250;
		}
		this.soundTime--;
	}
	
	private void spawnParticles(EntityPlayer player)
	{
		for (int i = 1; i < (10*(aggro/MAX_AGGRO)); ++i)
		{
			player.worldObj.spawnParticle("portal", player.posX + (this.rand.nextDouble() - 0.5D) * this.width, 
					player.posY + this.rand.nextDouble() * player.height - 0.75D, 
					player.posZ + (this.rand.nextDouble() - 0.5D) * player.width, 
					(this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(),
					(this.rand.nextDouble() - 0.5D) * 2.0D);
		}
	}
	
	@Override
	public void faceEntity(Entity par1Entity, float par2, float par3)
	{
		double d0 = par1Entity.posX - this.posX;
		double d1 = par1Entity.posZ - this.posZ;
		double d2 = (par1Entity.posY + par1Entity.getEyeHeight())  - (this.posY +this.getEyeHeight());
		double d3 = MathHelper.sqrt_double(d0 * d0 + d1 * d1);
		float f2 = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
		this.pitchLevel = (float)-((Math.atan(d2/d3) )* 180.0D / Math.PI);
		
		this.rotationYaw =  f2;
		this.rotationYawHead=f2;
		this.renderYawOffset=this.rotationYaw;
	}

	

	@Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeEntityToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setFloat("soundTime", this.soundTime);
		par1NBTTagCompound.setFloat("aggro", this.aggro);
		par1NBTTagCompound.setInteger("aggroMax", this.aggroMax);
		par1NBTTagCompound.setByte("textureState", this.textureState);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readEntityFromNBT(par1NBTTagCompound);
		this.soundTime = par1NBTTagCompound.getFloat("soundTime");
		
		//make them load with half aggro so they dont instantly teleport players
		this.aggro = par1NBTTagCompound.getFloat("aggro")/2;
		this.aggroMax = par1NBTTagCompound.getInteger("aggroMax");
		this.textureState = par1NBTTagCompound.getByte("textureState");
	}

	@Override
	public boolean getCanSpawnHere()
	{
		@SuppressWarnings("rawtypes")
		List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getBoundingBox( this.posX-15, posY-4, this.posZ-15, this.posX+15, this.posY+15, this.posZ+15));

		if(this.worldObj.provider.dimensionId==DDProperties.instance().LimboDimensionID)
		{
			if(list.size()>0)
			{
				return false;
			}

		}
		else if(this.worldObj.provider instanceof PocketProvider)	
		{
			if (list.size() > 5 ||
					this.worldObj.canBlockSeeTheSky((int)this.posX, (int)this.posY, (int)this.posZ))
			{
				return false;
			}
		}
		return this.worldObj.checkNoEntityCollision(this.boundingBox) &&
				this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() &&
				!this.worldObj.isAnyLiquid(this.boundingBox);
	}

	public DataWatcher getDataWatcher()
	{
		return this.dataWatcher;
	}
}