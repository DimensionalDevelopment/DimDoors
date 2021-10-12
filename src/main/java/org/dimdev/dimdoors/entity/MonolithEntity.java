package org.dimdev.dimdoors.entity;

import java.util.Random;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.util.math.Box;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.entity.ai.MonolithAggroGoal;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.world.ModDimensions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class MonolithEntity extends MobEntity {
    public static final int MAX_AGGRO = 250;
    private static final int MAX_AGGRO_CAP = 100;
    private static final int MIN_AGGRO_CAP = 25;
    private static final int MAX_TEXTURE_STATE = 18;
    private static final int MAX_SOUND_COOLDOWN = 200;
    public static final int MAX_AGGRO_RANGE = 35;
    private static final TrackedData<Integer> AGGRO = DataTracker.registerData(MonolithEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> SCALE = DataTracker.registerData(MonolithEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> PITCH = DataTracker.registerData(MonolithEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final float EYE_HEIGHT_PERCENTAGE = 0.55f;
    @Environment(EnvType.CLIENT)
    private static final Random clientRandom = new Random();

    private int soundTime = 0;
    private final int aggroCap;

    MonolithEntity(World world) {
        this(ModEntityTypes.MONOLITH, world);
    }

    public MonolithEntity(EntityType<? extends MonolithEntity> type, World world) {
        super(ModEntityTypes.MONOLITH, world);
        this.noClip = true;
        this.aggroCap = MathHelper.nextInt(this.getRandom(), MIN_AGGRO_CAP, MAX_AGGRO_CAP);
        this.setNoGravity(true);
        this.lookControl = new LookControl(this) {
            @Override
            protected boolean shouldStayHorizontal() {
                return false;
            }
        };

        this.setInvulnerable(true);
    }

    public boolean isDangerous() {
        return DimensionalDoorsInitializer.getConfig().getMonolithsConfig().monolithTeleportation && (ModDimensions.isLimboDimension(this.world) || DimensionalDoorsInitializer.getConfig().getMonolithsConfig().dangerousLimboMonoliths);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source != DamageSource.IN_WALL) {
            setAggro(MAX_AGGRO);
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

//    @Override
//    public Box getCollisionBox() {
//        return null;
//    }
//
//    @Override
//    public Box getHardCollisionBox(Entity entity) {
//        return null;
//    }

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
        this.dataTracker.startTracking(AGGRO, 0);
		this.dataTracker.startTracking(SCALE, 1f);
		this.dataTracker.startTracking(PITCH, 1f);
		this.calculateDimensions();
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
        if (!(ModDimensions.isLimboDimension(this.world) || ModDimensions.isPocketDimension(this.world))) {
            this.remove(RemovalReason.DISCARDED);
            super.mobTick();
            return;
        }

        super.mobTick();

        // Check for players and update aggro levels even if there are no players in range
    }

    public void updateAggroLevel(PlayerEntity player, boolean visibility) {
        // If we're working on the server side, adjust aggro level
        // If we're working on the client side, retrieve aggro level from dataWatcher
        if (player == null) {
            return;
        }

        if ((player.getInventory().armor.get(0).getItem() == ModItems.WORLD_THREAD_HELMET && player.getInventory().armor.get(1).getItem() == ModItems.WORLD_THREAD_CHESTPLATE && player.getInventory().armor.get(2).getItem() == ModItems.WORLD_THREAD_LEGGINGS && player.getInventory().armor.get(3).getItem() == ModItems.WORLD_THREAD_BOOTS)) {
            return;
        }

        if (!this.world.isClient) {
            if (player.distanceTo(this) > 70) {
                return;
            }

            int aggro = this.dataTracker.get(AGGRO);
            // Server side...
            // Rapidly increase the aggro level if this Monolith can see the player
            if (visibility) {
                if (ModDimensions.isLimboDimension(this.world)) {
                    if (this.isDangerous()) {
                        aggro++;
                    } else {
                        aggro += 36;
                    }
                } else {
                    // Aggro increases faster outside of Limbo
                    aggro += 3;
                }
            } else {
                if (this.isDangerous()) {
                    if (aggro > this.aggroCap) {
                        // Decrease aggro over time
                        aggro--;
                    } else if (aggro < this.aggroCap) {
                        // Increase aggro if a player is within range and aggro < aggroCap
                        aggro++;
                    }
                } else {
                    aggro -= 3;
                }
            }
            // Clamp the aggro level
            int maxAggro = this.isDangerous() ? MAX_AGGRO : 180;
            aggro = (short) MathHelper.clamp(aggro, 0, maxAggro);
            this.dataTracker.set(AGGRO, aggro);
        }
    }

    @Environment(EnvType.CLIENT)
    public int getTextureState() {
        // Determine texture state from aggro progress
        return MathHelper.clamp(MAX_TEXTURE_STATE * this.dataTracker.get(AGGRO) / MAX_AGGRO, 0, MAX_TEXTURE_STATE);
    }

    /**
     * Plays sounds at different levels of aggro, using soundTime to prevent too many sounds at once.
     *
     * @param pos The position to play the sounds at
     */
    public void playSounds(Vec3d pos) {
        float aggroPercent = this.getAggroProgress();
        float pitch = getPitch();
        if (this.soundTime <= 0) {
            this.playSound(ModSoundEvents.MONK, 1F, pitch);
            this.soundTime = 100;
        }
        if (aggroPercent > 0.70 && this.soundTime < 100) {
            this.world.playSound(null, new BlockPos(pos), ModSoundEvents.TEARING, SoundCategory.HOSTILE, 1F, (float) (1 + this.getRandom().nextGaussian()));
            this.soundTime = 100 + this.getRandom().nextInt(75);
        }
        if (aggroPercent > 0.80 && this.soundTime < MAX_SOUND_COOLDOWN) {
            this.world.playSound(null, new BlockPos(pos), ModSoundEvents.TEARING, SoundCategory.HOSTILE, 7, 1);
            this.soundTime = 250;
        }
        this.soundTime--;
    }

    @Override
    public float getEyeHeight(EntityPose entityPose) {
        return getDimensions(entityPose).height * EYE_HEIGHT_PERCENTAGE;
    }

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return getDimensions(pose).height * EYE_HEIGHT_PERCENTAGE;
	}

	@Environment(EnvType.CLIENT)
    public static void spawnParticles(int aggro) {
		PlayerEntity player = MinecraftClient.getInstance().player;
		if (aggro < 120) {
			return;
		}
		int count = 10 * aggro / MAX_AGGRO;
		for (int i = 1; i < count; ++i) {
			//noinspection ConstantConditions
			player.world.addParticle(ParticleTypes.PORTAL, player.getX() + (clientRandom.nextDouble() - 0.5D) * 3.0,
					player.getY() + clientRandom.nextDouble() * player.getHeight() - 0.75D,
					player.getZ() + (clientRandom.nextDouble() - 0.5D) * player.getWidth(),
					(clientRandom.nextDouble() - 0.5D) * 2.0D, -clientRandom.nextDouble(),
					(clientRandom.nextDouble() - 0.5D) * 2.0D);
		}
	}

    public float getAggroProgress() {
        return ((float) getAggro()) / MAX_AGGRO;
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new MonolithAggroGoal(this, MAX_AGGRO_RANGE));
    }

    public void facePlayer(PlayerEntity player) {
        this.lookControl.lookAt(player, 1.0f, 1.0f);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Aggro", getAggro());
        nbt.putFloat("scale", getScale());
        nbt.putFloat("pitch", getPitch());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setAggro(nbt.getInt("Aggro"));
        if (nbt.contains("scale", NbtType.FLOAT)) {
        	setScale(nbt.getFloat("scale"));
		}
        if (nbt.contains("pitch", NbtType.FLOAT)) {
			setPitch(nbt.getFloat("pitch"));
		}
    }

	public int getAggro() {
        return this.dataTracker.get(AGGRO);
    }

    public void setAggro(int aggro) {
        this.dataTracker.set(AGGRO, aggro);
    }

	@Override
	public float getScaleFactor() {
		return getScale();
	}

	public float getScale() {
    	return this.dataTracker.get(SCALE);
	}

    public void setScale(float scale) {
    	this.dataTracker.set(SCALE, scale);
    	calculateDimensions();
	}

	public float getPitch() {
    	return this.dataTracker.get(PITCH);
	}

	public void setPitch(float pitch) {
    	this.dataTracker.set(PITCH, pitch);
	}

	@Override
	public Box getBoundingBox(EntityPose pose) {
    	float scale = getScale();
		return super.getBoundingBox(pose).stretch(scale, scale, scale);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (SCALE.equals(data)) {
			this.calculateDimensions();
		}

		super.onTrackedDataSet(data);
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
