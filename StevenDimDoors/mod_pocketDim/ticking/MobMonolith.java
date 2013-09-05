package StevenDimDoors.mod_pocketDim.ticking;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.core.DDTeleporter;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import StevenDimDoors.mod_pocketDim.world.LimboProvider;
import StevenDimDoors.mod_pocketDim.world.PocketProvider;

public class MobMonolith extends EntityFlying implements IMob
{

	float soundTime = 0;
	int aggro = 0;
	byte textureState = 0;

	float scaleFactor = 0;
	int aggroMax;
	int destX=0;
	int destY=0;
	int destZ=0;

	public MobMonolith(World par1World) 
	{
		super(par1World);
		this.texture="/mods/DimDoors/textures/mobs/Monolith0.png";
		this.setSize(3F, 9.0F);
		this.noClip=true;
		this.scaleFactor= (float) ((rand.nextDouble()/2)+1);
		this.aggroMax=rand.nextInt(245)+200;

		if (properties == null)
			properties = DDProperties.instance();
	}

	private static DDProperties properties = null;

	@Override
	public boolean canDespawn()
	{
		return false;
	}

	@Override
	public int getMaxHealth() 
	{
		// TODO Auto-generated method stub
		return 20;
	}

	public boolean canBePushed()
	{
		return false;
	}

	public float getRenderSizeModifier()
	{
		return this.scaleFactor;
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

	@Override
	public void onEntityUpdate()
	{
		if (!(this.worldObj.provider instanceof LimboProvider || this.worldObj.provider instanceof PocketProvider))
		{
			this.setDead();
		}

		byte b0 = this.dataWatcher.getWatchableObjectByte(16);

		this.texture="/mods/DimDoors/textures/mobs/Monolith"+b0+".png";
		super.onEntityUpdate();

		if (this.isEntityAlive() && this.isEntityInsideOpaqueBlock())
		{
			this.setDead();
		}

		EntityPlayer entityPlayer = this.worldObj.getClosestPlayerToEntity(this, 30);

		if (entityPlayer != null)
		{
			if(this.soundTime<=0)
			{
				this.playSound("mods.DimDoors.sfx.monk",  1F, 1F);
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
					}
					if (this.worldObj.provider instanceof PocketProvider||this.worldObj.getClosestPlayerToEntity(this, 5)!=null)
					{
						aggro++;
						aggro++;

						if (rand.nextBoolean())
						{
							aggro++;
						}

					}
					if (aggro>430&&this.soundTime<100)
					{
						this.worldObj.playSoundEffect(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ,"mods.DimDoors.sfx.tearing",2F, 1F);
						this.soundTime=100;
					}
					if (aggro>445&&this.soundTime<200)
					{
						this.worldObj.playSoundEffect(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ,"mods.DimDoors.sfx.tearing",5F, 1F);
						this.soundTime=200;
					}
				}
				else if (!this.worldObj.isRemote && !entityPlayer.capabilities.isCreativeMode)
				{
					Point4D destination = new Point4D(
						(int) this.posX + MathHelper.getRandomIntegerInRange(rand, -250, 250),
						(int) this.posY + 500,
						(int) this.posZ + MathHelper.getRandomIntegerInRange(rand, -250, 250),
						properties.LimboDimensionID);
					DDTeleporter.teleportEntity(entityPlayer, destination);
					this.aggro = 0;

					entityPlayer.worldObj.playSoundAtEntity(entityPlayer,"mods.DimDoors.sfx.crack",13, 1);
				}
				if (!(this.worldObj.provider instanceof LimboProvider || this.worldObj.getClosestPlayerToEntity(this, 5) != null) || this.aggro > 300)
				{
					for (int i = 0; i < -1+this.textureState/2; ++i)
					{
						entityPlayer.worldObj.spawnParticle("portal", entityPlayer.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, entityPlayer.posY + this.rand.nextDouble() * (double)entityPlayer.height - 0.75D, entityPlayer.posZ + (this.rand.nextDouble() - 0.5D) * (double)entityPlayer.width, (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D);
					}
				}
			}
			else
			{
				if(aggro>0)
				{
					if(rand.nextInt(10)==0)
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
	
	public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
	{
		if(!(par1DamageSource==DamageSource.inWall))
		{
			this.aggro=400;
		}
		return false;
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
		this.rotationPitch =  f3;
		this.rotationYaw =  f2;

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
	
	public boolean getCanSpawnHere()
	{
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
			if(list.size()>5||this.worldObj.canBlockSeeTheSky((int)this.posX, (int)this.posY, (int)this.posZ))
			{
				return false;
			}
		}
		return this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox);
	}
}