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
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.core.DDTeleporter;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import StevenDimDoors.mod_pocketDim.world.LimboProvider;
import StevenDimDoors.mod_pocketDim.world.PocketProvider;

public class MobMonolith extends EntityFlying implements IMob
{
	public int aggro = 0;
	private float soundTime = 0;
	private byte textureState = 0;
	private float scaleFactor = 0;
	private int aggroMax;

	private static DDProperties properties = null;

	public MobMonolith(World par1World) 
	{
		super(par1World);
		this.setSize(3F, 9.0F);
		this.noClip=true;
		this.scaleFactor = (float) ((rand.nextDouble()/2)+1);
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

	public boolean canBePushed()
	{
		return false;
	}

	@Override
	public float getRenderSizeModifier()
	{
		return this.scaleFactor;
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

	public boolean isClipping()
	{

		int i = MathHelper.floor_double(this.boundingBox.minX);
		int j = MathHelper.floor_double(this.boundingBox.maxX + 1.0D);
		int k = MathHelper.floor_double(this.boundingBox.minY);
		int l = MathHelper.floor_double(this.boundingBox.maxY + 1.0D);
		int i1 = MathHelper.floor_double(this.boundingBox.minZ);
		int j1 = MathHelper.floor_double(this.boundingBox.maxZ + 1.0D);

		for (int k1 = i; k1 < j; ++k1)
		{
			for (int l1 = k; l1 < l; ++l1)
			{
				for (int i2 = i1; i2 < j1; ++i2)
				{
					if(!this.worldObj.isAirBlock(k1, l1, i2))
					{
						return true;
					}
				}
			}
		}


		return false;
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
		if(this.isClipping())
		{
			this.moveEntity(0, .1, 0);
		}

		EntityPlayer entityPlayer = this.worldObj.getClosestPlayerToEntity(this, 35);

		if (entityPlayer != null)
		{
			if(this.soundTime<=0)
			{
				this.playSound(mod_pocketDim.modid+":monk",  1F, 1F);
				this.soundTime=100;
			}

			this.faceEntity(entityPlayer, 1, 1);

			if (shouldAttackPlayer(entityPlayer))
			{
				if (aggro<470)
				{
					if (rand.nextInt(11)>this.textureState||this.aggro>=300||rand.nextInt(13)>this.textureState&&this.aggroMax>this.aggro)
					{
						aggro++;
						aggro++;

					}
					if (this.worldObj.provider instanceof PocketProvider||this.worldObj.getClosestPlayerToEntity(this, 5)!=null)
					{
						aggro++;

					}
					if (aggro>430&&this.soundTime<100)
					{
						this.worldObj.playSoundEffect(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ,mod_pocketDim.modid+":tearing",2F, 1F);
						this.soundTime=100;
					}
					if (aggro>445&&this.soundTime<200)
					{
						this.worldObj.playSoundEffect(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ,mod_pocketDim.modid+":tearing",5F, 1F);
						this.soundTime=200;
					}
				}
				else if (!this.worldObj.isRemote && properties.MonolithTeleportationEnabled && !entityPlayer.capabilities.isCreativeMode)
				{
					ChunkCoordinates coords = LimboProvider.getLimboSkySpawn(entityPlayer.worldObj.rand);
					Point4D destination = new Point4D((int) (coords.posX+entityPlayer.posX), coords.posY, (int) (coords.posZ+entityPlayer.posZ ), mod_pocketDim.properties.LimboDimensionID);
					DDTeleporter.teleportEntity(entityPlayer, destination, false);

					this.aggro = 0;
					entityPlayer.worldObj.playSoundAtEntity(entityPlayer,mod_pocketDim.modid+":crack",13, 1);
				}
				if (!(this.worldObj.provider instanceof LimboProvider || this.worldObj.getClosestPlayerToEntity(this, 5) != null) || this.aggro > 300)
				{
					for (int i = 0; i < -1+this.textureState/2; ++i)
					{
						entityPlayer.worldObj.spawnParticle("portal", entityPlayer.posX + (this.rand.nextDouble() - 0.5D) * this.width, entityPlayer.posY + this.rand.nextDouble() * entityPlayer.height - 0.75D, entityPlayer.posZ + (this.rand.nextDouble() - 0.5D) * entityPlayer.width, (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D);
					}
				}
			}
			else if(this.worldObj.provider instanceof PocketProvider)
			{
				if(aggro<this.aggroMax/2)
				{
					if(rand.nextInt(3)==0)
					{
						aggro++;
					}
				}
				else
				{
					if(aggro>0)
					{
						aggro--;
					}	
				}
			}
		}
		else 
		{
			if(aggro>0)
			{
				aggro--;

				if(rand.nextBoolean())
				{
					aggro--;
				}
			}
		}
		if (soundTime>=0)
		{
			soundTime--;
		}
		this.textureState= (byte) (this.aggro/25);
		if (!this.worldObj.isRemote)
		{
			this.dataWatcher.updateObject(16, Byte.valueOf(this.textureState));
		}
	}


	private boolean shouldAttackPlayer(EntityPlayer par1EntityPlayer)
	{
		return par1EntityPlayer.canEntityBeSeen(this);
	}

	public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
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
	public void faceEntity(Entity par1Entity, float par2, float par3)
	{
		double d0 = par1Entity.posX - this.posX;
		double d1 = par1Entity.posZ - this.posZ;
		double d2;

		if (par1Entity instanceof EntityLiving)
		{
			EntityLiving entityliving = (EntityLiving)par1Entity;
			d2 = entityliving.posY + entityliving.getEyeHeight() - (this.posY + this.getEyeHeight());
		}
		else
		{
			d2 = (par1Entity.boundingBox.minY + par1Entity.boundingBox.maxY)  - (this.posY + this.getEyeHeight());
		}

		double d3 = MathHelper.sqrt_double(d0 * d0 + d1 * d1);
		float f2 = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
		float f3 = (float)(-(Math.atan2(d2, d3) * 180.0D / Math.PI));
		this.rotationPitch =  f3;
		this.rotationYaw =  f2;

		this.rotationYaw =  f2;
		this.rotationYawHead=f2;
		this.renderYawOffset=this.rotationYaw;
	}

	@Override
	public float getRotationYawHead()
	{
		return 0.0F;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeEntityToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setFloat("soundTime", this.soundTime);
		par1NBTTagCompound.setInteger("aggro", this.aggro);
		par1NBTTagCompound.setInteger("aggroMax", this.aggroMax);
		par1NBTTagCompound.setByte("textureState", this.textureState);
		par1NBTTagCompound.setFloat("scaleFactor", this.scaleFactor);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readEntityFromNBT(par1NBTTagCompound);
		this.soundTime = par1NBTTagCompound.getFloat("soundTime");
		this.aggro = par1NBTTagCompound.getInteger("aggro");
		this.aggroMax = par1NBTTagCompound.getInteger("aggroMax");
		this.textureState = par1NBTTagCompound.getByte("textureState");
		this.scaleFactor = par1NBTTagCompound.getFloat("scaleFactor");
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