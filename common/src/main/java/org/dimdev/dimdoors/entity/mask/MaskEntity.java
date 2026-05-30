package org.dimdev.dimdoors.entity.mask;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.effect.ChasedEffect;
import org.dimdev.dimdoors.item.MaskItem;
import org.dimdev.dimdoors.item.MaskWandItem;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.world.ModDimensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;

public class MaskEntity extends Mob implements VibrationSystem {
    private static final EntityDataAccessor<Byte> MODE =
            SynchedEntityData.defineId(MaskEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> TYPE =
            SynchedEntityData.defineId(MaskEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> FROZEN =
            SynchedEntityData.defineId(MaskEntity.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState idleState = new AnimationState();
    public final AnimationState spottedState = new AnimationState();

    @Nullable
    private BlockPos homePos;

    private final MaskPatrolRoute patrolRoute = new MaskPatrolRoute();
    private MaskEchoBlockGoal echoBlockGoal;
    private final VibrationSystem.User vibrationUser;
    private VibrationSystem.Data vibrationData;
    private final DynamicGameEventListener<VibrationSystem.Listener> dynamicVibrationListener;

    private int idleSoundCooldown;

    private MaskMode resumeMode = MaskMode.WANDER;

    public MaskEntity(EntityType<? extends MaskEntity> entityType, Level level) {
        super(entityType, level);
        vibrationUser = new MaskVibrationUser(this);
        vibrationData = new VibrationSystem.Data();
        dynamicVibrationListener = new DynamicGameEventListener<>(new VibrationSystem.Listener(this));
        setNoGravity(true);
        xpReward = 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    public static void alertMasksNearBlock(Level level, BlockPos pos, Player player) {
        MaskAlert.alertMasksNearBlock(level, pos, player);
    }

    public static void notifyEchoesOfPlacedBlock(Level level, BlockPos pos, Player player) {
        MaskAlert.notifyEchoesOfPlacedBlock(level, pos, player);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MODE, (byte) MaskMode.WANDER.ordinal());
        builder.define(TYPE, (byte) MaskType.CYCLOP.ordinal());
        builder.define(FROZEN, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        goalSelector.addGoal(0, new MaskStunnedGoal(this));
        goalSelector.addGoal(1, new MaskChaseGoal(this));
        echoBlockGoal = new MaskEchoBlockGoal(this);
        goalSelector.addGoal(2, echoBlockGoal);

        goalSelector.addGoal(3, new MaskGuardGoal(this));
        goalSelector.addGoal(3, new MaskPatrolGoal(this));
        goalSelector.addGoal(3, new MaskWanderGoal(this));

        goalSelector.addGoal(4, new MaskPassiveScanGoal(this));
    }

    @Override
    public void tick() {
        if (!level().isClientSide) {
            if (getMaskType() == MaskType.RANDOM) {
                setMaskType(randomNormalType());
            }

            if (getMaskType() == MaskType.BLACK && getMode() != MaskMode.CHASE) {
                setMode(MaskMode.CHASE);
            }
        }

        super.tick();

        if (level().isClientSide) {
            spottedState.animateWhen(getMode() == MaskMode.CHASE, tickCount);
            idleState.animateWhen(getMode() != MaskMode.CHASE, tickCount);
            return;
        }

        if (isFrozen()) {
            setDeltaMovement(Vec3.ZERO);
            return;
        }

        tickVibrationSystem();
        tickSounds();
        MaskDetection.emitDetectionBubble(this);
    }

    @Override
    public void travel(@NotNull Vec3 travelVector) {
        MaskMovement.travel(this, travelVector);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (MODE.equals(key)) {
            applyModeFlags(getMode());
        }
    }

    private void tickSounds() {
        if (getMode() == MaskMode.CHASE || getMode() == MaskMode.STUNNED) {
            return;
        }

        if (idleSoundCooldown > 0) {
            idleSoundCooldown--;
            return;
        }

        if (random.nextInt(220) == 0) {
            level().playSound(
                    null,
                    blockPosition(),
                    SoundEvents.AMETHYST_BLOCK_CHIME,
                    SoundSource.HOSTILE,
                    0.55F,
                    0.65F + random.nextFloat() * 0.4F
            );
            idleSoundCooldown = 80;
        }
    }

    private void tickVibrationSystem() {
        if (getMaskType() == MaskType.SCULKING) {
            VibrationSystem.Ticker.tick(level(), vibrationData, vibrationUser);
        }
    }

    @Override
    public VibrationSystem.Data getVibrationData() {
        return vibrationData;
    }

    @Override
    public VibrationSystem.User getVibrationUser() {
        return vibrationUser;
    }

    @Override
    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> listenerConsumer) {
        if (level() instanceof ServerLevel serverLevel) {
            listenerConsumer.accept(dynamicVibrationListener, serverLevel);
        }
    }

    boolean canRunMode(MaskMode mode) {
        return !isFrozen() && getMode() == mode;
    }

    MaskPatrolRoute getPatrolRoute() {
        return patrolRoute;
    }

    @Nullable
    BlockPos getHomePos() {
        return homePos;
    }

    @Nullable
    Player findNearestPlayer(double range) {
        Player best = null;
        double bestDistance = range * range;

        for (Player player : level().players()) {
            if (player.isCreative() || player.isSpectator()) {
                continue;
            }

            double distance = distanceToSqr(player);
            if (distance <= bestDistance) {
                bestDistance = distance;
                best = player;
            }
        }

        return best;
    }

    void startChase(Player player) {
        if (getMode().isPassive()) {
            resumeMode = getMode();
        }

        clearEchoBlockTarget();
        patrolRoute.resetPause();
        setMode(MaskMode.CHASE);
    }

    void resetToHome() {
        setMode(resumeMode);
        patrolRoute.resetPause();

        if (homePos != null) {
            Vec3 home = MaskMovement.homeCenter(this);
            teleportTo(home.x, home.y, home.z);
        }

        setDeltaMovement(Vec3.ZERO);
    }

    void catchPlayer(Player player) {
        if (getMaskType() == MaskType.BLACK) {
            level().playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.WARDEN_SONIC_BOOM,
                    SoundSource.HOSTILE,
                    1.0F,
                    0.55F
            );

            if (ModDimensions.LIMBO_DIMENSION != null) {
                TeleportUtil.teleportUntargeted(player, ModDimensions.LIMBO_DIMENSION);
            }

            discard();
            return;
        }

        MaskItem.applyCaught(player, getMaskType());
        discard();
    }

    boolean canEchoTarget(BlockPos pos) {
        return echoBlockGoal.canTarget(pos);
    }

    void setEchoBlockTarget(BlockPos pos) {
        echoBlockGoal.setTarget(pos);
    }

    void clearEchoBlockTarget() {
        echoBlockGoal.clearTarget();
    }

    private MaskType randomNormalType() {
        MaskType[] values = {
                MaskType.CYCLOP,
                MaskType.ECHO,
                MaskType.ENLIGHTENED,
                MaskType.FORESIGHT,
                MaskType.SCULKING
        };
        return values[random.nextInt(values.length)];
    }

    public void configureFromWand(BlockPos home, List<BlockPos> waypoints, MaskType type) {
        homePos = home.immutable();
        setMaskType(type == MaskType.RANDOM ? randomNormalType() : type);
        patrolRoute.configure(homePos, waypoints);

        resumeMode = patrolRoute.canPatrol() ? MaskMode.PATROL : MaskMode.GUARD;

        if (getMaskType() == MaskType.BLACK) {
            resumeMode = MaskMode.CHASE;
            setMode(MaskMode.CHASE);
        } else {
            setMode(resumeMode);
        }

        Vec3 homeTarget = MaskMovement.homeCenter(this);
        teleportTo(homeTarget.x, homeTarget.y, homeTarget.z);
    }

    public void replaceHomeWaypoints(BlockPos home, List<BlockPos> waypoints) {
        if (homePos != null && !homePos.equals(home)) {
            return;
        }

        homePos = home.immutable();
        patrolRoute.configure(homePos, waypoints);
        patrolRoute.resetPause();

        MaskMode routeMode = patrolRoute.canPatrol() ? MaskMode.PATROL : MaskMode.GUARD;
        if (resumeMode.isPassive()) {
            resumeMode = routeMode;
        }
        if (getMode().isPassive()) {
            setMode(routeMode);
        }
    }

    public MaskMode getMode() {
        byte id = entityData.get(MODE);
        MaskMode[] values = MaskMode.values();
        return values[Mth.clamp(id, 0, values.length - 1)];
    }

    public void setMode(MaskMode mode) {
        entityData.set(MODE, (byte) mode.ordinal());
        applyModeFlags(mode);
    }

    private void applyModeFlags(MaskMode mode) {
        setGlowingTag(mode == MaskMode.CHASE);
        noPhysics = mode == MaskMode.CHASE;
        setInvulnerable(mode == MaskMode.CHASE || mode == MaskMode.STUNNED);
    }

    public MaskType getMaskType() {
        byte id = entityData.get(TYPE);
        MaskType[] values = MaskType.values();
        return values[Mth.clamp(id, 0, values.length - 1)];
    }

    public void setMaskType(MaskType type) {
        entityData.set(TYPE, (byte) type.ordinal());
    }

    public boolean isFrozen() {
        return entityData.get(FROZEN);
    }

    public void setFrozen(boolean frozen) {
        entityData.set(FROZEN, frozen);
        if (frozen) {
            setDeltaMovement(Vec3.ZERO);
        }
    }

    public void recallHomeAndToggleFrozen() {
        boolean freeze = !isFrozen();
        if (freeze && homePos != null) {
            Vec3 home = MaskMovement.homeCenter(this);
            teleportTo(home.x, home.y, home.z);
            setDeltaMovement(Vec3.ZERO);
        }
        setFrozen(freeze);
    }

    public void detachFromDestroyedHome(BlockPos home) {
        if (homePos != null && !homePos.equals(home)) {
            return;
        }

        homePos = null;
        patrolRoute.clear();
        clearEchoBlockTarget();
        resumeMode = MaskMode.WANDER;
        setFrozen(false);
        setMode(MaskMode.WANDER);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.FALLING_ANVIL)) {
            remove(RemovalReason.KILLED);
            return true;
        }

        Entity attacker = source.getEntity();
        if (attacker instanceof Player player && MaskWandItem.isHoldingMaskWand(player)) {
            if (player.isShiftKeyDown()) {
                recallHomeAndToggleFrozen();
                player.displayClientMessage(Component.literal(isFrozen() ? "Mask frozen" : "Mask released"), true);
            } else {
                MaskType nextType = getMaskType().nextEditable();
                setMaskType(nextType);
                player.displayClientMessage(Component.literal("Mask type: " + nextType.getSerializedName()), true);
            }
            return false;
        }

        if (getMode() == MaskMode.CHASE && isDirectPlayerAttack(source) && attacker instanceof Player player) {
            catchPlayer(player);
            return false;
        }

        if (getMode() == MaskMode.PATROL && isStunningHit(source)) {
            enterStunned();
        }

        return false;
    }

    private boolean isStunningHit(DamageSource source) {
        Entity direct = source.getDirectEntity();
        Entity attacker = source.getEntity();

        if (direct instanceof Projectile projectile) {
            return projectile.getOwner() instanceof Player;
        }

        return attacker instanceof Player;
    }

    private boolean isDirectPlayerAttack(DamageSource source) {
        Entity direct = source.getDirectEntity();
        return direct instanceof Player && direct == source.getEntity();
    }

    private void enterStunned() {
        setMode(MaskMode.STUNNED);
        clearEchoBlockTarget();
        setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public void playerTouch(Player player) {
        super.playerTouch(player);

        if (level().isClientSide) {
            return;
        }

        if (getMode() == MaskMode.STUNNED && isStompedBy(player)) {
            shatter(player);
            return;
        }

        if (getMode() == MaskMode.CHASE) {
            catchPlayer(player);
        }
    }

    private boolean isStompedBy(Player player) {
        return player.getY() > getY() + 0.35 && player.getDeltaMovement().y < -0.08;
    }

    private void shatter(Player player) {
        spawnAtLocation(new ItemStack(ModItems.MASK_SHARD));

        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            ChasedEffect.giveTo(serverPlayer);
        }

        level().playSound(null, blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 1.0F, 0.65F);
        remove(RemovalReason.KILLED);
    }

    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            MobSpawnType reason,
            @Nullable SpawnGroupData spawnData
    ) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData);

        if (homePos == null) {
            homePos = blockPosition();
        }

        if (getMaskType() == MaskType.RANDOM) {
            setMaskType(randomNormalType());
        }

        if (getMaskType() == MaskType.BLACK) {
            setMode(MaskMode.CHASE);
        }

        return data;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putString("Mode", getMode().getSerializedName());
        tag.putString("ResumeMode", resumeMode.getSerializedName());
        tag.putString("MaskType", getMaskType().getSerializedName());
        tag.putBoolean("Frozen", isFrozen());

        if (homePos != null) {
            tag.putLong("HomePos", homePos.asLong());
        }

        patrolRoute.save(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        setMode(readMode(tag, "Mode", MaskMode.WANDER));
        resumeMode = readMode(tag, "ResumeMode", getMode());
        setMaskType(readType(tag, "MaskType", MaskType.CYCLOP));
        setFrozen(tag.getBoolean("Frozen"));

        if (tag.contains("HomePos")) {
            homePos = BlockPos.of(tag.getLong("HomePos"));
        }

        patrolRoute.load(tag);

        if (homePos == null) {
            homePos = blockPosition();
        }
    }

    private static MaskMode readMode(CompoundTag tag, String key, MaskMode fallback) {
        if (!tag.contains(key)) {
            return fallback;
        }

        String value = tag.getString(key);
        for (MaskMode mode : MaskMode.values()) {
            if (mode.getSerializedName().equals(value)) {
                return mode;
            }
        }

        return fallback;
    }

    private static MaskType readType(CompoundTag tag, String key, MaskType fallback) {
        if (!tag.contains(key)) {
            return fallback;
        }

        String value = tag.getString(key);
        for (MaskType type : MaskType.values()) {
            if (type.getSerializedName().equals(value)) {
                return type;
            }
        }

        return fallback;
    }
}
