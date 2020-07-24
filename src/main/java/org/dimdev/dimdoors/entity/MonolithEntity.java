package org.dimdev.dimdoors.entity;

import java.util.Random;

import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.entity.ai.MonolithTask;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.world.ModDimensions;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;

public class MonolithEntity extends MobEntity {
    public final EntityDimensions DIMENSIONS = EntityDimensions.fixed(3f, 6f);

    public static final int MAX_AGGRO = 250;
    private static final int MAX_AGGRO_CAP = 100;
    private static final int MIN_AGGRO_CAP = 25;
    private static final int MAX_TEXTURE_STATE = 18;
    private static final int MAX_SOUND_COOLDOWN = 200;
    public static final int MAX_AGGRO_RANGE = 35;
    private static final TrackedData<Integer> AGGRO = DataTracker.registerData(MonolithEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final float EYE_HEIGHT = 1.5f;
    private static Random random;

    public float pitchLevel;
    private int aggro = 0;
    private int soundTime = 0;
    private final int aggroCap;

    MonolithEntity(World world) {
        this(ModEntityTypes.MONOLITH, world);
    }

    public MonolithEntity(EntityType<? extends MonolithEntity> type, World world) {
        super(ModEntityTypes.MONOLITH, world);
        random = this.getRandom();
        noClip = true;
        aggroCap = MathHelper.nextInt(getRandom(), MIN_AGGRO_CAP, MAX_AGGRO_CAP);
        this.setNoGravity(true);
        lookControl = new LookControl(this) {
            @Override
            protected boolean shouldStayHorizontal() {
                return false;
            }
        };

        setInvulnerable(true);
    }

    public EntityDimensions getDimensions(EntityPose entityPose) {
        return DIMENSIONS;
    }

    public boolean isDangerous() {
        return ModConfig.MONOLITHS.monolithTeleportation && (ModDimensions.isLimboDimension(world) || ModConfig.MONOLITHS.dangerousLimboMonoliths);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source != DamageSource.IN_WALL) {
            aggro = MAX_AGGRO;
        }
        return false;
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
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
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
        if (!(ModDimensions.isLimboDimension(world) || ModDimensions.isDimDoorsPocketDimension(world))) {
            this.remove();
            super.mobTick();
            return;
        }

        super.mobTick();

        // Check for players and update aggro levels even if there are no players in range
    }

    public void updateAggroLevel(PlayerEntity player, boolean visibility) {
        // If we're working on the server side, adjust aggro level
        // If we're working on the client side, retrieve aggro level from dataWatcher
        if(player == null) {
            return;
        }

        if((player.inventory.armor.get(0).getItem() == ModItems.WORLD_THREAD_HELMET && player.inventory.armor.get(1).getItem() == ModItems.WORLD_THREAD_CHESTPLATE && player.inventory.armor.get(2).getItem() == ModItems.WORLD_THREAD_LEGGINGS && player.inventory.armor.get(3).getItem() == ModItems.WORLD_THREAD_BOOTS)) {
            return;
        }

        if (!world.isClient) {
            if(player.distanceTo(this) > 70) {
                return;
            }

            int aggro = dataTracker.get(AGGRO);
            // Server side...
            // Rapidly increase the aggro level if this Monolith can see the player
            if (visibility) {
                if (ModDimensions.isLimboDimension(world)) {
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
                    } else if (aggro < aggroCap) {
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
        }
    }

    public int getTextureState() {
        // Determine texture state from aggro progress
        return MathHelper.clamp(MAX_TEXTURE_STATE * dataTracker.get(AGGRO) / MAX_AGGRO, 0, MAX_TEXTURE_STATE);
    }

    /**
     * Plays sounds at different levels of aggro, using soundTime to prevent too many sounds at once.
     *
     * @param pos The position to play the sounds at
     */
    public void playSounds(Vec3d pos) {
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

    @Override
    public float getEyeHeight(EntityPose entityPose) {
        return EYE_HEIGHT;
    }

    @Environment(EnvType.CLIENT)
    public static void spawnParticles(PacketContext context, PacketByteBuf data) {
        PlayerEntity player = context.getPlayer();
        int aggro = data.readInt();

        context.getTaskQueue().execute(() -> {
            if(aggro < 120) {
                return;
            }
            int count = 10 * aggro / MAX_AGGRO;
            for (int i = 1; i < count; ++i) {
                player.world.addParticle(ParticleTypes.PORTAL, player.getX() + (random.nextDouble() - 0.5D) * 3.0,
                        player.getY() + random.nextDouble() * player.getHeight() - 0.75D,
                        player.getZ() + (random.nextDouble() - 0.5D) * player.getWidth(),
                        (random.nextDouble() - 0.5D) * 2.0D, -random.nextDouble(),
                        (random.nextDouble() - 0.5D) * 2.0D);
            }
        });
    }

    public float getAggroProgress() {
        return (float) aggro / MAX_AGGRO;
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        goalSelector.add(0, new MonolithTask(this, MAX_AGGRO_RANGE));
    }

    public void facePlayer(PlayerEntity player) {
        lookControl.lookAt(player, 1.0f, 1.0f);
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

    public int getAggro() {
        return dataTracker.get(AGGRO);
    }

    public void setAggro(int aggro) {
        dataTracker.set(AGGRO, aggro);
    }

    @Override
    public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
        if (spawnReason == SpawnReason.CHUNK_GENERATION) {
            return super.canSpawn(world, spawnReason);
        }
        if (spawnReason == SpawnReason.NATURAL) {
            return this.getRandom().nextInt(32) == 2;
        }
        return false;
    }
}
