package com.zixiken.dimdoors.shared.entities;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.server.sound.DDSounds;
import com.zixiken.dimdoors.shared.DDConfig;
import com.zixiken.dimdoors.shared.TeleporterDimDoors;
import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.shared.world.PocketProvider;
import com.zixiken.dimdoors.shared.world.limbodimension.WorldProviderLimbo;
import com.zixiken.dimdoors.shared.world.pocketdimension.WorldProviderPublicPocket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

import static net.minecraft.network.datasync.DataSerializers.*;

public class MobMonolith extends EntityFlying implements IMob
{
    private static final int MAX_AGGRO = 250;
    private static final int MAX_AGGRO_CAP = 100;
    private static final int MIN_AGGRO_CAP = 25;
    private static final int MAX_TEXTURE_STATE = 18;
    private static final int MAX_SOUND_COOLDOWN = 200;
    private static final int MAX_AGGRO_RANGE = 35;
    private static final DataParameter<Integer> AGGRO = EntityDataManager.<Integer>createKey(MobMonolith.class, VARINT);

    private static final float WIDTH = 3f;
    private static final float HEIGHT = 3f;
    private static final float EYE_HEIGHT = HEIGHT / 2;

    public float pitchLevel;
    private int aggro = 0;
    private int soundTime = 0;
    private final int aggroCap;

    public MobMonolith(World world)
    {
        super(world);
        this.setSize(WIDTH, HEIGHT);
        this.noClip = true;
        this.aggroCap = MathHelper.getInt(this.rand, MIN_AGGRO_CAP, MAX_AGGRO_CAP);
    }

    public boolean isDangerous() {
        return DDConfig.isMonolithTeleportationEnabled() && (world.provider instanceof WorldProviderLimbo || !DDConfig.isDangerousLimboMonolithsDisabled());
    }

    @Override
    protected void damageEntity(DamageSource par1DamageSource, float par2) {}

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float par2) {
        if (damageSource != DamageSource.IN_WALL) {
            this.aggro = MAX_AGGRO;
        }
        return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return null;
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity par1Entity) {
        return null;
    }

    @Override
    public boolean canDespawn() {
        return false;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(57005);
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public float getEyeHeight() {
        return EYE_HEIGHT;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        // Add a short for the aggro level
        this.dataManager.register(AGGRO, 0);
    }

    @Override
    public boolean isEntityAlive()
    {
        return false;
    }

    @Override
    public void onEntityUpdate() {
        // Remove this Monolith if it's not in Limbo or in a pocket dimension
        if (!(this.world.provider instanceof WorldProviderLimbo)) {
            this.setDead();
            super.onEntityUpdate();
            return;
        }

        super.onEntityUpdate();

        // Check for players and update aggro levels even if there are no players in range
        EntityPlayer player = this.world.getClosestPlayerToEntity(this, MAX_AGGRO_RANGE);
        boolean visibility = (player != null) && player.canEntityBeSeen(this);
        this.updateAggroLevel(player, visibility);

        // Change orientation and face a player if one is in range
        if (player != null)
        {
            this.facePlayer(player);
            if (!this.world.isRemote && isDangerous())
            {
                // Play sounds on the server side, if the player isn't in Limbo.
                // Limbo is excluded to avoid drowning out its background music.
                // Also, since it's a large open area with many Monoliths, some
                // of the sounds that would usually play for a moment would
                // keep playing constantly and would get very annoying.
                playSounds(player);
            }

            if (visibility)
            {
                // Only spawn particles on the client side and outside Limbo
                if (world.isRemote && isDangerous()) {
                    this.spawnParticles(player);
                }

                // Teleport the target player if various conditions are met
                if (aggro >= MAX_AGGRO && !world.isRemote && DDConfig.isMonolithTeleportationEnabled() && !player.capabilities.isCreativeMode && isDangerous()) {
                    this.aggro = 0;
                    Location destination = WorldProviderLimbo.getLimboSkySpawn(player, world);
                    TeleporterDimDoors.instance().teleport(player, destination);
                    player.world.playSound(player, player.getPosition(), DDSounds.CRACK, SoundCategory.HOSTILE, 13, 1);
                }
            }
        }
    }

    private void updateAggroLevel(EntityPlayer player, boolean visibility) {
        // If we're working on the server side, adjust aggro level
        // If we're working on the client side, retrieve aggro level from dataWatcher
        if (!this.world.isRemote) {
            // Server side...
            // Rapidly increase the aggro level if this Monolith can see the player
            if (visibility) {
                if (world.provider instanceof WorldProviderLimbo) {
                    if (isDangerous())
                        aggro++;
                    else
                        aggro += 36;
                } else {
                    // Aggro increases faster outside of Limbo
                    aggro += 3;
                }
            } else {
                if (isDangerous()) {
                    if (aggro > aggroCap) {
                        // Decrease aggro over time
                        aggro--;
                    } else if (player != null && (aggro < aggroCap)) {
                        // Increase aggro if a player is within range and aggro < aggroCap
                        aggro++;
                    }
                } else
                    aggro -= 3;
            }
            // Clamp the aggro level
            int maxAggro = isDangerous()?MAX_AGGRO:180;
            aggro = (short) MathHelper.clamp(aggro, 0, maxAggro);
            this.dataManager.set(AGGRO, aggro);
        } else {
            // Client side...
            aggro = this.dataManager.get(AGGRO);
        }
    }

    public int getTextureState()
    {
        // Determine texture state from aggro progress
        return MathHelper.clamp(MAX_TEXTURE_STATE * aggro / MAX_AGGRO, 0, MAX_TEXTURE_STATE);
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
            playSound(DDSounds.MONK, 1F, 1F);
            this.soundTime = 100;
        }
        if ((aggroPercent > 0.70) && this.soundTime < 100)
        {
            world.playSound(entityPlayer, entityPlayer.getPosition(), DDSounds.TEARING, SoundCategory.HOSTILE, 1F, (float) (1 + this.rand.nextGaussian()));
            this.soundTime = 100 + this.rand.nextInt(75);
        }
        if ((aggroPercent > 0.80) && this.soundTime < MAX_SOUND_COOLDOWN) {
            world.playSound(entityPlayer, entityPlayer.getPosition(), DDSounds.TEARING, SoundCategory.HOSTILE, 7, 1F);
            this.soundTime = 250;
        }
        this.soundTime--;
    }

    private void spawnParticles(EntityPlayer player)
    {
        int count = 10 * aggro / MAX_AGGRO;
        for (int i = 1; i < count; ++i)
        {
            player.world.spawnParticle(EnumParticleTypes.PORTAL, player.posX + (this.rand.nextDouble() - 0.5D) * this.width,
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
        double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1);
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
        aggro = rootTag.getInteger("Aggro") / 2;
    }

    @Override
    public boolean getCanSpawnHere() {
        List list = world.getEntitiesWithinAABBExcludingEntity(this, new AxisAlignedBB( this.posX-15, posY-4, this.posZ-15, this.posX+15, this.posY+15, this.posZ+15));

        if (world.provider instanceof WorldProviderLimbo) {
            if(list.size() > 0) {
                return false;
            }

        }
        else if(world.provider instanceof WorldProviderPublicPocket)
        {
            if (list.size() > 5 || world.canBlockSeeSky(new BlockPos(posX, posY, posZ))) {
                return false;
            }
        }

        return world.checkNoEntityCollision(getCollisionBoundingBox()) && world.getCollisionBoxes(this, getEntityBoundingBox()).isEmpty() && !world.containsAnyLiquid(getCollisionBoundingBox());
    }
}