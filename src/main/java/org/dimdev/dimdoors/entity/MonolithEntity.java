package org.dimdev.dimdoors.entity;

import net.fabricmc.api.Dist;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.entity.ai.MonolithAggroGoal;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.world.ModDimensions;

public class MonolithEntity extends Mob {
    public static final int MAX_AGGRO = 250;
    private static final int MAX_AGGRO_CAP = 100;
    private static final int MIN_AGGRO_CAP = 25;
    private static final int MAX_TEXTURE_STATE = 18;
    private static final int MAX_SOUND_COOLDOWN = 200;
    public static final int MAX_AGGRO_RANGE = 35;
    private static final EntityDataAccessor<Integer> AGGRO = SynchedEntityData.defineId(MonolithEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(MonolithEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> PITCH = SynchedEntityData.defineId(MonolithEntity.class, EntityDataSerializers.FLOAT);
    private static final float EYE_HEIGHT_PERCENTAGE = 0.55f;
    @Environment(Dist.CLIENT)
    private static final RandomSource clientRandom = RandomSource.create();

    private int soundTime = 0;
    private final int aggroCap;

    MonolithEntity(Level world) {
        this(ModEntityTypes.MONOLITH, world);
    }

    public MonolithEntity(EntityType<? extends MonolithEntity> type, Level world) {
        super(ModEntityTypes.MONOLITH, world);
        this.noPhysics = true;
        this.aggroCap = Mth.nextInt(this.getRandom(), MIN_AGGRO_CAP, MAX_AGGRO_CAP);
        this.setNoGravity(true);
        this.lookControl = new LookControl(this) {
            @Override
            protected boolean resetXRotOnTick() {
                return false;
            }
        };

        this.setInvulnerable(true);
    }

    public boolean isDangerous() {
        return Constants.CONFIG_MANAGER.get().getMonolithsConfig().monolithTeleportation && (ModDimensions.isLimboDimension(this.level) || Constants.CONFIG_MANAGER.get().getMonolithsConfig().dangerousLimboMonoliths);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source != DamageSource.IN_WALL) {
            setAggro(MAX_AGGRO);
        }
        return false;
    }

    @Override
    protected int decreaseAirSupply(int i) {
        return 10;
    }

    @Override
    protected int increaseAirSupply(int i) {
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
    public boolean requiresCustomPersistence() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        // Add a short for the aggro level
        this.entityData.define(AGGRO, 0);
		this.entityData.define(SCALE, 1f);
		this.entityData.define(PITCH, 1f);
		this.refreshDimensions();
    }

    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public void move(MoverType movementType, Vec3 vec3d) {
    }

    @Override
    protected void customServerAiStep() {
        // Remove this Monolith if it's not in Limbo or in a pocket dungeon
        if (!(ModDimensions.isLimboDimension(this.level) || ModDimensions.isPocketDimension(this.level))) {
            this.remove(RemovalReason.DISCARDED);
            super.customServerAiStep();
            return;
        }

        super.customServerAiStep();

        // Check for players and update aggro levels even if there are no players in range
    }

    public void updateAggroLevel(Player player, boolean visibility) {
        // If we're working on the server side, adjust aggro level
        // If we're working on the client side, retrieve aggro level from dataWatcher
        if (player == null) {
            return;
        }

        if ((player.getInventory().armor.get(0).getItem() == ModItems.WORLD_THREAD_HELMET && player.getInventory().armor.get(1).getItem() == ModItems.WORLD_THREAD_CHESTPLATE && player.getInventory().armor.get(2).getItem() == ModItems.WORLD_THREAD_LEGGINGS && player.getInventory().armor.get(3).getItem() == ModItems.WORLD_THREAD_BOOTS)) {
            return;
        }

        if (!this.level.isClientSide) {
            if (player.distanceTo(this) > 70) {
                return;
            }

            int aggro = this.entityData.get(AGGRO);
            // Server side...
            // Rapidly increase the aggro level if this Monolith can see the player
            if (visibility) {
                if (ModDimensions.isLimboDimension(this.level)) {
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
            aggro = (short) Mth.clamp(aggro, 0, maxAggro);
            this.entityData.set(AGGRO, aggro);
        }
    }

    @Environment(Dist.CLIENT)
    public int getTextureState() {
        // Determine texture state from aggro progress
        return Mth.clamp(MAX_TEXTURE_STATE * this.entityData.get(AGGRO) / MAX_AGGRO, 0, MAX_TEXTURE_STATE);
    }

    /**
     * Plays sounds at different levels of aggro, using soundTime to prevent too many sounds at once.
     *
     * @param pos The position to play the sounds at
     */
    public void playSounds(Vec3 pos) {
        float aggroPercent = this.getAggroProgress();
        float pitch = getXRot();
        if (this.soundTime <= 0) {
            this.playSound(ModSoundEvents.MONK, 1F, pitch);
            this.soundTime = 100;
        }
        if (aggroPercent > 0.70 && this.soundTime < 100) {
            this.level.playSound(null, new BlockPos(pos), ModSoundEvents.TEARING, SoundSource.HOSTILE, 1F, (float) (1 + this.getRandom().nextGaussian()));
            this.soundTime = 100 + this.getRandom().nextInt(75);
        }
        if (aggroPercent > 0.80 && this.soundTime < MAX_SOUND_COOLDOWN) {
            this.level.playSound(null, new BlockPos(pos), ModSoundEvents.TEARING, SoundSource.HOSTILE, 7, 1);
            this.soundTime = 250;
        }
        this.soundTime--;
    }

    @Override
    public float getEyeHeight(Pose entityPose) {
        return getDimensions(entityPose).height * EYE_HEIGHT_PERCENTAGE;
    }

	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
		return getDimensions(pose).height * EYE_HEIGHT_PERCENTAGE;
	}

	@Environment(Dist.CLIENT)
    public static void spawnParticles(int aggro) {
		Player player = Minecraft.getInstance().player;
		if (aggro < 120) {
			return;
		}
		int count = 10 * aggro / MAX_AGGRO;
		for (int i = 1; i < count; ++i) {
			//noinspection ConstantConditions
			player.level.addParticle(ParticleTypes.PORTAL, player.getX() + (clientRandom.nextDouble() - 0.5D) * 3.0,
					player.getY() + clientRandom.nextDouble() * player.getBbHeight() - 0.75D,
					player.getZ() + (clientRandom.nextDouble() - 0.5D) * player.getBbWidth(),
					(clientRandom.nextDouble() - 0.5D) * 2.0D, -clientRandom.nextDouble(),
					(clientRandom.nextDouble() - 0.5D) * 2.0D);
		}
	}

    public float getAggroProgress() {
        return ((float) getAggro()) / MAX_AGGRO;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new MonolithAggroGoal(this, MAX_AGGRO_RANGE));
    }

    public void facePlayer(Player player) {
        this.lookControl.setLookAt(player, 1.0f, 1.0f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("Aggro", getAggro());
        nbt.putFloat("scale", getScale());
        nbt.putFloat("pitch", getXRot());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setAggro(nbt.getInt("Aggro"));
        if (nbt.contains("scale", NbtType.FLOAT)) {
        	setScale(nbt.getFloat("scale"));
		}
        if (nbt.contains("pitch", NbtType.FLOAT)) {
			setXRot(nbt.getFloat("pitch"));
		}
    }

	public int getAggro() {
        return this.entityData.get(AGGRO);
    }

    public void setAggro(int aggro) {
        this.entityData.set(AGGRO, aggro);
    }

//	@Override
//	public float getScale() {
//		return getScale();
//	}

	public float getScale() {
    	return this.entityData.get(SCALE);
	}

    public void setScale(float scale) {
    	this.entityData.set(SCALE, scale);
    	refreshDimensions();
	}

	public float getXRot() {
    	return this.entityData.get(PITCH);
	}

	public void setXRot(float pitch) {
    	this.entityData.set(PITCH, pitch);
	}

	@Override
	public AABB getLocalBoundsForPose(Pose pose) {
    	float scale = getScale();
		return super.getLocalBoundsForPose(pose).expandTowards(scale, scale, scale);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
		if (SCALE.equals(data)) {
			this.refreshDimensions();
		}

		super.onSyncedDataUpdated(data);
	}

	@Override
    public boolean checkSpawnRules(LevelAccessor world, MobSpawnType spawnReason) {
        if (spawnReason == MobSpawnType.CHUNK_GENERATION) {
            return super.checkSpawnRules(world, spawnReason);
        }
        if (spawnReason == MobSpawnType.NATURAL) {
            return this.getRandom().nextInt(32) == 2;
        }
        return false;
    }
}
