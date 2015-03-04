package StevenDimDoors.mod_pocketDim.ticking;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.core.DDTeleporter;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import StevenDimDoors.mod_pocketDim.world.LimboProvider;
import StevenDimDoors.mod_pocketDim.world.PocketProvider;

public class MobMonolith extends EntityFlying implements IMob
{
	private static final short MAX_AGGRO = 250;
	private static final short MAX_AGGRO_CAP = 100;
	private static final short MIN_AGGRO_CAP = 25;
	private static final int MAX_TEXTURE_STATE = 18;
	private static final int MAX_SOUND_COOLDOWN = 200;
	private static final int MAX_AGGRO_RANGE = 35;
	private static final int AGGRO_WATCHER_INDEX = 16;
	
	private static final float WIDTH = 3f;
	private static final float HEIGHT = 3f;
	private static final float EYE_HEIGHT = HEIGHT / 2;
	
	public float pitchLevel;
	private short aggro = 0;
	private int soundTime = 0;
	private final short aggroCap;

	private static DDProperties properties = null;

	public MobMonolith(World world) 
	{
		super(world);
		this.setSize(WIDTH, HEIGHT);
		this.noClip = true;
		this.aggroCap = (short) MathHelper.getRandomIntegerInRange(this.rand, MIN_AGGRO_CAP, MAX_AGGRO_CAP);
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
		if (par1DamageSource != DamageSource.inWall)
		{
			this.aggro = MAX_AGGRO;
		}
		return false;
	}
	
	@Override
	public boolean canBreatheUnderwater()
    {
        return true;
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
		this.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.maxHealth).setBaseValue(57005);
	}

	@Override
	public boolean canBePushed()
	{
		return false;
	}
	
	@Override
	public float getEyeHeight()
	{
		return EYE_HEIGHT;
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		// Add a short for the aggro level
		this.dataWatcher.addObject(AGGRO_WATCHER_INDEX, Short.valueOf((short) 0));
	}

	@Override
	public boolean isEntityAlive()
	{
		return false;
	}

	@Override
	public void onEntityUpdate()
	{
		// Remove this Monolith if it's not in Limbo or in a pocket dimension
		if (!(this.worldObj.provider instanceof LimboProvider || this.worldObj.provider instanceof PocketProvider))
		{
			this.setDead();
			super.onEntityUpdate();
			return;
		}
		
		super.onEntityUpdate();
		
		// Check for players and update aggro levels even if there are no players in range
		EntityPlayer player = this.worldObj.getClosestPlayerToEntity(this, MAX_AGGRO_RANGE);
		boolean visibility = (player != null) ? player.canEntityBeSeen(this) : false;
		this.updateAggroLevel(player, visibility);
		
		// Change orientation and face a player if one is in range
		if (player != null)
		{
			this.facePlayer(player);
			if (!this.worldObj.isRemote && !(this.worldObj.provider instanceof LimboProvider))
			{
				// Play sounds on the server side, if the player isn't in Limbo.
				// Limbo is excluded to avoid drowning out its background music.
				// Also, since it's a large open area with many Monoliths, some
				// of the sounds that would usually play for a moment would
				// keep playing constantly and would get very annoying.
				this.playSounds(player);
			}

			if (visibility)
			{
				// Only spawn particles on the client side and outside Limbo
				if (this.worldObj.isRemote && !(this.worldObj.provider instanceof LimboProvider))
				{
					this.spawnParticles(player);
				}
				
				// Teleport the target player if various conditions are met
				if (aggro >= MAX_AGGRO && !this.worldObj.isRemote &&
						properties.MonolithTeleportationEnabled && !player.capabilities.isCreativeMode &&
						!(this.worldObj.provider instanceof LimboProvider))
				{
					this.aggro = 0;
					Point4D destination = LimboProvider.getLimboSkySpawn(player, properties);
					DDTeleporter.teleportEntity(player, destination, false);
					player.worldObj.playSoundAtEntity(player, mod_pocketDim.modid + ":crack", 13, 1);
				}
			}
		}
	}

	private void updateAggroLevel(EntityPlayer player, boolean visibility)
	{
		// If we're working on the server side, adjust aggro level
		// If we're working on the client side, retrieve aggro level from dataWatcher
		if (!this.worldObj.isRemote)
		{
			// Server side...
			// Rapidly increase the aggro level if this Monolith can see the player
			if (visibility)
			{
				if (this.worldObj.provider instanceof LimboProvider)
				{
					aggro++;
				}
				else
				{
					// Aggro increases faster outside of Limbo
					aggro += 3;
				}
			}
			else
			{
				if (aggro > aggroCap)
				{
					// Decrease aggro over time
					aggro--;
				}
				else if (player != null && (aggro < aggroCap))
				{
					// Increase aggro if a player is within range and aggro < aggroCap
					aggro++;
				}
			}
			// Clamp the aggro level
			aggro = (short) MathHelper.clamp_int(aggro, 0, MAX_AGGRO);
			this.dataWatcher.updateObject(AGGRO_WATCHER_INDEX, Short.valueOf(aggro));
		}
		else
		{
			// Client side...
			aggro = this.dataWatcher.getWatchableObjectShort(AGGRO_WATCHER_INDEX);
		}
	}
	
	public int getTextureState()
	{
		// Determine texture state from aggro progress
		return MathHelper.clamp_int(MAX_TEXTURE_STATE * aggro / MAX_AGGRO, 0, MAX_TEXTURE_STATE);
	}
	
	/**
	 * Plays sounds at different levels of aggro, using soundTime to prevent too many sounds at once. 
	 * @param entityPlayer
	 */
	private void playSounds(EntityPlayer entityPlayer)
	{
		float aggroPercent = this.getAggroProgress();
		if (this.soundTime <= 0)
		{
			this.playSound(mod_pocketDim.modid + ":monk",  1F, 1F);
			this.soundTime = 100;
		}
		if ((aggroPercent > 0.70) && this.soundTime < 100)
		{
			this.worldObj.playSoundEffect(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, mod_pocketDim.modid + ":tearing", 1F, (float) (1 + this.rand.nextGaussian()));
			this.soundTime = 100 + this.rand.nextInt(75);
		}
		if ((aggroPercent > 0.80) && this.soundTime < 200)
		{
			this.worldObj.playSoundEffect(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, mod_pocketDim.modid + ":tearing", 7, 1F);
			this.soundTime = 250;
		}
		this.soundTime--;
	}
	
	private void spawnParticles(EntityPlayer player)
	{
		int count = 10 * aggro / MAX_AGGRO;
		for (int i = 1; i < count; ++i)
		{
			player.worldObj.spawnParticle("portal", player.posX + (this.rand.nextDouble() - 0.5D) * this.width, 
					player.posY + this.rand.nextDouble() * player.height - 0.75D, 
					player.posZ + (this.rand.nextDouble() - 0.5D) * player.width, 
					(this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(),
					(this.rand.nextDouble() - 0.5D) * 2.0D);
		}
	}
	
	public float getAggroProgress()
	{
		return ((float) aggro) / MAX_AGGRO;
	}
	
	private void facePlayer(EntityPlayer player)
	{
		double d0 = player.posX - this.posX;
		double d1 = player.posZ - this.posZ;
		double d2 = (player.posY + player.getEyeHeight()) - (this.posY + this.getEyeHeight());
		double d3 = MathHelper.sqrt_double(d0 * d0 + d1 * d1);
		float f2 = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
		this.pitchLevel = (float) -((Math.atan(d2/d3) )* 180.0D / Math.PI);
		this.rotationYaw =  f2;
		this.rotationYawHead = f2;
		this.renderYawOffset = this.rotationYaw;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound rootTag)
	{
		super.writeEntityToNBT(rootTag);
		rootTag.setInteger("Aggro", this.aggro);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound rootTag)
	{
		super.readEntityFromNBT(rootTag);
		
		// Load Monoliths with half aggro so they don't teleport players instantly
		this.aggro = (short) (rootTag.getInteger("Aggro") / 2);
	}

	@Override
	public boolean getCanSpawnHere()
	{
		@SuppressWarnings("rawtypes")
		List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getBoundingBox( this.posX-15, posY-4, this.posZ-15, this.posX+15, this.posY+15, this.posZ+15));

		if (this.worldObj.provider.dimensionId == DDProperties.instance().LimboDimensionID)
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
}