package org.dimdev.dimdoors.entity;

import com.flowpowered.math.vector.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.world.limbo.LimboDimension;
import org.dimdev.dimdoors.world.pocketdimension.DungeonPocketDimension;
import org.dimdev.util.Location;
import org.dimdev.util.TeleportUtil;

import static net.minecraft.entity.attribute.EntityAttributes.MAX_HEALTH;

public class MonolithEntity extends MobEntity {
    private static final int MAX_AGGRO = 250;
    private static final int MAX_AGGRO_CAP = 100;
    private static final int MIN_AGGRO_CAP = 25;
    private static final int MAX_TEXTURE_STATE = 18;
    private static final int MAX_SOUND_COOLDOWN = 200;
    private static final int MAX_AGGRO_RANGE = 35;
    private static final TrackedData<Integer> AGGRO = DataTracker.registerData(MonolithEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final float EYE_HEIGHT = 1.5f;

    public float pitchLevel;
    private int aggro = 0;
    private int soundTime = 0;
    private final int aggroCap;

    public MonolithEntity(EntityType<? extends MonolithEntity> type, World world) {
        super(type, world);
        noClip = true;
        aggroCap = MathHelper.nextInt(getRandom(), MIN_AGGRO_CAP, MAX_AGGRO_CAP);
        setNoGravity(true);
    }

    public boolean isDangerous() {
        return false; //return ModConfig.MONOLITHS.monolithTeleportation && (world.dimension instanceof LimboDimension || ModConfig.MONOLITHS.dangerousLimboMonoliths);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source != DamageSource.IN_WALL) {
            aggro = MAX_AGGRO;
        }
        return false;
    }

    @Override
    public int getMaxAir() {
        return super.getMaxAir();
    }

    @Override
    protected int getNextAirUnderwater(int i) {
        return 10;
    }

    @Override
    protected int getNextAirOnLand(int i) {
        return 10;
    }

    @Override
    public Box getCollisionBox() {
        return null;
    }

    @Override
    public Box getHardCollisionBox(Entity entity) {
        return null;
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        getAttributes().get(MAX_HEALTH).setBaseValue(57005);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public double getEyeY() {
        return super.getEyeY();
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        // Add a short for the aggro level
        dataTracker.startTracking(AGGRO, 0);
    }

    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public void move(MovementType movementType, Vec3d vec3d) {
    }

    @Override
    protected void mobTick() {
        // Remove this Monolith if it's not in Limbo or in a pocket dungeon
        if (!(world.dimension instanceof LimboDimension || world.dimension instanceof DungeonPocketDimension)) {
            remove();
            super.mobTick();
            return;
        }

        super.mobTick();

        // Check for players and update aggro levels even if there are no players in range
        PlayerEntity player = world.getClosestPlayer(this, MAX_AGGRO_RANGE);
        boolean visibility = player != null && player.canSee(this);
        updateAggroLevel(player, visibility);

        System.out.println(String.format("Player is %s.", player));

        // Change orientation and face a player if one is in range
        if (player != null) {
            facePlayer(player);
            if (!world.isClient && isDangerous()) {
                // Play sounds on the server side, if the player isn't in Limbo.
                // Limbo is excluded to avoid drowning out its background music.
                // Also, since it's a large open area with many Monoliths, some
                // of the sounds that would usually play for a moment would
                // keep playing constantly and would get very annoying.
                playSounds(player.getPos());
            }

            if (visibility) {
                // Only spawn particles on the client side and outside Limbo
                if (world.isClient && isDangerous()) {
                    spawnParticles(player);
                }

                // Teleport the target player if various conditions are met
                if (aggro >= MAX_AGGRO && !world.isClient && ModConfig.MONOLITHS.monolithTeleportation && !player.isCreative() && isDangerous()) {
                    aggro = 0;
//                    Location destination = LimboDimension.getLimboSkySpawn(player);
//                    TeleportUtil.teleport(player, destination, 0, 0);
                    player.world.playSound(null, new BlockPos(player.getPos()), ModSoundEvents.CRACK, SoundCategory.HOSTILE, 13, 1);
                }
            }
        }
    }

    private void updateAggroLevel(PlayerEntity player, boolean visibility) {
        // If we're working on the server side, adjust aggro level
        // If we're working on the client side, retrieve aggro level from dataWatcher
        if (!world.isClient) {
            // Server side...
            // Rapidly increase the aggro level if this Monolith can see the player
            if (visibility) {
                if (world.dimension instanceof LimboDimension) {
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
            dataTracker.set(AGGRO, aggro);
        } else {
            // Client side...
            aggro = dataTracker.get(AGGRO);
        }
    }

    public int getTextureState() {
        // Determine texture state from aggro progress
        return MathHelper.clamp(MAX_TEXTURE_STATE * aggro / MAX_AGGRO, 0, MAX_TEXTURE_STATE);
    }

    /**
     * Plays sounds at different levels of aggro, using soundTime to prevent too many sounds at once.
     *
     * @param pos The position to play the sounds at
     */
    private void playSounds(Vec3d pos) {
        float aggroPercent = getAggroProgress();
        if (soundTime <= 0) {
            playSound(ModSoundEvents.MONK, 1F, 1F);
            soundTime = 100;
        }
        if (aggroPercent > 0.70 && soundTime < 100) {
            world.playSound(null, new BlockPos(pos), ModSoundEvents.TEARING, SoundCategory.HOSTILE, 1F, (float) (1 + getRandom().nextGaussian()));
            soundTime = 100 + getRandom().nextInt(75);
        }
        if (aggroPercent > 0.80 && soundTime < MAX_SOUND_COOLDOWN) {
            world.playSound(null, new BlockPos(pos), ModSoundEvents.TEARING, SoundCategory.HOSTILE, 7, 1F);
            soundTime = 250;
        }
        soundTime--;
    }

    private void spawnParticles(PlayerEntity player) {
        int count = 10 * aggro / MAX_AGGRO;
        for (int i = 1; i < count; ++i) {
            player.world.addParticle(ParticleTypes.PORTAL, player.getX() + (getRandom().nextDouble() - 0.5D) * getWidth(),
                    player.getY() + getRandom().nextDouble() * player.getHeight() - 0.75D,
                    player.getZ() + (getRandom().nextDouble() - 0.5D) * player.getWidth(),
                    (getRandom().nextDouble() - 0.5D) * 2.0D, -getRandom().nextDouble(),
                    (getRandom().nextDouble() - 0.5D) * 2.0D);
        }
    }

    public float getAggroProgress() {
        return (float) aggro / MAX_AGGRO;
    }

    private void facePlayer(PlayerEntity player) {
        double d0 = player.getX() - getX();
        double d1 = player.getZ() - getY();
        double d2 = player.getY() + player.getEyeHeight(player.getPose()) - (getY() + EYE_HEIGHT);
        double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1);
        float f2 = (float) (Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
        setRotation((float) -(Math.atan(d2 / d3) * 180.0D / Math.PI), f2);
        setYaw(f2);
        setHeadYaw(f2);
        //renderYawOffset = rotationYaw;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putInt("Aggro", aggro);
        return tag;
    }

    @Override
    public void fromTag(CompoundTag nbt) {
        super.fromTag(nbt);
        aggro = nbt.getInt("Aggro");
    }
}
