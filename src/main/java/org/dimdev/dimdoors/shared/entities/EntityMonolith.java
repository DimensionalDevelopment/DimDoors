package org.dimdev.dimdoors.shared.entities;

import org.dimdev.dimdoors.shared.sound.ModSounds;
import org.dimdev.dimdoors.shared.DDConfig;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.TeleportUtils;
import org.dimdev.dimdoors.shared.world.limbodimension.WorldProviderLimbo;
import org.dimdev.dimdoors.shared.world.pocketdimension.WorldProviderDungeonPocket;
import org.dimdev.dimdoors.shared.world.pocketdimension.WorldProviderPublicPocket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

import static net.minecraft.network.datasync.DataSerializers.*;

public class EntityMonolith extends EntityFlying implements IMob {

    private static final int MAX_AGGRO = 250;
    private static final int MAX_AGGRO_CAP = 100;
    private static final int MIN_AGGRO_CAP = 25;
    private static final int MAX_TEXTURE_STATE = 18;
    private static final int MAX_SOUND_COOLDOWN = 200;
    private static final int MAX_AGGRO_RANGE = 35;
    private static final DataParameter<Integer> AGGRO = EntityDataManager.createKey(EntityMonolith.class, VARINT);

    private static final float WIDTH = 3f;
    private static final float HEIGHT = 3f;
    private static final float EYE_HEIGHT = HEIGHT / 2;

    public float pitchLevel;
    private int aggro = 0;
    private int soundTime = 0;
    private final int aggroCap;

    public EntityMonolith(World world) {
        super(world);
        setSize(WIDTH, HEIGHT);
        noClip = true;
        aggroCap = MathHelper.getInt(rand, MIN_AGGRO_CAP, MAX_AGGRO_CAP);
        setEntityInvulnerable(true);
    }

    public boolean isDangerous() {
        return DDConfig.isMonolithTeleportationEnabled() && (world.provider instanceof WorldProviderLimbo || DDConfig.isDangerousLimboMonolithsEnabled());
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source != DamageSource.IN_WALL) {
            aggro = MAX_AGGRO;
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
    public AxisAlignedBB getCollisionBox(Entity entityIn) {
        return null;
    }

    @Override
    public boolean canDespawn() {
        return false;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(57005);
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
        dataManager.register(AGGRO, 0);
    }

    @Override
    public boolean isEntityAlive() {
        return false;
    }

    @Override
    public void onEntityUpdate() {
        // Remove this Monolith if it's not in Limbo or in a pocket dungeon
        if (!(world.provider instanceof WorldProviderLimbo || world.provider instanceof WorldProviderDungeonPocket)) {
            setDead();
            super.onEntityUpdate();
            return;
        }

        super.onEntityUpdate();

        // Check for players and update aggro levels even if there are no players in range
        EntityPlayer player = world.getClosestPlayerToEntity(this, MAX_AGGRO_RANGE);
        boolean visibility = player != null && player.canEntityBeSeen(this);
        updateAggroLevel(player, visibility);

        // Change orientation and face a player if one is in range
        if (player != null) {
            facePlayer(player);
            if (!world.isRemote && isDangerous()) {
                // Play sounds on the server side, if the player isn't in Limbo.
                // Limbo is excluded to avoid drowning out its background music.
                // Also, since it's a large open area with many Monoliths, some
                // of the sounds that would usually play for a moment would
                // keep playing constantly and would get very annoying.
                playSounds(player);
            }

            if (visibility) {
                // Only spawn particles on the client side and outside Limbo
                if (world.isRemote && isDangerous()) {
                    spawnParticles(player);
                }

                // Teleport the target player if various conditions are met
                if (aggro >= MAX_AGGRO && !world.isRemote && DDConfig.isMonolithTeleportationEnabled() && !player.capabilities.isCreativeMode && isDangerous()) {
                    aggro = 0;
                    Location destination = WorldProviderLimbo.getLimboSkySpawn(player);
                    TeleportUtils.teleport(player, destination, 0, 0);
                    player.world.playSound(player, player.getPosition(), ModSounds.CRACK, SoundCategory.HOSTILE, 13, 1);
                }
            }
        }
    }

    private void updateAggroLevel(EntityPlayer player, boolean visibility) {
        // If we're working on the server side, adjust aggro level
        // If we're working on the client side, retrieve aggro level from dataWatcher
        if (!world.isRemote) {
            // Server side...
            // Rapidly increase the aggro level if this Monolith can see the player
            if (visibility) {
                if (world.provider instanceof WorldProviderLimbo) {
                    if (isDangerous()) {
                        aggro++;
                    } else {
                        aggro += 36;
                    }
                } else {
                    // Aggro increases faster outside of Limbo
                    aggro += 3;
                }
            } else {
                if (isDangerous()) {
                    if (aggro > aggroCap) {
                        // Decrease aggro over time
                        aggro--;
                    } else if (player != null && aggro < aggroCap) {
                        // Increase aggro if a player is within range and aggro < aggroCap
                        aggro++;
                    }
                } else {
                    aggro -= 3;
                }
            }
            // Clamp the aggro level
            int maxAggro = isDangerous() ? MAX_AGGRO : 180;
            aggro = (short) MathHelper.clamp(aggro, 0, maxAggro);
            dataManager.set(AGGRO, aggro);
        } else {
            // Client side...
            aggro = dataManager.get(AGGRO);
        }
    }

    public int getTextureState() {
        // Determine texture state from aggro progress
        return MathHelper.clamp(MAX_TEXTURE_STATE * aggro / MAX_AGGRO, 0, MAX_TEXTURE_STATE);
    }

    /**
     * Plays sounds at different levels of aggro, using soundTime to prevent too many sounds at once.
     *
     * @param entityPlayer
     */
    private void playSounds(EntityPlayer entityPlayer) {
        float aggroPercent = getAggroProgress();
        if (soundTime <= 0) {
            playSound(ModSounds.MONK, 1F, 1F);
            soundTime = 100;
        }
        if (aggroPercent > 0.70 && soundTime < 100) {
            world.playSound(entityPlayer, entityPlayer.getPosition(), ModSounds.TEARING, SoundCategory.HOSTILE, 1F, (float) (1 + rand.nextGaussian()));
            soundTime = 100 + rand.nextInt(75);
        }
        if (aggroPercent > 0.80 && soundTime < MAX_SOUND_COOLDOWN) {
            world.playSound(entityPlayer, entityPlayer.getPosition(), ModSounds.TEARING, SoundCategory.HOSTILE, 7, 1F);
            soundTime = 250;
        }
        soundTime--;
    }

    private void spawnParticles(EntityPlayer player) {
        int count = 10 * aggro / MAX_AGGRO;
        for (int i = 1; i < count; ++i) {
            player.world.spawnParticle(EnumParticleTypes.PORTAL, player.posX + (rand.nextDouble() - 0.5D) * width,
                    player.posY + rand.nextDouble() * player.height - 0.75D,
                    player.posZ + (rand.nextDouble() - 0.5D) * player.width,
                    (rand.nextDouble() - 0.5D) * 2.0D, -rand.nextDouble(),
                    (rand.nextDouble() - 0.5D) * 2.0D);
        }
    }

    public float getAggroProgress() {
        return (float) aggro / MAX_AGGRO;
    }

    private void facePlayer(EntityPlayer player) {
        double d0 = player.posX - posX;
        double d1 = player.posZ - posZ;
        double d2 = player.posY + player.getEyeHeight() - (posY + EYE_HEIGHT);
        double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1);
        float f2 = (float) (Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
        pitchLevel = (float) -(Math.atan(d2 / d3) * 180.0D / Math.PI);
        rotationYaw = f2;
        rotationYawHead = f2;
        renderYawOffset = rotationYaw;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("Aggro", aggro);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);

        // Load Monoliths with half aggro so they don't teleport players instantly
        aggro = nbt.getInteger("Aggro") / 2;
    }

    @Override
    public boolean getCanSpawnHere() {
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, new AxisAlignedBB(posX - 15, posY - 4, posZ - 15, posX + 15, posY + 15, posZ + 15));

        if (world.provider instanceof WorldProviderLimbo) {
            if (list.size() > 0) {
                return false;
            }
        } else if (world.provider instanceof WorldProviderPublicPocket) {
            if (list.size() > 5 || world.canBlockSeeSky(new BlockPos(posX, posY, posZ))) {
                return false;
            }
        }

        return world.checkNoEntityCollision(getCollisionBoundingBox()) && world.getCollisionBoxes(this, getEntityBoundingBox()).isEmpty() && !world.containsAnyLiquid(getCollisionBoundingBox());
    }
}
